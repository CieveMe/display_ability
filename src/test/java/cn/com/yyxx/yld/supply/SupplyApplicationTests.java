//package cn.com.yyxx.yld.supply;
////
////import cn.com.yyxx.yld.supply.core.PayFactoryProperties;
////import cn.com.yyxx.yld.supply.data.vo.RequestCancelPayVO;
////import cn.com.yyxx.yld.supply.data.vo.SmMerchantProductSaleItemVO;
////import cn.com.yyxx.yld.supply.data.vo.SmMerchantProductSaleOrderVO;
////import cn.com.yyxx.yld.supply.data.vo.UserWithPermissionVO;
////import cn.com.yyxx.yld.supply.manager.paymentcenter.PaymentCenterFeign;
////import cn.com.yyxx.yld.supply.service.sm.IStoreUploadMerchantProductSaleOrderService;
////import cn.com.yyxx.yld.supply.util.PayUtil;
////import com.fasterxml.jackson.databind.ObjectMapper;
////import org.junit.Ignore;
////import org.junit.Test;
////import org.junit.runner.RunWith;
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.boot.test.context.SpringBootTest;
////import org.springframework.test.context.junit4.SpringRunner;
////
////import java.math.BigDecimal;
////import java.sql.SQLException;
////import java.util.ArrayList;
////import java.util.List;
////import java.util.Map;
////
//
//import cn.com.yyxx.yld.supply.data.vo.SmMerchantProductSaleItemVO;
//import cn.com.yyxx.yld.supply.data.vo.SmMerchantProductSaleOrderVO;
//import cn.com.yyxx.yld.supply.data.vo.SuperVIPRequestPayVO;
//import cn.com.yyxx.yld.supply.data.vo.SuperVIPResponsePayVO;
//import cn.com.yyxx.yld.supply.manager.supervip.SuperVIPFeign;
//import cn.com.yyxx.yld.supply.service.IPayService;
//import cn.com.yyxx.yld.supply.service.impl.PayServiceImpl;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = SupplyApplication.class)
//@Slf4j
//public class SupplyApplicationTests {
//
//
//    @Test
//    public void test() {
//        SmMerchantProductSaleOrderVO order = new SmMerchantProductSaleOrderVO();
//        SmMerchantProductSaleItemVO item = new SmMerchantProductSaleItemVO();
//        List<SmMerchantProductSaleItemVO> items = new ArrayList<>();
//        //创建详情
//        item.setMpsiMpbiNo("");
//        //将详情加入到List中
//        items.add(item);
//        // 创建订单
//        order.setItems(items);
//
//
//        //进入你的方法 约等于 收银端请求服务器
//
//        SmMerchantProductSaleOrderVO smMerchantProductSaleOrderVO = payService.storeSaleInfo(order);
//
//        log.info("=====SmMerchantProductSaleOrderVO=====:{}",smMerchantProductSaleOrderVO);
//
//    }
//
//    @Autowired
//    private SuperVIPFeign superVip;
//
//    @Autowired
//    private PayServiceImpl payService;
//}
//
//
//
//
//
////
////      @Test
////      @Ignore
////    public void contextLoads() throws SQLException {
////          UserWithPermissionVO user = new UserWithPermissionVO();
////          user.setStoreId(41);
////          user.setStoreNo("测试");
////          user.setStoreName("测试");
////          user.setClientId(1);
////          user.setSbiCategory("测试一下");
////          user.setRealName("测试");
////          user.setSbiAreaId(1);
////          user.setJobNo(1);
////          user.setSbiProvinceId(1);
////          user.setSbiCityId(1);
////          user.setSbiAreaId(1);
////          user.setSbiChildType("测试");
////          user.setId(1L);
////
////
////          SmMerchantProductSaleOrderVO vo = new SmMerchantProductSaleOrderVO();
////          vo.setMpsoChangePrice(new BigDecimal(1));
////          vo.setMpsoActualPrice(new BigDecimal(1));
////          vo.setMpsoCashPrice(new BigDecimal(1));
////          vo.setMpsoActualPrice(new BigDecimal(1));
////          vo.setMpsoShouldPrice(new BigDecimal(1));
////          vo.setMpsoRealPrice(new BigDecimal(1));
////          vo.setMpsoTotalPrice(new BigDecimal(1));
////          vo.setMpsoOrderNo("测试");
////          vo.setMpsoSingleNo("测试");
////          vo.setMpsoOrderStatus("SOS_REFUND_FAILED");
////          vo.setMpsoPaymentMethod("SPM_CASH_PAYMENT");
////          vo.setMpsoSbiType("OSC_COMMUNITY");
////          vo.setMpsoSerialNo("00001");
////          SmMerchantProductSaleItemVO item = new SmMerchantProductSaleItemVO();
////          item.setMpsiMpbiNo("001ed9facc584e0fa2207f318035b792");
////          item.setMpsiNowPrice(new BigDecimal(1));
////          item.setMpsiRetailPrice(new BigDecimal(1));
////          item.setMpsiWholesalePrice(new BigDecimal(1));
////          item.setMpsiSubTotal(new BigDecimal(1));
////          item.setMpsiNum(new BigDecimal(1));
////          item.setMpsiDiscount(new BigDecimal(1));
////
////
////
////          List<SmMerchantProductSaleItemVO> itemVOS =  new ArrayList<>();
////          itemVOS.add(item);
////          vo.setItems(itemVOS);
////
//////          Boolean aBoolean = service.saveOrder(vo, user);
////
////
//////          System.out.println(aBoolean);
////      }
////
////      @Test
////      public void cancle(){
////          RequestCancelPayVO data = new RequestCancelPayVO();
////          data.setProtocol("CancelB_C");
////          RequestCancelPayVO.RequestCancelPayOrder order = new RequestCancelPayVO.RequestCancelPayOrder();
////          order.setOrderId("7ad784bdf3694700aa041786452cda02");
////          order.setStoreId(90000283);
////          data.setOrder(order);
////          Map<String,Object> encrypt =  new PayUtil().encrypt(data,objectMapper,payFactoryProperties);
////          System.out.println(encrypt);
////          if (encrypt==null){
////              System.out.println("加密失败");
////          }
////          System.out.println(paymentCenterFeign.cancle(encrypt));
////      }
////
////
////    @Autowired
////    private IStoreUploadMerchantProductSaleOrderService service;
////    @Autowired
////    private PayFactoryProperties payFactoryProperties;
////    @Autowired
////    private ObjectMapper objectMapper;
////    @Autowired
////    private PaymentCenterFeign paymentCenterFeign;
////
////}
