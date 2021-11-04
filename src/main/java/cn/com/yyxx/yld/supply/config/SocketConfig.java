package cn.com.yyxx.yld.supply.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "socket")
public class SocketConfig {

    public  String http="netty-server";
    public  String socketip="22221";
    public  String port="8090";
    public  String url="/test2";
}
