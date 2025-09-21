package ecommerce.productservice.service;

import ecommerce.apicommon1.model.response.ApiResponse;
import ecommerce.productservice.dto.response.BrandResponse;
import ecommerce.productservice.dto.response.TagResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TagBrandService {
    ApiResponse<List<TagResponse>> getAllTags();

    ApiResponse<List<BrandResponse>> getAllBrands();
}
