package ecommerce.productservice.dto.request;

import lombok.Data;

@Data
public class AttributeRequest {
    private String attributeName;
    private String attributeValueName;
}
