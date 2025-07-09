package ecommerce.productservice.service;

import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.aipcommon.model.response.ProductResponse;
import ecommerce.productservice.dto.request.ProductRequest;
import ecommerce.productservice.dto.request.ProductSearchRequest;
import ecommerce.productservice.entity.*;
import ecommerce.productservice.mapper.ProductMapper;
import ecommerce.productservice.repository.*;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    private final TagRepository tagRepository;
    private final ProductTagRepository productTagRepository;
    private final ProductMapper productMapper;

    public ApiResponse<ProductResponse> createProduct(ProductRequest request) {
        try {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + request.getCategoryId()));

            Product product = productMapper.toEntity(request);
            product.setCategory(category);
            Product saved = productRepository.save(product);

            List<String> imageUrls = request.getImageUrls();
            if (imageUrls != null && !imageUrls.isEmpty()) {
                for (String url : imageUrls) {
                    ProductImage image = new ProductImage();
                    image.setProduct(saved);
                    image.setImageUrl(url);
                    // Nếu muốn gắn thumbnail theo url
                    if (url.equals(request.getThumbnailUrl())) {
                        image.setIsThumbnail(1);
                    } else {
                        image.setIsThumbnail(0);
                    }
                    productImageRepository.save(image);
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

            List<String> tagName = productTagRepository.findByProduct_Id(saved.getId())
                    .stream()
                    .map(pt -> pt.getTag().getName())
                    .toList();


            ProductResponse response = productMapper.toResponse(saved);
            response.setTags(tagName);
            response.setThumbnailUrl(request.getThumbnailUrl());
            response.setImageUrls(request.getImageUrls());

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

    public ApiResponse<ProductResponse> updateProduct(Long id, ProductRequest request) {
        try {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + id));
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + request.getCategoryId()));

            product.setName(request.getName());
            product.setDescription(request.getDescription());
            product.setPrice(request.getPrice());
            product.setDiscountPrice(request.getDiscountPrice());
            product.setBrandId(request.getBrandId());
            product.setUnit(request.getUnit());
            product.setIsActive(request.getIsActive());
            product.setCategory(category);
            Product saved = productRepository.save(product);

            List<String> imageUrls = request.getImageUrls();
            if (imageUrls != null && !imageUrls.isEmpty()) {
                for (String url : imageUrls) {
                    ProductImage image = new ProductImage();
                    image.setProduct(saved);
                    image.setImageUrl(url);
                    // Nếu muốn gắn thumbnail theo url
                    if (url.equals(request.getThumbnailUrl())) {
                        image.setIsThumbnail(1);
                    } else {
                        image.setIsThumbnail(0);
                    }
                    productImageRepository.save(image);
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

            List<String> tagName = productTagRepository.findByProduct_Id(saved.getId())
                    .stream()
                    .map(pt -> pt.getTag().getName())
                    .toList();


            ProductResponse response = productMapper.toResponse(saved);
            response.setTags(tagName);
            response.setThumbnailUrl(request.getThumbnailUrl());
            response.setImageUrls(request.getImageUrls());

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

    public ApiResponse<List<ProductResponse>> getAllProduct() {
        try {
            List<Product> product = productRepository.findAllByIsActive(1);

            List<ProductResponse> response = product.stream()
                    .map(productMapper::toResponse)
                    .toList();

            return ApiResponse.<List<ProductResponse>>builder()
                    .code(200)
                    .message("Lấy product thành công")
                    .data(response)
                    .build();

        } catch (Exception e) {
            log.error("Lỗi: {}", e.getMessage(), e);
            return ApiResponse.<List<ProductResponse>>builder()
                    .code(500)
                    .message("Đã xảy ra lỗi trong hệ thống.")
                    .data(null)
                    .build();
        }
    }

    public ApiResponse<ProductResponse> deleteProduct(Long id) {
        try {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + id));

            product.setIsActive(0);
            productRepository.save(product);

            return ApiResponse.<ProductResponse>builder()
                    .code(200)
                    .message("Xóa product thành công")
                    .data(null)
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

    public ApiResponse<ProductResponse> getUserById(Long id) {
        try {
            Product product = productRepository.findById(id)
                    .orElse(null);

            if (product == null) {
                return ApiResponse.<ProductResponse>builder()
                        .code(404)
                        .message("Không tìm thấy người dùng với ID: " + id)
                        .data(null)
                        .build();
            }
            ProductResponse response = productMapper.toResponse(product);
            return ApiResponse.<ProductResponse>builder()
                    .code(200)
                    .message("Lấy thông tin người dùng thành công")
                    .data(response)
                    .build();
        } catch (Exception e) {
            log.error("Lỗi khi lấy người dùng theo ID: {}", e.getMessage(), e);
            return ApiResponse.<ProductResponse>builder()
                    .code(500)
                    .message("Đã xảy ra lỗi khi lấy thông tin người dùng")
                    .data(null)
                    .build();
        }
    }

    private Specification<Product> and(Specification<Product> base, Specification<Product> addition) {
        return (base == null) ? addition : base.and(addition);
    }
}
