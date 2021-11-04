package cn.com.yyxx.yld.supply.manager.supervip;


import cn.com.yyxx.yld.supply.data.vo.*;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author duanweidong
 * @date 2021/2/8 13:20
 */
@Component
public class MemberDiscountFeignFallbackFactory implements FallbackFactory<SuperVIPDiscountFeign> {
    public final static String TIP_FAIL_EXEC_CONSUMER_SERVICE = "无法连接获取折扣服务,请联系管理员";

    private Logger log = LoggerFactory.getLogger(MemberDiscountFeignFallbackFactory.class);
    @Override
    public SuperVIPDiscountFeign create(Throwable throwable) {
        log.error("超级会员微服务异常{}",throwable.toString());
        return new SuperVIPDiscountFeign() {
            @Override
            public Result<CardInfoToSaasVO> discountInfo(String sbiId) {
                log.error("超级会员获取折扣微服务连接失败");
                Result<CardInfoToSaasVO> responseDiscountVO = new Result<CardInfoToSaasVO>();
                responseDiscountVO.setMsg(TIP_FAIL_EXEC_CONSUMER_SERVICE);
                responseDiscountVO.setCode("0002");
                responseDiscountVO.setData(new CardInfoToSaasVO() {
                    @Override
                    public void setCardNo(String cardNo) {
                        super.setCardNo(cardNo);
                    }
                });
                return responseDiscountVO;
            }
        };
    }
}


//@Component
//public class SuperVIPFeignFallbackFactory implements FallbackFactory<SuperVIPFeign> {
//    public final static String TIP_FAIL_EXEC_CONSUMER_SERVICE = "无法连接支付服务,请联系管理员";
//
//    private Logger log = LoggerFactory.getLogger(SuperVIPFeignFallbackFactory.class);
//    @Override
//    public SuperVIPFeign create(Throwable throwable) {
//        log.error("超级会员微服务异常{}",throwable.toString());
//        return new SuperVIPFeign() {
//            @Override
//            public SuperVIPResponsePayVO billCheck(SuperVIPRequestPayVO superVIPRequestPayVO) {
//                log.error("支付-超级会员下单微服务连接失败");
//                SuperVIPResponsePayVO responsePayVO = new SuperVIPResponsePayVO();
//                responsePayVO.setMsg(TIP_FAIL_EXEC_CONSUMER_SERVICE);
//                responsePayVO.setCode("0002");
//                responsePayVO.setData(false);
//                return responsePayVO;
//            }
//        };
//    }
//}
