package cn.com.yyxx.yld.supply.util;

import cn.com.yyxx.yld.supply.core.PayFactoryProperties;
import cn.com.yyxx.yld.supply.exception.NotExceptException;
import cn.hutool.core.util.IdUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class PayUtil {
    private static Logger log = LoggerFactory.getLogger(PayUtil.class);

    private final static String DEFAULT_KEY = "abcdyyxx";
    private static final int TIME_OUT = 60000;


    public <T> T execute(Object data, String url, Class<T> clazz, PayFactoryProperties payFactoryProperties, ObjectMapper objectMapper) throws IOException {

        String str = objectMapper.writeValueAsString(data);
        log.info("未加密参数-> {}", str);
        String encrypt = "{\"body\":" + str + "}";
        log.info("加密:参数-> {}", encrypt);

        if (payFactoryProperties.getEncrypt()) {
            try {
                encrypt = "{\"body\": \"" + DesUtil.encrypt(str, DEFAULT_KEY) + "\"}";
            } catch (Exception e) {
                log.warn("加密发生错误,原因:", e);
                throw new NotExceptException("加密发生错误,原因{}", e.getMessage());
            }
        }

        log.warn("请求地址:{}, 参数:{}", url, encrypt);
        String body = HttpSend.httpRequest(url, "POST", encrypt, null);
        if (body.length() <= 0) {
            throw new NotExceptException("请求异常, 原因:{}");
        }
        log.info("返回结果:{}", body);
        if (clazz == null) {
            return null;
        }
        return objectMapper.readValue(body, clazz);
    }

    public Map<String,Object> encrypt(Object data, ObjectMapper objectMapper, PayFactoryProperties payFactoryProperties){
        try {
            String str = objectMapper.writeValueAsString(data);
            Map<String,Object> map = new HashMap<>();
            log.info("支付参数，{}",str);
            map.put("body",str);
            if (payFactoryProperties.getEncrypt()) {
                map.put("body", DesUtil.encrypt(str, DEFAULT_KEY));
            }
            return map;
        }catch (Exception ex){
            log.error("加密错误,{}",ex.toString());
            return null;
        }
    }

}
