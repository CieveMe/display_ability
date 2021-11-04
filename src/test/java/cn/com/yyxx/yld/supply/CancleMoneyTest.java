//package cn.com.yyxx.yld.supply;
//
//import cn.com.yyxx.yld.supply.core.PayFactoryProperties;
//import cn.com.yyxx.yld.supply.data.vo.RequestCancelPayVO;
//import cn.com.yyxx.yld.supply.util.PayUtil;
//import cn.hutool.http.HttpRequest;
//import cn.hutool.http.HttpResponse;
//import cn.hutool.http.HttpUtil;
//import cn.hutool.http.Method;
//import cn.hutool.json.JSONUtil;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.Ignore;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
///**
// * <p></p>
// *
// * @author liujun
// * @version 1.0.0
// * @program yld-supply-chain-v2
// * @description
// * @date 2021-05-13 18:27
// * @since 0.1.0
// **/
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = SupplyApplication.class)
//@Ignore
//public class CancleMoneyTest {
//    private Logger log = LoggerFactory.getLogger(CancleMoneyTest.class);
//    @Autowired
//    private ObjectMapper objectMapper;
//    @Autowired
//    private PayFactoryProperties payFactoryProperties;
//    @Test
//    @Ignore
//    public void test(){
//        List<String> aaa = new ArrayList<>();
//        aaa.add("718e850171f34406aea62ec90390a36a");
//        aaa.add("f623eb0e4ecf4cc0af3e9ed9726c61f2");
//        aaa.add("ed286b92cf6f4e138b0966785045a405");
//        aaa.add("faca1958e5b74063a67d55a1b59edb41");
//        aaa.add("655ffb95efab4fadac8b5fbb1f7f4c3f");
//        aaa.add("2dadb1ac0dc94c7bbd87f2b676c3d6f4");
//        aaa.add("c3f3ff608d2d431a9906f98be3c01322");
//        aaa.add("2a967bd5447d41e7adee042c88fccb6d");
//        aaa.add("a177a84f465f42bd845a6bca899c562e");
//        aaa.add("f131e89ac7614334921b74d98c4835b8");
//        aaa.add("be0152f7a715463794611daca5ffb5a5");
//        aaa.add("1d9ab7442c014d38a8f97f6ed72f66ab");
//        aaa.add("debf2f4097524d3d867fd154d42ab6c4");
//        aaa.add("aef2ad26585f4d7aaffaf7d24e601292");
//        aaa.add("0976c445a5d74043852eb23b56822a52");
//        aaa.add("ed8936e470f0422c9c6aaee8731b458f");
//        aaa.add("bfa170d4368d4952a348c60a70f25cc1");
//        aaa.add("68de0b59acd84131976f9f86b15a682e");
//        aaa.add("b0dd8dd904624cd6b1f037f1e5caa1ea");
//        aaa.add("9547acf11a8444c092979792b27dbb4e");
//        aaa.add("72425ec13e6a468d98749f0bcd6ea098");
//        aaa.add("8d21016434e542d5a6b109d6eda87c68");
//        aaa.add("4ab991d324de4811be53c6acc112a7f2");
//        aaa.add("e38763923975465788840d3c67dc99c2");
//        aaa.add("e67c4e9657604a99aecb57e283be4410");
//        aaa.add("7c236108fc29438eb962e0c0b7065df5");
//        aaa.add("c561d363ce8c4641bb966e1a302881dc");
//        aaa.add("b73a798eb2a44c3dbf6883c3e691ee9a");
//        aaa.add("58eae2442a64471ea17409f21cac6c7b");
//        aaa.add("67ccffba3e8a4be799672cbfdde97ca1");
//        aaa.add("9577873054084b9696691504ed76ddfa");
//        aaa.add("bd57463ad8d94c0abd36ac33a56393bd");
//        aaa.add("fa95b200920e4b61ab5fb4b7aadc3de3");
//        aaa.add("58e54bc76fe64d8aae8d93fada2a49f4");
//        aaa.add("82c6dd97ec75422c840d80d719e4911d");
//        aaa.add("8e8bd64fddce463c9c5ba36b0e1fdda2");
//        aaa.add("f1398f5a46c84bb0aba219d9df015bfa");
//        aaa.add("9c084433e2d642dab89f47d0477bb7d7");
//        aaa.add("8ebdb52d9dfd48378dbaaae4ef82e970");
//        aaa.add("d3f619a202f34e6d8783d132fed2b258");
//        aaa.add("50c6c74a8f6b47498e19f30922b76e63");
//        aaa.add("1d2fd7570ba547e2a2817405d7572e0d");
//        aaa.add("c1d8c6831fb8427f80bcd9a024946184");
//        aaa.add("4518aa2daaf342929e29046083015d20");
//        aaa.add("a0bca7f901204c60b1ea89b0277fd6da");
//        aaa.add("a9dc58b9fb004ce18ddc5680b4327760");
//        aaa.add("fc448f2bc1d24d1ca94c1c84685b9236");
////        aaa.add("37dfbdc1129d44519da2e9d363634468");
//        aaa.add("eb6125c0b7ea4a8caac0d207b9fb5ccd");
//        aaa.add("19099e2123724ba7a36f6a8d074c0373");
//        aaa.add("7b620aab2692400880e511b3194731f7");
//        aaa.add("9b318a505c56450a911ec75313ebc758");
//        aaa.add("f93b07e86b0a40f892dda0ab9bfa8fe2");
//        aaa.add("edb99595c7394786a759939a7a4d0bc2");
//        aaa.add("43721ad485254d4dbcce3fa9cf4a3500");
//        String url = "http://172.16.16.105:31365/asyncgateface";
//        String CANCEL_B_C = "CancelB_C";
//        int i =0;
//        for (String orderNo:aaa) {
//            i++;
//            RequestCancelPayVO data = new RequestCancelPayVO();
//            data.setProtocol(CANCEL_B_C);
//            RequestCancelPayVO.RequestCancelPayOrder order = new RequestCancelPayVO.RequestCancelPayOrder();
//            order.setOrderId(orderNo);
//            order.setStoreId(2471);
//            data.setOrder(order);
//            Map<String, Object> encrypt = new PayUtil().encrypt(data, objectMapper, payFactoryProperties);
//            if (encrypt == null) {
//                log.error("加密失败");
//            }
//
//            HttpRequest sendClient = HttpUtil.createRequest(Method.POST, url);
//            sendClient.timeout(30 * 1000);
//
//            sendClient.body(JSONUtil.toJsonStr(encrypt));
//            HttpResponse execute = sendClient.execute();
//            log.info("第{}笔",i);
//            log.info("httpResponse----->{}",execute.body());
//
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//                log.error("异常了");
//            }
//        }
//
//
//
//
//    }
//}