package cn.com.yyxx.yld.supply.manager.user;

import cn.com.yyxx.yld.supply.action.BaseResultResponse;
import cn.com.yyxx.yld.supply.data.vo.SimpleUserInfoVO;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author linmeng
 * @version 1.0
 * @since 2019/1/23 15:55
 */
@Slf4j
@Component
public class ApiUserGrantInfoManagerFallbackFactory implements FallbackFactory<IApiUserGrantInfoManager> {
    public final static String TIP_FAIL_EXEC_CONSUMER_SERVICE = "无法连接商品服务,请联系管理员";
    @Override
    public IApiUserGrantInfoManager create(Throwable throwable) {
        return new IApiUserGrantInfoManager(){

            @Override
            public BaseResultResponse<Map<Integer, String>> listByStoreIdsAndTypeCode(@RequestParam("storeIds") List<Integer> storeIds,
                                                                                      @RequestParam("sddCode") String sddCode) {
                log.warn("根据门店id list{},授权类型sddCode{}查询第三方登录授权信息表,发生错误原因:",storeIds,sddCode,throwable);
                return new BaseResultResponse<Map<Integer, String>>().initFailure(TIP_FAIL_EXEC_CONSUMER_SERVICE);
            }
            @Override
            public BaseResultResponse<List<SimpleUserInfoVO>> listByUserNoAndStore(List<String> userNos, Integer storeId) {
                return new BaseResultResponse<List<SimpleUserInfoVO>>().initFailure(TIP_FAIL_EXEC_CONSUMER_SERVICE);
            }

        };
    }

}
