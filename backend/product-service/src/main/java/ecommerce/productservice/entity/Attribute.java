package ecommerce.productservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity

@Table(name = "ATTRIBUTE", schema = "product")
public class Attribute {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attribute_seq")
    @SequenceGenerator(name = "attribute_seq", sequenceName = "SEQ_ATTRIBUTE_ID", allocationSize = 1)
    @Column(name = "ATTRIBUTE_ID")
    private Long attributeId;

    @Column(name = "NAME")
    private String name;
}
