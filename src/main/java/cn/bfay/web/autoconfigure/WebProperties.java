package cn.bfay.web.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * WebProperties.
 *
 * @author wangjiannan
 * @since 2020/8/11
 */
@Data
@ConfigurationProperties(prefix = "bfay.web")
public class WebProperties {
    private String userSign = "userid";
    private String corsAllowedOrigin = "*";
    private String corsEnabled = "false";
}