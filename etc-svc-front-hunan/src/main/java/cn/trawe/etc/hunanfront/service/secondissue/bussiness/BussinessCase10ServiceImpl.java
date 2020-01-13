package cn.trawe.etc.hunanfront.service.secondissue.bussiness;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import cn.trawe.etc.hunanfront.enums.InterfaceErrorCode;
import cn.trawe.etc.hunanfront.expose.v2.BaseResponseData;
import cn.trawe.etc.hunanfront.request.secondissue.SecondIssueReq;
import cn.trawe.etc.hunanfront.request.secondissue.SecondIssueResp;
import cn.trawe.etc.hunanfront.service.secondissue.apdu.RandomInfoApdu;
import cn.trawe.etc.hunanfront.service.secondissue.request.ApduReq;
import cn.trawe.util.LogUtil;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BussinessCase10ServiceImpl  extends BaseBussinessService implements BussinessServiceI{
	
	

	@Override
	public SecondIssueResp doService(SecondIssueReq req) {
		
		LogUtil.info(log,req.getOrderId(), "Case10请求:"+JSON.toJSONString(req));
		Map<String, ApduReq> result_info = req.getResultInfo(); 
		if(req.getResultInfo().isEmpty()||req.getResultInfo().size()!=4) {
			SecondIssueResp  resp  = new SecondIssueResp();
			resp.setErrorCode(InterfaceErrorCode.SYSTEM_ERROR.getValue());
			resp.setErrorMsg("读取OBU设备无响应");
			resp.setSuccess(BaseResponseData.Success.FAILED.toString());
			return resp;
		}
		//解析车辆密文
		//解析随机数
		String DF01 = result_info.get("0").getInner().getCmdValue();
		String encrypt = result_info.get("1").getInner().getCmdValue().substring(0, 144);
		LogUtil.info(log,req.getOrderId(), "车辆密文:"+encrypt);
		//res.data = res.data[1].substr(res.data[1].length - 148, 144);
		String randomRespApdu = result_info.get("2").getInner().getCmdValue();
		String randomRespApdu2 = result_info.get("3").getInner().getCmdValue();
		RandomInfoApdu random = RandAnalysisService.analysis(randomRespApdu, randomRespApdu2);
		
		//获取激活指令
		String activationCheck = activationCheck(req,"1",random.getRandom(),encrypt);
		SecondIssueResp secondResp = createWriteAcitveMacResp(activationCheck);
		secondResp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
		secondResp.setErrorCode(InterfaceErrorCode.RESULT_OK.getValue());
		secondResp.setErrorMsg(InterfaceErrorCode.RESULT_OK_INFO.getValue());
		secondResp.setCardNo(req.getCardNo());
		secondResp.setObuNo(req.getObuNo());
		secondResp.setOriginValue(req.getOriginValue());


		LogUtil.info(log,req.getOrderId(), "Case10响应:"+JSON.toJSONString(secondResp));
		return secondResp;
	}
	


}
