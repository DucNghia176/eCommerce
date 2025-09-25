package ecommerce.productservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "RATING", schema = "PRODUCT")
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rating_seq")
    @SequenceGenerator(name = "rating_seq", sequenceName = "SEQ_RATING_ID", allocationSize = 1)
    @Column(name = "RATING_ID", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "PRODUCT_ID", nullable = false)
    private Long productId;

    @Column(name = "USER_ID")
    private Long userId;

    @NotNull
    @Column(name = "SCORE")
    private Integer score;

    @Size(max = 500)
    @Column(name = "COMMENTS", length = 500)
    private String comments;

    @CreationTimestamp
    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;
}
