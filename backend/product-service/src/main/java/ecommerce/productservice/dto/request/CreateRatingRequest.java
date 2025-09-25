package ecommerce.productservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateRatingRequest {
    @NotNull(message = "Không được để trống mã sản phẩm")
    private Long productId;
    
    @NotNull(message = "Đánh giá không được trống")
    @Range(min = 1, max = 5, message = "Đánh giá phải nằm trong khoảng 1-5")
    private Integer score;
    private String comment;
}
