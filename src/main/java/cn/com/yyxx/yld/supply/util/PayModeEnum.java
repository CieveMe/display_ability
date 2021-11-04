package cn.com.yyxx.yld.supply.util;

public enum PayModeEnum {
    /**
     * payMode
     */
    WECHAT("SSY_WECHAT", "微信钱包", 2),
    SUPER_VIP("SPM_SUPER_VIP", "超级会员钱包", 4),
    ALIPAY("SSY_ALIPAY", "支付宝钱包", 5),
//    BAIDUPAY("SSY_BAIDUPAY","百度钱包"),
//    UNIONPAY_WALLET("SSY_UNIONPAY_WALLET","银联钱包"),
//    UNIONPAY("SSY_UNIONPAY", "银联二维码"),
//    JDPAY("SSY_JDPAY", "京东钱包"),
    ;

    PayModeEnum(String code, String name, Integer value) {
        this.code = code;
        this.name = name;
        this.value = value;
    }

    private final String code;
    private final String name;
    private final Integer value;

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public Integer getValue() {
        return value;
    }

    public static PayModeEnum getByCode(String code) {
        PayModeEnum[] values = PayModeEnum.values();
        for (PayModeEnum p : values) {
            if (p.getCode().equals(code)) {
                return p;
            }
        }
        return null;
    }
}
