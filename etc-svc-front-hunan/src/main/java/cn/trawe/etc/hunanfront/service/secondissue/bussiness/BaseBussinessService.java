package cn.trawe.etc.hunanfront.service.secondissue.bussiness;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ptc.board.log.BizDigestLog;

import cn.trawe.etc.hunanfront.enums.InterfaceErrorCode;
import cn.trawe.etc.hunanfront.exception.ActivationObuException;
import cn.trawe.etc.hunanfront.exception.NoticeException;
import cn.trawe.etc.hunanfront.exception.WriteObuException;
import cn.trawe.etc.hunanfront.expose.v2.BaseResponseData;
import cn.trawe.etc.hunanfront.feign.v2.IssueCenterApi;
import cn.trawe.etc.hunanfront.request.secondissue.SecondIssueReq;
import cn.trawe.etc.hunanfront.request.secondissue.SecondIssueResp;
import cn.trawe.etc.hunanfront.service.QueryMonitorServiceImpl;
import cn.trawe.etc.hunanfront.service.ThirdPartnerService;
import cn.trawe.etc.hunanfront.service.secondissue.analysis.ApduHeadAnalysisService;
import cn.trawe.etc.hunanfront.service.secondissue.analysis.CardAnalysisService;
import cn.trawe.etc.hunanfront.service.secondissue.analysis.ObuAnalysisService;
import cn.trawe.etc.hunanfront.service.secondissue.analysis.RandAnalysisService;
import cn.trawe.etc.hunanfront.service.secondissue.analysis.Sw1Sw2AnalysisService;
import cn.trawe.etc.hunanfront.service.secondissue.apdu.CardInfoApdu;
import cn.trawe.etc.hunanfront.service.secondissue.apdu.ObuInfoApdu;
import cn.trawe.etc.hunanfront.service.secondissue.response.ApduCardInner;
import cn.trawe.etc.hunanfront.service.secondissue.response.ApduCardResp;
import cn.trawe.etc.hunanfront.service.secondissue.response.ApduObuInner;
import cn.trawe.etc.hunanfront.service.secondissue.response.ApduObuResp;
import cn.trawe.etc.hunanfront.utils.HexUtils;
import cn.trawe.etc.route.expose.request.issuesecond.SecondActiveUploadRequest;
import cn.trawe.etc.route.expose.response.issuesecond.SecondActiveUploadResponse;
import cn.trawe.pay.expose.entity.EtcIssueOrder;
import cn.trawe.pay.expose.entity.IssueEtcCard;
import cn.trawe.pay.expose.request.issue.IssueOrderQueryReq;
import cn.trawe.pay.expose.request.issue.QueryByCardNoReq;
import cn.trawe.pay.expose.request.issue.QueryByOrderReq;
import cn.trawe.pay.expose.request.secondissue.ActivationCheckReq;
import cn.trawe.pay.expose.request.secondissue.ActivationQueryReq;
import cn.trawe.pay.expose.request.secondissue.CardActionReq;
import cn.trawe.pay.expose.request.secondissue.CardWriteReq;
import cn.trawe.pay.expose.request.secondissue.TagActionReq;
import cn.trawe.pay.expose.request.secondissue.TagWriteReq;
import cn.trawe.pay.expose.response.GlobalResponse;
import cn.trawe.pay.expose.response.issue.IssueEtcCardResp;
import cn.trawe.pay.expose.response.issue.IssueOrderQueryResp;
import cn.trawe.pay.expose.response.issue.NullResp;
import cn.trawe.pay.expose.response.secondissue.ActivationCheckResp;
import cn.trawe.pay.expose.response.secondissue.ActivationQueryResp;
import cn.trawe.pay.expose.response.secondissue.CardActionResp;
import cn.trawe.pay.expose.response.secondissue.CardWriteResp;
import cn.trawe.pay.expose.response.secondissue.TagActionResp;
import cn.trawe.pay.expose.response.secondissue.TagWriteResp;
import cn.trawe.util.LogUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BaseBussinessService {


	@Autowired
	protected IssueCenterApi IssueCenterApi;
	@Value("${api.token}")
	protected String token;
	
	 @Autowired
	 protected QueryMonitorServiceImpl queryMonitorServiceImpl;
	
	
	

	@Autowired
	protected CardAnalysisService  CardAnalysisService;

	@Autowired
	protected ObuAnalysisService  ObuAnalysisService;

	@Autowired
	protected RandAnalysisService  	RandAnalysisService;

	@Autowired
	protected Sw1Sw2AnalysisService  	Sw1Sw2AnalysisService;

	@Autowired
	protected BaseBussinessApduFlagImpl  BaseBussinessApduFlagImpl;

	@Autowired
	protected ApduHeadAnalysisService  	ApduHeadAnalysisService;

	@Autowired
	ThirdPartnerService ThirdPartnerService;

	//选择OBU 3F00 目录
	protected ApduObuResp obuRespChoose3F00 = new ApduObuResp( new ApduObuInner("00A40000023F00"));


	//选择卡片3F00 目录
	protected ApduCardResp cardRespChoose3F00 = new ApduCardResp(new ApduCardInner ("00A40000023F00"));


	//选择卡片1001目录
	protected ApduCardResp cardRespChoose1001 = new ApduCardResp( new ApduCardInner ("00A40000021001"));


	//选择卡片3F00 目录 0016 就在此目录
	protected ApduCardResp cardRespChoose0016 = new ApduCardResp(new ApduCardInner ("00A40000023F00"));


	//读取当前目录随机数指令
	protected ApduCardResp cardResp0016ReadRandom = new ApduCardResp(new ApduCardInner ("0084000004"));


	//定义读取OBU 信息指令
	protected ApduObuResp obuRespReadSystem = new ApduObuResp(new ApduObuInner ("00B081001B"));

	//定义读取0015卡信息指令
	protected ApduCardResp cardRespReadCard = new ApduCardResp(new ApduCardInner ("00B0950029"));


	protected String APDU_FLAG_TRUE ="1";


	protected int TOTAL_STEP =8;



	//写卡上报
	@BizDigestLog(bizType = "BaseBussinessService", version = "1.0", state = BizDigestLog.State.START)
	protected boolean cardAction(SecondIssueReq req,String isSuccess,int type,String kind) {
		CardActionReq cardReq = new CardActionReq();
		if(StringUtils.isBlank(req.getCardNo())||StringUtils.isBlank(req.getObuNo())) {
			if(org.apache.commons.lang3.StringUtils.isNotBlank(req.getOriginValue())) {
				JSONObject obj = JSON.parseObject(req.getOriginValue());
				ObuInfoApdu oubInfo = JSON.parseObject(obj.getString("obuInfo"), ObuInfoApdu.class);
				CardInfoApdu cardInfo = JSON.parseObject(obj.getString("cardInfo"), CardInfoApdu.class);
				cardReq.setCardNo(cardInfo.getCardNetNumber()+cardInfo.getCardNumber());
				req.setCardNo(cardInfo.getCardNetNumber()+cardInfo.getCardNumber());
				req.setObuNo(oubInfo.getContractNo());
			}
		}
		
		//判断是否存在卡号
		QueryByOrderReq reqQueryCardByOrderNo = new QueryByOrderReq();
		reqQueryCardByOrderNo.setOrderNo(req.getOrderId());
		reqQueryCardByOrderNo.setOwnerCode(4301);
		LogUtil.info(log, req.getOrderId(), "查询卡信息请求", JSON.toJSONString(reqQueryCardByOrderNo));
		IssueEtcCardResp<IssueEtcCard> cardRespByOrderNo = IssueCenterApi.queryByOrderNo(reqQueryCardByOrderNo);
		LogUtil.info(log, req.getOrderId(), "查询卡信息响应", cardRespByOrderNo);
		if(cardRespByOrderNo.getCode()!=0) {
        	throw new WriteObuException("查询卡信息失败,请稍后重试");
        }
		if(cardRespByOrderNo.getResult()==null) {
			throw new WriteObuException("卡表记录不存在,请确认订单号是否正确");
		}
		if(cardRespByOrderNo.getResult()!=null) {
			if(StringUtils.isBlank(cardRespByOrderNo.getResult().getCardNo())) {
				throw new WriteObuException("卡号不存在,写卡指令未下发请确认系统调用步骤");
			}
			
		}
		//激活进度查询
		ActivationQueryReq actReq = new ActivationQueryReq();
		actReq.setKind(kind);
		actReq.setOrderNo(req.getOrderId());
		actReq.setOwnerCode("4301");
		ActivationQueryResp activationQuery = IssueCenterApi.activationQuery(actReq);
		
		if(activationQuery.getCode()!=0) {
			throw new WriteObuException("查询激活进度失败,请稍后重试");  
		}
		//type :2----》0016
		//tyep :1----》0015
		if(1==type&&isSuccess.equals("true")) {
			//0016 必须写成功
			if("1".equals(activationQuery.getCardStatus())) {
				return true;
			}
			else if("2".equals(activationQuery.getCardStatus())) {
				cardReq.setType(type);
				cardReq.setOrderNo(req.getOrderId());
				cardReq.setKind(kind);
				cardReq.setResult(isSuccess);
				cardReq.setOwnerCode("4301");
				LogUtil.info(log,req.getOrderId(), "写卡上报请求:"+JSON.toJSONString(cardReq));
				CardActionResp cardAction = IssueCenterApi.cardAction(cardReq);
				LogUtil.info(log,req.getOrderId(), "写卡上报响应:"+JSON.toJSONString(cardAction));
				if(cardAction.getCode()!=0) {
					throw new  NoticeException(cardAction.getMsg());
				}
				return true;
			}
			else if(!"3".equals(activationQuery.getCardStatus())) {
				
				throw new WriteObuException("写0016文件未完成,不能上报写0015信息,请确认系统调用步骤");
				
			}
		}

		
		//特殊处理
		if("6988".equals(isSuccess)) {
			cardReq.setType(type);
			cardReq.setOrderNo(req.getOrderId());
			cardReq.setKind(kind);
			cardReq.setResult("false");
			cardReq.setOwnerCode("4301");
			cardReq.setFinishStatusCode("6988");
			LogUtil.info(log,req.getOrderId(), "写卡上报接口请求:"+JSON.toJSONString(cardReq));
			CardActionResp cardAction = IssueCenterApi.cardAction(cardReq);
			LogUtil.info(log,req.getOrderId(), "写卡上报接口响应::"+JSON.toJSONString(cardAction));
			if(cardAction.getCode()!=0) {
				throw new  NoticeException(cardAction.getMsg());
			}
			return false;
		}
		if("9999".equals(isSuccess)) {
			cardReq.setType(type);
			cardReq.setOrderNo(req.getOrderId());
			cardReq.setKind(kind);
			cardReq.setResult("true");
			cardReq.setOwnerCode("4301");
			cardReq.setCardNo(req.getCardNo());
		
			LogUtil.info(log,req.getOrderId(), "写卡上报接口请求:"+JSON.toJSONString(cardReq));
			CardActionResp cardAction = IssueCenterApi.cardAction(cardReq);
			LogUtil.info(log,req.getOrderId(), "写卡上报接口响应::"+JSON.toJSONString(cardAction));
			if(cardAction.getCode()!=0) {
				throw new  NoticeException(cardAction.getMsg());
			}
			return true;
		}
		cardReq.setType(type);
		cardReq.setOrderNo(req.getOrderId());
		cardReq.setKind(kind);
		cardReq.setResult(isSuccess);
		cardReq.setOwnerCode("4301");
		LogUtil.info(log,req.getOrderId(), "写卡上报请求:"+JSON.toJSONString(cardReq));
		CardActionResp cardAction = IssueCenterApi.cardAction(cardReq);
		LogUtil.info(log,req.getOrderId(), "写卡上报响应:"+JSON.toJSONString(cardAction));
		if(cardAction.getCode()!=0) {
			throw new  NoticeException(cardAction.getMsg());
		}
		return true;
	}
	//写卡
	@BizDigestLog(bizType = "BaseBussinessService", version = "1.0", state = BizDigestLog.State.START)
	protected String cardWrite(SecondIssueReq req,String fileType,String random,CardInfoApdu card,String orginValue) {

		CardWriteReq cardWriteReq = new CardWriteReq();
		if(StringUtils.isBlank(req.getCardNo())||StringUtils.isBlank(req.getObuNo())) {
			if(org.apache.commons.lang3.StringUtils.isNotBlank(req.getOriginValue())) {
				JSONObject obj = JSON.parseObject(req.getOriginValue());
				ObuInfoApdu oubInfo = JSON.parseObject(obj.getString("obuInfo"), ObuInfoApdu.class);
				CardInfoApdu cardInfo = JSON.parseObject(obj.getString("cardInfo"), CardInfoApdu.class);
				cardWriteReq.setCardNo(cardInfo.getCardNetNumber()+cardInfo.getCardNumber());
				cardWriteReq.setObuNo(oubInfo.getContractNo());
				req.setCardNo(cardInfo.getCardNetNumber()+cardInfo.getCardNumber());
				req.setObuNo(oubInfo.getContractNo());
			}
		}


		cardWriteReq.setOrderNo(req.getOrderId());
		cardWriteReq.setOwnerCode(4301);
		cardWriteReq.setCardNo(req.getCardNo());
		cardWriteReq.setObuNo(req.getObuNo());
		cardWriteReq.setRandomNum(random.toUpperCase());
		if(org.apache.commons.lang3.StringUtils.isNotBlank(orginValue)) {
			JSONObject obj = JSON.parseObject(orginValue);
			CardInfoApdu cardInfo = JSON.parseObject(obj.getString("cardInfo"), CardInfoApdu.class);
			cardWriteReq.setCardVer(Integer.valueOf(cardInfo.getCardVersion()
			));
		}
		if(card!=null&&!"".equals(card.getCardVersion())) {
			cardWriteReq.setCardVer(Integer.valueOf(card.getCardVersion()));
		}
		cardWriteReq.setType(fileType);
		cardWriteReq.setKind("0");
		updateEtcCard(req, cardWriteReq.getType(), "", false);
		LogUtil.info(log,req.getOrderId(), "写卡请求:"+JSON.toJSONString(cardWriteReq));
		CardWriteResp cardWrite = IssueCenterApi.cardWrite(cardWriteReq);
		LogUtil.info(log,req.getOrderId(), "写卡响应:"+JSON.toJSONString(cardWrite));
		if(cardWrite.getCode()!=0) {
			throw new WriteObuException(cardWrite.getMsg());
		}
		String dataMac = cardWrite.getDataMac();
		if("2".equals(fileType)) {
			addEtcCard(req,"true",2);
		}


		return dataMac;

	}
	//写OBU
	@BizDigestLog(bizType = "BaseBussinessService", version = "1.0", state = BizDigestLog.State.START)
	protected String obuWrite(SecondIssueReq req,String fileType,String random) {
		TagWriteReq tagWriteReq = new TagWriteReq();
		if(StringUtils.isBlank(req.getCardNo())||StringUtils.isBlank(req.getObuNo())) {
			if(org.apache.commons.lang3.StringUtils.isNotBlank(req.getOriginValue())) {
				JSONObject obj = JSON.parseObject(req.getOriginValue());
				ObuInfoApdu oubInfo = JSON.parseObject(obj.getString("obuInfo"), ObuInfoApdu.class);
				CardInfoApdu cardInfo = JSON.parseObject(obj.getString("cardInfo"), CardInfoApdu.class);
				tagWriteReq.setCardNo(cardInfo.getCardNetNumber()+cardInfo.getCardNumber());
				tagWriteReq.setObuNo(oubInfo.getContractNo());
				req.setCardNo(cardInfo.getCardNetNumber()+cardInfo.getCardNumber());
				req.setObuNo(oubInfo.getContractNo());
			}
		}
		tagWriteReq.setOrderNo(req.getOrderId());
		tagWriteReq.setCardNo(req.getCardNo());
		tagWriteReq.setObuNo(req.getObuNo());
		tagWriteReq.setKind("0");
		tagWriteReq.setOwnerCode("4301");
		tagWriteReq.setRandomForMac(random.toUpperCase());
		tagWriteReq.setFileType(fileType);
		if(org.apache.commons.lang3.StringUtils.isNotBlank(req.getOriginValue())) {
			JSONObject obj = JSON.parseObject(req.getOriginValue());
			ObuInfoApdu cardInfo = JSON.parseObject(obj.getString("obuInfo"), ObuInfoApdu.class);
			tagWriteReq.setEsamVersion(cardInfo.getContractVersion());
			tagWriteReq.setEsamType(cardInfo.getTreatyType());
			String providerCode ="湖南";
			byte[] hexStringToBytes = HexUtils.hexStringToBytes(cardInfo.getProviderCode().substring(0, 8));
			try {
				providerCode= new String(hexStringToBytes,"GBK");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				log.error(e.getMessage(),e.fillInStackTrace());
				providerCode ="湖南";
			}
			tagWriteReq.setSupplier(providerCode);
		}

		//判断卡号是否一致
		updateEtcCard(req, fileType, tagWriteReq.getFileType(),false);
		LogUtil.info(log,"基础服务请求报文：{}", JSON.toJSONString(tagWriteReq));
		TagWriteResp tagWrite = IssueCenterApi.tagWrite(tagWriteReq);
		LogUtil.info(log,"基础服务返回报文：{}", JSON.toJSONString(tagWrite));
		if(tagWrite.getCode()!=0) {
			throw new WriteObuException(tagWrite.getMsg());
		}
		if("1".equals(tagWriteReq.getFileType())) {
			//车辆文件
			updateEtcCard(req, "", fileType,true);
			String cmdWriteVehicleInfo = tagWrite.getCmdWriteVehicleInfo();
			return cmdWriteVehicleInfo;
		}

		if("2".equals(tagWriteReq.getFileType())) {
			//系统文件
			updateEtcCard(req, "", fileType,false);
			String cmdWriteSystemInfo = tagWrite.getCmdWriteSystemInfo();
			return cmdWriteSystemInfo;
		}
		throw new WriteObuException("OBU写指令不支持的文件类型");

	}
	//写OBU上报
	@BizDigestLog(bizType = "BaseBussinessService", version = "1.0", state = BizDigestLog.State.START)
	protected boolean tagAction(SecondIssueReq req,String fileType,String isSuccess,String kind) {
		
		
		//增加判断是否存在OBU号
		QueryByOrderReq reqQueryCardByOrderNo = new QueryByOrderReq();
		reqQueryCardByOrderNo.setOrderNo(req.getOrderId());
		reqQueryCardByOrderNo.setOwnerCode(4301);
		LogUtil.info(log, req.getOrderId(), "查询卡信息请求", JSON.toJSONString(reqQueryCardByOrderNo));
		IssueEtcCardResp<IssueEtcCard> cardRespByOrderNo = IssueCenterApi.queryByOrderNo(reqQueryCardByOrderNo);
		LogUtil.info(log, req.getOrderId(), "查询卡信息响应", cardRespByOrderNo);
		if(cardRespByOrderNo.getCode()!=0) {
        	throw new WriteObuException("查询卡信息失败,请稍后重试");
        }
		if(cardRespByOrderNo.getResult()==null) {
			throw new WriteObuException("卡表记录不存在,请确认订单号是否正确");
		}
		if(cardRespByOrderNo.getResult()!=null) {
			if(StringUtils.isBlank(cardRespByOrderNo.getResult().getObuCode())) {
				throw new WriteObuException("OBU号不存在,写OBU指令未下发请确认系统调用步骤");
			}
			
		}
		
		//查询激活步骤
		ActivationQueryReq actReq = new ActivationQueryReq();
		actReq.setKind(kind);
		actReq.setOrderNo(req.getOrderId());
		actReq.setOwnerCode("4301");
		ActivationQueryResp activationQuery = IssueCenterApi.activationQuery(actReq);
		
		if(activationQuery.getCode()!=0) {
			throw new WriteObuException("查询激活进度失败,请稍后重试");  
		}
		
		if("1".equals(fileType)) {
			//车辆文件
			if(!activationQuery.getCardStatus().equals("1")) {
				throw new  NoticeException("写0015信息未完成,不能上报写车辆信息,请确认系统调用步骤");
			}
		}
		if("2".equals(fileType)) {
			//系统文件
			if(!activationQuery.getVehicleStatus().equals("1")) {
				throw new  NoticeException("写车辆信息未完成,不能上报写系统信息,请确认系统调用步骤");
			}
		}

		TagActionReq tagAction = new TagActionReq();
		if(StringUtils.isBlank(req.getCardNo())||StringUtils.isBlank(req.getObuNo())) {
			if(org.apache.commons.lang3.StringUtils.isNotBlank(req.getOriginValue())) {
				JSONObject obj = JSON.parseObject(req.getOriginValue());
				ObuInfoApdu oubInfo = JSON.parseObject(obj.getString("obuInfo"), ObuInfoApdu.class);
				CardInfoApdu cardInfo = JSON.parseObject(obj.getString("cardInfo"), CardInfoApdu.class);
				tagAction.setCardNo(cardInfo.getCardNetNumber()+cardInfo.getCardNumber());
				tagAction.setObuNo(oubInfo.getContractNo());
				req.setCardNo(cardInfo.getCardNetNumber()+cardInfo.getCardNumber());
				req.setObuNo(oubInfo.getContractNo());
			}
		}
		//特殊处理
		if("6988".equals(isSuccess)) {
			tagAction.setObuNo(req.getObuNo());
			tagAction.setCardNo(req.getCardNo());
			tagAction.setOrderNo(req.getOrderId());
			tagAction.setOwnerCode("4301");
			tagAction.setFileType(fileType);
			tagAction.setKind(kind);
			tagAction.setResult("false");
			tagAction.setFinishStatusCode("6988");
			LogUtil.info(log,req.getOrderId(), "写OBU上报接口请求:"+JSON.toJSONString(tagAction));
			TagActionResp tagActionResp = IssueCenterApi.tagAction(tagAction);
			LogUtil.info(log,req.getOrderId(), "写OBU上报接口响应::"+JSON.toJSONString(tagActionResp));
			if(tagActionResp.getCode()!=0) {
				throw new  NoticeException(tagActionResp.getMsg());
			}
			return false;
		}
		tagAction.setObuNo(req.getObuNo());
		tagAction.setCardNo(req.getCardNo());
		tagAction.setOrderNo(req.getOrderId());
		tagAction.setOwnerCode("4301");
		tagAction.setFileType(fileType);
		tagAction.setKind(kind);
		//根据新需求处理
		//查询渠道用户名和密码
//		ThirdPartner partner = ThirdPartnerService.getPartnerByChannelNo(req.getChannelNo());
//        String accountNo = partner.getAccountNo();
//      	String password = partner.getPassword();
//    	JSONObject object = new JSONObject();
//      	object.put("Acount", accountNo);
//      	object.put("Password", password);
//      	tagAction.setPartnerId(object.toJSONString());
		//tagAction.setPartnerId(req.getChannelNo());
		tagAction.setResult(isSuccess);
		LogUtil.info(log,req.getOrderId(), "写OBU上报接口请求:"+JSON.toJSONString(tagAction));
		TagActionResp tagActionResp = IssueCenterApi.tagAction(tagAction);
		LogUtil.info(log,req.getOrderId(), "写OBU上报接口响应::"+JSON.toJSONString(tagActionResp));
		if(tagActionResp.getCode()!=0) {
			throw new NoticeException(tagActionResp.getMsg());
		}
		return true;
	}

	//获取激活指令
	@BizDigestLog(bizType = "BaseBussinessService", version = "1.0", state = BizDigestLog.State.START)
	protected String activationCheck(SecondIssueReq req,String fileType,String random,String VehicleInfoOfEncrypt) {

		ActivationCheckReq actCheckReq = new ActivationCheckReq();
		actCheckReq.setCardNo(req.getCardNo());
		actCheckReq.setObuNo(req.getObuNo());
		actCheckReq.setKind("1");
		actCheckReq.setOwnerCode("4301");
		//{"random:" :"00A40090","":"vehicelContent":"","startTime":"","expireTime":""}
		actCheckReq.setRandom(random);
		actCheckReq.setOrderNo(req.getOrderId());
		if(org.apache.commons.lang3.StringUtils.isNotBlank(req.getOriginValue())) {
			JSONObject obj = JSON.parseObject(req.getOriginValue());
			ObuInfoApdu obuInfo = JSON.parseObject(obj.getString("obuInfo"), ObuInfoApdu.class);
			CardInfoApdu cardInfo = JSON.parseObject(obj.getString("cardInfo"), CardInfoApdu.class);
			actCheckReq.setEsamStartTime(obuInfo.getSignedDate());
			actCheckReq.setEsamExpireTime(obuInfo.getExpiredDate());
			actCheckReq.setEsamVersion(obuInfo.getContractVersion());
			
		}
		actCheckReq.setVehicleInfoOfEncrypt(VehicleInfoOfEncrypt);
		LogUtil.info(log,req.getOrderId(), "重新激活请求:"+JSON.toJSONString(actCheckReq));
		ActivationCheckResp activationCheck = IssueCenterApi.activationCheck(actCheckReq);
		LogUtil.info(log,req.getOrderId(), "重新激活响应:"+JSON.toJSONString(activationCheck));
		if(activationCheck.getCode()!=0) {
			throw new ActivationObuException(activationCheck.getMsg());
		}
		return activationCheck.getInfo();
	}

	//激活上报
	@BizDigestLog(bizType = "BaseBussinessService", version = "1.0", state = BizDigestLog.State.START)
	protected boolean activationAction(SecondIssueReq req,String fileType,int isSuccess) {

		SecondActiveUploadRequest activeUpload = new SecondActiveUploadRequest();
		activeUpload.setCardNo(req.getCardNo());
		activeUpload.setObuNo(req.getObuNo());
		activeUpload.setKind(1);
		activeUpload.setOrderNo(req.getOrderId());
		activeUpload.setOwnerCode("4301");

		activeUpload.setActiveStatus(isSuccess);
		LogUtil.info(log,req.getOrderId(), "重新激活上报请求:"+JSON.toJSONString(activeUpload));
		SecondActiveUploadResponse activeAction = IssueCenterApi.activeAction(activeUpload);
		LogUtil.info(log,req.getOrderId(), "重新激活上报响应:"+JSON.toJSONString(activeAction));
		if(activeAction.getCode()!=0) {
			throw new ActivationObuException(activeAction.getMsg());
		}
		return true;
	}

	@BizDigestLog(bizType = "BaseBussinessService", version = "1.0", state = BizDigestLog.State.START)
	protected SecondIssueResp createRead0016RandomResp(boolean isAndReadCard,boolean isAndReadObu) {
		Map<String,Object> apduList = new HashMap<String,Object>();
		SecondIssueResp  secondResp = new SecondIssueResp();
		secondResp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
		secondResp.setCurStep(0);
		secondResp.setTotalStep(10);
		secondResp.setErrorCode(InterfaceErrorCode.RESULT_OK.getValue());
		secondResp.setErrorMsg(InterfaceErrorCode.RESULT_OK_INFO.getValue());
		ApduCardResp cardRespChoose = new ApduCardResp();
		ApduCardInner innerChoose = new ApduCardInner ("00A40000023F00");
		cardRespChoose.setInner(innerChoose);
		ApduCardResp cardResp0016 = new ApduCardResp();
		ApduCardInner inner0016 = new ApduCardInner ("0084000004");
		cardResp0016.setInner(inner0016);
		apduList.put("0", obuRespReadSystem);
		apduList.put("1", cardRespReadCard);
		apduList.put("2",cardRespChoose);
		apduList.put("3",cardResp0016);
		secondResp.setRespInfo(apduList);
		return secondResp;

	}
	@BizDigestLog(bizType = "BaseBussinessService", version = "1.0", state = BizDigestLog.State.START)
	protected SecondIssueResp createRead0015RandomResp() {

		Map<String,Object> apduList = new HashMap<String,Object>();
		SecondIssueResp  secondResp = new SecondIssueResp();
		secondResp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
		secondResp.setCurStep(2);
		secondResp.setTotalStep(TOTAL_STEP);
		secondResp.setErrorCode(InterfaceErrorCode.RESULT_OK.getValue());
		secondResp.setErrorMsg(InterfaceErrorCode.RESULT_OK_INFO.getValue());
		ApduCardResp cardRespChoose = new ApduCardResp();
		ApduCardInner innerChoose = new ApduCardInner ("00A40000021001");
		cardRespChoose.setInner(innerChoose);
		ApduCardResp cardResp0015 = new ApduCardResp();
		ApduCardInner inner0015 = new ApduCardInner ("0084000004");
		cardResp0015.setInner(inner0015);
		apduList.put("0",cardRespChoose);
		apduList.put("1",cardResp0015);
		secondResp.setRespInfo(apduList);

		return secondResp;
	}

	protected SecondIssueResp createWrite0016Resp(String dataMac) {

		Map<String,Object> apduList = new HashMap<String,Object>();
		ApduCardResp cardRespCard0016 = new ApduCardResp();
		ApduCardInner innerCard = new ApduCardInner (dataMac);
		cardRespCard0016.setInner(innerCard);
		SecondIssueResp  secondResp = new SecondIssueResp();
		secondResp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
		secondResp.setCurStep(1);
		secondResp.setTotalStep(TOTAL_STEP);
		secondResp.setErrorCode(InterfaceErrorCode.RESULT_OK.getValue());
		secondResp.setErrorMsg(InterfaceErrorCode.RESULT_OK_INFO.getValue());
		apduList.put("0",cardRespCard0016);
		secondResp.setRespInfo(apduList);
		return secondResp;
	}

	@BizDigestLog(bizType = "BaseBussinessService", version = "1.0", state = BizDigestLog.State.START)
	protected SecondIssueResp createWrite0015Resp(String dataMac) {
		//组装响应指令报文  cur_step =3
		//获取MAC 成功 组装 写卡指令响应报文
		SecondIssueResp  secondResp = new SecondIssueResp();
		Map<String,Object> apduList = new HashMap<String,Object>();
		ApduCardResp cardRespCard0015 = new ApduCardResp();
		ApduCardInner innerCard = new ApduCardInner (dataMac);
		cardRespCard0015.setInner(innerCard);
		secondResp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
		secondResp.setCurStep(3);
		secondResp.setTotalStep(TOTAL_STEP);
		secondResp.setErrorCode(InterfaceErrorCode.RESULT_OK.getValue());
		secondResp.setErrorMsg(InterfaceErrorCode.RESULT_OK_INFO.getValue());
		apduList.put("0",cardRespCard0015);
		secondResp.setRespInfo(apduList);
		return secondResp;
	}
	@BizDigestLog(bizType = "BaseBussinessService", version = "1.0", state = BizDigestLog.State.START)
	protected SecondIssueResp createReadVehicleRandomResp() {
		SecondIssueResp  secondResp = new SecondIssueResp();
		Map<String,Object> apduList = new HashMap<String,Object>();
		secondResp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
		secondResp.setCurStep(4);
		secondResp.setTotalStep(TOTAL_STEP);
		secondResp.setErrorCode(InterfaceErrorCode.RESULT_OK.getValue());
		secondResp.setErrorMsg(InterfaceErrorCode.RESULT_OK_INFO.getValue());
		ApduObuResp obuRespChoose = new ApduObuResp();
		ApduObuInner innerChoose = new ApduObuInner ("00A4000002DF01");
		obuRespChoose.setInner(innerChoose);
		ApduObuResp obuRespVeh = new ApduObuResp();
		ApduObuInner innerVeh = new ApduObuInner ("0084000004");
		obuRespVeh.setInner(innerVeh);
		apduList.put("0",obuRespChoose);
		apduList.put("1",obuRespVeh);
		secondResp.setRespInfo(apduList);
		return secondResp;
	}
	@BizDigestLog(bizType = "BaseBussinessService", version = "1.0", state = BizDigestLog.State.START)
	protected SecondIssueResp createReadSystemRandomResp() {
		SecondIssueResp  secondResp = new SecondIssueResp();
		Map<String,Object> apduList = new HashMap<String,Object>();
		secondResp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
		secondResp.setCurStep(6);
		secondResp.setTotalStep(TOTAL_STEP);
		secondResp.setErrorCode(InterfaceErrorCode.RESULT_OK.getValue());
		secondResp.setErrorMsg(InterfaceErrorCode.RESULT_OK_INFO.getValue());
		ApduObuResp obuRespChoose = new ApduObuResp();
		ApduObuInner innerChoose = new ApduObuInner ("00A40000023F00");
		obuRespChoose.setInner(innerChoose);
		ApduObuResp obuRespSys = new ApduObuResp();
		ApduObuInner innerSys = new ApduObuInner ("0084000004");
		obuRespSys.setInner(innerSys);
		apduList.put("0",obuRespChoose);
		apduList.put("1",obuRespSys);
		secondResp.setRespInfo(apduList);
		return secondResp;
	}
	@BizDigestLog(bizType = "BaseBussinessService", version = "1.0", state = BizDigestLog.State.START)
	protected SecondIssueResp createWriteVehicleResp(String dataMac) {

		SecondIssueResp  secondResp = new SecondIssueResp();
		Map<String,Object> apduList = new HashMap<String,Object>();
		ApduObuResp obuVehcleResp = new ApduObuResp();
		ApduObuInner innerObu = new ApduObuInner (dataMac);
		obuVehcleResp.setInner(innerObu);
		secondResp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
		secondResp.setCurStep(5);
		secondResp.setTotalStep(TOTAL_STEP);
		secondResp.setErrorCode(InterfaceErrorCode.RESULT_OK.getValue());
		secondResp.setErrorMsg(InterfaceErrorCode.RESULT_OK_INFO.getValue());
		apduList.put("0",obuVehcleResp);
		secondResp.setRespInfo(apduList);
		return secondResp;
	}
	@BizDigestLog(bizType = "BaseBussinessService", version = "1.0", state = BizDigestLog.State.START)
	protected SecondIssueResp createWriteSystemResp(String dataMac) {

		SecondIssueResp  secondResp = new SecondIssueResp();
		Map<String,Object> apduList = new HashMap<String,Object>();
		ApduObuResp obuVehcleResp = new ApduObuResp();
		ApduObuInner innerObu = new ApduObuInner (dataMac);
		obuVehcleResp.setInner(innerObu);
		secondResp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
		secondResp.setCurStep(7);
		//TODO
		secondResp.setTotalStep(TOTAL_STEP);
		//secondResp.setTotalStep(10);
		secondResp.setErrorCode(InterfaceErrorCode.RESULT_OK.getValue());
		secondResp.setErrorMsg(InterfaceErrorCode.RESULT_OK_INFO.getValue());
		apduList.put("0",obuVehcleResp);
		//解析OBU 指令
		secondResp.setRespInfo(apduList);
		return secondResp;
	}
	@BizDigestLog(bizType = "BaseBussinessService", version = "1.0", state = BizDigestLog.State.START)
	protected SecondIssueResp createWriteActiveRandomResp(String encryptRandom) {
		SecondIssueResp  secondResp = new SecondIssueResp();
		Map<String,Object> apduList = new HashMap<String,Object>();
		secondResp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
		secondResp.setCurStep(1);
		secondResp.setTotalStep(3);
		secondResp.setErrorCode(InterfaceErrorCode.RESULT_OK.getValue());
		secondResp.setErrorMsg(InterfaceErrorCode.RESULT_OK_INFO.getValue());
		//读取车辆密文指令拼装
//		  cmd[0] = '00A4000002DF01';
//		  cmd[1] = '00B400000A' + randomNo + '3B00';
		ApduObuResp obuRespChooseDF01 = new ApduObuResp();
		ApduObuInner innerChooseDF01 = new ApduObuInner("00A4000002DF01");
		obuRespChooseDF01.setInner(innerChooseDF01);
		ApduObuResp obuRespEncrypt = new ApduObuResp();
		ApduObuInner innerEncrypt = new ApduObuInner ("00B400000A"+encryptRandom.toUpperCase()+"3B00");
		obuRespEncrypt.setInner(innerEncrypt);
		ApduObuResp obuRespChoose = new ApduObuResp();
		ApduObuInner innerChoose = new ApduObuInner("00A40000023F00");
		obuRespChoose.setInner(innerChoose);
		ApduObuResp obuRespSys = new ApduObuResp();
		ApduObuInner innerSys = new ApduObuInner ("0084000004");
		obuRespSys.setInner(innerSys);
		//激活随机数指令
		apduList.put("0",obuRespChooseDF01);
		apduList.put("1",obuRespEncrypt);
		apduList.put("2",obuRespChoose);
		apduList.put("3",obuRespSys);
		secondResp.setRespInfo(apduList);

		return secondResp;
	}
	@BizDigestLog(bizType = "BaseBussinessService", version = "1.0", state = BizDigestLog.State.START)
	protected SecondIssueResp createWriteAcitveMacResp(String dataMac) {
		SecondIssueResp  secondResp = new SecondIssueResp();
		Map<String,Object> apduList = new HashMap<String,Object>();
		ApduObuResp obuActiveResp = new ApduObuResp();
		ApduObuInner innerObu = new ApduObuInner (dataMac);
		obuActiveResp.setInner(innerObu);
		secondResp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
		secondResp.setCurStep(2);
		secondResp.setTotalStep(3);
		secondResp.setErrorCode(InterfaceErrorCode.RESULT_OK.getValue());
		secondResp.setErrorMsg(InterfaceErrorCode.RESULT_OK_INFO.getValue());
		apduList.put("0",obuActiveResp);
		secondResp.setRespInfo(apduList);
		return secondResp;
	}

	protected void addEtcCard(SecondIssueReq req,String isSuccess,int type)  {


		IssueOrderQueryReq issueOrderQueryReq = new IssueOrderQueryReq();
        issueOrderQueryReq.setOrderNo(req.getOrderId());
        issueOrderQueryReq.setPageNo(1);
        issueOrderQueryReq.setPageSize(1);
        LogUtil.info(log, req.getOrderId(), "查询订单信息请求:", JSON.toJSONString(issueOrderQueryReq));
        IssueOrderQueryResp issueOrderQueryResp = IssueCenterApi.orderQuery(issueOrderQueryReq, "");
        LogUtil.info(log, req.getOrderId(), "查询订单信息响应:", JSON.toJSONString(issueOrderQueryResp));
        EtcIssueOrder etcIssueOrder = null;
        if (issueOrderQueryResp != null && CollectionUtils.isNotEmpty(issueOrderQueryResp.getResult())) {
            etcIssueOrder = issueOrderQueryResp.getResult().get(0);
        }
        if(etcIssueOrder!=null) {
       	 
       	QueryByCardNoReq reqQueryCard = new QueryByCardNoReq();
    		reqQueryCard.setOwnerCode(4301);
    		reqQueryCard.setCardNo(req.getCardNo());
            LogUtil.info(log, req.getOrderId(), "查询卡信息请求", JSON.toJSONString(reqQueryCard));
            IssueEtcCardResp<IssueEtcCard> cardResp = IssueCenterApi.queryByCardNo(reqQueryCard);
            LogUtil.info(log, req.getOrderId(), "查询卡信息响应", cardResp);
            if(cardResp.getCode()!=0) {
            	throw new WriteObuException("查询卡信息失败,请稍后重试");
            }
    		if(cardResp.getCode()==0&&cardResp.getResult()!=null) {
    			IssueEtcCard result =cardResp.getResult();
    			String orderNo = result.getOrderNo();
    			if(!orderNo.equals(req.getOrderId())) {
    				log.info("存在卡信息但是订单号不匹配");
    				SecondIssueResp  resp  = new SecondIssueResp();
    				resp.setErrorCode(InterfaceErrorCode.SYSTEM_ERROR.getValue());
    				resp.setErrorMsg("卡号: "+result.getCardNo()+"已与车牌号 : "+result.getPlateNo()+"绑定,请更换卡片重新绑定");
    				resp.setSuccess(BaseResponseData.Success.FAILED.toString());
    				throw new WriteObuException("卡号:"+result.getCardNo()+"已与车牌号:"+result.getPlateNo()+"绑定,请更换卡片重新激活");
    			}
    			
            }
    		//通过订单号去查询卡号：
    		QueryByOrderReq reqQueryCardByOrderNo = new QueryByOrderReq();
    		reqQueryCardByOrderNo.setOrderNo(req.getOrderId());
    		reqQueryCardByOrderNo.setOwnerCode(4301);
    		LogUtil.info(log, req.getOrderId(), "查询卡信息请求", JSON.toJSONString(reqQueryCardByOrderNo));
    		IssueEtcCardResp<IssueEtcCard> cardRespByOrderNo = IssueCenterApi.queryByOrderNo(reqQueryCardByOrderNo);
    		LogUtil.info(log, req.getOrderId(), "查询卡信息响应", cardRespByOrderNo);
    		if(cardRespByOrderNo.getCode()==0) {
    			if(cardRespByOrderNo.getResult()!=null) {
    				IssueEtcCard result =cardRespByOrderNo.getResult();
    				String cardNo = result.getCardNo();

    				if(!cardNo.equals(req.getCardNo())) {
    					LogUtil.info(log, req.getOrderId(), "通过订单号查询存在卡信息但是与请求卡号不匹配");
    					throw new WriteObuException("请使用正确的卡片,该车牌"+result.getPlateNo()+"已与卡号:"+cardNo+"绑定,请更换卡片重新激活");
    					//throw new WriteObuException("卡号: "+result.getCardNo()+"已与车牌号 : "+result.getPlateNo()+"绑定,请更换卡片重新激活");
    				}
    				if(StringUtils.isNotBlank(result.getObuCode())) {
    					if(!result.getObuCode().equals(req.getObuNo())) {
    						throw new WriteObuException("请使用正确的OBU设备,该车牌"+result.getPlateNo()+"已与OBU号:"+result.getObuCode()+"绑定,请更换OBU重新激活");
    					}
    				}
    			}
    			

    		}
    		else {
    			throw new WriteObuException("查询卡信息失败,请稍后重试");
    		}
    		
    		if(cardResp.getResult()==null) {
    		   //不存在卡片信息新增
    			 IssueEtcCard card = new IssueEtcCard();
    			 BeanUtils.copyProperties(etcIssueOrder, card);
	             card.setCardNo(req.getCardNo());
	             //card.setObuCode(req.getObuNo());
                 card.setStatus(1);
                 card.setOrderNo(etcIssueOrder.getOrderNo());
	             card.setPartnerId(etcIssueOrder.getPartnerId());
	             card.setCreateTime(new Date());
                 card.setUpdateTime(new Date());
                 LogUtil.info(log, req.getOrderId(), "新增卡信息请求:", JSON.toJSONString(card));
                 GlobalResponse<NullResp> saveOrUpdateEtcCard = IssueCenterApi.saveOrUpdateEtcCard((JSONObject)JSON.toJSON(card));
                 LogUtil.info(log, req.getOrderId(), "新增卡信息响应:", JSON.toJSONString(saveOrUpdateEtcCard));
                if(saveOrUpdateEtcCard.getCode()!=0) {
               	 throw new WriteObuException("新增卡信息失败,请稍后重试");
                }
    		}
    		
        }else {
        	throw new WriteObuException("查询发行订单失败,请稍后重试");
        }
        
		
	}


	protected void updateEtcCard(SecondIssueReq req,String cardfileType,String obuFileType,boolean isUpdate) {

		QueryByCardNoReq reqQueryCard = new QueryByCardNoReq();
		reqQueryCard.setOwnerCode(4301);
		reqQueryCard.setCardNo(req.getCardNo());
        LogUtil.info(log, req.getOrderId(), "查询卡信息请求", JSON.toJSONString(reqQueryCard));
        IssueEtcCardResp<IssueEtcCard> cardResp = IssueCenterApi.queryByCardNo(reqQueryCard);
        LogUtil.info(log, req.getOrderId(), "查询卡信息响应", cardResp);
        if(cardResp.getCode()!=0) {
        	throw new WriteObuException("查询卡信息失败,请稍后重试");
        }
		
		if(cardResp.getResult()!=null) {
				IssueEtcCard result =cardResp.getResult();
				String orderNo = result.getOrderNo();
				if(!orderNo.equals(req.getOrderId())) {
					log.info("存在卡信息但是订单号不匹配");
					SecondIssueResp  resp  = new SecondIssueResp();
					resp.setErrorCode(InterfaceErrorCode.SYSTEM_ERROR.getValue());
					resp.setErrorMsg("该卡号: "+result.getCardNo()+"已与车牌号: "+result.getPlateNo()+" 绑定,请更换卡片重新绑定");
					resp.setSuccess(BaseResponseData.Success.FAILED.toString());
					throw new WriteObuException("该卡号: "+result.getCardNo()+"已与车牌号: "+result.getPlateNo()+" 绑定,请更换卡片重新绑定");
				}
				/*if(StringUtils.isNotBlank(result.getObuCode())) {
					if(!result.getObuCode().equals(req.getObuNo())) {
						//throw new WriteObuException("该OBU号: "+result.getObuCode()+"已与车牌号:"+result.getPlateNo()+"绑定,请更换OBU重新激活");
						throw new WriteObuException("该卡号: "+result.getCardNo()+"已与OBU号:"+result.getObuCode()+"绑定,请更换OBU重新激活");
					}
				}*/
	    }
			
		//通过OBU号查询卡信息
		JSONObject cardJson = new JSONObject();
		cardJson.put("owner_code", 4301);
		cardJson.put("obu_code", req.getObuNo());
		LogUtil.info(log, req.getOrderId(), "查询OBU信息请求", JSON.toJSONString(cardJson));
		GlobalResponse<List<IssueEtcCard>> queryCardByJson = IssueCenterApi.queryCardByJson(cardJson);
		LogUtil.info(log, req.getOrderId(), "查询OBU信息响应", queryCardByJson);
		if(queryCardByJson.getCode()!=0) {
        	throw new WriteObuException("查询OBU信息失败,请稍后重试");
        }
		if(queryCardByJson.getResult()!=null&&queryCardByJson.getResult().size()>=1) {
			IssueEtcCard result =queryCardByJson.getResult().get(0);
			String cardNo = result.getCardNo();
			if(!result.getOrderNo().equals(req.getOrderId())) {
				LogUtil.info(log, req.getOrderId(), "通过OBU号查询存在卡信息但是订单号不匹配");
				//throw new WriteObuException("请使用正确的OBU设备,该车牌"+result.getPlateNo()+"已与OBU号:"+result.getObuCode()+"绑定,请更换OBU重新激活");
				throw new WriteObuException("该OBU号: "+result.getObuCode()+"已与车牌号: "+result.getPlateNo()+" 绑定,请更换OBU重新绑定");
			}
			/*if(!cardNo.equals(req.getCardNo())) {
				LogUtil.info(log, req.getOrderId(), "通过OBU号查询存在卡信息但是与请求卡号不匹配");
				//throw new WriteObuException("请使用正确的卡片,该车牌"+result.getPlateNo()+"已与卡号:"+cardNo+"绑定,请更换卡片重新激活");
				//throw new WriteObuException("卡号: "+result.getCardNo()+"已与车牌号 : "+result.getPlateNo()+"绑定,请更换卡片重新激活");
				throw new WriteObuException("请使用正确的卡片,该OBU号:"+result.getObuCode()+"已与卡号:"+cardNo+"绑定,请更换卡片重新激活");
			}*/
			if(StringUtils.isNotBlank(result.getObuCode())) {
				if(!result.getObuCode().equals(req.getObuNo())) {
					LogUtil.info(log, req.getOrderId(), "通过OBU号查询存在卡信息但是与请求OBU号不匹配");
					throw new WriteObuException("请使用正确的OBU设备,该车牌"+result.getPlateNo()+"已与OBU号:"+result.getObuCode()+"绑定,请更换OBU重新激活");
				}
			}
		}
		
		
		QueryByOrderReq reqQueryCardByOrderNo = new QueryByOrderReq();
		reqQueryCardByOrderNo.setOrderNo(req.getOrderId());
		reqQueryCardByOrderNo.setOwnerCode(4301);
		LogUtil.info(log, req.getOrderId(), "查询卡信息请求", JSON.toJSONString(reqQueryCardByOrderNo));
		IssueEtcCardResp<IssueEtcCard> cardRespByOrderNo = IssueCenterApi.queryByOrderNo(reqQueryCardByOrderNo);
		LogUtil.info(log, req.getOrderId(), "查询卡信息响应", cardRespByOrderNo);
		if(cardRespByOrderNo.getCode()!=0) {
        	throw new WriteObuException("查询卡信息失败,请稍后重试");
        }
		
		if(cardRespByOrderNo.getResult()!=null) {
				IssueEtcCard result =cardRespByOrderNo.getResult();
				String cardNo = result.getCardNo();

				if(!cardNo.equals(req.getCardNo())) {
					LogUtil.info(log, req.getOrderId(), "通过订单号查询存在卡信息但是与请求卡号不匹配");
					throw new WriteObuException("请使用正确的卡片,该车牌"+result.getPlateNo()+"已与卡号:"+cardNo+"绑定,请更换卡片重新激活");
					//throw new WriteObuException("卡号: "+result.getCardNo()+"已与车牌号 : "+result.getPlateNo()+"绑定,请更换卡片重新激活");
				}
				if(StringUtils.isNotBlank(result.getObuCode())) {
					if(!result.getObuCode().equals(req.getObuNo())) {
						throw new WriteObuException("请使用正确的OBU设备,该车牌"+result.getPlateNo()+"已与OBU号:"+result.getObuCode()+"绑定,请更换OBU重新激活");
					}
				}
				//更新OBU 号
				if(isUpdate) {	
					result.setObuCode(req.getObuNo());
					LogUtil.info(log, req.getOrderId(), "更新卡信息请求:", JSON.toJSONString(result));
					GlobalResponse<NullResp> saveOrUpdateEtcCard = IssueCenterApi.saveOrUpdateEtcCard((JSONObject)JSON.toJSON(result));
					LogUtil.info(log, req.getOrderId(), "更新卡信息响应:", JSON.toJSONString(saveOrUpdateEtcCard));
					if(saveOrUpdateEtcCard.getCode()!=0) {
						throw new WriteObuException("更新卡信息失败,请稍后重试");
					}
				}
		}
		
	}
}
