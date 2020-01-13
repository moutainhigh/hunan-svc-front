package cn.trawe.etc.hunanfront.service.secondissue;

import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;

import cn.trawe.etc.hunanfront.config.InterfaceCenter;
import cn.trawe.etc.hunanfront.entity.ThirdPartner;
import cn.trawe.etc.hunanfront.enums.InterfaceErrorCode;
import cn.trawe.etc.hunanfront.expose.v2.BaseRequest;
import cn.trawe.etc.hunanfront.expose.v2.BaseResponse;
import cn.trawe.etc.hunanfront.expose.v2.BaseResponseData;
import cn.trawe.etc.hunanfront.request.secondissue.SecondIssueReq;
import cn.trawe.etc.hunanfront.request.secondissue.SecondIssueResp;
import cn.trawe.etc.hunanfront.service.BaseService;
import cn.trawe.etc.hunanfront.service.ThirdPartnerService;
import cn.trawe.etc.hunanfront.service.secondissue.bussiness.BaseBussinessApduType2;
import cn.trawe.etc.hunanfront.service.secondissue.bussiness.BaseBussinessApduType3;
import cn.trawe.etc.hunanfront.service.secondissue.response.ApduCardInner;
import cn.trawe.etc.hunanfront.service.secondissue.response.ApduCardResp;
import cn.trawe.etc.hunanfront.service.secondissue.response.ApduObuInner;
import cn.trawe.etc.hunanfront.service.secondissue.response.ApduObuResp;
import cn.trawe.etc.hunanfront.utils.SignUtil2;
import cn.trawe.etc.route.expose.request.issuesecond.SecondIssueOrderRequest;
import cn.trawe.etc.route.expose.response.issuesecond.SecondIssueOrderResponse;
import cn.trawe.pay.common.etcmsg.EtcObjectResponse;
import cn.trawe.pay.expose.entity.EtcIssueOrder;
import cn.trawe.pay.expose.entity.SecondIssueProcess;
import cn.trawe.pay.expose.request.issue.GetOrderReq;
import cn.trawe.pay.expose.request.issue.IssueOrderQueryReq;
import cn.trawe.pay.expose.request.issue.ThirdOutOrderQueryReq;
import cn.trawe.pay.expose.request.secondissue.ActivationQueryReq;
import cn.trawe.pay.expose.request.secondissue.CardActionReq;
import cn.trawe.pay.expose.response.issue.IssueOrderQueryResp;
import cn.trawe.pay.expose.response.issue.ThirdOutOrderQueryResp;
import cn.trawe.pay.expose.response.secondissue.ActivationQueryResp;
import cn.trawe.pay.expose.response.secondissue.CardActionResp;
import cn.trawe.util.LogUtil;
import cn.trawe.utils.ValidateUtil;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class HunanSecondIssueBussinessImpl extends BaseService implements SecondIssueBussinessI {

//	@Autowired
//    private ApiClient apiClient;
    @Value("${api.token}")
    private String token;
    
    private static final String CMD_TYPE_KEY ="cmdType";
    
    private static final String CMD_VALUE_KEY ="cmdValue";
    
    private static final String CMD_TYPE_CARD_VALUE ="0";
    
    private static final String CMD_TYPE_ESAM_VALUE ="1";
    
    @Autowired
    BaseBussinessApduType2 BaseBussinessApduType2;
    
    @Autowired
    BaseBussinessApduType3 BaseBussinessApduType3;
    
    
	
	@Autowired
	private ThirdPartnerService thirdPartnerService;
	
	 
   
    @Value("${secondissue.uploadPicture}")
    private String isUploadPicture;
    
    
    

    
	
	

	/**
	 * 激活进度查询
	 * 0016 -> 0015 ->车辆 ->系统->激活
	 */
	/**
	 *
	 */
	@Override
	public BaseResponse autoIssue(BaseRequest req ) {
		SecondIssueResp  secondResp = new SecondIssueResp();
		//验证签名
		//ThirdPartner partner = new ThirdPartner() ;
	     ThirdPartner partner = thirdPartnerService.getPartner(req);
	        if(partner == null){
	            return paramsError("渠道信息不存在", req.getCharset(),partner);
	        }
	        boolean flag = thirdPartnerService.check(req, partner);
	        if(!flag){
	        	return paramsError("验签失败", req.getCharset(),partner);
	        }
		SecondIssueReq secondReq = JSON.parseObject(req.getBizContent(), SecondIssueReq.class);
//		//根据外部订单号查询网发订单号
		EtcIssueOrder etcIssueOrder = getOrder(secondReq,req,partner);
        //替换为网发平台订单号
        secondReq.setOrderId(etcIssueOrder.getOrderNo());
		if("1".equals(secondReq.getType())) {
			 //增加判断逻辑是否允许再次激活，当前业务流程未定
			//再次激活
			//判断是否已经上传车头照片
			//新建二发订单
			if(etcIssueOrder!=null&&(etcIssueOrder.getOrderStatus()!=11)) {
	        	 return paramsError("订单未完成,不允许重新激活", req.getCharset(),partner);
	        }
			ActivationQueryReq actReq = new ActivationQueryReq();
			actReq.setKind("1");
			actReq.setOrderNo(secondReq.getOrderId());
			actReq.setOwnerCode("4301");
			ActivationQueryResp activationQuery = IssueCenterApi.activationQuery(actReq);
			
			if(activationQuery.getCode()!=0) {
				secondResp.setSuccess(BaseResponseData.Success.FAILED.toString());
				secondResp.setErrorCode(InterfaceErrorCode.SYSTEM_ERROR.getValue());
				secondResp.setErrorMsg("查询二发异常，基础服务返回 : " +JSON.toJSONString(activationQuery));
				return succeedResponseSecondIssue(req.getCharset(),secondResp,partner.getTrawePrivateKey());  
			}
			
			switch(secondReq.getCurStep()) {
				case 1:
				{
					//解析读取车辆指令 
					//获取激活指令接口
					//返回写卡结果
					secondResp = case9.doService(secondReq);
					break;
					//cur_step 1  total_step 2
				}
				case 2:
				{
					//解析写激活指令
					//写卡成功上报成功流程结束
					
					//cur_step 2 total_step 2
					secondResp = case10.doService(secondReq);
					break;
				}
				case 3:
				{
					//解析写激活指令
					//写卡成功上报成功流程结束
					
					//cur_step 2 total_step 2
					secondResp = case11.doService(secondReq);
					break;
				}
				default:{
					
					secondResp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
					secondResp.setCurStep(0);
					secondResp.setTotalStep(3);
					secondResp.setErrorCode(InterfaceErrorCode.RESULT_OK.getValue());
					secondResp.setErrorMsg(InterfaceErrorCode.RESULT_OK_INFO.getValue());
					Map<String, Object> apduList = CreateApduHeader.createHeaderReadObuAndCard();

//					ApduObuResp obuRespChoose = new ApduObuResp();
//					ApduObuInner innerChoose = new ApduObuInner ("00A4000002DF01");
//					obuRespChoose.setInner(innerChoose);
//					ApduObuResp obuRespVeh = new ApduObuResp();
//					//读取车辆密文随机数8个字节
//					ApduObuInner innerVeh = new ApduObuInner ("0084000008");
//					obuRespVeh.setInner(innerVeh);
//					apduList.put("5",obuRespChoose);
//					apduList.put("6",obuRespVeh);
					secondResp.setRespInfo(apduList);
					secondResp.setApduFlag("1");
					LogUtil.info(log, secondReq.getOrderId(), "组装指令: " +JSON.toJSONString(secondResp));
					LogUtil.info(log, secondReq.getOrderId(), "响应报文 : "+succeedResponseSecondIssue(req.getCharset(),secondResp,partner.getTrawePrivateKey()));
					return succeedResponseSecondIssue(req.getCharset(),secondResp,partner.getTrawePrivateKey()); 
					
				}
				
			}
			return succeedResponseSecondIssue(req.getCharset(),secondResp,partner.getTrawePrivateKey());  
			
		}
		if("0".equals(secondReq.getType())) {
			
			if(etcIssueOrder!=null&&(etcIssueOrder.getOrderStatus()<10)) {
	        	 return paramsError("订单未发货,请完成订单再安装激活", req.getCharset(),partner);
	        }
		switch(secondReq.getCurStep()) {
		 
			case 1:
			{
				//解析卡号
				 
				//解析OBU号
				
				//0016响应指令开始解析
				
				//解析完成调用0016获取MAC接口
				
				
				//获取MAC 成功 组装 写卡指令响应报文
				
				//cur_step =1
				secondResp = case1.doService(secondReq);
				break;
				
			}
			
			case 2:
			{
				//写0016指令结果解析
				
			    //成功上报0016写成功
				
				//组装0015读取随机数指令 cur_step =2 
				secondResp = case2.doService(secondReq);
				break;
			}
			
			case 3:
			{
				//读取0015随机数指令结果解析
				
				//获取0015写卡指令
				
				//组装响应指令报文  cur_step =3 
				secondResp = case3.doService(secondReq);
				break;
			}
			
			case 4:
			{
				//写卡0015指令解析
				
				//成功上报0015成功
				
				//组装读取车辆随机数指令  cur_step =4
				secondResp = case4.doService(secondReq);
				break;
			}
			case 5:
			{
				//解析读取车辆随机数指令
				
				//成功获取写车辆信息指令
				
				//组装写车辆信息指令报文 cur_step =5
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
			case 9:
			{
				//解析激活指令
				//成功获取激活MAC
				
				//如果已经激活//直接报上全部写完状态位  total_step 10  cur_step 10
				
				//组装激活写卡指令  cur_step =9
				secondResp = case9.doService(secondReq);
				break;
				
	
			}
			case 10:
			{
				//解析激活指令
				//成功上报激活成功
				
				//流程结束
				secondResp = case10.doService(secondReq);
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
					return succeedResponseSecondIssue(req.getCharset(),secondResp,partner.getTrawePrivateKey());  
				}
				//如果是工行必须上传2张图片
                //查询二发订单
				if("1".equals(isUploadPicture)) {
					try {
						
						SecondIssueOrderRequest secondIssueOrder = new SecondIssueOrderRequest();
						secondIssueOrder.setOwnerCode("4301");
						secondIssueOrder.setKind("0");
						secondIssueOrder.setOrderNo(secondReq.getOrderId());
						LogUtil.info(log, secondReq.getOrderId(), "开始查询二发记录"+JSON.toJSONString(secondIssueOrder));
						SecondIssueOrderResponse orderQuery = IssueCenterApi.orderQuery(secondIssueOrder);
						LogUtil.info(log, secondReq.getOrderId(), "查询二发记录响应"+JSON.toJSONString(orderQuery));
						if(orderQuery.getCode()==0&&orderQuery.getResult()!=null) {
							SecondIssueProcess iss =JSON.parseObject(orderQuery.getResult().toString(),SecondIssueProcess.class);
							if(StringUtils.isBlank(iss.getObuOutsideImageUrl())||StringUtils.isBlank(iss.getObuInnerImageUrl())) {
								secondResp.setSuccess(BaseResponseData.Success.FAILED.toString());
								
								secondResp.setErrorCode(InterfaceErrorCode.SYSTEM_ERROR.getValue());
								secondResp.setErrorMsg("OBU 车内或车外照为空");
								log.info("OBU 车内或车外照为空");
								return succeedResponseSecondIssue(req.getCharset(),secondResp,partner.getTrawePrivateKey());  
								
							}
						}
					
				}
				catch(Exception e) {
					log.error(e.getLocalizedMessage(),e.fillInStackTrace());
					secondResp.setSuccess(BaseResponseData.Success.FAILED.toString());
					secondResp.setErrorCode(InterfaceErrorCode.SYSTEM_ERROR.getValue());
					secondResp.setErrorMsg(e.getMessage());
					return succeedResponseSecondIssue(req.getCharset(),secondResp,partner.getTrawePrivateKey());  
				}
				}
				
				
				//秘钥不对
				if (activationQuery.getFinishStatus().equals("9")) {
					
					//判断卡号是否一致，不一致提示卡号已被绑定
					if(activationQuery.getCardStatus().equals("1")) {
						secondResp.setErrorMsg("OBU激活状态码:6988,请线下营业厅处理");
					}else {
						secondResp.setErrorMsg("卡片激活状态码:6988,请线下营业厅处理");
					}
			        //直接返回联系客服
					LogUtil.info(log, secondReq.getOrderId(), "查询结果为finis_status 9,有秘钥错误,直接返回联系客服!");
					secondResp.setErrorCode(InterfaceErrorCode.CUSTOMER_SERVICE.getValue());
					secondResp.setSuccess(BaseResponseData.Success.FAILED.toString());
					LogUtil.info(log, secondReq.getOrderId(), "响应报文 : "+succeedResponseSecondIssue(req.getCharset(),secondResp,partner.getTrawePrivateKey()));
					return succeedResponseSecondIssue(req.getCharset(),secondResp,partner.getTrawePrivateKey());  
			     }
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
					LogUtil.info(log, secondReq.getOrderId(), "响应报文 : "+succeedResponseSecondIssue(req.getCharset(),secondResp,partner.getTrawePrivateKey()));
					return succeedResponseSecondIssue(req.getCharset(),secondResp,partner.getTrawePrivateKey());  
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
					secondResp.setTotalStep(TOTAL_STEP);
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
					LogUtil.info(log, secondReq.getOrderId(), "响应报文 : "+succeedResponseSecondIssue(req.getCharset(),secondResp,partner.getTrawePrivateKey()));
					return succeedResponseSecondIssue(req.getCharset(),secondResp,partner.getTrawePrivateKey());  
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
						secondResp.setTotalStep(TOTAL_STEP);
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
						LogUtil.info(log, secondReq.getOrderId(), "响应报文 : "+succeedResponseSecondIssue(req.getCharset(),secondResp,partner.getTrawePrivateKey()));
						return succeedResponseSecondIssue(req.getCharset(),secondResp,partner.getTrawePrivateKey());  
			      }
				
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
					LogUtil.info(log, secondReq.getOrderId(), "响应报文 : "+succeedResponseSecondIssue(req.getCharset(),secondResp,partner.getTrawePrivateKey()));
					return succeedResponseSecondIssue(req.getCharset(),secondResp,partner.getTrawePrivateKey());  
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
					LogUtil.info(log, secondReq.getOrderId(), "响应报文 : "+succeedResponseSecondIssue(req.getCharset(),secondResp,partner.getTrawePrivateKey()));
					return succeedResponseSecondIssue(req.getCharset(),secondResp,partner.getTrawePrivateKey());  
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
							return succeedResponseSecondIssue(req.getCharset(),secondResp,partner.getTrawePrivateKey()); 
						}
						
						secondResp.setCardNo(activationQuery.getCardNo());
			            secondResp.setObuNo(activationQuery.getObuNo());    
						secondResp.setCurStep(TOTAL_STEP);
						secondResp.setTotalStep(TOTAL_STEP);
						secondResp.setErrorCode(InterfaceErrorCode.RESULT_OK.getValue());
						secondResp.setErrorMsg(InterfaceErrorCode.RESULT_OK_INFO.getValue());
						LogUtil.info(log, secondReq.getOrderId(), "响应报文 : "+succeedResponseSecondIssue(req.getCharset(),secondResp,partner.getTrawePrivateKey()));
						return succeedResponseSecondIssue(req.getCharset(),secondResp,partner.getTrawePrivateKey());  
				  }
				
					//以上情况都不满足
					//开始组装激活随机数指令
					//读取拆迁位指令 
//					//cur_step =8 total_step 10
//				    LogUtil.info(log, secondReq.getOrderId(), "开始组装激活指令");
//				    secondResp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
//					secondResp.setCurStep(8);
//					secondResp.setTotalStep(10);
//					secondResp.setErrorCode(InterfaceErrorCode.RESULT_OK.getValue());
//					secondResp.setErrorMsg(InterfaceErrorCode.RESULT_OK_INFO.getValue());
//					ApduObuResp obuRespChoose = new ApduObuResp();
//					ApduObuInner innerChoose = new ApduObuInner("00A40000023F00");
//					obuRespChoose.setInner(innerChoose);
//					ApduObuResp obuRespSys = new ApduObuResp();
//					ApduObuInner innerSys = new ApduObuInner ("0084000004");
//					obuRespSys.setInner(innerSys);
//					apduList.put("0", obuRespReadSystem);
//					apduList.put("1", cardRespReadCard);
//				
//					//激活随机数指令
//					apduList.put("2",obuRespChoose);
//					apduList.put("3",innerSys);
//					secondResp.setRespInfo(apduList);
//					secondResp.setApduFlag("1");
//					LogUtil.info(log, secondReq.getOrderId(), "组装指令: " +JSON.toJSONString(secondResp));
//					LogUtil.info(log, secondReq.getOrderId(), "响应报文 : "+succeedResponseSecondIssue(req.getCharset(),secondResp,partner.getTrawePrivateKey()));
					return succeedResponseSecondIssue(req.getCharset(),secondResp,partner.getTrawePrivateKey());  
			}
		}
		 return succeedResponseSecondIssue(req.getCharset(),secondResp,partner.getTrawePrivateKey());  
		}
		if("2".equals(secondReq.getType())) {
			//直接发行OBU
			secondResp = BaseBussinessApduType2.doService(secondReq, partner);
			LogUtil.info(log, secondReq.getOrderId(), "auto-issue-type-->2响应报文 : "+JSON.toJSONString(secondResp));

		}
		if("3".equals(secondReq.getType())) {
			//直接发行卡直接发行OBU
			secondResp = BaseBussinessApduType3.doService(secondReq, partner);
			LogUtil.info(log, secondReq.getOrderId(), "auto-issue-type-->3响应报文 : "+JSON.toJSONString(secondResp));

		}
		return succeedResponseSecondIssue(req.getCharset(),secondResp,partner.getTrawePrivateKey());  

		
		
		
		
		
	}
	
    BaseResponse succeedResponseSecondIssue(String charset, SecondIssueResp SecondIssueResp,String privateKey) {

        BaseResponse<SecondIssueResp> resp = new BaseResponse<SecondIssueResp>();
        resp.setResponse(SecondIssueResp);
        
        try{
            resp.setSign(SignUtil2.signResponse(SecondIssueResp,privateKey,charset,true));
        }catch (AlipayApiException e){
            log.error(e.getMessage(),e.fillInStackTrace());
        }
		LogUtil.info(log, "auo_issue", "响应报文 : "+JSON.toJSONString(resp));
        return resp;
    }
    
    public EtcIssueOrder  getOrder(SecondIssueReq secondReq,BaseRequest req,ThirdPartner partner) {
    	EtcIssueOrder etcIssueOrder = null;
//		ThirdOutOrderQueryReq outOrderReq = new ThirdOutOrderQueryReq();
//		outOrderReq.setOutOrderId(secondReq.getOrderId());
//		LogUtil.info(log, secondReq.getOrderId(), "外部订单号:"+secondReq.getOrderId());
//		LogUtil.info(log, secondReq.getOrderId(), "查询网发平台订单请求:"+JSON.toJSONString(outOrderReq));
//		ThirdOutOrderQueryResp queryThirdOutOrder = IssueCenterApi.queryThirdOutOrder(outOrderReq);
//		LogUtil.info(log, secondReq.getOrderId(), "查询网发平台订单响应:"+JSON.toJSONString(queryThirdOutOrder));
//		if(queryThirdOutOrder.getCode()==0) {
//			secondReq.setOrderId(queryThirdOutOrder.getOrderNo());
//			LogUtil.info(log, secondReq.getOrderId(), "网发平台订单号:"+queryThirdOutOrder.getOrderNo());
//			IssueOrderQueryReq issueOrderQueryReq = new IssueOrderQueryReq();
//	        issueOrderQueryReq.setOrderNo(queryThirdOutOrder.getOrderNo());
//	        issueOrderQueryReq.setPageNo(1);
//	        issueOrderQueryReq.setPageSize(1);
//	        LogUtil.info(log, secondReq.getOrderId(), "中台订单查询请求:"+JSON.toJSONString(issueOrderQueryReq));
//	        
//	        IssueOrderQueryResp issueOrderQueryResp = IssueCenterApi.orderQuery(issueOrderQueryReq, "");
//	        LogUtil.info(log, secondReq.getOrderId(), "中台订单查询响应:"+JSON.toJSONString(issueOrderQueryResp));
//	        
//	        if (issueOrderQueryResp != null && CollectionUtils.isNotEmpty(issueOrderQueryResp.getResult())) {
//	          etcIssueOrder = issueOrderQueryResp.getResult().get(0);
//	        }
//		    if(etcIssueOrder==null) {
//		    	
//		    	 throw new RuntimeException("查询发行订单失败,请稍后重试");
//		      	
//		      	 //paramsError("查询发行订单失败,请稍后重试", req.getCharset(),partner);
//		     }
//		}else {
			String outOrderId  =secondReq.getOrderId();
			GetOrderReq centerReq1 = new GetOrderReq();
			centerReq1.setOutOrderId(outOrderId);
	    	LogUtil.info(log, outOrderId, "中台查询订单请求:"+JSON.toJSONString(centerReq1));
			EtcObjectResponse<EtcIssueOrder> centerResp1 = IssueCenterApi.getOrder(centerReq1);
			LogUtil.info(log, outOrderId, "中台查询订单响应:"+JSON.toJSONString(centerResp1));
			if(InterfaceCenter.TIMEOUT.getCode()==centerResp1.getCode()) {
				
				throw new RuntimeException("网络异常,请稍后重试");
			}
			if(InterfaceCenter.SUCCESS.getCode()!=centerResp1.getCode()) {
				throw new RuntimeException("系统异常,请稍后重试");
			}
			
		    if (ValidateUtil.isEmpty(centerResp1.getData())) {
		    	throw new RuntimeException("发行订单不存在");
		    	//paramsError(", req.getCharset(),partner);
		    }
		          
		    etcIssueOrder =centerResp1.getData();
		//}
		return etcIssueOrder;
    }
    
    
    public  BaseResponse  defaultMethod(SecondIssueReq secondReq,BaseRequest req,ThirdPartner partner){
    	SecondIssueResp  secondResp = new SecondIssueResp();
		
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
			return succeedResponseSecondIssue(req.getCharset(),secondResp,partner.getTrawePrivateKey());  
		}
		//如果是工行必须上传2张图片
        //查询二发订单
		if("1".equals(isUploadPicture)) {
			try {
				
				SecondIssueOrderRequest secondIssueOrder = new SecondIssueOrderRequest();
				secondIssueOrder.setOwnerCode("4301");
				secondIssueOrder.setKind("0");
				secondIssueOrder.setOrderNo(secondReq.getOrderId());
				LogUtil.info(log, secondReq.getOrderId(), "开始查询二发记录"+JSON.toJSONString(secondIssueOrder));
				SecondIssueOrderResponse orderQuery = IssueCenterApi.orderQuery(secondIssueOrder);
				LogUtil.info(log, secondReq.getOrderId(), "查询二发记录响应"+JSON.toJSONString(orderQuery));
				if(orderQuery.getCode()==0&&orderQuery.getResult()!=null) {
					SecondIssueProcess iss =JSON.parseObject(orderQuery.getResult().toString(),SecondIssueProcess.class);
					if(StringUtils.isBlank(iss.getObuOutsideImageUrl())||StringUtils.isBlank(iss.getObuInnerImageUrl())) {
						secondResp.setSuccess(BaseResponseData.Success.FAILED.toString());
						
						secondResp.setErrorCode(InterfaceErrorCode.SYSTEM_ERROR.getValue());
						secondResp.setErrorMsg("OBU 车内或车外照为空");
						log.info("OBU 车内或车外照为空");
						return succeedResponseSecondIssue(req.getCharset(),secondResp,partner.getTrawePrivateKey());  
						
					}
				}
			
		}
		catch(Exception e) {
			log.error(e.getLocalizedMessage(),e.fillInStackTrace());
			secondResp.setSuccess(BaseResponseData.Success.FAILED.toString());
			secondResp.setErrorCode(InterfaceErrorCode.SYSTEM_ERROR.getValue());
			secondResp.setErrorMsg(e.getMessage());
			return succeedResponseSecondIssue(req.getCharset(),secondResp,partner.getTrawePrivateKey());  
		}
		}
		
		
		//秘钥不对
		if (activationQuery.getFinishStatus().equals("9")) {
	        //直接返回联系客服
			LogUtil.info(log, secondReq.getOrderId(), "查询结果为finis_status 9,有秘钥错误,直接返回联系客服!");
			secondResp.setErrorCode(InterfaceErrorCode.CUSTOMER_SERVICE.getValue());
			secondResp.setErrorMsg("秘钥错误,激活状态码:6988");
			secondResp.setSuccess(BaseResponseData.Success.FAILED.toString());
			LogUtil.info(log, secondReq.getOrderId(), "响应报文 : "+succeedResponseSecondIssue(req.getCharset(),secondResp,partner.getTrawePrivateKey()));
			return succeedResponseSecondIssue(req.getCharset(),secondResp,partner.getTrawePrivateKey());  
	     }
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
			LogUtil.info(log, secondReq.getOrderId(), "响应报文 : "+succeedResponseSecondIssue(req.getCharset(),secondResp,partner.getTrawePrivateKey()));
			return succeedResponseSecondIssue(req.getCharset(),secondResp,partner.getTrawePrivateKey());  
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
			secondResp.setTotalStep(TOTAL_STEP);
			secondResp.setErrorCode(InterfaceErrorCode.RESULT_OK.getValue());
			secondResp.setErrorMsg(InterfaceErrorCode.RESULT_OK_INFO.getValue());
			Map<String, Object> apduList = CreateApduHeader.createHeaderReadObuAndCard();
			//
