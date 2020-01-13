package cn.trawe.etc.hunanfront.service.secondissue.bussiness;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.trawe.etc.hunanfront.enums.InterfaceErrorCode;
import cn.trawe.etc.hunanfront.expose.v2.BaseResponseData;
import cn.trawe.etc.hunanfront.request.secondissue.SecondIssueReq;
import cn.trawe.etc.hunanfront.request.secondissue.SecondIssueResp;
import cn.trawe.etc.hunanfront.service.secondissue.apdu.ApduFlagResponse;
import cn.trawe.etc.hunanfront.service.secondissue.apdu.RandomInfoApdu;
import cn.trawe.etc.hunanfront.service.secondissue.request.ApduReq;
import cn.trawe.util.LogUtil;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BussinessCase7ServiceImpl  extends BaseBussinessService implements BussinessServiceI{
	
	

	@Override
	public SecondIssueResp doService(SecondIssueReq req) {
		
		LogUtil.info(log,req.getOrderId(), "Case07请求:"+JSON.toJSONString(req));

		
		//解析读取车辆系统随机数信息指令
		Map<String, ApduReq> result_info = req.getResultInfo();
		ApduFlagResponse apdu =null;
		String randomRespApdu =null;
		String randomRespApdu2 =null;
		if(APDU_FLAG_TRUE.equals(req.getApduFlag())) {
			if(req.getResultInfo().isEmpty()||req.getResultInfo().size()!=7) {
				SecondIssueResp  resp  = new SecondIssueResp();
				resp.setErrorCode(InterfaceErrorCode.SYSTEM_ERROR.getValue());
				resp.setErrorMsg("读取OBU设备无响应");
				resp.setSuccess(BaseResponseData.Success.FAILED.toString());
				return resp;
			}
			apdu = ApduHeadAnalysisService.doService(req);
			req.setCardNo(apdu.getCardInfo().getCardNetNumber()+apdu.getCardInfo().getCardNumber());
			req.setObuNo(apdu.getObuInfo().getContractNo());
			//选择1001目录
			randomRespApdu = result_info.get("5").getInner().getCmdValue();
			//读取随机数
			randomRespApdu2 = result_info.get("6").getInner().getCmdValue();
			JSONObject  origin = new JSONObject();
			origin.put("cardInfo",apdu.getCardInfo());
			origin.put("obuInfo",  apdu.getObuInfo());
			req.setOriginValue(origin.toJSONString());
		}
		else {
			if(req.getResultInfo().isEmpty()||req.getResultInfo().size()!=2) {
				SecondIssueResp  resp  = new SecondIssueResp();
				resp.setErrorCode(InterfaceErrorCode.SYSTEM_ERROR.getValue());
				resp.setErrorMsg("读取OBU设备无响应");
				resp.setSuccess(BaseResponseData.Success.FAILED.toString());
				return resp;
			}
			randomRespApdu = result_info.get("0").getInner().getCmdValue();
			randomRespApdu2 = result_info.get("1").getInner().getCmdValue();
		}

		//解析完成调用写车辆信息获取MAC接口
		RandomInfoApdu random = RandAnalysisService.analysis(randomRespApdu, randomRespApdu2);
		String obuWrite = obuWrite(req,"2",random.getRandom());
		//成功获取写系统信息MAC cur_step =7
		
		SecondIssueResp createWriteSystemResp = createWriteSystemResp(obuWrite);
		createWriteSystemResp.setCardNo(req.getCardNo());
		createWriteSystemResp.setObuNo(req.getObuNo());
		createWriteSystemResp.setOriginValue(req.getOriginValue());
		LogUtil.info(log,req.getOrderId(), "Case07响应:"+JSON.toJSONString(createWriteSystemResp));
		return createWriteSystemResp;
	}

}
