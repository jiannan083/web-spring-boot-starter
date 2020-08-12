package cn.bfay.web;

import cn.bfay.web.exception.GlobalExceptionHandler;
import cn.bfay.web.support.ExecutorAutoConfig;
import cn.bfay.web.support.FeignAutoConfig;
import cn.bfay.web.support.FilterAutoConfig;
import cn.bfay.web.support.WebResponseBodyAdvice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * WebAutoConfiguration.
 *
 * @author wangjiannan
 * @since 2019/10/25
 */
@Slf4j
@Configuration
@Import(value = {WebResponseBodyAdvice.class, FilterAutoConfig.class,
        GlobalExceptionHandler.class, ExecutorAutoConfig.class, FeignAutoConfig.class})
public class WebAutoConfiguration {

}
