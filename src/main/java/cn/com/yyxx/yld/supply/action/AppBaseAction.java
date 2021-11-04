package cn.com.yyxx.yld.supply.action;

import cn.com.yyxx.yld.supply.exception.NotExceptException;
import cn.com.yyxx.yld.supply.exception.ServiceExceptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * <p></p>
 *
 * @author liujun
 * @version 1.0.0
 * @program yld-supply-v2
 * @description
 * @date 2021-01-18 15:17
 * @since 0.1.0
 **/
public class AppBaseAction {

    private static final Logger log = LoggerFactory.getLogger(AppBaseAction.class);
    private static final String NOT_KNOW_MSG = "发生未知错误";

    public AppBaseAction() {
    }

    protected <T> BaseResultResponse<T> deal(Supplier<T> impl) {
        BaseResultResponse result = new BaseResultResponse();

        try {
            return result.initSuccess(impl.get());
        } catch (ServiceExceptException | NotExceptException var4) {
            log.warn("发生错误,原因:{}", var4.getMessage());
            return result.initFailure(var4.getMessage());
        } catch (Exception var5) {
            log.warn("发生错误", var5);
            return result.initFailure("发生未知错误");
        }
    }

    protected <T> BaseResultResponse<T> dealBase(Supplier<BaseResultResponse<T>> impl) {
        try {
            return (BaseResultResponse)impl.get();
        } catch (ServiceExceptException | NotExceptException var3) {
            log.warn("发生错误,原因:{}", var3.getMessage());
            return (new BaseResultResponse()).initFailure(var3.getMessage());
        } catch (Exception var4) {
            log.warn("发生错误", var4);
            return (new BaseResultResponse()).initFailure("发生未知错误");
        }
    }
}