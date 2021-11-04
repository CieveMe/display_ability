package cn.com.yyxx.yld.supply.manager.supervip;


import cn.com.yyxx.yld.supply.data.vo.Result;
import cn.com.yyxx.yld.supply.data.vo.SuperVIPRequestPayVO;
import cn.com.yyxx.yld.supply.data.vo.SuperVIPResponsePayVO;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author duanweidong
 * @date 2021/2/8 13:20
 */
@Component
public class SuperVIPFeignFallbackFactory implements FallbackFactory<SuperVIPFeign> {
    public final static String TIP_FAIL_EXEC_CONSUMER_SERVICE = "无法连接支付服务,请联系管理员";

    private Logger log = LoggerFactory.getLogger(SuperVIPFeignFallbackFactory.class);
    @Override
    public SuperVIPFeign create(Throwable throwable) {
        log.error("超级会员微服务异常{}",throwable.toString());
        return new SuperVIPFeign() {
            @Override
            public Result<Boolean> billCheck(SuperVIPRequestPayVO superVIPRequestPayVO) {
                log.error("支付-超级会员下单微服务连接失败");
                Result<Boolean> responsePay = new Result<Boolean>();
                responsePay.setMsg(TIP_FAIL_EXEC_CONSUMER_SERVICE);
                responsePay.setCode("0002");
                responsePay.setData(false);
                return responsePay;
            }
        };
    }
}
