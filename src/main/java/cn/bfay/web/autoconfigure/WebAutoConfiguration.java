package cn.bfay.web.autoconfigure;

import cn.bfay.web.support.FilterAutoConfig;
import cn.bfay.web.support.GlobalExceptionHandler;
import cn.bfay.web.support.WebResponseBodyAdvice;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * WebAutoConfiguration.
 *
 * @author wangjiannan
 * @since 2019/10/25
 */
@Configuration
@EnableConfigurationProperties(WebProperties.class)
@Import(value = {WebResponseBodyAdvice.class, FilterAutoConfig.class, GlobalExceptionHandler.class})
public class WebAutoConfiguration {

}
