package cn.trawe.etc.hunanfront.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.trawe.etc.hunanfront.feign.DingtalkHttpService;
import cn.trawe.etc.hunanfront.request.EtcMonitorRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * 监控报警服务
 * @author guzelin
 *
 */
@Service
@Slf4j
public class QueryMonitorServiceImpl {

	@Value("${spring.application.name}")
	private String applicationName;
	@Value("${etc-monitor.monitor-enable}")
	private Boolean monitorEnable;
	@Value("${etc-monitor.alert-url}")
	private String alertUrl;
	@Value("${etc-monitor.alert-after-failures}")
	private int alertAfterFailures;
	@Value("${etc-monitor.alert-intervals-ms}")
	private long alertIntervalsMs;

	@Autowired
	DingtalkHttpService dingtalkHttpService;
	
	
	 
	@Value("${spring.application.active}")
	protected  String active;

	/**
	 * 中英文对应map
	 */
	private static Map<String,String> nameMap = new ConcurrentHashMap<>();
	/**
	 * 连续调用失败次数
	 */
	private static Map<String,Integer> countMap = new ConcurrentHashMap<>();
	/**
	 * 上一次调用的时间
	 */
	private static Map<String,Long> lastTimeMap = new ConcurrentHashMap<>();
	static{
//		nameMap.put("add_order", "车辆注册（订单提交）");
//		nameMap.put("add_user", "用户注册");
		nameMap.put("check_plate", "车牌校验");
		nameMap.put("black_white", "卡拉黑漂白");
		nameMap.put("deliver_notice", "发货通知");
//		nameMap.put("query_obu", "查询OBU信息");
//		nameMap.put("set_confirm", "安装确认");
//		nameMap.put("set_apply", "安装申请（预激活）");
		nameMap.put("valid_worker", "登录接口");
//		nameMap.put("activate", "请求激活指令接口（旧）");
		nameMap.put("order_submit", "创建订单接口");
		nameMap.put("check_vehicleinfo", "二发车辆信息验证接口");
		nameMap.put("second_issueinstruct", "二发请求指令接口");
		nameMap.put("obu_apply", "激活申请接口");
		nameMap.put("obu_instruct", "激活请求指令接口");
		nameMap.put("userInfoUpload", "中台开户接口");
		nameMap.put("carInfoSave", "中台车辆信息保存接口");
		nameMap.put("carInfoSubmit", "中台车辆信息提交接口");
		nameMap.put("submitObuOrder", "中台订单提交接口");
		nameMap.put("checkVehicleLicense", "中台办理资格校验接口");
		nameMap.put("imageUpload", "中台车头照上传接口");
		nameMap.put("thirdOrderCancel", "中台取消订单接口");
		nameMap.put("edit", "中台修改订单接口");
		nameMap.put("getOrder", "中台通过第三方订单号查询订单接口");
		nameMap.put("orderQuery", "中台查询订单接口");
		nameMap.put("JsonLimitResponseHandler", "限流接口");
		
		
	}
	/**
	 * 发送方法
	 * @param method
	 * @param message
	 */
	public void send(String method,String message){
		//如果未启用，就直接返回
		if(!monitorEnable){
			return;
		}
		int count = 0;
		//对不同的接口做不同的计数
		if(countMap.containsKey(method)){
			count = countMap.get(method)+1;
			countMap.put(method, count);
			//如果小于配置的次数，不做处理，否则就发送
			if(count<alertAfterFailures){
				return;
			}
		}else{
			countMap.put(method, count+1);
			return;
		}
		if(lastTimeMap.containsKey(method)){
			long lastTime = lastTimeMap.get(method);
			if((System.currentTimeMillis() - lastTime)<alertIntervalsMs){
				return;
			}
		}
		//组装需发送的数据
		String content = applicationName +"--"+active+ "," + getMethod(method) + "," + message;
		EtcMonitorRequest request = new EtcMonitorRequest();
		request.setMsgtype("text");
		EtcMonitorRequest.MonitorText monitorText = request.new MonitorText();
		monitorText.setContent(content);
		request.setText(monitorText);
		String response = dingtalkHttpService.doPost(request);
		//发送结束，将结果至为0
		countMap.put(method, 0);
		//存储当前发送时间
		lastTimeMap.put(method, System.currentTimeMillis());
	}
	/**
	 * 将相应接口计数次数至为0
	 * @param name
	 */
	public void refresh(String name){
		countMap.put(name, 0);
	}
	/**
	 * 获取对应中文描述
	 * @param name
	 * @return
	 */
	private String getMethod(String name){
		return nameMap.get(name)+"("+name+")";
	}
}
