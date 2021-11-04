package cn.com.yyxx.yld.supply.action;

import cn.com.yyxx.yld.supply.exception.NotExceptException;
import io.swagger.annotations.ApiModelProperty;
import jodd.util.StringUtil;

import java.io.Serializable;

/**
 * <p></p>
 *
 * @author hz
 * @version 1.0.0
 * @date 2021-01-18 15:02
 * @since 0.17.0
 **/
public class BaseResultResponse<T> implements Serializable {

    private static final long serialVersionUID = 7996865952498720327L;
    @ApiModelProperty(
        value = "是否成功",
        name = "success"
    )
    private Boolean success;
    @ApiModelProperty(
        value = "结果类型",
        name = "type"
    )
    private String type;
    @ApiModelProperty(
        value = "（错误）消息",
        name = "message"
    )
    private String message;
    @ApiModelProperty(
        value = "结果数据",
        name = "data"
    )
    private T data;

    public BaseResultResponse(Boolean success) {
        this.success = success;
    }

    public BaseResultResponse(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public BaseResultResponse(Boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    public BaseResultResponse(Boolean success, String type, String message) {
        this.success = success;
        this.type = type;
        this.message = message;
    }

    public BaseResultResponse(Boolean success, String type, T data) {
        this.success = success;
        this.type = type;
        this.data = data;
    }

    public BaseResultResponse(Boolean success, String type, String message, T data) {
        this.success = success;
        this.type = type;
        this.message = message;
        this.data = data;
    }

    public BaseResultResponse<T> initSuccess(T data) {
        this.success = true;
        this.data = data;
        return this;
    }

    public BaseResultResponse<T> initFailure(String message) {
        this.success = false;
        this.message = message;
        return this;
    }


    public static <T> T getResponseDataNotAllowNull(BaseResultResponse<T> response)
        throws RuntimeException {
        T data = getResponseDataAllowNull(response);
        if (data == null) {
            throw new NotExceptException("响应为空");
        } else {
            return data;
        }
    }

    public static <T> T getResponseDataAllowNull(BaseResultResponse<T> response)
        throws RuntimeException {
        if (response.getSuccess() != null && response.getSuccess()) {
            return response.getData();
        } else {
            throw new NotExceptException(
                "响应失败:" + (StringUtil.isBlank(response.getMessage()) ? "原因未知"
                    : response.getMessage()));
        }
    }

    public Boolean getSuccess() {
        return this.success;
    }

    public String getType() {
        return this.type;
    }

    public String getMessage() {
        return this.message;
    }

    public T getData() {
        return this.data;
    }

    public void setSuccess(final Boolean success) {
        this.success = success;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public void setData(final T data) {
        this.data = data;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof BaseResultResponse)) {
            return false;
        } else {
            BaseResultResponse<?> other = (BaseResultResponse)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label59: {
                    Object this$success = this.getSuccess();
                    Object other$success = other.getSuccess();
                    if (this$success == null) {
                        if (other$success == null) {
                            break label59;
                        }
                    } else if (this$success.equals(other$success)) {
                        break label59;
                    }

                    return false;
                }

                Object this$type = this.getType();
                Object other$type = other.getType();
                if (this$type == null) {
                    if (other$type != null) {
                        return false;
                    }
                } else if (!this$type.equals(other$type)) {
                    return false;
                }

                Object this$message = this.getMessage();
                Object other$message = other.getMessage();
                if (this$message == null) {
                    if (other$message != null) {
                        return false;
                    }
                } else if (!this$message.equals(other$message)) {
                    return false;
                }

                Object this$data = this.getData();
                Object other$data = other.getData();
                if (this$data == null) {
                    if (other$data != null) {
                        return false;
                    }
                } else if (!this$data.equals(other$data)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof BaseResultResponse;
    }

    @Override
    public int hashCode() {
        boolean PRIME = true;
        int result = 1;
        Object $success = this.getSuccess();
        result = result * 59 + ($success == null ? 43 : $success.hashCode());
        Object $type = this.getType();
        result = result * 59 + ($type == null ? 43 : $type.hashCode());
        Object $message = this.getMessage();
        result = result * 59 + ($message == null ? 43 : $message.hashCode());
        Object $data = this.getData();
        result = result * 59 + ($data == null ? 43 : $data.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "BaseResultResponse(success=" + this.getSuccess() + ", type=" + this.getType()
            + ", message=" + this.getMessage() + ", data=" + this.getData() + ")";
    }

    public BaseResultResponse() {
    }
}