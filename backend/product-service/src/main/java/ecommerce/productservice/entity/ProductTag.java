package ecommerce.productservice.entity;


import ecommerce.productservice.entity.imp.ProductTagId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "PRODUCT_TAG", schema = "PRODUCT")
@IdClass(ProductTagId.class)
public class ProductTag {

    @Id
    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    @Id
    @ManyToOne
    @JoinColumn(name = "TAG_ID")
    private Tag tag;

    // Getters & Setters
}
