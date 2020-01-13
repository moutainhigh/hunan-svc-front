package cn.trawe.etc.hunanfront.service.secondissue.bussiness;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

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
public class BaseBussinessApduType2 extends BaseService {

	
	public SecondIssueResp doService(SecondIssueReq secondReq,ThirdPartner partner) {
		SecondIssueResp secondResp = new SecondIssueResp();
		//查询订单号
      	//EtcIssueOrder order = getOrder("",secondReq.getOrderId());
      	//secondReq.setOrderId(order.getOrderNo());
		
		
		
		switch (secondReq.getCurStep()) {
		    
			    
			
			   
			case 5:
			{
				
				//解析读取车辆随机数指令
				
				//成功获取写车辆信息指令
				
				//组装写车辆信息指令报文 cur_step =5
				init(secondReq,partner);
				secondResp = case5.doService(secondReq);
				break;
			}
			case 6:
			{
				//解析写车辆信息指令报文
				
				//成功上报写成功
				
				//组装读取系统信息随机数 cur_step =6
				secondResp = case6.doService(secondReq);
				break;
			}
			case 7:
			{
				//解析读取车辆系统随机数信息指令
				
				//成功获取写系统信息MAC cur_step =7
				secondResp = case7.doService(secondReq);
				break;
				
			}
			case 8:
			{
				//解析写车辆系统信息指令
				
				//成功上报写成功
				
				//组装激活随机数指令  cur_step =8
				//组装读取拆迁位指令
				secondResp = case8.doService(secondReq);
				break;
		
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
				
				//秘钥不对
				
				//发行成功
				if (activationQuery.getFinishStatus().equals("1")) {
			        //直接返回total_step 10 cur_step 10
					LogUtil.info(log, secondReq.getOrderId(), "查询结果为finish_status 1");
					secondResp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
					secondResp.setCardNo(activationQuery.getCardNo());
		            secondResp.setObuNo(activationQuery.getObuNo());    
					secondResp.setCurStep(TOTAL_STEP);
					secondResp.setTotalStep(TOTAL_STEP);
					secondResp.setErrorCode(InterfaceErrorCode.RESULT_OK.getValue());
					secondResp.setErrorMsg(InterfaceErrorCode.RESULT_OK_INFO.getValue());
					return secondResp;  			     }

				
				
				if (activationQuery.getVehicleStatus().equals("0")  ||activationQuery.getVehicleStatus().equals("2")) {
					// 车辆为初始或者写失败
					//0   : cmdType:0 cmdValue:00A4000002DF01   选择目录
					//1   : cmdType:0 cmdValue:0084000004  // 读取随机数 
					//拼接写指令参数返回 
					//cur_step 4 total_step 10
					 LogUtil.info(log, secondReq.getOrderId(), "vehicle_status : 0 || vehicle_status : 2 ");
					 secondResp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
					secondResp.setCurStep(4);
					secondResp.setTotalStep(TOTAL_STEP);
					secondResp.setErrorCode(InterfaceErrorCode.RESULT_OK.getValue());
					secondResp.setErrorMsg(InterfaceErrorCode.RESULT_OK_INFO.getValue());
					Map<String, Object> apduList = CreateApduHeader.createHeaderReadObuAndCard();
					ApduObuResp obuRespChoose = new ApduObuResp();
					ApduObuInner innerChoose = new ApduObuInner ("00A4000002DF01");
					obuRespChoose.setInner(innerChoose);
					ApduObuResp obuRespVeh = new ApduObuResp();
					ApduObuInner innerVeh = new ApduObuInner ("0084000004");
					obuRespVeh.setInner(innerVeh);
					apduList.put("5",obuRespChoose);
					apduList.put("6",obuRespVeh);
					secondResp.setRespInfo(apduList);
					secondResp.setApduFlag("1");
					LogUtil.info(log, secondReq.getOrderId(), "组装指令: " +JSON.toJSONString(secondResp));
					return secondResp;  
			      }
				if (activationQuery.getSystemStatus().equals("0")  ||activationQuery.getSystemStatus().equals("2")) {
					// 系统为初始或者写失败
					//0   : cmdType:0 cmdValue:00A40000023F00   选择目录
					//1   : cmdType:0 cmdValue:0084000004  // 读取随机数 
					//拼接写指令参数返回 
					//cur_step 6 total_step 10
					LogUtil.info(log, secondReq.getOrderId(), "system_status : 0 || system_status : 2");
					secondResp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
					secondResp.setCurStep(6);
					secondResp.setTotalStep(TOTAL_STEP);
					secondResp.setErrorCode(InterfaceErrorCode.RESULT_OK.getValue());
					secondResp.setErrorMsg(InterfaceErrorCode.RESULT_OK_INFO.getValue());
					Map<String, Object> apduList = CreateApduHeader.createHeaderReadObuAndCard();
					ApduObuResp obuRespChoose = new ApduObuResp();
					ApduObuInner innerChoose = new ApduObuInner ("00A40000023F00");
					obuRespChoose.setInner(innerChoose);
					ApduObuResp obuRespSys = new ApduObuResp();
					ApduObuInner innerSys = new ApduObuInner ("0084000004");
					obuRespSys.setInner(innerSys);
					apduList.put("5",obuRespChoose);
					apduList.put("6",obuRespSys);
					secondResp.setRespInfo(apduList);
					secondResp.setApduFlag("1");
					LogUtil.info(log, secondReq.getOrderId(), "组装指令: " +JSON.toJSONString(secondResp));
					return secondResp;  
			      }
				  if("1".equals(activationQuery.getSystemStatus())&&activationQuery.getVehicleStatus().equals("1")&&activationQuery.getCardStatus().equals("1")) {
					  //全部写卡片成功但是最后一步上报失败
					    CardActionReq cardReq = new CardActionReq();
						cardReq.setType(3);
						cardReq.setOrderNo(secondReq.getOrderId());
						cardReq.setKind("0");
						cardReq.setResult("true");
						cardReq.setOwnerCode("4301");
						LogUtil.info(log,secondReq.getOrderId(), JSON.toJSONString(cardReq));
						CardActionResp cardAction = IssueCenterApi.cardAction(cardReq);
						LogUtil.info(log,secondReq.getOrderId(), JSON.toJSONString(cardAction));
						if(cardAction.getCode()!=0) {
							secondResp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
							secondResp.setErrorCode(InterfaceErrorCode.SYSTEM_ERROR.getValue());
							secondResp.setErrorMsg("写0016通知中台失败");
							return secondResp;  
						}
						
						secondResp.setCardNo(activationQuery.getCardNo());
			            secondResp.setObuNo(activationQuery.getObuNo());    
						secondResp.setCurStep(TOTAL_STEP);
						secondResp.setTotalStep(TOTAL_STEP);
						secondResp.setErrorCode(InterfaceErrorCode.RESULT_OK.getValue());
						secondResp.setErrorMsg(InterfaceErrorCode.RESULT_OK_INFO.getValue());
						return secondResp;  
				  }
				  if (activationQuery.getFinishStatus().equals("9")&&(activationQuery.getVehicleStatus().equals("2"))) {
				        //直接返回联系客服
						LogUtil.info(log, secondReq.getOrderId(), "查询结果为finis_status 9,有秘钥错误,直接返回联系客服!");
						secondResp.setErrorCode(InterfaceErrorCode.CUSTOMER_SERVICE.getValue());
						secondResp.setErrorMsg("激活状态码:6988");
						secondResp.setSuccess(BaseResponseData.Success.FAILED.toString());
						return secondResp;  			    
					}
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
		if(secondReq.getResultInfo().isEmpty()||secondReq.getResultInfo().size()!=7) {
			SecondIssueResp  resp  = new SecondIssueResp();
			resp.setErrorCode(InterfaceErrorCode.SYSTEM_ERROR.getValue());
			resp.setErrorMsg("读取OBU设备无响应");
			resp.setSuccess(BaseResponseData.Success.FAILED.toString());
			throw new RuntimeException("读取OBU设备无响应");
		}
		apdu = ApduHeadAnalysisService.doService(secondReq);
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
	        
	     }
		
		 if (activationQuery.getCardStatus().equals("2")  ||activationQuery.getCardStatus().equals("3")) {
	       
				
			
				//获取0015
				String dataMac0015 = cardWrite(secondReq, "1", "42B6A67D",null,secondReq.getOriginValue());
				LogUtil.info(log, secondReq.getOrderId(), "开始鸡肋操作模拟获取0015成功"+dataMac0015);
				//上报成功
				cardAction(secondReq,"true",1,"0");
				LogUtil.info(log, secondReq.getOrderId(), "开始鸡肋操作模拟获取写卡上报成功"+dataMac0015);
	      }
		
	}

}
