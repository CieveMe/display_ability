package cn.com.yyxx.yld.supply.data.dto;

import java.util.Map;

public class ApiMapReMsg {

    private String message;
    private Map<String, Object> data1;
    private Object data;
    private String success;

    public ApiMapReMsg(String success, String message) {
        this.success = success;
        this.message = message;
    }

    public ApiMapReMsg(Object data) {
        this.success = "true";
        this.message = "";
        this.data = data;
    }
    public ApiMapReMsg(Map<String, Object> data) {
        this.success = "true";
        this.message = "";
        this.data1 = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getData1() {
        return data1;
    }

    public Object getData() {
        return data;
    }

    public ApiMapReMsg setData(Object data) {
        this.data = data;
        return this;
    }

    public void setData1(Map<String, Object> data) {
        this.data1 = data;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }


    @Override
    public String toString() {
        return "ApiMapReMsg{" +
            "message='" + message + '\'' +
            ", data1=" + data1 +
            ", data=" + data +
            ", success='" + success + '\'' +
            '}';
    }
}
