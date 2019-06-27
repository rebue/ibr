package rebue.ibr.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringCloudApplication
@EnableScheduling
public class IbrSchedulerApplication {
    public static void main(String[] args) {
        SpringApplication.run(IbrSchedulerApplication.class, args);
    }
}
