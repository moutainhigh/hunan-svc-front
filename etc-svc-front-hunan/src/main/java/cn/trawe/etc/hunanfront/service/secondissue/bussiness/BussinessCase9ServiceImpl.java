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
import cn.trawe.etc.hunanfront.service.secondissue.request.ApduReq;
import cn.trawe.util.LogUtil;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BussinessCase9ServiceImpl  extends BaseBussinessService implements BussinessServiceI{
	
	

	@Override
	public SecondIssueResp doService(SecondIssueReq req) {
		
		LogUtil.info(log,req.getOrderId(), "Case09请求:"+JSON.toJSONString(req));

		Map<String, ApduReq> result_info = req.getResultInfo(); 
		//解析激活指令

		if(req.getResultInfo().isEmpty()||req.getResultInfo().size()!=5) {
			SecondIssueResp  resp  = new SecondIssueResp();
			resp.setErrorCode(InterfaceErrorCode.SYSTEM_ERROR.getValue());
			resp.setErrorMsg("读取OBU设备无响应");
			resp.setSuccess(BaseResponseData.Success.FAILED.toString());
			return resp;
		}
		ApduFlagResponse apdu =null;
		String randomRespApdu =null;
		String randomRespApdu2 =null;
		apdu = ApduHeadAnalysisService.doService(req);
		//判断防拆位
		if(!apdu.getObuInfo().getDisassembleStatus().equals("00")) {
			SecondIssueResp  resp  = new SecondIssueResp();
			resp.setErrorCode(InterfaceErrorCode.SYSTEM_ERROR.getValue());
			resp.setErrorMsg("防拆位未拆卸,不允许重新激活");
			resp.setSuccess(BaseResponseData.Success.FAILED.toString());
			return resp;
		}
		req.setCardNo(apdu.getCardInfo().getCardNetNumber()+apdu.getCardInfo().getCardNumber());
		req.setObuNo(apdu.getObuInfo().getContractNo());
//		//选择DF01目录
//		randomRespApdu = result_info.get("5").getInner().getCmdValue();
//		//读取随机数
//		randomRespApdu2 = result_info.get("6").getInner().getCmdValue();
		
		JSONObject  origin = new JSONObject();
		origin.put("cardInfo",apdu.getCardInfo());
		origin.put("obuInfo",  apdu.getObuInfo());
		req.setOriginValue(origin.toJSONString());
		LogUtil.info(log,req.getOrderId(), "卡信息:"+JSON.toJSONString(apdu.getCardInfo()));
		LogUtil.info(log,req.getOrderId(), "obu信息:"+JSON.toJSONString(apdu.getObuInfo()));
		//RandomInfoApdu random = RandAnalysisService.analysis(randomRespApdu, randomRespApdu2);
		
        req.setCardNo(apdu.getCardInfo().getCardNetNumber()+apdu.getCardInfo().getCardNumber());
        req.setObuNo(apdu.getObuInfo().getContractNo());
    	SecondIssueResp secondResp = createWriteActiveRandomResp( apdu.getObuInfo().getContractNo());
        //String dataMac = activationCheck(req,"",random.getRandom(),"");
		//组装激活写卡指令  cur_step =9
	
        secondResp.setCardNo(req.getCardNo());
        secondResp.setObuNo(req.getObuNo());
        origin.put("encrypt_random",  apdu.getObuInfo().getContractNo());
        secondResp.setOriginValue(origin.toJSONString());

		LogUtil.info(log,req.getOrderId(), "Case09响应:"+JSON.toJSONString(secondResp));
		return secondResp;
	}

}
