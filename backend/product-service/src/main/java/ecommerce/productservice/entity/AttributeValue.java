package ecommerce.productservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class AttributeValue {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "value_seq")
    @SequenceGenerator(name = "value_seq", sequenceName = "SEQ_VALUE_ID", allocationSize = 1)
    @Column(name = "VALUE_ID", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "ATTRIBUTE_ID")
    private Attribute attribute;

    @Column(name = "VALUE")
    private String value;

}
