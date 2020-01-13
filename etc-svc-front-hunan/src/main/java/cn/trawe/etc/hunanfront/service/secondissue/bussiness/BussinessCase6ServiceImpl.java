package cn.trawe.etc.hunanfront.service.secondissue.bussiness;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import cn.trawe.etc.hunanfront.enums.InterfaceErrorCode;
import cn.trawe.etc.hunanfront.exception.AnalySisException;
import cn.trawe.etc.hunanfront.expose.v2.BaseResponseData;
import cn.trawe.etc.hunanfront.request.secondissue.SecondIssueReq;
import cn.trawe.etc.hunanfront.request.secondissue.SecondIssueResp;
import cn.trawe.util.LogUtil;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BussinessCase6ServiceImpl  extends BaseBussinessService implements BussinessServiceI{
	
	

	@Override
	public SecondIssueResp doService(SecondIssueReq req) {
		
		LogUtil.info(log,req.getOrderId(), "Case06请求:"+JSON.toJSONString(req));
		if(req.getResultInfo().isEmpty()||req.getResultInfo().size()!=1) {
			SecondIssueResp  resp  = new SecondIssueResp();
			resp.setErrorCode(InterfaceErrorCode.SYSTEM_ERROR.getValue());
			resp.setErrorMsg("读取OBU设备无响应");
			resp.setSuccess(BaseResponseData.Success.FAILED.toString());
			return resp;
		}
		//解析写车辆信息指令报文
		//成功上报写成功
		//组装读取系统信息随机数 cur_step =6
		boolean analysis =false;
		analysis = Sw1Sw2AnalysisService.analysis(req,req.getResultInfo().get("0").getInner().getCmdValue());
		if(analysis) {
			tagAction(req,"1","true","0");
		}
		else {
			//是否上报失败
			tagAction(req,"1","false","0");
			if("6988".equals(req.getResultInfo().get("0").getInner().getCmdValue())) {
				tagAction(req,"1","6988","0");
				throw new AnalySisException("OBU激活状态码:6988,请线下营业厅处理");
				
			}
			else {
				throw new AnalySisException("激活失败,状态码:"+req.getResultInfo().get("0").getInner().getCmdValue());
			}
			
			
		}
		SecondIssueResp createReadSystemRandomResp = createReadSystemRandomResp();
		createReadSystemRandomResp.setCardNo(req.getCardNo());
		createReadSystemRandomResp.setObuNo(req.getObuNo());
		createReadSystemRandomResp.setOriginValue(req.getOriginValue());
		LogUtil.info(log,req.getOrderId(), "Case06响应:"+JSON.toJSONString(createReadSystemRandomResp));
		return createReadSystemRandomResp;
	}

}
