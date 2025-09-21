package ecommerce.productservice.controller;

import ecommerce.apicommon1.model.response.ApiResponse;
import ecommerce.productservice.dto.response.BrandResponse;
import ecommerce.productservice.service.TagBrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/brand")
@RequiredArgsConstructor
public class BrandController {
    private final TagBrandService tagBrandService;

    @GetMapping
    public ApiResponse<List<BrandResponse>> getAllBrands() {
        return tagBrandService.getAllBrands();
    }
}