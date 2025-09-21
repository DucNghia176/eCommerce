package ecommerce.productservice.service.impl;

import ecommerce.apicommon1.model.response.ApiResponse;
import ecommerce.productservice.dto.response.BrandResponse;
import ecommerce.productservice.dto.response.TagResponse;
import ecommerce.productservice.entity.Brand;
import ecommerce.productservice.entity.Tag;
import ecommerce.productservice.repository.BrandRepository;
import ecommerce.productservice.repository.TagRepository;
import ecommerce.productservice.service.TagBrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagBrandServiceImp implements TagBrandService {
    private final TagRepository tagRepository;
    private final BrandRepository brandRepository;

    @Override
    public ApiResponse<List<TagResponse>> getAllTags() {
        try {
            List<Tag> tags = tagRepository.findAll();

            List<TagResponse> responses = tags.stream()
                    .map(tag -> new TagResponse(tag.getId(), tag.getName()))
                    .toList();
            return ApiResponse.<List<TagResponse>>builder()
                    .code(200)
                    .message("Lấy danh sách tag thành công")
                    .data(responses)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<List<TagResponse>>builder()
                    .code(500)
                    .message("Lỗi hệ thống: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<List<BrandResponse>> getAllBrands() {
        try {
            List<Brand> brands = brandRepository.findAll();

            List<BrandResponse> responses = brands.stream()
                    .map(brand -> new BrandResponse(brand.getId(), brand.getName()))
                    .toList();

            return ApiResponse.<List<BrandResponse>>builder()
                    .code(200)
                    .message("Lấy danh sách tag thành công")
                    .data(responses)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<List<BrandResponse>>builder()
                    .code(500)
                    .message("Lỗi hệ thống: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }
}
