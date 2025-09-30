package ecommerce.productservice.controller;

import ecommerce.apicommon1.model.response.ApiResponse;
import ecommerce.apicommon1.model.response.ProductPriceResponse;
import ecommerce.productservice.dto.request.CreateProductRequest;
import ecommerce.productservice.dto.request.ProductSearchRequest;
import ecommerce.productservice.dto.request.ProductUpdateInfoRequest;
import ecommerce.productservice.dto.request.SearchRequest;
import ecommerce.productservice.dto.response.ProductResponse;
import ecommerce.productservice.dto.response.ProductViewResponse;
import ecommerce.productservice.dto.response.SearchProductResponse;
import ecommerce.productservice.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/create")
    public ApiResponse<ProductResponse> createProduct(@Valid @RequestPart("data") CreateProductRequest request,
                                                      @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        ProductResponse productResponse = productService.createProduct(request, images);
        return ApiResponse.<ProductResponse>builder()
                .code(200)
                .message("Tạo product thành công")
                .data(productResponse)
                .build();
    }

//    @PutMapping("update/{id}")
//    public ApiResponse<ProductResponse> updateProduct(@PathVariable Long id,
//                                                      @RequestBody ProductRequest request) {
//        return productService.updateProduct(id, request);
//    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("update/{id}")
    public ApiResponse<ProductResponse> updateInfoProduct(
            @PathVariable Long id,
            @RequestPart("data") ProductUpdateInfoRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        ProductResponse productResponse = productService.updateProduct(id, request, images);

        return ApiResponse.<ProductResponse>builder()
                .code(200)
                .message("Cập nhật product thành công")
                .data(productResponse)
                .build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("delete/{id}")
    public ApiResponse<ProductResponse> deleteProduct(@PathVariable Long id) {
        ProductResponse productResponse = productService.deleteProduct(id);
        return ApiResponse.<ProductResponse>builder()
                .code(200)
                .message("Xóa product thành công")
                .data(productResponse)
                .build();
    }

    @GetMapping
    public ApiResponse<Page<ProductResponse>> getAllProduct(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ProductResponse> productResponses = productService.getAllProduct(page, size);
        return ApiResponse.<Page<ProductResponse>>builder()
                .code(200)
                .message("Lấy product thành công")
                .data(productResponses)
                .build();
    }

    @PostMapping("search")
    public ApiResponse<Page<ProductResponse>> searchProduct(
            @RequestBody ProductSearchRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return productService.searchProduct(request, page, size);
    }

    @GetMapping("search")
    public ApiResponse<Page<SearchProductResponse>> search(@RequestParam(required = false) String keyword,
                                                           @RequestParam(required = false) Long category,
                                                           @RequestParam(required = false) Long brand,
                                                           @RequestParam(required = false) BigDecimal priceFrom,
                                                           @RequestParam(required = false) BigDecimal priceTo,
                                                           @RequestParam(required = false) Double ratingFrom,
                                                           Pageable pageable) {
        SearchRequest request = new SearchRequest();
        request.setKeyword(keyword);
        request.setCategoryId(category);
        request.setBrandId(brand);
        request.setPriceFrom(priceFrom);
        request.setPriceTo(priceTo);
        request.setRatingFrom(ratingFrom);
        Page<SearchProductResponse> responses = productService.search(request, pageable);
        return ApiResponse.<Page<SearchProductResponse>>builder()
                .code(200)
                .message("Tìm kiếm thành công")
                .data(responses)
                .build();
    }


    @GetMapping("/{id}/skuCode")
    public String getSkuCode(@PathVariable Long id) {
        return productService.getSkuCodeByProductId(id);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{id}/price")
    public BigDecimal getPrice(@PathVariable Long id) {
        return productService.getPriceByProductId1(id);
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @GetMapping("/view/{id}")
    public ApiResponse<ProductViewResponse> getAllProducts(@PathVariable Long id) {
        ProductViewResponse productViewResponse = productService.viewProduct(id);
        return ApiResponse.<ProductViewResponse>builder()
                .code(200)
                .message("Hiển thị thành công")
                .data(productViewResponse)
                .build();
    }

    @PostMapping("exists")
    public Map<Long, Boolean> checkProduct(@RequestBody List<Long> ids) {
        return productService.findProduct(ids);
    }

    @GetMapping("/price/{id}")
    public ProductPriceResponse productPrice(@PathVariable Long id) {
        return productService.getPriceByProductId(id);
    }
}
