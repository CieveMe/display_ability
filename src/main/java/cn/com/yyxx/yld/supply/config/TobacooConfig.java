package cn.com.yyxx.yld.supply.config;


import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "tobacoo")
public class TobacooConfig {

    public  String http="192.168.50.162:8080";
    public  String socketip="22221";
    public  String port="8080";
    public  String url="/managex/send_socket_public.rhtml";
    public  Boolean enable=false;
}
