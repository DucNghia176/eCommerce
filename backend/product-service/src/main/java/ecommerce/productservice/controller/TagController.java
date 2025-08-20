package ecommerce.productservice.controller;

import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.productservice.dto.response.TagResponse;
import ecommerce.productservice.service.TagBrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/tag")
@RequiredArgsConstructor
public class TagController {
    private final TagBrandService tagBrandService;

    @GetMapping
    public ApiResponse<List<TagResponse>> getAllTags() {
        return tagBrandService.getAllTags();
    }
}
