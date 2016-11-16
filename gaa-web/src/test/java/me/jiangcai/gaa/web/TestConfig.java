package me.jiangcai.gaa.web;

import me.jiangcai.gaa.web.config.MVCConfig;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

/**
 * @author CJ
 */
@Import({MVCConfig.class, DSConfig.class})
@ImportResource("classpath:/datasource_local.xml")
class TestConfig {
}
