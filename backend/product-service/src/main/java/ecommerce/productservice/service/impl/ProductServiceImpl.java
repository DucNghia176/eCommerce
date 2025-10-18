package ecommerce.productservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import ecommerce.apicommon1.client.RedisClient;
import ecommerce.apicommon1.kafka.event.ProductCreateEvent;
import ecommerce.apicommon1.model.response.ProductPriceResponse;
import ecommerce.apicommon1.util.CacheHelper;
import ecommerce.productservice.client.InventoryClient;
import ecommerce.productservice.dto.request.AttributeRequest;
import ecommerce.productservice.dto.request.CreateProductRequest;
import ecommerce.productservice.dto.request.ProductUpdateInfoRequest;
import ecommerce.productservice.dto.request.SearchRequest;
import ecommerce.productservice.dto.response.*;
import ecommerce.productservice.entity.*;
import ecommerce.productservice.kafka.event.NotificationEvent;
import ecommerce.productservice.mapper.ProductMapper;
import ecommerce.productservice.repository.*;
import ecommerce.productservice.service.CloudinaryService;
import ecommerce.productservice.service.ProductService;
import ecommerce.productservice.status.NotificationType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    private final TagRepository tagRepository;
    private final ProductMapper productMapper;
    private final CloudinaryService cloudinaryService;
    private final KafkaTemplate<String, ProductCreateEvent> productKafka;
    private final KafkaTemplate<String, NotificationEvent> notificationKafka;
    private final InventoryClient inventoryClient;
    private final BrandRepository brandRepository;
    private final ProductAttributeRepository productAttributeRepository;
    private final AttributeRepository attributeRepository;
    private final AttributeValueRepository attributeValueRepository;
    private final RedisClient redisClient;
    private final ObjectMapper objectMapper;
    private final CacheHelper cacheHelper;
    private final RatingRepository ratingRepository;

    @Transactional
    @Override
    public ProductResponse createProduct(CreateProductRequest request, List<MultipartFile> imageUrls) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy danh mục với ID: " + request.getCategoryId()));

        // Tạo SKU và entity
        Product product = productMapper.toEntity(request);
        String generatedSku = "SKU-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        product.setSkuCode(generatedSku);

        // Thuộc tính sản phẩm
        Set<ProductAttribute> productAttributes = buildProductAttributes(product, request.getAttributes());
        product.setProductAttributes(productAttributes);
        product.setCategory(category);

        // Lưu để có ID cho upload ảnh
        productRepository.save(product);

        // Upload ảnh
        replaceProductImages(product, imageUrls, false);

        // Gửi event Kafka
        productKafka.send("product-create", ProductCreateEvent.builder()
                .productId(product.getId())
                .skuCode(product.getSkuCode())
                .name(product.getName())
                .importPrice(product.getPrice())
                .build());

        notificationKafka.send("new-product-topic", new NotificationEvent(
                null,
                "Sản phẩm mới: " + product.getName(),
                NotificationType.NEW_PRODUCT,
                product.getId()
        ));

        return buildProductResponse(product);
    }

    @Transactional
    @Override
    public ProductResponse updateProduct(Long id, ProductUpdateInfoRequest request, List<MultipartFile> imageUrls) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy sản phẩm với ID: " + id));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy danh mục với ID: " + request.getCategoryId()));

        // Cập nhật thông tin cơ bản
        product = Product.builder()
                .name(request.getName())
                .price(request.getPrice())
                .description(request.getDescription())
                .category(category)
                .discount(request.getDiscount())
                .brandId(request.getBrandId())
                .unit(request.getUnit())
                .build();

        // Cập nhật tags
        List<Tag> tags = tagRepository.findAllById(request.getTags());
        product.getTags().clear();
        product.getTags().addAll(tags);

        // Cập nhật thuộc tính
        productAttributeRepository.deleteByProductId(product.getId());
        Set<ProductAttribute> productAttributes = buildProductAttributes(product, request.getAttributes());
        product.setProductAttributes(productAttributes);

        // Cập nhật ảnh
        replaceProductImages(product, imageUrls, true);

        productRepository.save(product);
        return buildProductResponse(product);
    }


    private Set<ProductAttribute> buildProductAttributes(Product product, List<AttributeRequest> attributes) {
        Set<ProductAttribute> productAttributes = new HashSet<>();

        for (AttributeRequest attrReq : attributes) {
            Attribute attribute = attributeRepository.findByNameIgnoreCase(attrReq.getAttributeName().trim())
                    .orElseGet(() -> {
                        Attribute newAttr = Attribute.builder()
                                .name(attrReq.getAttributeName().toLowerCase().trim())
                                .build();
                        return attributeRepository.save(newAttr);
                    });

            AttributeValue value = attributeValueRepository.findByValueIgnoreCase(attrReq.getAttributeValueName().trim())
                    .orElseGet(() -> {
                        AttributeValue newValue = AttributeValue.builder()
                                .value(attrReq.getAttributeValueName().toLowerCase().trim())
                                .build();
                        return attributeValueRepository.save(newValue);
                    });

            productAttributes.add(ProductAttribute.builder()
                    .attribute(attribute)
                    .value(value)
                    .product(product)
                    .build());
        }
        return productAttributes;
    }

    private void replaceProductImages(Product product, List<MultipartFile> imageUrls, boolean clearOld) {
        if (clearOld) {
            productImageRepository.deleteByProductId(product.getId());
            cloudinaryService.deleteFolderByProductId(product.getId());
        }

        List<ProductImage> newImages = new ArrayList<>();
        for (int i = 0; i < imageUrls.size(); i++) {
            MultipartFile file = imageUrls.get(i);
            if (!file.isEmpty()) {
                String url = cloudinaryService.uploadFileProduct(file, product.getId());
                ProductImage image = new ProductImage();
                image.setProduct(product);
                image.setImageUrl(url);
                image.setIsThumbnail(i == 0 ? 1 : 0);
                newImages.add(image);
            }
        }
        productImageRepository.saveAll(newImages);
    }

    private ProductResponse buildProductResponse(Product product) {
        ProductResponse response = productMapper.toResponse(product);
        List<ProductImage> images = productImageRepository.findByProductId(product.getId());
        response.setImageUrls(images.stream().map(ProductImage::getImageUrl).toList());
        response.setThumbnailUrl(
                images.stream()
                        .filter(img -> img.getIsThumbnail() != null && img.getIsThumbnail() == 1)
                        .map(ProductImage::getImageUrl)
                        .findFirst()
                        .orElse(null)
        );
        response.setTags(product.getTags().stream()
                .map(tag -> new TagResponse(tag.getId(), tag.getName()))
                .toList());
        return response;
    }

    @Override
    public Page<ProductResponse> getAllProduct(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<ProductResponse> response = productRepository.findAllActiveProducts(pageable);

        List<String> skuCodes = response.getContent()
                .stream()
                .map(ProductResponse::getSkuCode)
                .toList();

        Map<String, Integer> quantityMap = inventoryClient.getQuantities(skuCodes);

        response.getContent().forEach(product ->
                product.setQuantity(quantityMap.getOrDefault(product.getSkuCode(), 0))
        );

        return response;

    }

    @Override
    public ProductResponse deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy danh mục với ID: " + id));

        product.setIsActive(0);
        productRepository.save(product);

        return productMapper.toResponse(product);
    }

    @Override
    public Page<SearchProductResponse> search(SearchRequest request, Pageable pageable) {
        String redisKey = String.format(
                "product:search:%s_%s_%s_%s_%s_%s_%d_%d",
                request.getKeyword(),
                request.getCategoryId(),
                request.getBrandId(),
                request.getPriceFrom(),
                request.getPriceTo(),
                request.getRatingFrom(),
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        if (redisClient.exists(redisKey)) {
            cacheHelper.getPageCache(redisKey, SearchProductResponse.class);
        }

        Page<SearchProductResponse> responses = productRepository.searchProducts(
                request.getKeyword(),
                request.getCategoryId(),
                request.getBrandId(),
                request.getPriceFrom(),
                request.getPriceTo(),
                request.getRatingFrom(),
                pageable
        );
        cacheHelper.setPageCache(redisKey, responses, 15, TimeUnit.MINUTES);

        return responses;
    }

    @Override
    public String getSkuCodeByProductId(Long productId) {
        return productRepository.findSkuCodeById(productId);
    }

    @Override
    public Page<ProductByTagResponse> getAllProductByTag(List<String> tags, Pageable pageable) {
        String tagPart = String.join(",", tags);
        String redisKey = String.format(
                "product:getByTag:%s_%d_%d",
                tagPart,
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        if (redisClient.exists(redisKey)) {
            cacheHelper.getPageCache(redisKey, ProductByTagResponse.class);
        }

        List<Long> tagIds = tagRepository.findByNameIn(tags)
                .stream()
                .map(Tag::getId)
                .toList();
        Page<Product> products = productRepository.getAllByTagIds(tagIds, pageable);

        // Lấy quantity qua feign-client
        List<String> skuCodes = products.getContent().stream()
                .map(Product::getSkuCode)
                .toList();
        Map<String, Integer> quantityMap = inventoryClient.getQuantities(skuCodes);

        // Lấy Brand
        List<Long> brandIds = products.getContent().stream()
                .map(Product::getBrandId)
                .toList();
        Map<Long, Brand> brandMap = brandRepository.findAllById(brandIds)
                .stream()
                .collect(Collectors.toMap(Brand::getId, Function.identity()));

        Page<ProductByTagResponse> responses = products.map(product -> {
            ProductByTagResponse response = new ProductByTagResponse();
            ProductResponse base = productMapper.toResponse(product);

            BeanUtils.copyProperties(base, response);
            // Set quantity
            response.setQuantity(quantityMap.getOrDefault(product.getSkuCode(), 0));

            // Set brand
            Brand brand = brandMap.get(product.getBrandId());
            if (brand != null) {
                response.setBrand(BrandResponse.builder()
                        .name(brand.getName())
                        .id(brand.getId())
                        .build());
            } else {
                response.setBrand(null);
            }

            // Set images và thumbnail
            List<ProductImage> images = productImageRepository.findAllByProductId(product.getId());
            response.setImageUrls(images.stream().map(ProductImage::getImageUrl).toList());
            response.setThumbnailUrl(images.stream()
                    .filter(img -> img.getIsThumbnail() != null && img.getIsThumbnail() == 1)
                    .map(ProductImage::getImageUrl)
                    .findFirst()
                    .orElse(null));

            response.setScore(Optional.ofNullable(ratingRepository.getAverageScoreByProductId(product.getId())).orElse(0.0));
            response.setUser(Optional.ofNullable(ratingRepository.countByProductId(product.getId())).orElse(0L));

            return response;
        });

        cacheHelper.setPageCache(redisKey, responses, 15, TimeUnit.MINUTES);

        return responses;
    }

    @Override
    public ProductViewResponse viewProduct(Long productId) {
        String redisKey = "product:view:" + productId;

        if (redisClient.exists(redisKey)) {
            Object cached = redisClient.getCache(redisKey);
            return objectMapper.convertValue(cached, ProductViewResponse.class);
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy danh mục với ID: " + productId));

        ProductViewResponse response = productRepository.findProduct(productId);

        response.setQuantity(inventoryClient.getQuantity(response.getSkuCode()));

        List<String> imageUrls = productImageRepository.findImageUrl(productId);

        List<ProductAttributeProjection> rows = productAttributeRepository.findAttributesByProductId(productId);

        Map<String, List<String>> group = rows.stream()
                .collect(Collectors.groupingBy(ProductAttributeProjection::getAttribute, Collectors.mapping(ProductAttributeProjection::getValue, Collectors.toList())
                ));

        List<ProductAttributeResponse> attribute = group.entrySet().stream()
                .map(att -> new ProductAttributeResponse(att.getKey(), att.getValue()))
                .toList();
        response.setImageUrls(imageUrls);
        response.setAttributes(attribute);

        List<Long> relatedIds = productRepository.findRelatedProductIds(product.getCategory().getId(), productId);
        if (relatedIds.size() > 100) {
            Collections.shuffle(relatedIds);
            relatedIds = relatedIds.subList(0, 100);
        }
        response.setRelatedProducts(relatedIds);

        redisClient.putTtlCache(redisKey, response, 12, TimeUnit.HOURS);

        return response;
    }

    @Override
    public Map<Long, Boolean> findProduct(List<Long> ids) {
        Set<Long> existingIds = productRepository.findAllById(ids)
                .stream()
                .map(Product::getId)
                .collect(Collectors.toSet());

        return ids.stream()
                .collect(Collectors.toMap(Function.identity(), existingIds::contains));
    }

    @Override
    public ProductPriceResponse getPriceByProductId(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Không có sản phẩm"));

        BigDecimal originalPrice = product.getPrice();
        BigDecimal discountPercent = product.getDiscount();
        BigDecimal discountDecimal = discountPercent != null
                ? discountPercent.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal finalPrice = originalPrice.multiply(BigDecimal.ONE.subtract(discountDecimal));

        return ProductPriceResponse.builder()
                .productId(id)
                .originalPrice(originalPrice)
                .discountPercent(discountPercent)
                .finalPrice(finalPrice)
                .build();
    }

    @Override
    public Map<Long, ProductImageInfo> getImageUrl(List<Long> ids) {
        Map<Long, ProductImageInfo> response = productImageRepository.findByProductIdInAndIsThumbnail(ids)
                .stream()
                .collect(Collectors.toMap(
                        img -> img.getProduct().getId(),
                        img -> ProductImageInfo.builder()
                                .imageUrl(img.getImageUrl())
                                .name(img.getProduct().getName())
                                .build()
                ));
        return response;
    }
}
