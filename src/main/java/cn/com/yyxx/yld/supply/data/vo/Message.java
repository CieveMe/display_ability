package cn.com.yyxx.yld.supply.data.vo;

import lombok.Data;

@Data
public class Message {

    private String code;/*SENDCLIENT("0x0003", "从服务器发送至客户端"),*/
    private String cmd;/*红包*/
    private ChannelUser send;/*发送者对象*/
    private ChannelUser receive;/*接受者对象*/

    private String body;/**/
    private String sendtime;/*发送时间 时间戳*/
    private String expires;/*过期时间*/

}