//			ApduCardResp cardRespChoose = new ApduCardResp();
//			ApduCardInner innerChoose = new ApduCardInner ("00A40000021001");
//			cardRespChoose.setInner(innerChoose);
//			ApduCardResp cardResp0015 = new ApduCardResp();
//			ApduCardInner inner0015 = new ApduCardInner ("0084000004");
//			cardResp0015.setInner(inner0015);
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
			LogUtil.info(log, secondReq.getOrderId(), "响应报文 : "+succeedResponseSecondIssue(req.getCharset(),secondResp,partner.getTrawePrivateKey()));
			return succeedResponseSecondIssue(req.getCharset(),secondResp,partner.getTrawePrivateKey());  
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
				secondResp.setTotalStep(TOTAL_STEP);
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
				LogUtil.info(log, secondReq.getOrderId(), "响应报文 : "+succeedResponseSecondIssue(req.getCharset(),secondResp,partner.getTrawePrivateKey()));
				return succeedResponseSecondIssue(req.getCharset(),secondResp,partner.getTrawePrivateKey());  
	      }
		
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
			LogUtil.info(log, secondReq.getOrderId(), "响应报文 : "+succeedResponseSecondIssue(req.getCharset(),secondResp,partner.getTrawePrivateKey()));
			return succeedResponseSecondIssue(req.getCharset(),secondResp,partner.getTrawePrivateKey());  
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
			LogUtil.info(log, secondReq.getOrderId(), "响应报文 : "+succeedResponseSecondIssue(req.getCharset(),secondResp,partner.getTrawePrivateKey()));
			return succeedResponseSecondIssue(req.getCharset(),secondResp,partner.getTrawePrivateKey());  
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
					return succeedResponseSecondIssue(req.getCharset(),secondResp,partner.getTrawePrivateKey()); 
				}
				
				secondResp.setCardNo(activationQuery.getCardNo());
	            secondResp.setObuNo(activationQuery.getObuNo());    
				secondResp.setCurStep(TOTAL_STEP);
				secondResp.setTotalStep(TOTAL_STEP);
				secondResp.setErrorCode(InterfaceErrorCode.RESULT_OK.getValue());
				secondResp.setErrorMsg(InterfaceErrorCode.RESULT_OK_INFO.getValue());
				LogUtil.info(log, secondReq.getOrderId(), "响应报文 : "+succeedResponseSecondIssue(req.getCharset(),secondResp,partner.getTrawePrivateKey()));
				return succeedResponseSecondIssue(req.getCharset(),secondResp,partner.getTrawePrivateKey());  
		  }
		
			//以上情况都不满足
			//开始组装激活随机数指令
			//读取拆迁位指令 
