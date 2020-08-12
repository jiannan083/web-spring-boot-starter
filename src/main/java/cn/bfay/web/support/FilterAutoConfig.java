package cn.bfay.web.support;

import cn.bfay.web.filter.HttpServletRequestReplacedFilter;
import cn.bfay.web.filter.TraceLoggingFilter;
import cn.bfay.web.filter.XssAntiFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
@Configuration
public class FilterAutoConfig {
    private Integer order = Ordered.HIGHEST_PRECEDENCE + 6;

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
        filterRegBean.setFilter(new TraceLoggingFilter());
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

    @Value("${cors.allowed-origin:all}")
    private String corsAllowedOrigin;

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistrationBean() {
        String allowedOrigins = "*";
        if (!corsAllowedOrigin.equalsIgnoreCase("all")) {
            allowedOrigins = corsAllowedOrigin;
        }

        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin(allowedOrigins);
        config.setAllowCredentials(false);
        config.addAllowedMethod("*");
        //config.addAllowedHeader("*");
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration("/**", config);

        FilterRegistrationBean<CorsFilter> filterRegBean = new FilterRegistrationBean<>();
        filterRegBean.setFilter(new CorsFilter(configSource));
        filterRegBean.setOrder(order);
        return filterRegBean;
    }
}
