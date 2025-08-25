package ecommerce.productservice.service;

import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.productservice.dto.request.ProductRequest;
import ecommerce.productservice.dto.request.ProductSearchRequest;
import ecommerce.productservice.dto.request.ProductUpdateInfoRequest;
import ecommerce.productservice.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Service
public interface ProductService {
    ApiResponse<ProductResponse> createProduct(ProductRequest request, List<MultipartFile> imageUrls);

//    public ApiResponse<ProductResponse> updateProduct(Long id, ProductRequest request);

    ApiResponse<ProductResponse> updateProduct(Long id, ProductUpdateInfoRequest request, List<MultipartFile> imageUrls);

    ApiResponse<Page<ProductResponse>> getAllProduct(int page, int size);

    ApiResponse<ProductResponse> deleteProduct(Long id);

    ApiResponse<Page<ProductResponse>> searchProduct(ProductSearchRequest request, int page, int size);

    String getSkuCodeByProductId(Long productId);

    BigDecimal getPriceByProductId(Long productId);

    ApiResponse<ProductResponse> getProductById(Long id);
}
