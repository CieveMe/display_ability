package cn.com.yyxx.yld.supply.data.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;


@Data
public class UserWithPermissionVO {
    private static final long serialVersionUID = -8644728723054459805L;
    /**
     * 用户ID
     */
    private Long id;
    /**
     * 用户名
     */
    private String username;
    /**
     * 用户密码
     */
    private String password;
    /**
     * 账号是否锁定
     */
    private Boolean accountNonLocked;
    /**
     * 账号是否启用
     */
    private Boolean enabled;
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 真实姓名
     */
    private String realName;
    /**
     * 头像
     */
    private String headPortrait;

    /**
     * 是否店主
     */
    private Boolean owner;

    /**
     * 员工号
     */
    private Integer jobNo;

    /**
     * 门店ID
     */
    private Integer storeId;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 门店省份ID
     */
    private Integer sbiProvinceId;

    /**
     * 门店编号
     */
    private String storeNo;

    /**
     * 门店城市ID
     */
    private Integer sbiCityId;

    /**
     * 门店区县ID
     */
    private Integer sbiAreaId;

    /**
     * 门店类别
     */
    private String sbiCategory;

    /**
     * 门店类型
     */
    private String sbiChildType;

    /**
     * 客户端ID
     */
    private Integer clientId;

    /**
     * 客户端名称
     */
    private String clientName;

    /**
     * 客户端校验码
     */
    private String clientCode;

    /**
     * 会话密钥
     */
    private String sessionKey;

    /**
     * 权限数据
     */
    //private List<BaseOrganizationResourceInfoVO> permissions;

    public UserWithPermissionVO  initClient(Integer clientId, String clientName, String clientCode,String storeNo) {
        this.clientId = clientId;
        this.clientName = clientName;
        this.clientCode = clientCode;
        this.storeNo = storeNo;
        return this;
    }

    @JsonIgnore
    public String getJobNum() {
        if (getUsername().contains(getStoreNo())) {
            return getUsername().replace(getStoreNo(), "");
        }
        return getJobNo() + "";
    }
}
