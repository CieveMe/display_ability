package cn.com.yyxx.yld.supply.action;

import cn.com.yyxx.yld.supply.config.AuthUrlCofig;
import cn.com.yyxx.yld.supply.data.vo.UserWithPermissionVO;
import cn.com.yyxx.yld.supply.exception.NotExceptException;
import cn.com.yyxx.yld.supply.redis.RedisUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

public class NewAppBaseAction extends AppBaseAction {
    private Logger log = LoggerFactory.getLogger(NewAppBaseAction.class);

    public UserWithPermissionVO getUser(HttpServletRequest request) {

        String store_id = request.getParameter("store_id");
        String serial_num = request.getParameter("serial_num");
        String user_no = request.getParameter("user_no");
        // 内测数据
//        String store_id = "7";
//        String serial_num = "LDYF00013657";
//        String user_no = "ceb45755a37d476887e0daa1930b7811";

        if (store_id == null || ("").equals(store_id)) {
            log.error("门店id不能为空！！！");
            throw new NotExceptException("门店id不能为空！！！");
        }
        if (serial_num == null || ("").equals(serial_num)) {
            log.error("设备序列号不能为空！！！");
            throw new NotExceptException("设备序列号不能为空！！！");
        }
        if (user_no == null || ("").equals(user_no)) {
            log.error("用户编号不能为空！！！");
            throw new NotExceptException("用户编号不能为空！！！");
        }

        String key = "store_id=" + store_id + "|serial_num=" + serial_num + "|user_no=" + user_no + "";
        log.info("*****************redis的key值为REDIS***************"+key);
        Object o = redisUtil.get(key);
        log.info("redis value is null->" + (o == null));
        UserWithPermissionVO user = new UserWithPermissionVO();

        if (o == null) {
            log.warn("===========================key为空==================================");
            log.warn(key);
            String str =
                "?store_id=" + store_id + "&serial_num=" + serial_num + "&user_no=" + user_no + "";
            //http://192.168.50.140:16200/common/query/store_info/get?store_id=24&serial_num=193KBC801755&user_no=976d150a8fa44db3ae7f42268be04a62
            //http://dev.yingliduo.cn:10020/org/common/query/store_info/get?store_id=26&serial_num=193KBC801755&user_no=976d150a8fa44db3ae7f42268be04a62
            //http://nginx.yingliduo.cn:46200/org/common/query/store_info/get?store_id=26&serial_num=193KBC801755&user_no=976d150a8fa44db3ae7f42268be04a62
            log.info("请求地址:{}, 参数:{}", authUrlCofig.getAuthurl(), str);
            HttpRequest req = HttpUtil.createRequest(Method.GET, authUrlCofig.getAuthurl() + "" + str);
            req.timeout(10 * 1000);
//        req.body(encrypt);
            HttpResponse execute = req.execute();
            log.debug("返回参数:{}", execute);
            if (!execute.isOk()) {
                throw new NotExceptException("请求异常, 原因:{}", execute.getStatus());
            }
            String body = execute.body();
            log.debug("返回结果:{}", body);


            JSONObject jsonObject = JSONUtil.parseObj(body);
            String success = jsonObject.getStr("success", "false");
            log.info("request status is success->" + success);
            if ("true".equals(success)) {


                JSONObject data = jsonObject.getJSONObject("data");
//            @ApiModelProperty("商户Id")
                String sbiId = data.getStr("sbiId");
                user.setStoreId(Integer.parseInt(sbiId));
//            @ApiModelProperty("商户名称")
                String sbiFullName = data.getStr("sbiFullName");
                user.setStoreName(sbiFullName);
//            @ApiModelProperty("商户编号")
                String sbiCode = data.getStr("sbiCode");
                user.setStoreNo(sbiCode);
//            @ApiModelProperty("商户类别")
                String sbiCategory = data.getStr("sbiCategory");
                user.setSbiCategory(sbiCategory);
//            @ApiModelProperty("门店类型")
                String sbiChildType = data.getStr("sbiChildType");
                user.setSbiChildType(sbiChildType);
//            @ApiModelProperty("门店省份ID")
                String sbiProvinceId = data.getStr("sbiProvinceId");
                user.setSbiProvinceId(Integer.parseInt(sbiProvinceId));
//            @ApiModelProperty("门店城市ID")
                String sbiCityId = data.getStr("sbiCityId");
                user.setSbiCityId(Integer.parseInt(sbiCityId));
//            @ApiModelProperty("门店区县ID")
                String sbiAreaId = data.getStr("sbiAreaId");
                user.setSbiAreaId(Integer.parseInt(sbiAreaId));
//            @ApiModelProperty("设备ID")
                String sdcId = data.getStr("sdcId");
                user.setClientId(Integer.parseInt(sdcId));
//            @ApiModelProperty("设备名称")
                String sdcClientName = data.getStr("sdcClientName");
                user.setClientName(sdcClientName);
//            @ApiModelProperty("设备校验码")
                String sdcVerifyCode = data.getStr("sdcVerifyCode");
                user.setClientCode(sdcVerifyCode);
//            @ApiModelProperty("客户端版本")
                String sdcClientVersion = data.getStr("sdcClientVersion");
//            @ApiModelProperty("用户ID")
                String ubiId = data.getStr("ubiId");
                log.info("super:ubiId->" + ubiId);
                user.setId(Long.valueOf(ubiId));
                //////
//            @ApiModelProperty("用户名")
                String ubiUserName = data.getStr("ubiUserName");
                user.setUsername(ubiUserName);
//            @ApiModelProperty("工号")
                String ubiJobNumber = data.getStr("ubiJobNumber");
                user.setJobNo(Integer.valueOf(ubiJobNumber));
//            @ApiModelProperty("密码")
                String ubiPassword = data.getStr("ubiPassword");
                user.setPassword(ubiPassword);
//            @ApiModelProperty("昵称")
                String ubiNikeName = data.getStr("ubiNikeName");
                user.setNickName(ubiNikeName);
//            @ApiModelProperty("真实姓名")
                String ubiRealName = data.getStr("ubiRealName");
                user.setRealName(ubiRealName);
//            @ApiModelProperty("身份证号")
                String ubiIdCard = data.getStr("ubiIdCard");
//            @ApiModelProperty("是否店主")
                String ubiIsOwner = data.getStr("ubiIsOwner");
                user.setOwner(Boolean.parseBoolean(ubiIsOwner));

                /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                ////拼接user

                log.debug("==========================common UserWithPermissionVO===========================================");
                log.debug(user.toString());
                log.debug("==========================common UserWithPermissionVO===========================================");
                JSONObject jo = JSONUtil.parseObj(user);
                redisUtil.put(key, jo.toString());

            } else {
                throw new NotExceptException("UserWithPermissionVO:查询失败！！！，失败原因:" + jsonObject.getStr("message", "接口内部错误"));
            }
        } else {
            log.debug("===========================key不为空==================================");
            log.debug(key);
            log.debug(o + "");
            String body = o + "";
            user = JSONUtil.toBean(body, UserWithPermissionVO.class);
            log.info("redis value is->" + o.toString());
        }
        return user;
    }


    @Resource
    private RedisUtil redisUtil;

    @Autowired
    AuthUrlCofig authUrlCofig;
}
