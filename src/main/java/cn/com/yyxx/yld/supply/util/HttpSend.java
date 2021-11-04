package cn.com.yyxx.yld.supply.util;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class HttpSend {
    /***
     * <p></p>
     * @param requestMethod 请求方式
     * @param outstr 参数
     * @return java.lang.String
     * @throws
     * @author hk
     * @date 2020-01-04 11:31
     * @since
     */

    public static String httpRequest(String requestUrl, String requestMethod,String outstr, String header) {
        StringBuffer buffer = null;
        try {
            if ("GET".equals(requestMethod)) {
                requestUrl = requestUrl + "?" + outstr;
            }
            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            /*设置可输入*/
            connection.setDoInput(true);
            /*设置该连接是可以输出的*/
            connection.setDoOutput(true);
            /*设置请求方式*/
            connection.setRequestMethod(requestMethod);
            connection.setUseCaches(false);
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.disconnect();
            if (header != null && !"".equals(header)) {
                System.out.println(header);
                connection.setRequestProperty("AccessToken", header);
            }
            connection.connect();
            OutputStream os = connection.getOutputStream();

            if (null != outstr) {
                os.write(outstr.getBytes(StandardCharsets.UTF_8));
                os.flush();
                os.close();
            }
            InputStream is = connection.getInputStream();

            InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            buffer = new StringBuffer();
            String line = null;
            while ((line = br.readLine()) != null) {
                buffer.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return buffer.toString();
    }


    /**
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     *
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    public static String createLinkString(Map<String, String> params) {
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        String prestr = "";
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            /* 拼接时，不包括最后一个&字符*/
            if (i == keys.size() - 1) {
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }
        return prestr;
    }

}
