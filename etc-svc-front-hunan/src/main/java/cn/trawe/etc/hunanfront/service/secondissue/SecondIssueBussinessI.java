package cn.trawe.etc.hunanfront.service.secondissue;

import cn.trawe.etc.hunanfront.expose.v2.BaseRequest;
import cn.trawe.etc.hunanfront.expose.v2.BaseResponse;

/**
  * 二发业务控制类
  * 不同省份不同实现控制
 * @author jianjun.chai
 *
 */
public interface SecondIssueBussinessI {
	
	public BaseResponse autoIssue(BaseRequest request);

}
