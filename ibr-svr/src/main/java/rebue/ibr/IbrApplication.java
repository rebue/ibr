package rebue.ibr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

// 这个注解是为了使该包内的过滤器生效。
@ServletComponentScan("rebue")
@SpringCloudApplication
@EnableFeignClients(basePackages = { "rebue.ord.svr.feign", "rebue.afc.svr.feign" })
public class IbrApplication {
    public static void main(final String[] args) {
        SpringApplication.run(IbrApplication.class, args);
    }

}