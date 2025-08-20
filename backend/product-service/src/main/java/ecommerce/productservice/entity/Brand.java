package ecommerce.productservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "BRAND", schema = "PRODUCT")
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "brand_seq")
    @SequenceGenerator(name = "brand_seq", sequenceName = "SEQ_BRAND_ID", allocationSize = 1)
    @Column(name = "BRAND_ID")
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;
}
