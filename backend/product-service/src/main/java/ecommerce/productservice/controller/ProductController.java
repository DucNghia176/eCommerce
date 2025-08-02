package ecommerce.productservice.controller;

import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.aipcommon.model.response.ProductResponse;
import ecommerce.productservice.dto.request.ProductRequest;
import ecommerce.productservice.dto.request.ProductSearchRequest;
import ecommerce.productservice.dto.request.ProductUpdateInfoRequest;
import ecommerce.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping("/create")
    public ApiResponse<ProductResponse> createProduct(@RequestPart("data") @ModelAttribute ProductRequest request,
                                                      @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        return productService.createProduct(request, images);
    }

//    @PutMapping("update/{id}")
//    public ApiResponse<ProductResponse> updateProduct(@PathVariable Long id,
//                                                      @RequestBody ProductRequest request) {
//        return productService.updateProduct(id, request);
//    }

    @PutMapping("update/info/{id}")
    public ApiResponse<ProductResponse> updateInfoProduct(@PathVariable Long id,
                                                          @RequestBody ProductUpdateInfoRequest request) {
        return productService.updateProductInfo(id, request);
    }

    @PutMapping("update/image/{id}")
    public ApiResponse<ProductResponse> updateProductImage(@PathVariable Long id,
                                                           @RequestPart("images") List<MultipartFile> images) {
        return productService.updateProductImage(id, images);
    }

    @DeleteMapping("delete/{id}")
    public ApiResponse<ProductResponse> deleteProduct(@PathVariable Long id) {
        return productService.deleteProduct(id);
    }

    @GetMapping
    public ApiResponse<List<ProductResponse>> getAllProduct() {
        return productService.getAllProduct();
    }

    @PostMapping("search")
    public ApiResponse<List<ProductResponse>> searchProduct(@RequestBody ProductSearchRequest request) {
        return productService.searchProduct(request);
    }

    @GetMapping("/{id}/skuCode")
    public String getSkuCode(@PathVariable Long id) {
        return productService.getSkuCodeByProductId(id);
    }

    @GetMapping("/{id}/price")
    public BigDecimal getPrice(@PathVariable Long id) {
        return productService.getPriceByProductId(id);
    }
}
