package cn.com.yyxx.yld.supply.manager.paymentcenter;

import cn.com.yyxx.yld.supply.data.vo.ResponsePayVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@FeignClient(name = "${yld.payment.center.name}", url = "${yld.payment.center.url}", fallbackFactory = PaymentCenterFeignFallbackFactory.class)
public interface PaymentCenterFeign {

    @RequestMapping(value = "/asyncgateface", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    ResponsePayVO pay(@RequestBody Map<String,Object> requestPayVO);

    @RequestMapping(value = "/asyncgateface", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    ResponsePayVO cancle(@RequestBody Map<String,Object> requestPayVO);

    @RequestMapping(value = "/gateface", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    ResponsePayVO query(@RequestBody Map<String,Object> requestPayVO);
}
