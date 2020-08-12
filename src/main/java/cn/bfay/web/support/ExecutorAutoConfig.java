package cn.bfay.web.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池.
 *
 * @author wangjiannan
 */
@Slf4j
@Configuration
public class ExecutorAutoConfig {

    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 线程池维护线程的最少数量
        executor.setCorePoolSize(10);
        // 线程池维护线程的最大数量
        executor.setMaxPoolSize(20);
        // 缓存队列
        executor.setQueueCapacity(50);
        // 允许的空闲时间
        executor.setKeepAliveSeconds(300);
        //executor.setThreadNamePrefix("Pool-A");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略是调用任务的线程执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
