package ecommerce.productservice.service;

import ecommerce.apicommon1.model.response.PageResponse;
import ecommerce.apicommon1.model.response.ProductSimpleResponse;
import ecommerce.productservice.dto.request.CreateProductRequest;
import ecommerce.productservice.dto.request.ProductUpdateInfoRequest;
import ecommerce.productservice.dto.request.SearchRequest;
import ecommerce.productservice.dto.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public interface ProductService {
    ProductResponse createProduct(CreateProductRequest request, List<MultipartFile> imageUrls);

    ProductResponse updateProduct(Long id, ProductUpdateInfoRequest request, List<MultipartFile> imageUrls);

    Page<ProductResponse> getAllProduct(int page, int size);

    ProductResponse deleteProduct(Long id);

    PageResponse<SearchProductResponse> search(SearchRequest request, Pageable pageable);

    String getSkuCodeByProductId(Long productId);

    PageResponse<ProductByTagResponse> getAllProductByTag(List<String> tags, Pageable pageable);

    ProductViewResponse viewProduct(Long productId);

    Map<Long, Boolean> findProduct(List<Long> ids);

    ProductSimpleResponse getPriceByProductId(Long id);

    Map<Long, ProductImageInfo> getImageUrl(List<Long> ids);
}