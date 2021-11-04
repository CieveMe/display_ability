package cn.com.yyxx.yld.supply.manager.product;

import cn.com.yyxx.yld.supply.action.BaseResultResponse;
import cn.com.yyxx.yld.supply.data.vo.*;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 *
 * </p>
 *
 * @author hz
 * @version 1.0
 * @since 2019/2/14 17:14
 */
@Component
public class ApiMerchantProductManagerFallbackFactory implements
    FallbackFactory<IApiMerchantProductManager> {

    public final static String TIP_FAIL_EXEC_CONSUMER_SERVICE = "无法连接商品服务,请联系管理员";
    private Logger log = LoggerFactory.getLogger(ApiMerchantProductManagerFallbackFactory.class);
    @Override
    public IApiMerchantProductManager create(Throwable throwable) {
        return new IApiMerchantProductManager(){

            @Override
            public BaseResultResponse<List<PmMerchantProductBasicAndLibVO>> listByNoAndStoreId(
                    ArrayList<String> nos, Integer storeId) {
                return new BaseResultResponse<List<PmMerchantProductBasicAndLibVO>>()
                    .initFailure(TIP_FAIL_EXEC_CONSUMER_SERVICE);
            }
            @Override
            public BaseResultResponse<List<BasicInfoIdAndNoVO>> getListByMpbiIdAndSbiId(
                List<Long> ids, Integer mpbiSbiId) {
                return new BaseResultResponse<List<BasicInfoIdAndNoVO>>()
                    .initFailure(TIP_FAIL_EXEC_CONSUMER_SERVICE);
            }
            @Override
            public BaseResultResponse<List<PmMerchantProductBasicInfoVO>> getListByPislIdAndSbiId(
                @RequestParam List<Long> ids, @RequestParam Integer sbiid) {
                return new BaseResultResponse<List<PmMerchantProductBasicInfoVO>>()
                    .initFailure(TIP_FAIL_EXEC_CONSUMER_SERVICE);
            }
            @Override
            public BaseResultResponse<ProductInfoStandardLibVO> getPmProductInfoStandardLibEById(
                Long pislId) {
                return new BaseResultResponse<ProductInfoStandardLibVO>()
                    .initFailure(TIP_FAIL_EXEC_CONSUMER_SERVICE);
            }
        };
    }
}
