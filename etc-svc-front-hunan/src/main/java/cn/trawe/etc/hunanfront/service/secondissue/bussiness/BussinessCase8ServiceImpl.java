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
public class BussinessCase8ServiceImpl  extends BaseBussinessService implements BussinessServiceI{
	
	

	@Override
	public SecondIssueResp doService(SecondIssueReq req) {
		
		LogUtil.info(log,req.getOrderId(), "Case08请求:"+JSON.toJSONString(req));
		if(req.getResultInfo().isEmpty()||req.getResultInfo().size()!=1) {
			SecondIssueResp  resp  = new SecondIssueResp();
			resp.setErrorCode(InterfaceErrorCode.SYSTEM_ERROR.getValue());
			resp.setErrorMsg("读取OBU设备无响应");
			resp.setSuccess(BaseResponseData.Success.FAILED.toString());
			return resp;
		}
		
		//解析写车辆系统MAC指令
		boolean analysis =false;
		analysis = Sw1Sw2AnalysisService.analysis(req,req.getResultInfo().get("0").getInner().getCmdValue());
		if(analysis) {
			//解析卡号ObuInfoApdu
			tagAction(req,"2","true","0");
			////上报全流程写完
			//直接响应成功
			cardAction(req, "true", 3,"0");
			
		}else {
			//是否上报失败
			tagAction(req,"2","false","0");
			if("6988".equals(req.getResultInfo().get("0").getInner().getCmdValue())) {
				tagAction(req,"2","6988","0");
				throw new AnalySisException("OBU激活状态码:6988,请线下营业厅处理");
			}
			else {
				throw new AnalySisException("激活失败,状态码:"+req.getResultInfo().get("0").getInner().getCmdValue());
			}
			
			

		}
		//成功上报写成功
		
		//组装激活随机数指令  cur_step =8
		//组装读取拆迁位指令
		//因湖南新接口这一步结束调
		//直接响应成功
		SecondIssueResp secondResp = new SecondIssueResp();
		
		secondResp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
		secondResp.setCurStep(TOTAL_STEP);
		secondResp.setTotalStep(TOTAL_STEP);
		secondResp.setErrorCode(InterfaceErrorCode.RESULT_OK.getValue());
		secondResp.setErrorMsg(InterfaceErrorCode.RESULT_OK_INFO.getValue());
		secondResp.setCardNo(req.getCardNo());
		secondResp.setObuNo(req.getObuNo());
		secondResp.setOriginValue(req.getOriginValue());
		LogUtil.info(log,req.getOrderId(), "Case08响应:"+JSON.toJSONString(secondResp));
		return secondResp;
	}

}
