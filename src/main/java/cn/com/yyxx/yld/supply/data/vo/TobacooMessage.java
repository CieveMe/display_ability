package cn.com.yyxx.yld.supply.data.vo;

import lombok.Data;

/**
 * <p></p>
 *
 * @author haoch
 * @version 1.0.0
 * @program yld-tobacco-app
 * @description
 * @date 2021-07-17 16:24
 * @since 0.1.0
 **/
@Data
public class TobacooMessage {

    /**
     * ip地址
     */
    private String ip;

    /**
     * 消息类型 (可定义为枚举  进行扩展)
     */
    private String type;

    /**
     * 是否群发
     */
    private Boolean group;

    /**
     * 用户名(电话号码)
     */
    private String username;

    /**
     * 消息标题
     */
    private String title;

    /**
     * 消息体
     */
    private String message;

    /**
     * 用户id
     */
    private String userid;



}
