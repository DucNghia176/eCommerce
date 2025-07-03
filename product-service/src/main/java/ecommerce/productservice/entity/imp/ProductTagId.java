package ecommerce.productservice.entity.imp;

import java.io.Serializable;
import java.util.Objects;

public class ProductTagId implements Serializable {
    private Long product;
    private Long tag;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductTagId)) return false;
        ProductTagId that = (ProductTagId) o;
        return Objects.equals(product, that.product) && Objects.equals(tag, that.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, tag);
    }
}
