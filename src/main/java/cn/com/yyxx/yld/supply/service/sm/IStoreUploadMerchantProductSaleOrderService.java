package cn.com.yyxx.yld.supply.service.sm;

import cn.com.yyxx.yld.supply.data.vo.OrderCallBackVO;
import cn.com.yyxx.yld.supply.data.vo.OwnerOrderVo;
import cn.com.yyxx.yld.supply.data.vo.SmMerchantProductSaleOrderVO;
import cn.com.yyxx.yld.supply.data.vo.UserWithPermissionVO;

/**
 * <p>
 *
 * </p>
 *
 * @author FYX
 * @version 0.16.0
 * @date 2020/12/09 09:56
 **/
public interface IStoreUploadMerchantProductSaleOrderService {

    /**
     * <p>根据订单编号查询订单和详情信息</p>
     *
     * @param orderNo 订单编号
     * @return cn.com.yyxx.yld.supply.data.vo.SmMerchantProductSaleOrderVO
     * @author FYX
     * @date 2020/12/9 9:52
     * @since 0.16.0
     */
    SmMerchantProductSaleOrderVO getOrderByNo(String orderNo, UserWithPermissionVO user);

    /**
     * <p>
     * 保存订单和订单品细
     * </p>
     *
     * @param order       订单Vo
     * @param userInfo 用户信息
     * @param serialNum 设备编号
     * @param userNo 用户编号
     * @param termIp 设备IP
     * @return java.lang.Boolean
     * @author FYX
     * @date 2020/12/9 15:27
     * @since 0.16.0
     */
    OrderCallBackVO saveOrder(SmMerchantProductSaleOrderVO order, UserWithPermissionVO userInfo,String serialNum, String userNo, String termIp,String hsm);

    /**
     * <p>
     * 根据订单编号删除订单
     * </p>
     *
     * @param orderNo
     * @param userInfo
     * @return java.lang.Boolean
     * @author FYX
     * @date 2020/12/11 16:52
     * @since 0.16.0
     */
    Boolean deleteOrder(String orderNo, UserWithPermissionVO userInfo);

    /**
     *
     * 发送到账卡片时查询今天的第几笔,金额是多少
     * @param mpsoSbiId
     * @author ZhangShengYi
     * @date 2021/7/7 10:14
     * @return cn.com.yyxx.yld.supply.data.vo.OwnerOrderVo
     */
    OwnerOrderVo queryOwnerOrderVo(Integer mpsoSbiId);

}
