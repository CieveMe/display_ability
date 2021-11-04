package cn.com.yyxx.yld.supply.action.commom;

import cn.com.yyxx.yld.supply.action.BaseResultResponse;
import cn.com.yyxx.yld.supply.action.NewAppBaseAction;
import cn.com.yyxx.yld.supply.service.IAppendService;
import cn.com.yyxx.yld.supply.service.impl.PayServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author hz
 * @version 1.0
 * @date 2021/07/06 17:16
 */
@RestController
@RequestMapping("/common/db/order/refund")
@Api(tags = "整单退款补齐品项(item)退款数据")
public class CommonDataUpdateAct  extends NewAppBaseAction {
    private Logger log = LoggerFactory.getLogger(PayServiceImpl.class);

    /**
     *
     * @author hz
     * @date 2021/07/06 17:23
     */
    @RequestMapping(value = "/itemAppend", method = RequestMethod.POST)
    @ApiOperation("整单退款补齐品项(item)退款数据")
    public BaseResultResponse<Boolean> pay(@RequestParam(value = "startT" ) String startT,
                                           @RequestParam(value = "endT" ) String endT) {
        return deal(() -> service.itemAppend(startT, endT));
    }

    /**
     *
     * @author hz
     * @date 2021/07/06 17:23
     */
    @RequestMapping(value = "/itemUpdate", method = RequestMethod.POST)
    @ApiOperation("整单退款补齐品项(item)退款数据")
    public BaseResultResponse<Boolean> itemUpdate(@RequestParam(value = "startT" ) String startT,
                                           @RequestParam(value = "endT" ) String endT) {
        return deal(() -> service.itemUpdate(startT, endT));
    }

    @Autowired
    private IAppendService service;
}
