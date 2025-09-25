package ecommerce.productservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PRODUCT", schema = "product")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq")
    @SequenceGenerator(name = "product_seq", sequenceName = "SEQ_PRODUCT_ID", allocationSize = 1)
    @Column(name = "PRODUCT_ID")
    @EqualsAndHashCode.Include
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

    @Column(name = "DISCOUNT")
    private BigDecimal discount;

    @ManyToOne
    @JoinColumn(name = "CATEGORY_ID", nullable = false)
    @JsonIgnore
    private Category category;

    @Column(name = "BRAND_ID")
    private Long brandId;

    @Column(name = "CATEGORY_BRAND_ID")
    private Long categoryBrandId;

    @Column(name = "UNIT")
    private String unit;

    @Column(name = "IS_ACTIVE")
    private Integer isActive;

    @Column(name = "AVERAGE_RATING")
    private Double averageRating;

    @Column(name = "TOTAL_REVIEWS")
    private Long totalReviews;

    @CreationTimestamp
    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UPDATED_AT", updatable = false)
    private LocalDateTime updateAt;

    @ManyToMany
    @JoinTable(
            name = "PRODUCT_TAG",
            joinColumns = @JoinColumn(name = "PRODUCT_ID"),
            inverseJoinColumns = @JoinColumn(name = "TAG_ID")
    )
    @JsonIgnore
    private Set<Tag> tags = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private Set<ProductAttribute> productAttributes = new HashSet<>();
}

