package cn.com.yyxx.yld.supply.manager.supervip;

import cn.com.yyxx.yld.supply.data.vo.Result;
import cn.com.yyxx.yld.supply.data.vo.SuperVIPRequestPayVO;
import cn.com.yyxx.yld.supply.data.vo.SuperVIPResponsePayVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@FeignClient(name = "${yld.supervip.center.name}", url = "${yld.supervip.center.url}", fallbackFactory = SuperVIPFeignFallbackFactory.class)
public interface SuperVIPFeign {

    @RequestMapping(value = "/member/api/bill/check", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    Result<Boolean> billCheck(@RequestBody SuperVIPRequestPayVO superVIPRequestPayVO);
}

