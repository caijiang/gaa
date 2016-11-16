package me.jiangcai.gaa.sdk;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author CJ
 */
@Configuration
@Import(GAASpringConfig.InnerConfig.class)
@ComponentScan(basePackages = {"me.jiangcai.gaa.sdk"})
public class GAASpringConfig {

    static class InnerConfig{
        @Bean
        public ObjectMapper clientObjectMapper(){
            return new ObjectMapper();
        }
    }

}
