package cn.com.yyxx.yld.supply.staticMap;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author hz
 * @version 1.0
 * @date 2020/4/28 11:26
 * @since 0.11.0
 */
@Getter
public enum PayStatusCode {
    /**
     * 交易状态
     */
    SUCCESS("交易成功", "0001"),
    FAIL("交易失败", "0002"),
    NEED_PASSWORD("本单需要支付密码", "0003"),
    BACK_OUT("交易撤销", "0004")
    ;


    private String code;
    private String name;

    PayStatusCode(String name, String code){
        this.name = name;
        this.code = code;
    }

    public static PayStatusCode queryByCode(String code){
        PayStatusCode[] values = PayStatusCode.values();
        Map<String, PayStatusCode> map = new HashMap<>();
        for(PayStatusCode rc : values){
            map.put(rc.getCode(), rc);
        }
        return map.get(code);
    }
}
