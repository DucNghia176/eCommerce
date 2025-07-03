package ecommerce.productservice.controller;

import ecommerce.productservice.dto.request.ProductRequest;
import ecommerce.productservice.dto.response.ProductResponse;
import ecommerce.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping("/create")
    public ProductResponse createProduct(@RequestBody ProductRequest request) {
        return productService.createProduct(request);
    }
}
