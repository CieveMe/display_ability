package cn.com.yyxx.yld.supply.data.vo;

import lombok.Data;


@Data
public class ChannelUser {

    private String name;/*mongodb中查询条件*/
    private String pass;
    private String type;
    private String begintime;
    private String lasttime;

    public ChannelUser() {
    }

    public ChannelUser(String name) {
        this.name = name;
    }


}
