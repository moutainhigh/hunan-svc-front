package cn.trawe.etc.hunanfront.common;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.ptc.board.flowlimit.web.response.LimitResponseHandler;

import cn.trawe.etc.hunanfront.expose.v2.BaseResponse;
import cn.trawe.etc.hunanfront.service.BaseService;
import cn.trawe.etc.hunanfront.service.QueryMonitorServiceImpl;


/**
 * @author Jiang Guangxing
 */
public class JsonLimitResponseHandler extends BaseService implements LimitResponseHandler {
    public void init(Map<String, Object> params) {
        //do nothing
    }

    @Override
    public void responseTo(HttpServletRequest req, HttpServletResponse res, String s) throws IOException {
        res.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        BaseResponse response = otherError("系统异常,请稍后重试-9", req.getParameter("charset"),null);
        queryMonitorServiceImpl.send("JsonLimitResponseHandler", "进入限流方法...");
        SerializeConfig serializeConfig = new SerializeConfig();
        serializeConfig.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
        res.getWriter().print(JSON.toJSONString(response, serializeConfig));
    }
}
