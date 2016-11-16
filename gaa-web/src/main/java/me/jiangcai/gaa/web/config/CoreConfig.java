package me.jiangcai.gaa.web.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * 唯一入口
 *
 * @author CJ
 */
@Configuration
@Import({CommonConfig.class, DataSupportConfig.class})
@ComponentScan("me.jiangcai.gaa.web.service")
@EnableJpaRepositories("me.jiangcai.gaa.web.repository")
class CoreConfig {

}
