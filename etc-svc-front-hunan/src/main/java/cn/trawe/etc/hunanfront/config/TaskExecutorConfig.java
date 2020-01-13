//package cn.trawe.etc.hunanfront.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.task.TaskExecutor;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//
//import java.util.concurrent.ThreadPoolExecutor;
//
///**
// * @author Jiang Guangxing
// */
//@Configuration
//@EnableAsync
//public class TaskExecutorConfig {
//    @Bean
//    public TaskExecutor taskExecutor() {
//        int coreSize = Runtime.getRuntime().availableProcessors() * 2;
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        // 设置核心线程数
//        executor.setCorePoolSize(coreSize);
//        // 设置最大线程数
//        executor.setMaxPoolSize(coreSize);
//        // 设置队列容量
//        executor.setQueueCapacity(10000);
//        // 设置默认线程名称
//        executor.setThreadNamePrefix("apply-taskExecutor-");
//        // 设置拒绝策略
//        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
//        // 等待所有任务结束后再关闭线程池
//        executor.setWaitForTasksToCompleteOnShutdown(true);
//        // 等待所有任务结束后再关闭线程池，等待最长时间
//        executor.setAwaitTerminationSeconds(60 * 3);
//        executor.initialize();
//        return executor;
//    }
//}
