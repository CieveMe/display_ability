package cn.com.yyxx.yld.supply.action.commom;

import cn.com.yyxx.yld.supply.action.BaseResultResponse;
import cn.com.yyxx.yld.supply.action.NewAppBaseAction;
import cn.com.yyxx.yld.supply.data.vo.*;
import cn.com.yyxx.yld.supply.exception.NotExceptException;
import cn.com.yyxx.yld.supply.service.impl.SalesOrderServiceImpl;
import cn.com.yyxx.yld.supply.service.sm.IStoreUploadMerchantProductSaleOrderService;
import cn.com.yyxx.yld.supply.util.ClientIpUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author FYX
 * @version 0.16.0
 * @date 2020/12/09 09:53
 **/
@RestController
@RequestMapping("/common/supply/v2/order")
@Api(tags = "智慧菜场版-根据订单号查询订单、上传订单、取消下单（已支付不支持）")
public class CommonSaleOrderAct extends NewAppBaseAction {

    @GetMapping("/get_order_by_no/{orderNo}")
    public BaseResultResponse<SmMerchantProductSaleOrderVO> getOrderByNo(
        @PathVariable("orderNo") String orderNo, HttpServletRequest request) {
        UserWithPermissionVO userInfo = super.getUser(request);
        return deal(() -> service.getOrderByNo(orderNo, userInfo));
    }

    @PostMapping("/save_order")
    public BaseResultResponse<OrderCallBackVO> saveOrder(
        @RequestBody SmMerchantProductSaleOrderVO order,
        HttpServletRequest request) {
        UserWithPermissionVO userInfo = super.getUser(request);
        String termIp = new ClientIpUtil().getClientIpAddress(request);
        String serialNum = request.getParameter("serial_num");
        String userNo = request.getParameter("user_no");
        String hsm = request.getParameter("hsm");
        return deal(() -> service.saveOrder(order, userInfo, serialNum, userNo, termIp,hsm));
    }

    @PostMapping("/delete_order")
    public BaseResultResponse<Boolean> deleteOrder(@RequestParam("orderNo") String orderNo,
        HttpServletRequest request) {
        UserWithPermissionVO userInfo = super.getUser(request);
        return deal(() -> service.deleteOrder(orderNo, userInfo));
    }


    @PostMapping(value = "/new_put_in")
    @ApiOperation("新版入库")
    public BaseResultResponse<NewPutInVo> newPutIn(
        @RequestBody List<PutInStoreItemVO> items, HttpServletRequest request
    ) {

        UserWithPermissionVO user = super.getUser(request);
        String serialNum = request.getParameter("serial_num");
        String userNo = request.getParameter("user_no");
        if (StrUtil.isEmpty(serialNum)) {
            throw new NotExceptException("无法获取机器序列号");
        }

        BaseResultResponse<NewPutInVo> base = salesOrderServiceimpl
            .newPutIn(items, user, serialNum, userNo, "AddStock");

        return base;

    }

//    @PostMapping(value = "/new_put_in_for_phone")
//    @ApiOperation("新版入库for索证索票 手机端")
//    public BaseResultResponse<NewPutInVo> newPutInForPhone(
//            @RequestBody List<PutInStoreItemVO> items, HttpServletRequest request
//    ) {
//        //收银员编号
//        String userNo = request.getParameter("user_no");
//        if (userNo == null) {
//            throw new NotExceptException("userNo不能为空!");
//        }
//        //门店id
//        String storeId = request.getParameter("store_id");
//        if (storeId == null) {
//            throw new NotExceptException("storeId不能为空!");
//        }
//        //工号
//        String ubiJobNumber = request.getParameter("ubi_job_number");
//        if (ubiJobNumber == null) {
//            throw new NotExceptException("ubiJobNumber不能为空!");
//        }
//        // 真实姓名
//        String ubiRealName = request.getParameter("ubi_real_name");
//        if (ubiRealName == null) {
//            throw new NotExceptException("ubiRealName不能为空!");
//        }
//        // 门店名称
//        String ubiStoreName = request.getParameter("ubi_store_name");
//        if (ubiStoreName == null) {
//            throw new NotExceptException("ubiStoreName不能为空!");
//        }
//        // 操作人员id
//        String ubiId = request.getParameter("ubi_id");
//        if (ubiId == null) {
//            throw new NotExceptException("ubi_id不能为空!");
//        }
//        BaseResultResponse<NewPutInVo> base = salesOrderServiceimpl.newPutInForPhone(items, storeId, ubiJobNumber, userNo, "AddStock", ubiRealName, ubiStoreName,ubiId);
//
//        return base;
//
//    }


    /**
     * 烟草一建入库
     *
     * @param request 请求
     */
    @ApiOperation("一建入库")
    @RequestMapping(value = "/one_click_library", method = RequestMethod.POST)
    public BaseResultResponse<Boolean> oneBuiltIntoTheWarehouse(HttpServletRequest request) {
        UserWithPermissionVO user = super.getUser(request);
        String serialNum = request.getParameter("serial_num");
        String userNo = request.getParameter("user_no");
        String orderId = request.getParameter("id");
        Long orderIdLong = new Long(orderId);
        return deal(() -> salesOrderServiceimpl
            .oneBuiltIntoTheWarehouse(user, orderIdLong, true, serialNum, userNo));
    }


    @Autowired
    private IStoreUploadMerchantProductSaleOrderService service;

    @Autowired
    private SalesOrderServiceImpl salesOrderServiceimpl;
}
