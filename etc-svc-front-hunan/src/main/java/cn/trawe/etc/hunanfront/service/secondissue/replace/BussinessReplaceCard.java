package cn.trawe.etc.hunanfront.service.secondissue.replace;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.trawe.etc.hunanfront.config.InterfaceCenter;
import cn.trawe.etc.hunanfront.entity.ThirdPartner;
import cn.trawe.etc.hunanfront.enums.InterfaceErrorCode;
import cn.trawe.etc.hunanfront.expose.v2.BaseResponseData;
import cn.trawe.etc.hunanfront.feign.entity.hunan.HunanGatewayBaseResp;
import cn.trawe.etc.hunanfront.feign.entity.hunan.OrderEquipmentReq;
import cn.trawe.etc.hunanfront.request.secondissue.SecondIssueReq;
import cn.trawe.etc.hunanfront.request.secondissue.SecondIssueResp;
import cn.trawe.etc.hunanfront.service.BaseService;
import cn.trawe.etc.hunanfront.service.secondissue.apdu.ApduFlagResponse;
import cn.trawe.etc.hunanfront.service.secondissue.apdu.CardInfoApdu;
import cn.trawe.etc.hunanfront.service.secondissue.apdu.ObuInfoApdu;
import cn.trawe.etc.hunanfront.service.secondissue.response.ApduCardInner;
import cn.trawe.etc.hunanfront.service.secondissue.response.ApduCardResp;
import cn.trawe.etc.hunanfront.utils.HexUtils;
import cn.trawe.pay.expose.entity.EtcIssueOrder;
import cn.trawe.pay.expose.request.secondissue.ActivationQueryReq;
import cn.trawe.pay.expose.response.secondissue.ActivationQueryResp;
import cn.trawe.util.LogUtil;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BussinessReplaceCard extends BaseService{
	
	
	private static int  CARD_TOTAL_STEP =4;
	
	public SecondIssueResp  doService(SecondIssueReq secondReq, ThirdPartner partner) {
		
		SecondIssueResp secondResp = new SecondIssueResp();
		//查询订单号
      	EtcIssueOrder order = getOrder("",secondReq.getOrderId());
      	secondReq.setOrderId(order.getOrderNo());

		switch (secondReq.getCurStep()) {
			case 1:
			{
				//解析卡号
				if(secondReq.getResultInfo().isEmpty()||secondReq.getResultInfo().size()!=7) {
					SecondIssueResp  resp  = new SecondIssueResp();
					resp.setErrorCode(InterfaceErrorCode.SYSTEM_ERROR.getValue());
					resp.setErrorMsg("读取OBU设备无响应");
					resp.setSuccess(BaseResponseData.Success.FAILED.toString());
					return resp;
				}
				ApduFlagResponse apdu =null;
				apdu = ApduHeadAnalysisService.doService(secondReq);
				//解析OBU号				
				//华软设备登记接口
				OrderEquipmentReq gateWayEquipmentReq = new OrderEquipmentReq();				
				ObuInfoApdu obuInfo = apdu.getObuInfo();				
				CardInfoApdu cardInfo = apdu.getCardInfo();
				gateWayEquipmentReq.setCardType(23);
				//gateWayEquipmentReq.setCardType(Integer.valueOf(cardInfo.getCardType()));
				gateWayEquipmentReq.setFaceCardNum(cardInfo.getCardNumber());
				gateWayEquipmentReq.setPhyCardNum("FFFFFFFF");
				
				gateWayEquipmentReq.setRepairType(1);
				gateWayEquipmentReq.setVersion(Integer.valueOf(cardInfo.getCardVersion()));
				gateWayEquipmentReq.setListNo(order.getOrderNo());
				//gateWayEquipmentReq.setContractType(Integer.valueOf(obuInfo.getTreatyType()));
				//gateWayEquipmentReq.setContractVersion(Integer.valueOf(obuInfo.getContractVersion()));
				String providerCode ="湖南";
				byte[] hexStringToBytes = HexUtils.hexStringToBytes(obuInfo.getProviderCode().substring(0, 8));
				try {
					providerCode= new String(hexStringToBytes,"GBK");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					log.error(e.getMessage(),e.fillInStackTrace());
					providerCode ="湖南";
				}
				//gateWayEquipmentReq.setSupplier(providerCode);
				//gateWayEquipmentReq.setObuId("FFFFFFFF");
				//gateWayEquipmentReq.setSerialNumber(obuInfo.getContractNo());
				JSONObject object = new JSONObject();
		    	
				String accountNo = partner.getAccountNo();
		      	String password = partner.getPassword();
		      	object.put("Account", accountNo);
		      	object.put("Password", password);
				HunanGatewayBaseResp orderEquipmentResp = gateWay.orderEquipmentReq(gateWayEquipmentReq, object.toJSONString());
				if(InterfaceCenter.SUCCESS.getCode()!=orderEquipmentResp.getCode()) {
					secondResp.setErrorCode(StringUtils.isBlank(orderEquipmentResp.getErrorCode())?BaseResponseData.ErrorCode.SYSTEM_ERROR.toString():orderEquipmentResp.getErrorCode());
					secondResp.setErrorMsg(orderEquipmentResp.getMsg());
					secondResp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
					LogUtil.info(log, secondReq.getOrderId(), "拓展订单登记响应:"+JSON.toJSONString(secondResp));
					return secondResp;
				}
//				
				//0016响应指令开始解析
				
				//解析完成调用0016获取MAC接口
				
				
				//获取MAC 成功 组装 写卡指令响应报文
				
				//cur_step =1
				
				secondResp = case1.doService(secondReq);
				secondResp.setTotalStep(CARD_TOTAL_STEP);
				break;
				
			}
			
			case 2:
			{
				//写0016指令结果解析
				
			    //成功上报0016写成功
				
				//组装0015读取随机数指令 cur_step =2 
				secondResp = case2.doService(secondReq);
				secondResp.setTotalStep(CARD_TOTAL_STEP);
				break;
			}
			
			case 3:
			{
				//读取0015随机数指令结果解析
				
				//获取0015写卡指令
				
				//组装响应指令报文  cur_step =3 
				secondResp = case3.doService(secondReq);
				secondResp.setTotalStep(CARD_TOTAL_STEP);
				break;
			}
			
			case 4:
			{
				//写卡0015指令解析
				
				//成功上报0015成功
				
				//组装读取车辆随机数指令  cur_step =4
				secondResp = case4.doService(secondReq);
				cardAction(secondReq, "true", 3,"0");
				secondResp.setTotalStep(CARD_TOTAL_STEP);
				secondResp.setCurStep(CARD_TOTAL_STEP);
				secondResp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
				secondResp.setCardNo(secondReq.getCardNo());
	            secondResp.setObuNo(secondReq.getObuNo());    

				secondResp.setErrorCode(InterfaceErrorCode.RESULT_OK.getValue());
				secondResp.setErrorMsg(InterfaceErrorCode.RESULT_OK_INFO.getValue());
				//写卡成功，直接返回步数相等
				break;
			}
			default:{
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
				if (activationQuery.getFinishStatus().equals("9")) {
			        //直接返回联系客服
					LogUtil.info(log, secondReq.getOrderId(), "查询结果为finis_status 9,有秘钥错误,直接返回联系客服!");
					secondResp.setErrorCode(InterfaceErrorCode.CUSTOMER_SERVICE.getValue());
					secondResp.setErrorMsg("秘钥错误,激活状态码:6988");
					secondResp.setSuccess(BaseResponseData.Success.FAILED.toString());
					LogUtil.info(log, secondReq.getOrderId(), "组装指令: " +JSON.toJSONString(secondResp));
					return secondResp;  
			     }
				//发行成功
				if (activationQuery.getFinishStatus().equals("1")) {
			        //直接返回total_step 10 cur_step 10
					LogUtil.info(log, secondReq.getOrderId(), "查询结果为finish_status 1");
					secondResp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
					secondResp.setCardNo(activationQuery.getCardNo());
		            secondResp.setObuNo(activationQuery.getObuNo());    
					secondResp.setCurStep(CARD_TOTAL_STEP);
					secondResp.setTotalStep(CARD_TOTAL_STEP);
					secondResp.setErrorCode(InterfaceErrorCode.RESULT_OK.getValue());
					secondResp.setErrorMsg(InterfaceErrorCode.RESULT_OK_INFO.getValue());
					LogUtil.info(log, secondReq.getOrderId(), "组装指令: " +JSON.toJSONString(secondResp));
					return secondResp;  
			     }

				
				
				//0016写失败或者初始化状态
				if (activationQuery.getCardStatus().equals("0")  ||activationQuery.getCardStatus().equals("4")) {
					LogUtil.info(log, secondReq.getOrderId(), "card_status : 0 || card_status : 4");
			        //返回 cur_step 0  total_step 10
					//
					//0   : cmdType:0 cmdValue:00A40000023F00   选择目录
					//1   : cmdType:0 cmdValue:0084000004  // 读取随机数 
					//拼接写指令参数返回 
					secondResp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
					secondResp.setCurStep(0);
					secondResp.setTotalStep(CARD_TOTAL_STEP);
					secondResp.setErrorCode(InterfaceErrorCode.RESULT_OK.getValue());
					secondResp.setErrorMsg(InterfaceErrorCode.RESULT_OK_INFO.getValue());
					Map<String, Object> apduList = CreateApduHeader.createHeaderReadObuAndCard();
					//
//					ApduCardResp cardRespChoose = new ApduCardResp();
//					ApduCardInner innerChoose = new ApduCardInner ("00A40000021001");
//					cardRespChoose.setInner(innerChoose);
//					ApduCardResp cardResp0015 = new ApduCardResp();
//					ApduCardInner inner0015 = new ApduCardInner ("0084000004");
//					cardResp0015.setInner(inner0015);
					ApduCardResp cardRespChoose = new ApduCardResp();
					ApduCardInner innerChoose = new ApduCardInner ("00A40000023F00");
					cardRespChoose.setInner(innerChoose);
					ApduCardResp cardResp0016 = new ApduCardResp();
					ApduCardInner inner0016 = new ApduCardInner ("0084000004");
					cardResp0016.setInner(inner0016);
					apduList.put("5",cardRespChoose);
					apduList.put("6",cardResp0016);
					secondResp.setRespInfo(apduList);
					secondResp.setApduFlag("1");
					LogUtil.info(log, secondReq.getOrderId(), "组装指令: " +JSON.toJSONString(secondResp));
					return secondResp;  
			     }
				
				 if (activationQuery.getCardStatus().equals("2")  ||activationQuery.getCardStatus().equals("3")) {
			        // 写0015失败 或0016成功
					//0   : cmdType:0 cmdValue:00A40000021001   选择目录
					//1   : cmdType:0 cmdValue:0084000004  // 读取随机数 
					//拼接写指令参数返回 
					//cur_step 2 total_step 10
						
					 LogUtil.info(log, secondReq.getOrderId(), "card_status :2 || card_status :3");
					    secondResp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
						secondResp.setCurStep(2);
						secondResp.setTotalStep(CARD_TOTAL_STEP);
						secondResp.setErrorCode(InterfaceErrorCode.RESULT_OK.getValue());
						secondResp.setErrorMsg(InterfaceErrorCode.RESULT_OK_INFO.getValue());
						Map<String, Object> apduList = CreateApduHeader.createHeaderReadObuAndCard();
						//
						ApduCardResp cardRespChoose = new ApduCardResp();
						ApduCardInner innerChoose = new ApduCardInner ("00A40000021001");
						cardRespChoose.setInner(innerChoose);
						ApduCardResp cardResp0015 = new ApduCardResp();
						ApduCardInner inner0015 = new ApduCardInner ("0084000004");
						cardResp0015.setInner(inner0015);
						apduList.put("5",cardRespChoose);
						apduList.put("6",cardResp0015);
						secondResp.setRespInfo(apduList);
						secondResp.setApduFlag("1");
						LogUtil.info(log, secondReq.getOrderId(), "组装指令: " +JSON.toJSONString(secondResp));
						return secondResp;  
			      }
				 if (activationQuery.getCardStatus().equals("1")) {
					    LogUtil.info(log, secondReq.getOrderId(), "查询结果为finish_status 1");
						secondResp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
						secondResp.setCardNo(activationQuery.getCardNo());
			            secondResp.setObuNo(activationQuery.getObuNo());    
						secondResp.setCurStep(CARD_TOTAL_STEP);
						secondResp.setTotalStep(CARD_TOTAL_STEP);
						secondResp.setErrorCode(InterfaceErrorCode.RESULT_OK.getValue());
						secondResp.setErrorMsg(InterfaceErrorCode.RESULT_OK_INFO.getValue());
						LogUtil.info(log, secondReq.getOrderId(), "组装指令: " +JSON.toJSONString(secondResp));
						return secondResp;  
				 }
			}
		}
	
		LogUtil.info(log, secondReq.getOrderId(), "ending: " +JSON.toJSONString(secondResp));
		return secondResp;
		
	}

}
