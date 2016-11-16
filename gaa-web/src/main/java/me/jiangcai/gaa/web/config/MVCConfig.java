package me.jiangcai.gaa.web.config;

import me.jiangcai.gaa.web.entity.LocaleConverter;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.Locale;

/**
 * @author CJ
 * @see RepositoryRestConfigurerAdapter
 */
@EnableWebMvc
@Import({CoreConfig.class, RestConfig.class})
public class MVCConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        super.addFormatters(registry);
        registry.addConverter(String.class, Locale.class, new Converter<String, Locale>() {
            LocaleConverter converter = new LocaleConverter();

            @Override
            public Locale convert(String source) {
                return converter.convertToEntityAttribute(source);
            }
        });
    }
}
