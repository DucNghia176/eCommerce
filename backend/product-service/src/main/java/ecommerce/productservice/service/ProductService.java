package ecommerce.productservice.service;

import ecommerce.apicommon1.model.response.ApiResponse;
import ecommerce.apicommon1.model.response.ProductPriceResponse;
import ecommerce.productservice.dto.request.CreateProductRequest;
import ecommerce.productservice.dto.request.ProductSearchRequest;
import ecommerce.productservice.dto.request.ProductUpdateInfoRequest;
import ecommerce.productservice.dto.request.SearchRequest;
import ecommerce.productservice.dto.response.CreateProductResponse;
import ecommerce.productservice.dto.response.ProductResponse;
import ecommerce.productservice.dto.response.ProductViewResponse;
import ecommerce.productservice.dto.response.SearchProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public interface ProductService {
    CreateProductResponse createProduct(CreateProductRequest request, List<MultipartFile> imageUrls);

//    public ApiResponse<ProductResponse> updateProduct(Long id, ProductRequest request);

    ProductResponse updateProduct(Long id, ProductUpdateInfoRequest request, List<MultipartFile> imageUrls);

    Page<ProductResponse> getAllProduct(int page, int size);

    ProductResponse deleteProduct(Long id);

    ApiResponse<Page<ProductResponse>> searchProduct(ProductSearchRequest request, int page, int size);

    Page<SearchProductResponse> search(SearchRequest request, Pageable pageable);

    String getSkuCodeByProductId(Long productId);

    BigDecimal getPriceByProductId1(Long productId);

    ApiResponse<ProductResponse> getProductById(Long id);

    ProductViewResponse viewProduct(Long productId);

    Map<Long, Boolean> findProduct(List<Long> ids);

    ProductPriceResponse getPriceByProductId(Long id);

}