package ecommerce.productservice.service;

import ecommerce.productservice.dto.request.ProductRequest;
import ecommerce.productservice.dto.response.ProductResponse;
import ecommerce.productservice.entity.*;
import ecommerce.productservice.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    private final TagRepository tagRepository;
    private final ProductTagRepository productTagRepository;

    public ProductResponse createProduct(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + request.getCategoryId()));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .discountPrice(request.getDiscountPrice())
                .category(category)
                .brandId(request.getBrandId())
                .unit(request.getUnit())
                .isActive(1)
                .build();

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


        return ProductResponse.builder()
                .id(saved.getId())
                .name(saved.getName())
                .categoryName(saved.getCategory().getName())
                .price(saved.getPrice())
                .tags(tagName)
                .imageUrls(request.getImageUrls())
                .thumbnailUrl(request.getThumbnailUrl())
                .createdAt(saved.getCreatedAt())
                .build();
    }
}
