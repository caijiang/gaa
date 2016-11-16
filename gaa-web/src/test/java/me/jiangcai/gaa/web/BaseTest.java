package me.jiangcai.gaa.web;

import me.jiangcai.gaa.web.boot.DispatcherServletInitializer;
import me.jiangcai.lib.test.SpringWebTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;

/**
 * @author CJ
 */
@WebAppConfiguration
@ContextConfiguration(classes = {TestConfig.class})
public abstract class BaseTest extends SpringWebTest {

    @Override
    protected DefaultMockMvcBuilder buildMockMVC(DefaultMockMvcBuilder builder) {
        return builder.addFilters(DispatcherServletInitializer.shareFilters());
    }
}
