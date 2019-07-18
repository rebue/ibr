package rebue.ibr.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringCloudApplication
@EnableScheduling
@EnableFeignClients(basePackages = { "rebue.ibr.svr.feign" })
public class IbrSchedulerApplication {
    public static void main(String[] args) {
        SpringApplication.run(IbrSchedulerApplication.class, args);
    }
}
