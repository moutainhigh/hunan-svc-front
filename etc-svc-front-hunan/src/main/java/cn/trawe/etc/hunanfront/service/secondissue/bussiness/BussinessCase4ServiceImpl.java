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
public class BussinessCase4ServiceImpl  extends BaseBussinessService implements BussinessServiceI{
	
	

	@Override
	public SecondIssueResp doService(SecondIssueReq req) {
		
		LogUtil.info(log,req.getOrderId(), "Case04请求:"+JSON.toJSONString(req));
		if(req.getResultInfo().isEmpty()||req.getResultInfo().size()!=1) {
			SecondIssueResp  resp  = new SecondIssueResp();
			resp.setErrorCode(InterfaceErrorCode.SYSTEM_ERROR.getValue());
			resp.setErrorMsg("读取OBU设备无响应");
			resp.setSuccess(BaseResponseData.Success.FAILED.toString());
			return resp;
		}
		boolean analysis =false;
	    analysis = Sw1Sw2AnalysisService.analysis(req,req.getResultInfo().get("0").getInner().getCmdValue());
		//写卡0015指令解析
		if(analysis) {
			cardAction(req,"true",1,"0");
		}
		else {
			//是否上报失败
			cardAction(req,"false",1,"0");
			if("6988".equals(req.getResultInfo().get("0").getInner().getCmdValue())) {
				cardAction(req,"6988",1,"0");
				throw new AnalySisException("卡片激活状态码:6988,请线下营业厅处理");
			}
			else {
				throw new AnalySisException("激活失败,状态码:"+req.getResultInfo().get("0").getInner().getCmdValue());
			}
			
			

		}
		SecondIssueResp createWriteVehicleResp = createReadVehicleRandomResp();
		createWriteVehicleResp.setCardNo(req.getCardNo());
		createWriteVehicleResp.setObuNo(req.getObuNo());
		createWriteVehicleResp.setOriginValue(req.getOriginValue());
		//组装读取车辆随机数指令  cur_step =4
		LogUtil.info(log,req.getOrderId(), "Case04响应:"+JSON.toJSONString(createWriteVehicleResp));
		return createWriteVehicleResp;
	}

}
