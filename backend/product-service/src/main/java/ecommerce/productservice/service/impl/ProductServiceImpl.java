package ecommerce.productservice.service.impl;

import ecommerce.apicommon1.kafka.event.ProductCreateEvent;
import ecommerce.apicommon1.model.response.ApiResponse;
import ecommerce.apicommon1.model.response.ProductPriceResponse;
import ecommerce.productservice.client.InventoryClient;
import ecommerce.productservice.dto.request.ProductRequest;
import ecommerce.productservice.dto.request.ProductSearchRequest;
import ecommerce.productservice.dto.request.ProductUpdateInfoRequest;
import ecommerce.productservice.dto.response.*;
import ecommerce.productservice.entity.Category;
import ecommerce.productservice.entity.Product;
import ecommerce.productservice.entity.ProductImage;
import ecommerce.productservice.entity.Tag;
import ecommerce.productservice.kafka.event.NotificationEvent;
import ecommerce.productservice.mapper.ProductMapper;
import ecommerce.productservice.repository.*;
import ecommerce.productservice.service.CloudinaryService;
import ecommerce.productservice.service.ProductService;
import ecommerce.productservice.status.NotificationType;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;
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

    @Transactional
    @Override
    public ProductResponse createProduct(ProductRequest request, List<MultipartFile> imageUrls) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy danh mục với ID: " + request.getCategoryId()));


        Product product = productMapper.toEntity(request);
        if (request.getSkuCode() == null || request.getSkuCode().isBlank()) {
            String generatedSku = "SKU-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            product.setSkuCode(generatedSku);
        } else {
            product.setSkuCode(request.getSkuCode());
        }

        product.setCategory(category);

        Product saved = productRepository.save(product);

        if (imageUrls != null && !imageUrls.isEmpty()) {
            for (int i = 0; i < imageUrls.size(); i++) {
                MultipartFile file = imageUrls.get(i);
                if (!file.isEmpty()) {
                    String url = cloudinaryService.uploadFileProduct(file, product.getId());

                    ProductImage newImage = new ProductImage();
                    newImage.setProduct(product);
                    newImage.setImageUrl(url);
                    newImage.setIsThumbnail(i == 0 ? 1 : 0); // ảnh đầu tiên là thumbnail
                    productImageRepository.save(newImage);
                }
            }
        }

        ProductResponse response = productMapper.toResponse(saved);
        response.setTags(saved.getTags().stream()
                .map(tag -> new TagResponse(tag.getId(), tag.getName()))
                .toList());

        List<ProductImage> imageEntities = productImageRepository.findByProductId(saved.getId());
        response.setImageUrls(imageEntities.stream().map(ProductImage::getImageUrl).toList());
        response.setThumbnailUrl(
                imageEntities.stream()
                        .filter(img -> img.getIsThumbnail() != null && img.getIsThumbnail() == 1)
                        .map(ProductImage::getImageUrl)
                        .findFirst()
                        .orElse(null)
        );

        ProductCreateEvent event = new ProductCreateEvent(
                product.getSkuCode(),
                product.getName(),
                product.getPrice()
        );
        productKafka.send("product-create", event);

        NotificationEvent notificationEvent = new NotificationEvent(
                null,
                "Sản phẩm mới: " + product.getName(),
                NotificationType.NEW_PRODUCT,
                product.getId()
        );
        notificationKafka.send("new-product-topic", notificationEvent);

        return response;
    }

    @Transactional
    @Override
    public ProductResponse updateProduct(Long id, ProductUpdateInfoRequest request, List<MultipartFile> imageUrls) {
        //  Lấy product và category
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy sản phẩm với ID: " + id));
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy danh mục với ID: " + request.getCategoryId()));

        // Cập nhật thông tin product
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setDiscount(request.getDiscount());
        product.setBrandId(request.getBrandId());
        product.setUnit(request.getUnit());
        product.setCategory(category);

        List<Tag> tags = tagRepository.findAllById(request.getTags());
        product.getTags().clear(); // Xóa tag cũ
        product.getTags().addAll(tags);

        //  Nếu có ảnh mới -> Xóa ảnh cũ và upload ảnh mới
        if (imageUrls != null && !imageUrls.isEmpty()) {
            productImageRepository.deleteByProductId(product.getId());
            cloudinaryService.deleteFolderByProductId(product.getId());

            for (int i = 0; i < imageUrls.size(); i++) {
                MultipartFile file = imageUrls.get(i);
                if (!file.isEmpty()) {
                    String url = cloudinaryService.uploadFileProduct(file, product.getId());

                    ProductImage newImage = new ProductImage();
                    newImage.setProduct(product);
                    newImage.setImageUrl(url);
                    newImage.setIsThumbnail(i == 0 ? 1 : 0);
                    productImageRepository.save(newImage);
                }
            }
        }

        // Save product sau khi cập nhật đầy đủ
        productRepository.save(product);

        //  Lấy danh sách ảnh để trả về
        ProductResponse response = productMapper.toResponse(product);
        List<ProductImage> imageEntities = productImageRepository.findByProductId(product.getId());
        response.setImageUrls(imageEntities.stream().map(ProductImage::getImageUrl).toList());
        response.setThumbnailUrl(
                imageEntities.stream()
                        .filter(img -> img.getIsThumbnail() != null && img.getIsThumbnail() == 1)
                        .map(ProductImage::getImageUrl)
                        .findFirst()
                        .orElse(null)
        );

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
    public ApiResponse<Page<ProductResponse>> searchProduct(ProductSearchRequest request, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

            Specification<Product> spec = (root, query, cb) -> cb.equal(root.get("isActive"), 1);

            if (request.getName() != null && !request.getName().isBlank()) {
                Specification<Product> nameSpec = (root, query, cb) ->
                        cb.like(cb.lower(root.get("name")), "%" + request.getName().toLowerCase() + "%");
                spec = and(spec, nameSpec);
            }

            if (request.getPriceFrom() != null && request.getPriceTo() != null) {
                spec = and(spec, (root, query, cb) ->
                        cb.between(root.get("price"), request.getPriceFrom(), request.getPriceTo()));
            } else if (request.getPriceFrom() != null) {
                spec = and(spec, (root, query, cb) ->
                        cb.greaterThanOrEqualTo(root.get("price"), request.getPriceFrom()));
            } else if (request.getPriceTo() != null) {
                spec = and(spec, (root, query, cb) ->
                        cb.lessThanOrEqualTo(root.get("price"), request.getPriceTo()));
            }

            if (request.getCategoryName() != null && !request.getCategoryName().isBlank()) {
                Specification<Product> categorySpec = (root, query, cb) ->
                        cb.equal(cb.lower(root.get("category").get("name")), request.getCategoryName().toLowerCase());
                spec = and(spec, categorySpec);
            }

            if (request.getHasDiscount() != null) {
                if (request.getHasDiscount()) {
                    spec = and(spec, (root, query, cb) ->
                            cb.isNotNull(root.get("discountPrice")));
                } else {
                    spec = and(spec, (root, query, cb) ->
                            cb.isNull(root.get("discountPrice")));
                }
            }

            if (request.getTagName() != null && !request.getTagName().isEmpty()) {
                spec = and(spec, (root, query, cb) -> {
                    Join<Product, Tag> tagJoin = root.join("tags", JoinType.INNER);
                    query.distinct(true);
                    return tagJoin.get("id").in(request.getTagName());
                });
            }

            // Sử dụng findAll với Pageable
            Page<Product> productPage = productRepository.findAll(spec, pageable);

            Page<ProductResponse> responsePage = productPage.map(product -> {
                ProductResponse res = productMapper.toResponse(product);

                // Lấy tags
                List<TagResponse> tags = product.getTags().stream()
                        .map(tag -> new TagResponse(tag.getId(), tag.getName()))
                        .toList();
                res.setTags(tags);

                // Lấy ảnh
                List<ProductImage> imageEntities = productImageRepository.findByProductId(product.getId());
                res.setImageUrls(imageEntities.stream().map(ProductImage::getImageUrl).toList());

                // Thumbnail
                String thumbnail = imageEntities.stream()
                        .filter(img -> img.getIsThumbnail() != null && img.getIsThumbnail() == 1)
                        .map(ProductImage::getImageUrl)
                        .findFirst()
                        .orElse(null);
                res.setThumbnailUrl(thumbnail);

                // Brand
                if (product.getBrandId() != null) {
                    brandRepository.findById(product.getBrandId())
                            .ifPresent(brand -> res.setBrand(new BrandResponse(brand.getId(), brand.getName())));
                }

                // Quantity
                int quantity = inventoryClient.getQuantity(productRepository.findSkuCodeById(product.getId()));
                res.setQuantity(quantity);

                return res;
            });

            return ApiResponse.<Page<ProductResponse>>builder()
                    .code(200)
                    .message(responsePage.isEmpty() ? "Không tìm thấy sản phẩm phù hợp" : "Tìm kiếm thành công")
                    .data(responsePage)
                    .build();

        } catch (Exception e) {
            return ApiResponse.<Page<ProductResponse>>builder()
                    .code(500)
                    .message("Đã xảy ra lỗi khi tìm kiếm")
                    .data(null)
                    .build();
        }
    }

    @Override
    public String getSkuCodeByProductId(Long productId) {
        return productRepository.findSkuCodeById(productId);
    }

    @Override
    public BigDecimal getPriceByProductId1(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Product not found"));

        BigDecimal price = product.getPrice();
        BigDecimal discount = product.getDiscount();

        return price.multiply(BigDecimal.ONE.subtract(discount));
    }

    private Specification<Product> and(Specification<Product> base, Specification<Product> addition) {
        return (base == null) ? addition : base.and(addition);
    }

    @Override
    public ApiResponse<ProductResponse> getProductById(Long id) {
        try {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Không tìm thấy sản phẩm " + id));
            ProductResponse response = productMapper.toResponse(product);
            List<ProductImage> imageEntities = productImageRepository.findByProductId(product.getId());

            List<TagResponse> tags = product.getTags().stream()
                    .map(tag -> new TagResponse(tag.getId(), tag.getName()))
                    .toList();
            response.setTags(tags);

            response.setImageUrls(imageEntities.stream().map(ProductImage::getImageUrl).toList());
            response.setThumbnailUrl(
                    imageEntities.stream()
                            .filter(img -> img.getIsThumbnail() != null && img.getIsThumbnail() == 1)
                            .map(ProductImage::getImageUrl)
                            .findFirst()
                            .orElse(null)
            );
            return ApiResponse.<ProductResponse>builder()
                    .code(200)
                    .message("Lấy sản phầm với id =" + id)
                    .data(response)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<ProductResponse>builder()
                    .code(500)
                    .message("Lỗi hệ thống: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Override
    public ProductViewResponse viewProduct(Long productId) {
        productRepository.findById(productId)
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
                ? discountPercent.divide(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        BigDecimal finalPrice = originalPrice.multiply(BigDecimal.ONE.subtract(discountDecimal));

        return ProductPriceResponse.builder()
                .productId(id)
                .originalPrice(originalPrice)
                .discountPercent(discountPercent)
                .finalPrice(finalPrice)
                .build();
    }
}