//			//cur_step =8 total_step 10
//		    LogUtil.info(log, secondReq.getOrderId(), "开始组装激活指令");
//		    secondResp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
//			secondResp.setCurStep(8);
//			secondResp.setTotalStep(10);
//			secondResp.setErrorCode(InterfaceErrorCode.RESULT_OK.getValue());
//			secondResp.setErrorMsg(InterfaceErrorCode.RESULT_OK_INFO.getValue());
//			ApduObuResp obuRespChoose = new ApduObuResp();
//			ApduObuInner innerChoose = new ApduObuInner("00A40000023F00");
//			obuRespChoose.setInner(innerChoose);
//			ApduObuResp obuRespSys = new ApduObuResp();
//			ApduObuInner innerSys = new ApduObuInner ("0084000004");
//			obuRespSys.setInner(innerSys);
//			apduList.put("0", obuRespReadSystem);
//			apduList.put("1", cardRespReadCard);
//		
//			//激活随机数指令
//			apduList.put("2",obuRespChoose);
//			apduList.put("3",innerSys);
//			secondResp.setRespInfo(apduList);
//			secondResp.setApduFlag("1");
//			LogUtil.info(log, secondReq.getOrderId(), "组装指令: " +JSON.toJSONString(secondResp));
//			LogUtil.info(log, secondReq.getOrderId(), "响应报文 : "+succeedResponseSecondIssue(req.getCharset(),secondResp,partner.getTrawePrivateKey()));
			return succeedResponseSecondIssue(req.getCharset(),secondResp,partner.getTrawePrivateKey()); 
    	
    }
	
	

}
