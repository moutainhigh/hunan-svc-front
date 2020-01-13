package cn.trawe.etc.hunanfront.service.secondissue.bussiness;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import cn.trawe.etc.hunanfront.enums.InterfaceErrorCode;
import cn.trawe.etc.hunanfront.exception.AnalySisException;
import cn.trawe.etc.hunanfront.expose.v2.BaseResponseData;
import cn.trawe.etc.hunanfront.request.secondissue.SecondIssueReq;
import cn.trawe.etc.hunanfront.request.secondissue.SecondIssueResp;
import cn.trawe.etc.hunanfront.service.secondissue.response.ApduCardInner;
import cn.trawe.etc.hunanfront.service.secondissue.response.ApduCardResp;
import cn.trawe.util.LogUtil;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BussinessCase11ServiceImpl  extends BaseBussinessService implements BussinessServiceI{
	
	

	@Override
	public SecondIssueResp doService(SecondIssueReq req) {
		
		LogUtil.info(log,req.getOrderId(), "Case11请求:"+JSON.toJSONString(req));
		if(req.getResultInfo().isEmpty()||req.getResultInfo().size()!=1) {
			SecondIssueResp  resp  = new SecondIssueResp();
			resp.setErrorCode(InterfaceErrorCode.SYSTEM_ERROR.getValue());
			resp.setErrorMsg("读取OBU设备无响应");
			resp.setSuccess(BaseResponseData.Success.FAILED.toString());
			return resp;
		}
		
		//解析激活指令
		boolean analysis = Sw1Sw2AnalysisService.analysis(req,req.getResultInfo().get("0").getInner().getCmdValue());
		//成功上报激活成功
		if(analysis) {
		
			activationAction(req,"",1);
			//增加流程上报全部写完
			//直接响应成功
			cardAction(req, "9999", 3,"1");
		}
		else {
			//是否上报失败
			activationAction(req,"",2);
			if("6988".equals(req.getResultInfo().get("0").getInner().getCmdValue())) {
				
				throw new AnalySisException("OBU激活状态码:6988,请线下营业厅处理");
			}
			else {
				throw new AnalySisException("激活失败,状态码:"+req.getResultInfo().get("0").getInner().getCmdValue());	
			}
			
			
			

		}
		
				
		SecondIssueResp secondResp = new SecondIssueResp();
		secondResp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
		secondResp.setCurStep(3);
		secondResp.setTotalStep(3);
		secondResp.setErrorCode(InterfaceErrorCode.RESULT_OK.getValue());
		secondResp.setErrorMsg(InterfaceErrorCode.RESULT_OK_INFO.getValue());
		secondResp.setCardNo(req.getCardNo());
		secondResp.setObuNo(req.getObuNo());
		secondResp.setOriginValue(req.getOriginValue());


		LogUtil.info(log,req.getOrderId(), "Case11响应:"+JSON.toJSONString(secondResp));
		return secondResp;
	}
	
	public static void main(String[] args) {
		Map<String,Object> resultInfo = new HashMap<String,Object>();
		ApduCardResp resp = new ApduCardResp(new ApduCardInner("00A40000023F00"));
		resultInfo.put("0", resp);
		System.out.println(JSON.toJSONString(resultInfo));
		
		Map<String,Object> reqInfo = new HashMap<String,Object>();
		reqInfo.put("0", "{\"0\":{\"inner\":{\"cmdType\":1,\"cmdValue\":\"00A40000023F00\"}}}\r\n" + 
				"");
	}

}
