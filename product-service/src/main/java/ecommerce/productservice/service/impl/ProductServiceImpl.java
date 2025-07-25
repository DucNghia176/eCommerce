package ecommerce.productservice.service.impl;

import ecommerce.aipcommon.config.TokenInfo;
import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.aipcommon.model.response.ProductResponse;
import ecommerce.productservice.dto.request.ProductRequest;
import ecommerce.productservice.dto.request.ProductSearchRequest;
import ecommerce.productservice.dto.request.ProductUpdateInfoRequest;
import ecommerce.productservice.entity.*;
import ecommerce.productservice.mapper.ProductMapper;
import ecommerce.productservice.repository.*;
import ecommerce.productservice.service.CloudinaryService;
import ecommerce.productservice.service.ProductService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
    private final TokenInfo tokenInfo;
    private final CloudinaryService cloudinaryService;

    @Override
    public ApiResponse<ProductResponse> createProduct(ProductRequest request, List<MultipartFile> imageUrls) {
        try {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + request.getCategoryId()));

            Product product = productMapper.toEntity(request);
            product.setCategory(category);
            Product saved = productRepository.save(product);

            if (imageUrls != null && !imageUrls.isEmpty()) {
                for (int i = 0; i < imageUrls.size(); i++) {
                    MultipartFile file = imageUrls.get(i);
                    if (!file.isEmpty()) {
                        String url = cloudinaryService.uploadFile(file, product.getId());

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

            List<String> tagName = productTagRepository.findByProduct_Id(saved.getId())
                    .stream()
                    .map(pt -> pt.getTag().getName())
                    .toList();


            ProductResponse response = productMapper.toResponse(saved);
            response.setTags(tagName);
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

//    @Override
//    public ApiResponse<ProductResponse> updateProduct(Long id, ProductRequest request) {
//        try {
//            Product product = productRepository.findById(id)
//                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + id));
//            Category category = categoryRepository.findById(request.getCategoryId())
//                    .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + request.getCategoryId()));
//
//            product.setName(request.getName());
//            product.setDescription(request.getDescription());
//            product.setPrice(request.getPrice());
//            product.setDiscountPrice(request.getDiscountPrice());
//            product.setBrandId(request.getBrandId());
//            product.setUnit(request.getUnit());
//            product.setIsActive(request.getIsActive());
//            product.setCategory(category);
//            Product saved = productRepository.save(product);
//
//            List<String> imageUrls = request.getImageUrls();
//            if (imageUrls != null && !imageUrls.isEmpty()) {
//                for (String url : imageUrls) {
//                    ProductImage image = new ProductImage();
//                    image.setProduct(saved);
//                    image.setImageUrl(url);
//                    // Nếu muốn gắn thumbnail theo url
//                    if (url.equals(request.getThumbnailUrl())) {
//                        image.setIsThumbnail(1);
//                    } else {
//                        image.setIsThumbnail(0);
//                    }
//                    productImageRepository.save(image);
//                }
//            }
//
//            List<Long> tagId = request.getTags();
//            if (tagId != null && !tagId.isEmpty()) {
//                List<Tag> tags = tagRepository.findAllById(tagId);
//                for (Tag tag : tags) {
//                    ProductTag pt = new ProductTag();
//                    pt.setProduct(saved);
//                    pt.setTag(tag);
//                    productTagRepository.save(pt);
//                }
//            }
//
//            List<String> tagName = productTagRepository.findByProduct_Id(saved.getId())
//                    .stream()
//                    .map(pt -> pt.getTag().getName())
//                    .toList();
//
//
//            ProductResponse response = productMapper.toResponse(saved);
//            response.setTags(tagName);
//            response.setThumbnailUrl(request.getThumbnailUrl());
//            response.setImageUrls(request.getImageUrls());
//
//            return ApiResponse.<ProductResponse>builder()
//                    .code(200)
//                    .message("Cập nhật product thành công")
//                    .data(response)
//                    .build();
//
//        } catch (Exception e) {
//            log.error("Lỗi: {}", e.getMessage(), e);
//            return ApiResponse.<ProductResponse>builder()
//                    .code(500)
//                    .message("Đã xảy ra lỗi trong hệ thống.")
//                    .data(null)
//                    .build();
//        }
//    }

    @Override
    public ApiResponse<ProductResponse> updateProductInfo(Long id, ProductUpdateInfoRequest request) {
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
            product.setCategory(category);
            Product saved = productRepository.save(product);
            ProductResponse response = productMapper.toResponse(saved);
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

    @Transactional
    @Override
    public ApiResponse<ProductResponse> updateProductImage(Long id, List<MultipartFile> imageUrls) {
        try {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + id));

            if (imageUrls != null && !imageUrls.isEmpty()) {
                List<ProductImage> oldImages = productImageRepository.findByProductId(product.getId());
                productImageRepository.deleteByProductId(product.getId());
                cloudinaryService.deleteFolderByProductId(product.getId());
            }
            // Upload ảnh mới
            if (imageUrls != null && !imageUrls.isEmpty()) {
                for (int i = 0; i < imageUrls.size(); i++) {
                    MultipartFile file = imageUrls.get(i);
                    if (!file.isEmpty()) {
                        String url = cloudinaryService.uploadFile(file, product.getId());

                        ProductImage newImage = new ProductImage();
                        newImage.setProduct(product);
                        newImage.setImageUrl(url);
                        newImage.setIsThumbnail(i == 0 ? 1 : 0); // ảnh đầu tiên là thumbnail
                        productImageRepository.save(newImage);
                    }
                }
            }
            ProductResponse response = productMapper.toResponse(product);
            List<ProductImage> imageEntities = productImageRepository.findByProductId(product.getId());
            response.setImageUrls(
                    imageEntities.stream().map(ProductImage::getImageUrl).toList()
            );
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

    @Override
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
    public ApiResponse<ProductResponse> getProductByUserId(Long id) {
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
