package cn.bfay.web;

import cn.bfay.web.exception.GlobalExceptionHandler;
import cn.bfay.web.support.FilterAutoConfig;
import cn.bfay.web.support.WebResponseBodyAdvice;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * WebAutoConfiguration.
 *
 * @author wangjiannan
 * @since 2019/10/25
 */
@Configuration
@Import(value = {WebResponseBodyAdvice.class, FilterAutoConfig.class, GlobalExceptionHandler.class})
public class WebAutoConfiguration {

}
