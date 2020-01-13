package cn.trawe.etc.hunanfront.service.secondissue.bussiness;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.trawe.etc.hunanfront.entity.ThirdPartner;
import cn.trawe.etc.hunanfront.enums.InterfaceErrorCode;
import cn.trawe.etc.hunanfront.expose.v2.BaseResponseData;
import cn.trawe.etc.hunanfront.request.secondissue.SecondIssueReq;
import cn.trawe.etc.hunanfront.request.secondissue.SecondIssueResp;
import cn.trawe.etc.hunanfront.service.BaseService;
import cn.trawe.etc.hunanfront.service.secondissue.apdu.ApduFlagResponse;
import cn.trawe.etc.hunanfront.service.secondissue.response.ApduCardInner;
import cn.trawe.etc.hunanfront.service.secondissue.response.ApduCardResp;
import cn.trawe.etc.hunanfront.service.secondissue.response.ApduObuInner;
import cn.trawe.etc.hunanfront.service.secondissue.response.ApduObuResp;
import cn.trawe.pay.expose.entity.EtcIssueOrder;
import cn.trawe.pay.expose.request.secondissue.ActivationQueryReq;
import cn.trawe.pay.expose.request.secondissue.CardActionReq;
import cn.trawe.pay.expose.response.secondissue.ActivationQueryResp;
import cn.trawe.pay.expose.response.secondissue.CardActionResp;
import cn.trawe.util.LogUtil;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BaseBussinessApduType3 extends BaseService {

	
	public SecondIssueResp doService(SecondIssueReq secondReq,ThirdPartner partner) {
		SecondIssueResp secondResp = new SecondIssueResp();
		//查询订单号
      	//EtcIssueOrder order = getOrder("",secondReq.getOrderId());
      	//secondReq.setOrderId(order.getOrderNo());
		
		
		
		switch (secondReq.getCurStep()) {
		    
		    
			
			case 1:
			{
				//解析卡号
				 
				//解析OBU号
				
				//0016响应指令开始解析
				
				//解析完成调用0016获取MAC接口
				
				
				//获取MAC 成功 组装 写卡指令响应报文
				
				//cur_step =1
				init(secondReq,partner);
				
				
				secondResp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
				secondResp.setCurStep(1);
				secondResp.setTotalStep(1);
				secondResp.setErrorCode(InterfaceErrorCode.RESULT_OK.getValue());
				secondResp.setErrorMsg(InterfaceErrorCode.RESULT_OK_INFO.getValue());
				
				LogUtil.info(log,secondReq.getOrderId(), "跳过卡、跳过OBU成功");
				return secondResp;
				
				
			}
			
			
			default :{
				LogUtil.info(log, secondReq.getOrderId(), "开始查询激活记录"+JSON.toJSONString(secondReq));
				ActivationQueryReq actReq = new ActivationQueryReq();
				actReq.setKind("0");
				actReq.setOrderNo(secondReq.getOrderId());
				actReq.setOwnerCode("4301");
				LogUtil.info(log, secondReq.getOrderId(), "查询激活记录请求 :"+JSON.toJSONString(actReq));
				ActivationQueryResp activationQuery = IssueCenterApi.activationQuery(actReq);
				
				LogUtil.info(log, secondReq.getOrderId(), "查询激活记录响应 :"+JSON.toJSONString(activationQuery));
				if(activationQuery.getCode()!=0) {
					secondResp.setSuccess(BaseResponseData.Success.FAILED.toString());
					secondResp.setErrorCode(InterfaceErrorCode.SYSTEM_ERROR.getValue());
					secondResp.setErrorMsg("查询异常: 异常信息 :" +JSON.toJSONString(activationQuery));
					return secondResp;
				}
				

					secondResp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
					secondResp.setCurStep(0);
					secondResp.setTotalStep(TOTAL_STEP);
					secondResp.setErrorCode(InterfaceErrorCode.RESULT_OK.getValue());
					secondResp.setErrorMsg(InterfaceErrorCode.RESULT_OK_INFO.getValue());
					Map<String, Object> apduList = CreateApduHeader.createHeaderReadObuAndCard();
					
					secondResp.setRespInfo(apduList);
					secondResp.setApduFlag("1");
					LogUtil.info(log, secondReq.getOrderId(), "组装指令: " +JSON.toJSONString(secondResp));
					
			     
			}

		}
		return secondResp;
	}

	private void init(SecondIssueReq secondReq, ThirdPartner partner) {
		
		
		LogUtil.info(log, secondReq.getOrderId(), "开始查询激活记录"+JSON.toJSONString(secondReq));
		ActivationQueryReq actReq = new ActivationQueryReq();
		actReq.setKind("0");
		actReq.setOrderNo(secondReq.getOrderId());
		actReq.setOwnerCode("4301");
		LogUtil.info(log, secondReq.getOrderId(), "查询激活记录请求 :"+JSON.toJSONString(actReq));
		ActivationQueryResp activationQuery = IssueCenterApi.activationQuery(actReq);
		ApduFlagResponse apdu =null;
		if(secondReq.getResultInfo().isEmpty()||secondReq.getResultInfo().size()!=5) {
			SecondIssueResp  resp  = new SecondIssueResp();
			resp.setErrorCode(InterfaceErrorCode.SYSTEM_ERROR.getValue());
			resp.setErrorMsg("读取OBU设备无响应");
			resp.setSuccess(BaseResponseData.Success.FAILED.toString());
			throw new RuntimeException("读取OBU设备无响应");
		}
		apdu = ApduHeadAnalysisService.doService(secondReq);
		JSONObject  origin = new JSONObject();
		origin.put("cardInfo",apdu.getCardInfo());
		origin.put("obuInfo",  apdu.getObuInfo());
		secondReq.setOriginValue(origin.toJSONString());
		secondReq.setCardNo(apdu.getCardInfo().getCardNetNumber()+apdu.getCardInfo().getCardNumber());
		secondReq.setObuNo(apdu.getObuInfo().getContractNo());
		if (activationQuery.getCardStatus().equals("0")  ||activationQuery.getCardStatus().equals("4")) {
			LogUtil.info(log, secondReq.getOrderId(), "开始鸡肋操作模拟获取0016");
			//获取0016
			String dataMac = cardWrite(secondReq,"2","42B6A67D",apdu.getCardInfo(),"");
			LogUtil.info(log, secondReq.getOrderId(), "开始鸡肋操作模拟获取0016成功"+dataMac);
			//0016上报成功
			cardAction(secondReq,"true",2,"0");
			//获取0015
			String dataMac0015 = cardWrite(secondReq, "1", "42B6A67D",null,secondReq.getOriginValue());
			LogUtil.info(log, secondReq.getOrderId(), "开始鸡肋操作模拟获取0015成功"+dataMac0015);
			//上报成功
			cardAction(secondReq,"true",1,"0");
			LogUtil.info(log, secondReq.getOrderId(), "开始鸡肋操作模拟获取写卡上报成功"+dataMac0015);
			
			LogUtil.info(log, secondReq.getOrderId(), "开始鸡肋操作模拟获取写车辆");
			String obuWrite = obuWrite(secondReq,"1","42B6A67D");
			LogUtil.info(log, secondReq.getOrderId(), "开始鸡肋操作模拟获取写车辆成功"+obuWrite);
			LogUtil.info(log, secondReq.getOrderId(), "开始鸡肋操作模拟获取写车辆上报成功");
			tagAction(secondReq,"1","true","0");
			LogUtil.info(log, secondReq.getOrderId(), "开始鸡肋操作模拟获取写系统");
			String obuWriteSys = obuWrite(secondReq,"2","42B6A67D");
			LogUtil.info(log, secondReq.getOrderId(), "开始鸡肋操作模拟获取写系统成功"+obuWriteSys);
			
			tagAction(secondReq,"2","true","0");
			LogUtil.info(log, secondReq.getOrderId(), "开始鸡肋操作模拟获取写系统上报成功");
			cardAction(secondReq, "true", 3,"0");
	     }
		
		 if (activationQuery.getCardStatus().equals("2")  ||activationQuery.getCardStatus().equals("3")) {
			
			//获取0015
			String dataMac0015 = cardWrite(secondReq, "1", "42B6A67D",null,secondReq.getOriginValue());
			LogUtil.info(log, secondReq.getOrderId(), "开始鸡肋操作模拟获取0015成功"+dataMac0015);
			//上报成功
			cardAction(secondReq,"true",1,"0");
			LogUtil.info(log, secondReq.getOrderId(), "开始鸡肋操作模拟获取写卡上报成功"+dataMac0015);
			LogUtil.info(log, secondReq.getOrderId(), "开始鸡肋操作模拟获取写车辆");
			String obuWrite = obuWrite(secondReq,"1","42B6A67D");
			LogUtil.info(log, secondReq.getOrderId(), "开始鸡肋操作模拟获取写车辆成功"+obuWrite);
			LogUtil.info(log, secondReq.getOrderId(), "开始鸡肋操作模拟获取写车辆上报成功");
			tagAction(secondReq,"1","true","0");
			LogUtil.info(log, secondReq.getOrderId(), "开始鸡肋操作模拟获取写系统");
			String obuWriteSys = obuWrite(secondReq,"2","42B6A67D");
			LogUtil.info(log, secondReq.getOrderId(), "开始鸡肋操作模拟获取写系统成功"+obuWriteSys);
			
			tagAction(secondReq,"2","true","0");
			LogUtil.info(log, secondReq.getOrderId(), "开始鸡肋操作模拟获取写系统上报成功");
			cardAction(secondReq, "true", 3,"0");
	      }
		 
		 if (activationQuery.getVehicleStatus().equals("0")  ||activationQuery.getVehicleStatus().equals("2")) {
			 LogUtil.info(log, secondReq.getOrderId(), "开始鸡肋操作模拟获取写车辆");
				String obuWrite = obuWrite(secondReq,"1","42B6A67D");
				LogUtil.info(log, secondReq.getOrderId(), "开始鸡肋操作模拟获取写车辆成功"+obuWrite);
				LogUtil.info(log, secondReq.getOrderId(), "开始鸡肋操作模拟获取写车辆上报成功");
				tagAction(secondReq,"1","true","0");
				LogUtil.info(log, secondReq.getOrderId(), "开始鸡肋操作模拟获取写系统");
				String obuWriteSys = obuWrite(secondReq,"2","42B6A67D");
				LogUtil.info(log, secondReq.getOrderId(), "开始鸡肋操作模拟获取写系统成功"+obuWriteSys);
				
				tagAction(secondReq,"2","true","0");
				LogUtil.info(log, secondReq.getOrderId(), "开始鸡肋操作模拟获取写系统上报成功");
				cardAction(secondReq, "true", 3,"0");
		 }
		 
		 if (activationQuery.getSystemStatus().equals("0")  ||activationQuery.getSystemStatus().equals("2")) {
			 LogUtil.info(log, secondReq.getOrderId(), "开始鸡肋操作模拟获取写系统");
				String obuWriteSys = obuWrite(secondReq,"2","42B6A67D");
				LogUtil.info(log, secondReq.getOrderId(), "开始鸡肋操作模拟获取写系统成功"+obuWriteSys);
				
				tagAction(secondReq,"2","true","0");
				LogUtil.info(log, secondReq.getOrderId(), "开始鸡肋操作模拟获取写系统上报成功");
				cardAction(secondReq, "true", 3,"0");
	     }
		
	}

}
