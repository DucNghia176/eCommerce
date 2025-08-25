package ecommerce.productservice.controller;

import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.productservice.dto.request.ProductRequest;
import ecommerce.productservice.dto.request.ProductSearchRequest;
import ecommerce.productservice.dto.request.ProductUpdateInfoRequest;
import ecommerce.productservice.dto.response.ProductResponse;
import ecommerce.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping("/create")
    public ApiResponse<ProductResponse> createProduct(@RequestPart("data") ProductRequest request,
                                                      @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        return productService.createProduct(request, images);
    }

//    @PutMapping("update/{id}")
//    public ApiResponse<ProductResponse> updateProduct(@PathVariable Long id,
//                                                      @RequestBody ProductRequest request) {
//        return productService.updateProduct(id, request);
//    }

    @PutMapping("update/{id}")
    public ApiResponse<ProductResponse> updateInfoProduct(
            @PathVariable Long id,
            @RequestPart("data") ProductUpdateInfoRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        return productService.updateProduct(id, request, images);
    }

    @DeleteMapping("delete/{id}")
    public ApiResponse<ProductResponse> deleteProduct(@PathVariable Long id) {
        return productService.deleteProduct(id);
    }

    @GetMapping
    public ApiResponse<Page<ProductResponse>> getAllProduct(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return productService.getAllProduct(page, size);
    }

    @PostMapping("search")
    public ApiResponse<Page<ProductResponse>> searchProduct(
            @RequestBody ProductSearchRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return productService.searchProduct(request, page, size);
    }


    @GetMapping("/{id}/skuCode")
    public String getSkuCode(@PathVariable Long id) {
        return productService.getSkuCodeByProductId(id);
    }

    @GetMapping("/{id}/price")
    public BigDecimal getPrice(@PathVariable Long id) {
        return productService.getPriceByProductId(id);
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }
}
