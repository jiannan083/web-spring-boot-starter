package cn.bfay.web.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * HealthCheck.
 *
 * @author wangjiannan
 */
@Slf4j
public class HealthCheckFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws IOException {
        try (
            PrintWriter printWriter = response.getWriter()
        ) {
            printWriter.append("ok");
            printWriter.flush();
        }
    }

}
