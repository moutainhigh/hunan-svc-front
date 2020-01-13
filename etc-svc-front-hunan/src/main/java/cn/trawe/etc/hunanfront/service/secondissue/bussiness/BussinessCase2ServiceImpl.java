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
public class BussinessCase2ServiceImpl  extends BaseBussinessService implements BussinessServiceI{
	
	

	@Override
	public SecondIssueResp doService(SecondIssueReq req) {
		
		LogUtil.info(log,req.getOrderId(), "Case02请求:"+JSON.toJSONString(req));
		if(req.getResultInfo().isEmpty()||req.getResultInfo().size()!=1) {
			SecondIssueResp  resp  = new SecondIssueResp();
			resp.setErrorCode(InterfaceErrorCode.SYSTEM_ERROR.getValue());
			resp.setErrorMsg("读取OBU设备无响应");
			resp.setSuccess(BaseResponseData.Success.FAILED.toString());
			return resp;
		}
		boolean analysis =false;
		//写0016指令结果解析
		analysis = Sw1Sw2AnalysisService.analysis(req,req.getResultInfo().get("0").getInner().getCmdValue());
		if(analysis) {
			cardAction(req,"true",2,"0");
		}
		else {
			//是否上报失败
			cardAction(req,"false",2,"0");
			if("6988".equals(req.getResultInfo().get("0").getInner().getCmdValue())) {
				cardAction(req, "6988",2,"0");
				throw new AnalySisException("卡片激活状态码:6988,请线下营业厅处理");
			}
			else {
				throw new AnalySisException("激活失败,状态码:"+req.getResultInfo().get("0").getInner().getCmdValue());
			}
			
			
		}
		
		//成功上报0016写成功
		//组装0015读取随机数指令 cur_step =2 
		SecondIssueResp createRead0015RandomResp = createRead0015RandomResp();
		createRead0015RandomResp.setCardNo(req.getCardNo());
		createRead0015RandomResp.setObuNo(req.getObuNo());
		createRead0015RandomResp.setOriginValue(req.getOriginValue());
		LogUtil.info(log,req.getOrderId(), "Case02响应:"+JSON.toJSONString(createRead0015RandomResp));
		return createRead0015RandomResp;
	}

}
