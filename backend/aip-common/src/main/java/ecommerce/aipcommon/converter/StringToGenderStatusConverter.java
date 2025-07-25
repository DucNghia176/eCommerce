package ecommerce.aipcommon.converter;

import ecommerce.aipcommon.model.status.GenderStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToGenderStatusConverter implements Converter<String, GenderStatus> {
    @Override
    public GenderStatus convert(String source) {
        return GenderStatus.fromValue(source);
    }
}