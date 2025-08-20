package ecommerce.productservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "PRODUCT", schema = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq")
    @SequenceGenerator(name = "product_seq", sequenceName = "SEQ_PRODUCT_ID", allocationSize = 1)
    @Column(name = "PRODUCT_ID")
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "SKU_CODE", nullable = false)
    private String skuCode;
    @Lob
    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "PRICE", nullable = false)
    private BigDecimal price;

    @Column(name = "DISCOUNT_PRICE")
    private BigDecimal discountPrice;

    @ManyToOne
    @JoinColumn(name = "CATEGORY_ID", nullable = false)
    private Category category;

    @Column(name = "BRAND_ID")
    private Long brandId;

    @Column(name = "UNIT")
    private String unit;

    @Column(name = "IS_ACTIVE")
    private Integer isActive;

    @CreationTimestamp
    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UPDATED_AT", updatable = false)
    private LocalDateTime updateAt;
}

