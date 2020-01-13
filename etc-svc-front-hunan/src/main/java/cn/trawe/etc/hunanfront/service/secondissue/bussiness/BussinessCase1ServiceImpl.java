package cn.trawe.etc.hunanfront.service.secondissue.bussiness;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.trawe.etc.hunanfront.enums.InterfaceErrorCode;
import cn.trawe.etc.hunanfront.expose.v2.BaseResponseData;
import cn.trawe.etc.hunanfront.request.secondissue.SecondIssueReq;
import cn.trawe.etc.hunanfront.request.secondissue.SecondIssueResp;
import cn.trawe.etc.hunanfront.service.secondissue.analysis.CardAnalysisService;
import cn.trawe.etc.hunanfront.service.secondissue.analysis.ObuAnalysisService;
import cn.trawe.etc.hunanfront.service.secondissue.analysis.RandAnalysisService;
import cn.trawe.etc.hunanfront.service.secondissue.apdu.ApduFlagResponse;
import cn.trawe.etc.hunanfront.service.secondissue.apdu.RandomInfoApdu;
import cn.trawe.etc.hunanfront.service.secondissue.request.ApduReq;
import cn.trawe.util.LogUtil;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BussinessCase1ServiceImpl  extends BaseBussinessService implements BussinessServiceI{
	
	@Autowired
	private CardAnalysisService  CardAnalysisService;
	
	@Autowired
	private ObuAnalysisService  ObuAnalysisService;
	
	@Autowired
	private RandAnalysisService  	RandAnalysisService;

	@Override
	public SecondIssueResp doService(SecondIssueReq req) {
		LogUtil.info(log,req.getOrderId(), "Case01请求:"+JSON.toJSONString(req));
		Map<String, ApduReq> result_info = req.getResultInfo(); 
		
		//判断
		if(req.getResultInfo().isEmpty()||req.getResultInfo().size()!=7) {
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
		req.setCardNo(apdu.getCardInfo().getCardNetNumber()+apdu.getCardInfo().getCardNumber());
		req.setObuNo(apdu.getObuInfo().getContractNo());
		//选择3F00目录
		randomRespApdu = result_info.get("5").getInner().getCmdValue();
		//读取随机数
		randomRespApdu2 = result_info.get("6").getInner().getCmdValue();
		JSONObject  origin = new JSONObject();
		origin.put("cardInfo",apdu.getCardInfo());
		origin.put("obuInfo",  apdu.getObuInfo());
		req.setOriginValue(origin.toJSONString());
		LogUtil.info(log,req.getOrderId(), "卡信息:"+JSON.toJSONString(apdu.getCardInfo()));
		LogUtil.info(log,req.getOrderId(), "obu信息:"+JSON.toJSONString(apdu.getObuInfo()));
		RandomInfoApdu random = RandAnalysisService.analysis(randomRespApdu, randomRespApdu2);
		//CardWriteResp cardWrite = apiClient.cardWrite(cardWriteReq, token);
		String dataMac = cardWrite(req,"2",random.getRandom(),apdu.getCardInfo(),"");
		//获取MAC 成功 组装 写卡指令响应报文
		SecondIssueResp createWrite0016Resp = createWrite0016Resp(dataMac);
		createWrite0016Resp.setCardNo(apdu.getCardInfo().getCardNetNumber()+apdu.getCardInfo().getCardNumber());
		createWrite0016Resp.setObuNo(apdu.getObuInfo().getContractNo());
		createWrite0016Resp.setOriginValue(origin.toJSONString());
		LogUtil.info(log,req.getOrderId(), "Case01响应:"+JSON.toJSONString(createWrite0016Resp));
		return createWrite0016Resp;
	}

}
