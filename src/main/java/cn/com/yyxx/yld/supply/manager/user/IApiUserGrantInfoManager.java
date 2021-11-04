package cn.com.yyxx.yld.supply.manager.user;

import cn.com.yyxx.yld.supply.action.BaseResultResponse;
import cn.com.yyxx.yld.supply.data.vo.SimpleUserInfoVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author hz
 * @version 1.0
 * @since 2019/1/23 15:54
 */
@FeignClient(value = "${yld.user.info.name}", url = "${yld.user.info.url}", fallbackFactory = ApiUserGrantInfoManagerFallbackFactory.class)
public interface IApiUserGrantInfoManager {

    /**
     * <p>
     *  根据门店id list,授权类型sddCode,dddCode查询第三方登录授权信息表
     * </p
     * @param storeIds 门店id list
     * @param sddCode 授权类型sddCode
     * @return java.util.List<cn.com.yyxx.yld.uim.data.vo.user.UmUserGrantInfoVO>
     * @author hz
     * @since 2019/3/9 14:51
     */
    @RequestMapping(value = "/api/user/grant/list_by_store_list",method = RequestMethod.POST)
    @ResponseBody
    BaseResultResponse<Map<Integer ,String>> listByStoreIdsAndTypeCode(@RequestParam("storeIds") List<Integer> storeIds,
                                                                       @RequestParam("sddCode") String sddCode);

    /**
     * <p>
     * 根据用户编号list和门店id查询用户信息
     * </p
     * @param userNos 用户编号list
     * @param storeId 门店id
     * @return cn.com.yyxx.model.core.BaseResultResponse<cn.com.yyxx.yld.uim.data.vo.user.SimpleUserInfoVO>
     * @author hz
     * @since 2019/2/14 11:58
     */
    @RequestMapping(value = "/api/user/list",method = RequestMethod.POST)
    BaseResultResponse<List<SimpleUserInfoVO>> listByUserNoAndStore(@RequestParam("userNos") List<String> userNos, @RequestParam("storeId") Integer storeId);

}
