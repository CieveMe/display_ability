package cn.com.yyxx.yld.supply;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringCloudApplication
@ComponentScan(value = {"cn.com.yyxx"})
@EnableFeignClients(value = {"cn.com.yyxx.yld"})
@EnableAsync
public class SupplyApplication {

    public static void main(String[] args) {
        SpringApplication.run(SupplyApplication.class, args);
    }

}
