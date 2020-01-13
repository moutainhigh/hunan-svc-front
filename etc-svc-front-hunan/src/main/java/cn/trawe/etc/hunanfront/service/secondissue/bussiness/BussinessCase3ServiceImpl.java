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
public class BussinessCase3ServiceImpl  extends BaseBussinessService implements BussinessServiceI{
	
	

	@Override
	public SecondIssueResp doService(SecondIssueReq req) {
		
		LogUtil.info(log,req.getOrderId(), "Case03请求:"+JSON.toJSONString(req));
		boolean analysis =false;
		ApduFlagResponse apdu =null;
		String randomRespApdu =null;
		String randomRespApdu2 =null;
		Map<String, ApduReq> result_info = req.getResultInfo();
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
		//读取0015随机数指令结果解析
		
		//解析完成调用0015获取MAC接口
		RandomInfoApdu random = RandAnalysisService.analysis(randomRespApdu, randomRespApdu2);
		//TODO
		String dataMac = cardWrite(req, "1", random.getRandom(),null,req.getOriginValue());
		//组装0015写卡指令
		
		//组装响应指令报文  cur_step =3
		//获取MAC 成功 组装 写卡指令响应报文
		SecondIssueResp createWrite0015Resp = createWrite0015Resp(dataMac);
		createWrite0015Resp.setCardNo(req.getCardNo());
		createWrite0015Resp.setObuNo(req.getObuNo());
		createWrite0015Resp.setOriginValue(req.getOriginValue());
		LogUtil.info(log,req.getOrderId(), "Case03响应:"+JSON.toJSONString(createWrite0015Resp));
		return createWrite0015Resp;
	}

}
