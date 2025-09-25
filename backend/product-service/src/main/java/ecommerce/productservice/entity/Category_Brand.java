package ecommerce.productservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "CATEGORY_BRAND", schema = "PRODUCT")
public class Category_Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_brand_seq")
    @SequenceGenerator(name = "category_brand_seq", sequenceName = "SEQ_CATEGORY_BRAND_ID", allocationSize = 1)
    @Column(name = "CATEGORY_BRAND_ID")
    private Long id;

    @Column(name = "CATEGORY_ID")
    private Long categoryId;

    @Column(name = "BRAND_ID")
    private Long brandId;
}
