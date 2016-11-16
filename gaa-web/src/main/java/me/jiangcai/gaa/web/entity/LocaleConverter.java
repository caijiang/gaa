package me.jiangcai.gaa.web.entity;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Locale;

/**
 * @author CJ
 */
@Converter(autoApply = true)
public class LocaleConverter implements AttributeConverter<Locale, String> {

    @Override
    public String convertToDatabaseColumn(Locale attribute) {
        if (attribute == null)
            return null;
        return attribute.toLanguageTag();
    }

    @Override
    public Locale convertToEntityAttribute(String dbData) {
        if (dbData == null)
            return null;
        return Locale.forLanguageTag(dbData);
    }
}
