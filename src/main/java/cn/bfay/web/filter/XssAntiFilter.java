package cn.bfay.web.filter;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * 防御Xss攻击.
 *
 * @author wangjiannan
 */
@Slf4j
public class XssAntiFilter implements Filter {

    /**
     * 是否过滤富文本内容.
     */
    private static boolean IS_INCLUDE_RICH_TEXT = true;

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain filterChain) throws IOException, ServletException {
        //log.info("xss filter is open function!");
        XssHttpServletRequestWrapper xssRequest =
            new XssHttpServletRequestWrapper(
                (HttpServletRequest) request,
                IS_INCLUDE_RICH_TEXT);
        filterChain.doFilter(xssRequest, response);
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }
}
