package cn.bfay.web.support;

import cn.bfay.web.interceptor.OkhttpLoggingInterceptor;
import feign.Client;
import feign.okhttp.OkHttpClient;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.cloud.openfeign.ribbon.CachingSpringLoadBalancerFactory;
import org.springframework.cloud.openfeign.ribbon.LoadBalancerFeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * feign 配置.
 *
 * @author wangjiannan
 */
@Configuration
public class FeignAutoConfig {

    /**
     * okhttp feignclient loadbalance.
     *
     * @param cachingFactory cachingFactory
     * @param clientFactory  clientFactory
     * @param okHttpClient   okHttpClient
     * @return {@link Client}
     */
    @Bean
    public Client feignClient(CachingSpringLoadBalancerFactory cachingFactory,
                              SpringClientFactory clientFactory, okhttp3.OkHttpClient okHttpClient) throws Exception {
        final X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };

        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, new TrustManager[] {trustManager}, new SecureRandom());
        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        OkHttpClient delegate = new OkHttpClient(okHttpClient.newBuilder()
            .hostnameVerifier((hostname, session) -> true)
            .sslSocketFactory(sslSocketFactory, trustManager)
            .addInterceptor(new OkhttpLoggingInterceptor())
            .build());
        return new LoadBalancerFeignClient(delegate, cachingFactory, clientFactory);
    }
}
