package ecommerce.productservice.service.impl;

import ecommerce.aipcommon.kafka.event.ProductCreateEvent;
import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.productservice.client.InventoryClient;
import ecommerce.productservice.dto.request.ProductRequest;
import ecommerce.productservice.dto.request.ProductSearchRequest;
import ecommerce.productservice.dto.request.ProductUpdateInfoRequest;
import ecommerce.productservice.dto.response.BrandResponse;
import ecommerce.productservice.dto.response.ProductResponse;
import ecommerce.productservice.dto.response.TagResponse;
import ecommerce.productservice.entity.*;
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
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    private final TagRepository tagRepository;
    private final ProductTagRepository productTagRepository;
    private final ProductMapper productMapper;
    private final CloudinaryService cloudinaryService;
    private final KafkaTemplate<String, ProductCreateEvent> productKafka;
    private final KafkaTemplate<String, NotificationEvent> notificationKafka;
    private final InventoryClient inventoryClient;
    private final BrandRepository brandRepository;

    @Transactional
    @Override
    public ApiResponse<ProductResponse> createProduct(ProductRequest request, List<MultipartFile> imageUrls) {
        try {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + request.getCategoryId()));


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

            List<Long> tagId = request.getTags();
            if (tagId != null && !tagId.isEmpty()) {
                List<Tag> tags = tagRepository.findAllById(tagId);
                for (Tag tag : tags) {
                    ProductTag pt = new ProductTag();
                    pt.setProduct(saved);
                    pt.setTag(tag);
                    productTagRepository.save(pt);
                }
            }

            List<TagResponse> tags = productTagRepository.findByProduct_Id(saved.getId())
                    .stream()
                    .map(pt -> new TagResponse(pt.getTag().getId(), pt.getTag().getName()))
                    .toList();


            ProductResponse response = productMapper.toResponse(saved);
            response.setTags(tags);
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

            return ApiResponse.<ProductResponse>builder()
                    .code(200)
                    .message("Tạo product thành công")
                    .data(response)
                    .build();

        } catch (Exception e) {
            log.error("Lỗi: {}", e.getMessage(), e);
            return ApiResponse.<ProductResponse>builder()
                    .code(500)
                    .message("Đã xảy ra lỗi trong hệ thống.")
                    .data(null)
                    .build();
        }
    }

    @Transactional
    @Override
    public ApiResponse<ProductResponse> updateProduct(Long id, ProductUpdateInfoRequest request, List<MultipartFile> imageUrls) {
        try {
            // 1️⃣ Lấy product và category
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + id));
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + request.getCategoryId()));

            // 2️⃣ Cập nhật thông tin product
            product.setName(request.getName());
            product.setDescription(request.getDescription());
            product.setPrice(request.getPrice());
            product.setDiscountPrice(request.getDiscountPrice());
            product.setBrandId(request.getBrandId());
            product.setUnit(request.getUnit());
            product.setCategory(category);

            List<Tag> tags = tagRepository.findAllById(request.getTags());
            for (Tag tag : tags) {
                ProductTag pt = new ProductTag();
                pt.setProduct(product);
                pt.setTag(tag);
                productTagRepository.save(pt);
            }
            // 3️⃣ Nếu có ảnh mới -> Xóa ảnh cũ và upload ảnh mới
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

            // 4️⃣ Save product sau khi cập nhật đầy đủ
            productRepository.save(product);

            // 5️⃣ Lấy danh sách ảnh để trả về
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

            return ApiResponse.<ProductResponse>builder()
                    .code(200)
                    .message("Cập nhật product thành công")
                    .data(response)
                    .build();

        } catch (Exception e) {
            log.error("Lỗi: {}", e.getMessage(), e);
            return ApiResponse.<ProductResponse>builder()
                    .code(500)
                    .message("Đã xảy ra lỗi trong hệ thống.")
                    .data(null)
                    .build();
        }
    }


    @Override
    public ApiResponse<Page<ProductResponse>> getAllProduct(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Product> productPage = productRepository.findAllByIsActive(1, pageable);

            Page<ProductResponse> response = productPage.map(product -> {
                ProductResponse res = productMapper.toResponse(product);

                // Lấy tags
                List<TagResponse> tags = productTagRepository.findByProduct_Id(product.getId())
                        .stream()
                        .map(pt -> new TagResponse(pt.getTag().getId(), pt.getTag().getName()))
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

                if (product.getBrandId() != null) {
                    brandRepository.findById(product.getBrandId())
                            .ifPresent(brand -> res.setBrand(
                                    new BrandResponse(brand.getId(), brand.getName())
                            ));
                }


                int quantity = inventoryClient.getQuantity(productRepository.findSkuCodeById(product.getId()));
                res.setQuantity(quantity);

                return res;
            });

            return ApiResponse.<Page<ProductResponse>>builder()
                    .code(200)
                    .message("Lấy product thành công")
                    .data(response)
                    .build();

        } catch (Exception e) {
            log.error("Lỗi: {}", e.getMessage(), e);
            return ApiResponse.<Page<ProductResponse>>builder()
                    .code(500)
                    .message("Đã xảy ra lỗi trong hệ thống.")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<ProductResponse> deleteProduct(Long id) {
        try {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + id));

            product.setIsActive(0);
            productRepository.save(product);
            ProductResponse response = productMapper.toResponse(product);
            return ApiResponse.<ProductResponse>builder()
                    .code(200)
                    .message("Xóa product thành công")
                    .data(response)
                    .build();

        } catch (Exception e) {
            log.error("Lỗi: {}", e.getMessage(), e);
            return ApiResponse.<ProductResponse>builder()
                    .code(500)
                    .message("Đã xảy ra lỗi trong hệ thống.")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<List<ProductResponse>> searchProduct(ProductSearchRequest request) {
        try {
            Specification<Product> spec = null;

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
                    Join<Product, ProductTag> tagJoin = root.join("productTags", JoinType.INNER);
                    query.distinct(true); // Tránh trùng
                    return tagJoin.get("tag").get("id").in(request.getTagName());
                });
            }

            List<Product> products = productRepository.findAll(spec);
            List<ProductResponse> responses = products.stream()
                    .map(productMapper::toResponse)
                    .toList();
            if (responses.isEmpty()) {
                return ApiResponse.<List<ProductResponse>>builder()
                        .code(200)
                        .message("Không tìm thấy sản phẩm phù hợp")
                        .data(List.of()) // vẫn trả về danh sách rỗng
                        .build();
            } else {
                return ApiResponse.<List<ProductResponse>>builder()
                        .code(200)
                        .message("Tìm kiếm thành công")
                        .data(responses)
                        .build();
            }

        } catch (Exception e) {
            log.error("Không tìm thấy sản phẩm " + e.getMessage(), e);
            return ApiResponse.<List<ProductResponse>>builder()
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
    public BigDecimal getPriceByProductId(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        BigDecimal price = product.getPrice();
        BigDecimal discount = product.getDiscountPrice();

        BigDecimal finalPrice = price.multiply(BigDecimal.ONE.subtract(discount));
        return finalPrice;
    }

    private Specification<Product> and(Specification<Product> base, Specification<Product> addition) {
        return (base == null) ? addition : base.and(addition);
    }

    @Override
    public ApiResponse<ProductResponse> getProductById(Long id) {
        try {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm " + id));
            ProductResponse response = productMapper.toResponse(product);
            List<ProductImage> imageEntities = productImageRepository.findByProductId(product.getId());

            List<TagResponse> tags = productTagRepository.findByProduct_Id(product.getId())
                    .stream()
                    .map(pt -> new TagResponse(pt.getTag().getId(), pt.getTag().getName()))
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
}
