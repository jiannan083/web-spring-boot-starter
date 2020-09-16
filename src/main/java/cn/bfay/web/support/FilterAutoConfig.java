package cn.bfay.web.support;

import javax.annotation.Resource;
import cn.bfay.web.autoconfigure.WebProperties;
import cn.bfay.web.filter.HttpServletRequestReplacedFilter;
import cn.bfay.web.filter.TraceLoggingFilter;
import cn.bfay.web.filter.XssAntiFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * FilterAutoConfig.
 *
 * @author wangjiannan
 * @since 2019/10/25
 */
@Slf4j
public class FilterAutoConfig {
    private final Integer order = Ordered.HIGHEST_PRECEDENCE + 6;

    @Resource
    private WebProperties webProperties;

    /**
     * 可读httprequest filter.
     *
     * @return {@link FilterRegistrationBean}
     */
    @Bean
    public FilterRegistrationBean<HttpServletRequestReplacedFilter>
    httpServletRequestReplacedFilterFilterRegistrationBean() {
        FilterRegistrationBean<HttpServletRequestReplacedFilter> filterRegBean = new FilterRegistrationBean<>();
        filterRegBean.setFilter(new HttpServletRequestReplacedFilter());
        filterRegBean.addUrlPatterns("/*");
        filterRegBean.setOrder(order + 3);
        return filterRegBean;
    }

    /**
     * trace日志filter.
     *
     * @return {@link FilterRegistrationBean}
     */
    @Bean
    public FilterRegistrationBean<TraceLoggingFilter> traceLogFilter() {
        FilterRegistrationBean<TraceLoggingFilter> filterRegBean = new FilterRegistrationBean<>();
        filterRegBean.setFilter(new TraceLoggingFilter(webProperties));
        filterRegBean.addUrlPatterns("/*");
        filterRegBean.setOrder(order + 2);
        return filterRegBean;
    }

    /**
     * xss 防御 filter.
     *
     * @return {@link FilterRegistrationBean}
     */
    @Bean
    public FilterRegistrationBean<XssAntiFilter> xssAntiFilterFilterRegistrationBean() {
        FilterRegistrationBean<XssAntiFilter> filterRegBean = new FilterRegistrationBean<>();
        filterRegBean.setFilter(new XssAntiFilter());
        filterRegBean.addUrlPatterns("/*");
        filterRegBean.setOrder(order + 1);
        return filterRegBean;
    }

    @Bean
    @ConditionalOnProperty(prefix = "szyx.web", value = "cors-enabled", havingValue = "true")
    public FilterRegistrationBean<CorsFilter> corsFilterRegistrationBean() {
        String corsAllowedOrigin = webProperties.getCorsAllowedOrigin();
        CorsConfiguration config = new CorsConfiguration();
        boolean value = StringUtils.isBlank(corsAllowedOrigin) || corsAllowedOrigin.equalsIgnoreCase("*");
        if (value) {
            config.addAllowedOrigin("*");
        } else {
            String[] origins = corsAllowedOrigin.split(",");
            for (String origin : origins) {
                config.addAllowedOrigin(origin);
            }
        }
        config.setAllowCredentials(false);
        config.addAllowedMethod("*");
        //config.addAllowedHeader("*");
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration("/**", config);

        FilterRegistrationBean<CorsFilter> filterRegBean = new FilterRegistrationBean<>();
        filterRegBean.setFilter(new CorsFilter(configSource));
        filterRegBean.setOrder(order);
        log.info(">>>Init cors and allowed origin \"{}\"", value ? "*" : corsAllowedOrigin);
        return filterRegBean;
    }
}
