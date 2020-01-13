package cn.trawe.etc.hunanfront.request;

import lombok.Getter;
import lombok.Setter;

/**
 * 业务接口监控报警请求参数
 * @author guzelin
 */
@Setter
@Getter
public class EtcMonitorRequest{

	private String msgtype;
	private MonitorText text;
	/**
	 * 业务接口监控内部参数
	 * @author guzelin
	 */
	@Setter
	@Getter
	public class MonitorText {
		private String content;
	}
}
