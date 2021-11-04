package cn.com.yyxx.yld.supply.data.qo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author hz
 * @version 1.0
 * @since 2019/1/23 11:10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WxMpTemplateMessageQo implements Serializable {

    private static final long serialVersionUID = -6400797104710220922L;

    /**
     * 接收者openid.
     */
    private String toUser;

    /**
     * 模板ID.
     */
    private String templateId;

    /**
     * 模板跳转链接.
     * <pre>
     * url和miniprogram都是非必填字段，若都不传则模板无跳转；若都传，会优先跳转至小程序。
     * 开发者可根据实际需要选择其中一种跳转方式即可。当用户的微信客户端版本不支持跳小程序时，将会跳转至url。
     * </pre>
     */
    private String url;

    /**
     * 模板数据.
     */
    private Map<String,String> data = new HashMap<>();

}
