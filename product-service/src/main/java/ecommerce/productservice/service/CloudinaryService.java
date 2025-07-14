package ecommerce.productservice.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public String uploadFile(MultipartFile file, Long id) {
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", "product/" + id + "/"
            ));
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Không thể upload ảnh lên Cloudinary", e);
        }
    }

    public String extractPublicId(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) return null;

        try {
            int uploadIndex = imageUrl.indexOf("/upload/");
            if (uploadIndex == -1) return null;

            String pathAfterUpload = imageUrl.substring(uploadIndex + "/upload/".length());

            // Xoá đuôi mở rộng nếu có (vd: .jpg, .png)
            int dotIndex = pathAfterUpload.lastIndexOf('.');
            if (dotIndex != -1) {
                pathAfterUpload = pathAfterUpload.substring(0, dotIndex);
            }

            return pathAfterUpload; // chính là publicId đầy đủ: ví dụ "user/avatar1" hoặc "admin/avatar2"
        } catch (Exception e) {
            return null;
        }
    }


    public void deleteFile(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new RuntimeException("Không thể xoá ảnh trên Cloudinary", e);
        }
    }

    public void deleteFolderByProductId(Long productId) {
        try {
            String folderPath = "product/" + productId;
            cloudinary.api().deleteResourcesByPrefix(folderPath, ObjectUtils.emptyMap());
        } catch (Exception e) {
            throw new RuntimeException("Không thể xoá folder ảnh trên Cloudinary", e);
        }
    }
}
