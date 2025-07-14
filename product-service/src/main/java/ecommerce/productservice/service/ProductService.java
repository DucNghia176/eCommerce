package ecommerce.productservice.service;

import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.aipcommon.model.response.ProductResponse;
import ecommerce.productservice.dto.request.ProductRequest;
import ecommerce.productservice.dto.request.ProductSearchRequest;
import ecommerce.productservice.dto.request.ProductUpdateInfoRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface ProductService {
    public ApiResponse<ProductResponse> createProduct(ProductRequest request, List<MultipartFile> imageUrls);

//    public ApiResponse<ProductResponse> updateProduct(Long id, ProductRequest request);

    public ApiResponse<ProductResponse> updateProductInfo(Long id, ProductUpdateInfoRequest request);

    public ApiResponse<ProductResponse> updateProductImage(Long id, List<MultipartFile> imageUrls);

    public ApiResponse<List<ProductResponse>> getAllProduct();

    public ApiResponse<ProductResponse> deleteProduct(Long id);

    public ApiResponse<List<ProductResponse>> searchProduct(ProductSearchRequest request);

    public ApiResponse<ProductResponse> getProductByUserId(Long id);
}
