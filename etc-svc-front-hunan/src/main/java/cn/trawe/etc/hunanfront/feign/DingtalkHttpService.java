package cn.trawe.etc.hunanfront.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.trawe.etc.hunanfront.request.EtcMonitorRequest;


/**
 * 向钉钉发送消息
 * @author guzelin
 *
 */
@FeignClient(name = "dingtalk-http-service", url = "${etc-monitor.alert-url}")
public interface DingtalkHttpService {

	/**
     * post请求
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.POST,consumes = "application/json")
    String doPost(@RequestBody EtcMonitorRequest json);
}
