package cn.com.yyxx.yld.supply.manager.supervip;

import cn.com.yyxx.yld.supply.data.vo.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotBlank;


@FeignClient(name = "${yld.supervip.discount.name}", url = "${yld.supervip.discount.url}", fallbackFactory = MemberDiscountFeignFallbackFactory.class)
public interface SuperVIPDiscountFeign {

    @RequestMapping(value = "/finance/card/cardInfoToSaas", method = RequestMethod.GET)
    Result<CardInfoToSaasVO> discountInfo(@NotBlank(message = "参数不允许为空") @RequestParam("cardNo") String cardNo);
}
