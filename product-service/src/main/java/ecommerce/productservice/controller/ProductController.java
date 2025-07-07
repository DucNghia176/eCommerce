package ecommerce.productservice.controller;

import ecommerce.apicommon.model.response.ApiResponse;
import ecommerce.apicommon.model.response.ProductResponse;
import ecommerce.productservice.dto.request.ProductRequest;
import ecommerce.productservice.dto.request.ProductSearchRequest;
import ecommerce.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping("/create")
    public ApiResponse<ProductResponse> createProduct(@RequestBody ProductRequest request) {
        return productService.createProduct(request);
    }

    @PutMapping("update/{id}")
    public ApiResponse<ProductResponse> updateProduct(@PathVariable Long id, @RequestBody ProductRequest request) {
        return productService.updateProduct(id, request);
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
}
