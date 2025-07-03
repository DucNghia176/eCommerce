package ecommerce.productservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "TAG", schema = "PRODUCT")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tag_seq")
    @SequenceGenerator(name = "tag_seq", sequenceName = "SEQ_TAG_ID", allocationSize = 1)
    @Column(name = "TAG_ID")
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    // Getters & Setters
}
