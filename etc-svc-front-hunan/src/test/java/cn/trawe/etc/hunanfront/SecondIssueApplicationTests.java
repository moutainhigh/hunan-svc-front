package cn.trawe.etc.hunanfront;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.trawe.etc.hunanfront.entity.ThirdPartner;
import cn.trawe.etc.hunanfront.enums.InterfaceErrorCode;
import cn.trawe.etc.hunanfront.exception.WriteObuException;
import cn.trawe.etc.hunanfront.expose.v2.BaseRequest;
import cn.trawe.etc.hunanfront.expose.v2.BaseResponse;
import cn.trawe.etc.hunanfront.expose.v2.BaseResponseData;
import cn.trawe.etc.hunanfront.feign.ApiClient;
import cn.trawe.etc.hunanfront.feign.v2.IssueCenterApi;
import cn.trawe.etc.hunanfront.request.ApplyOrderSyncReq;
import cn.trawe.etc.hunanfront.request.secondissue.SecondIssueReq;
import cn.trawe.etc.hunanfront.request.secondissue.SecondIssueResp;
import cn.trawe.etc.hunanfront.service.PickService;
import cn.trawe.etc.hunanfront.service.secondissue.HunanSecondIssueBussinessImpl;
import cn.trawe.etc.hunanfront.service.secondissue.bussiness.BussinessCase1ServiceImpl;
import cn.trawe.etc.hunanfront.service.secondissue.bussiness.BussinessCase2ServiceImpl;
import cn.trawe.etc.hunanfront.service.secondissue.bussiness.BussinessCase3ServiceImpl;
import cn.trawe.etc.hunanfront.service.secondissue.request.ApduInner;
import cn.trawe.etc.hunanfront.service.secondissue.request.ApduReq;
import cn.trawe.etc.route.expose.request.issuesecond.SecondActiveUploadRequest;
import cn.trawe.etc.route.expose.request.issuesecond.SecondIssueOrderRequest;
import cn.trawe.etc.route.expose.response.issuesecond.SecondIssueOrderResponse;
import cn.trawe.pay.expose.entity.IssueEtcCard;
import cn.trawe.pay.expose.entity.SecondIssueProcess;
import cn.trawe.pay.expose.request.issue.QueryByCardNoReq;
import cn.trawe.pay.expose.request.issue.QueryByOrderReq;
import cn.trawe.pay.expose.request.secondissue.ActivationCheckReq;
import cn.trawe.pay.expose.request.secondissue.ActivationQueryReq;
import cn.trawe.pay.expose.response.GlobalResponse;
import cn.trawe.pay.expose.response.issue.IssueEtcCardResp;
import cn.trawe.pay.expose.response.issue.NullResp;
import cn.trawe.pay.expose.response.secondissue.ActivationQueryResp;
import cn.trawe.util.LogUtil;
import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EtcSvcFrontHunanBootstrap.class)
@Slf4j
public class SecondIssueApplicationTests {
   
	@Autowired
	private BussinessCase1ServiceImpl BussinessCase1ServiceImpl;
	
	@Autowired
	private BussinessCase2ServiceImpl BussinessCase2ServiceImpl;
	
	@Autowired
	private BussinessCase3ServiceImpl BussinessCase3ServiceImpl;
	
	
	@Autowired
	HunanSecondIssueBussinessImpl  HunanSecondIssueBussinessImpl;
	
	@Autowired
	PickService  PickService;
	
	
	/*
	 * @Autowired private ApiClient apiClient;
	 * 
	 * 
	 */
	
	@Autowired
	protected IssueCenterApi IssueCenterApi;
	
	
    @Value("${api.token}")
    private String token;
	
    @Test
	public void case0_type1() {
		SecondIssueReq req = new SecondIssueReq();
    	Map<String,ApduReq> info = new HashMap<String,ApduReq>();
    	req.setCurStep(0);
    	req.setOrderId("4319070821255224623");
    	req.setType("1");
//		ActivationQueryReq actReq = new ActivationQueryReq();
//		actReq.setKind("0");
//		actReq.setOrderNo("4319062416004708971");
//		actReq.setOwnerCode("4301");
//		ActivationQueryResp activationQuery = apiClient.activationQuery(actReq, token);
//	    System.out.println("case0 resp :"+JSON.toJSONString(activationQuery));
    	
    	BaseRequest baseReq = new BaseRequest();
    	baseReq.setBizContent(JSON.toJSONString(req));
    	baseReq.setCharset("UTF-8");
    	BaseResponse doService = HunanSecondIssueBussinessImpl.autoIssue(baseReq);
    	System.out.println(JSON.toJSONString(doService));
	}
    
    
    
	@Test
	public void case0() {
		SecondIssueReq req = new SecondIssueReq();
    	Map<String,ApduReq> info = new HashMap<String,ApduReq>();
    	req.setCurStep(0);
    	req.setOrderId("20190505262300347201455444");
//		ActivationQueryReq actReq = new ActivationQueryReq();
//		actReq.setKind("0");
//		actReq.setOrderNo("4319062416004708971");
//		actReq.setOwnerCode("4301");
//		ActivationQueryResp activationQuery = apiClient.activationQuery(actReq, token);
//	    System.out.println("case0 resp :"+JSON.toJSONString(activationQuery));
    	
    	BaseRequest baseReq = new BaseRequest();
    	baseReq.setBizContent(JSON.toJSONString(req));
    	baseReq.setCharset("UTF-8");
    	BaseResponse doService = HunanSecondIssueBussinessImpl.autoIssue(baseReq);
    	System.out.println(JSON.toJSONString(doService));
	}
	
    @Test
    public void case1() {
    	SecondIssueReq req = new SecondIssueReq();
    	Map<String,ApduReq> info = new HashMap<String,ApduReq>();
    	req.setCurStep(1);
    	ApduReq reqApdu = new ApduReq();
    	ApduInner inner = new ApduInner();
    	//卡片内容
    	inner.setCmdType(1);
    	inner.setCmdValue("bafec4cf430100011710430119002325000529032019061220240612cfe64144473435360000000000");
    	reqApdu.setInner(inner);
    	info.put("1",reqApdu);
    	ApduReq reqApdu2 = new ApduReq();
    	ApduInner inner2 = new ApduInner();
    	inner2.setCmdType(1);
    	//OBU 内容
    	inner2.setCmdValue("bafec4cf0001000100004301190220052903201906122029061201");
    	reqApdu2.setInner(inner2);
    	info.put("0",reqApdu2);
    	ApduReq reqApdu3 = new ApduReq();
    	ApduInner inner3 = new ApduInner();
    	inner3.setCmdType(1);
    	inner3.setCmdValue("00A40000023F0000A4009000");
    	reqApdu3.setInner(inner3);
    	info.put("2",reqApdu3);
    	ApduReq reqApdu4 = new ApduReq();
    	ApduInner inner4 = new ApduInner();
    	inner4.setCmdType(1);
    	inner4.setCmdValue("00A40000029000A400");
    	reqApdu4.setInner(inner4);
    	info.put("3",reqApdu4);
    	req.setResultInfo(info);
    	req.setChannelNo("11");
    	req.setOrderId("20190505262300347201455444");
    	req.setType("0");
    	BaseRequest baseReq = new BaseRequest();
    	baseReq.setBizContent(JSON.toJSONString(req));
    	baseReq.setCharset("UTF-8");
    	BaseResponse doService = HunanSecondIssueBussinessImpl.autoIssue(baseReq);
    	System.out.println(JSON.toJSONString(doService));
    }
    
    @Test
    public void case2() {
    	//0016写卡响应  上报0016写成功
    	SecondIssueReq req = new SecondIssueReq();
    	Map<String,ApduReq> info = new HashMap<String,ApduReq>();
    	req.setCurStep(2);
    	ApduReq reqApdu = new ApduReq();
    	ApduInner inner = new ApduInner();
    	inner.setCmdType(1);
    	inner.setCmdValue("9000");
    	reqApdu.setInner(inner);
    	info.put("0", reqApdu);
    	req.setResultInfo(info);
    	req.setChannelNo("11");
    	req.setOrderId("2164319082822373941613");
    	//req.setOutBizNo("36190605152852344");
    	req.setType("0");
    	req.setCardNo("43011900232500052901");
    	req.setObuNo("4301190220052901");
    	BaseRequest baseReq = new BaseRequest();
    	baseReq.setBizContent(JSON.toJSONString(req));
    	baseReq.setCharset("UTF-8");
    	BaseResponse doService = HunanSecondIssueBussinessImpl.autoIssue(baseReq);
        System.out.println("下发读取0015随机数指令");
    	System.out.println(JSON.toJSONString(doService));
    }
    
    @Test
    public void case3() {
    	//获取到0015随机数指令---》发送卡片---》渠道调用----》湖南省网发平台
    	SecondIssueReq req = new SecondIssueReq();
    	Map<String,ApduReq> info = new HashMap<String,ApduReq>();
    	req.setCurStep(3);
    	ApduReq reqApdu = new ApduReq();
    	ApduInner inner = new ApduInner();
    	inner.setCmdType(1);
    	//0015选择目录卡片指令响应
    	inner.setCmdValue("9000");
    	reqApdu.setInner(inner);
    	
    	ApduReq reqApdu2 = new ApduReq();
    	ApduInner inner2 = new ApduInner();
    	inner2.setCmdType(1);
    	//0015随机数卡片响应指令
    	inner2.setCmdValue("00A400009000");
    	reqApdu2.setInner(inner2);
    	info.put("0", reqApdu);
    	info.put("1", reqApdu2);
    	req.setResultInfo(info);
    	req.setChannelNo("11");
    	req.setOrderId("20190505262300347201455444");
    	//req.setOutBizNo("36190605152852344");
    	req.setType("0");
    	req.setCardNo("43011900232500052903");
    	req.setObuNo("4301190220052903");
    	BaseRequest baseReq = new BaseRequest();
    	baseReq.setBizContent(JSON.toJSONString(req));
    	baseReq.setCharset("UTF-8");
    	BaseResponse doService = HunanSecondIssueBussinessImpl.autoIssue(baseReq);
        System.out.println("成功下发 0015 写卡指令");
    	System.out.println(JSON.toJSONString(doService));
    }
    
    @Test
    public void case4() {
    	//写卡0015指令解析
    	SecondIssueReq req = new SecondIssueReq();
    	Map<String,ApduReq> info = new HashMap<String,ApduReq>();
    	req.setCurStep(4);
    	ApduReq reqApdu = new ApduReq();
    	ApduInner inner = new ApduInner();
    	inner.setCmdType(1);
    	inner.setCmdValue("9000");
    	reqApdu.setInner(inner);
    	info.put("0", reqApdu);
    	req.setResultInfo(info);
    	req.setChannelNo("11");
    	req.setOrderId("2164319082822373941613");
    	req.setType("0");
    	req.setCardNo("43011900232500052903");
    	req.setObuNo("4301190220052903");
    	BaseRequest baseReq = new BaseRequest();
    	baseReq.setBizContent(JSON.toJSONString(req));
    	baseReq.setCharset("UTF-8");
    	BaseResponse doService = HunanSecondIssueBussinessImpl.autoIssue(baseReq);
    	System.out.println("成功下发读取车辆随机数指令");
    	System.out.println(JSON.toJSONString(doService));
		//组装读取车辆随机数指令  cur_step =4
    }
    
    @Test
    public void case5() {
    	//上报卡片响应
    	SecondIssueReq req = new SecondIssueReq();
    	Map<String,ApduReq> info = new HashMap<String,ApduReq>();
    	req.setCurStep(5);
    	ApduReq reqApdu = new ApduReq();
    	ApduInner inner = new ApduInner();
    	inner.setCmdType(1);
    	//读取车辆随机数选择目录卡片指令响应
    	inner.setCmdValue("9000");
    	reqApdu.setInner(inner);
    	
    	ApduReq reqApdu2 = new ApduReq();
    	ApduInner inner2 = new ApduInner();
    	inner2.setCmdType(1);
    	//车辆随机数卡片响应指令
    	inner2.setCmdValue("00A400009000");
    	reqApdu2.setInner(inner2);
    	info.put("0", reqApdu);
    	info.put("1", reqApdu2);
    	req.setResultInfo(info);
    	req.setChannelNo("11");
    	req.setOrderId("20190505262300347201455444");
    	//req.setOutBizNo("36190605152852344");
    	req.setType("0");
    	req.setCardNo("43011900232500052903");
    	req.setObuNo("4301190220052903");
    	BaseRequest baseReq = new BaseRequest();
    	baseReq.setBizContent(JSON.toJSONString(req));
    	baseReq.setCharset("UTF-8");
    	BaseResponse doService = HunanSecondIssueBussinessImpl.autoIssue(baseReq);
    	System.out.println("成功下发写车辆信息指令");
    	System.out.println(JSON.toJSONString(doService));
    }
    
    @Test
    public void case6() {
    	//解析写车辆信息指令
    	SecondIssueReq req = new SecondIssueReq();
    	Map<String,ApduReq> info = new HashMap<String,ApduReq>();
    	req.setCurStep(6);
    	ApduReq reqApdu = new ApduReq();
    	ApduInner inner = new ApduInner();
    	inner.setCmdType(1);
    	inner.setCmdValue("9000");
    	reqApdu.setInner(inner);
    	info.put("0", reqApdu);
    	req.setResultInfo(info);
    	req.setChannelNo("11");
    	req.setOrderId("2164319082822373941613");
    	//req.setOutBizNo("36190605152852344");
    	req.setType("0");
    	
    	req.setCardNo("43011900232500052903");
    	req.setObuNo("4301190220052903");
    	BaseRequest baseReq = new BaseRequest();
    	baseReq.setBizContent(JSON.toJSONString(req));
    	baseReq.setCharset("UTF-8");
    	BaseResponse doService = HunanSecondIssueBussinessImpl.autoIssue(baseReq);
    	System.out.println("成功下发读取系统信息随机数指令");
    	System.out.println(JSON.toJSONString(doService));
    	
    }
    
    @Test
    public void case7() {
    	//上报写OBU 系统随机数指令响应
    	SecondIssueReq req = new SecondIssueReq();
    	Map<String,ApduReq> info = new HashMap<String,ApduReq>();
    	req.setCurStep(7);
    	ApduReq reqApdu = new ApduReq();
    	ApduInner inner = new ApduInner();
    	inner.setCmdType(1);
    	//读取车辆随机数选择目录卡片指令响应
    	inner.setCmdValue("9000");
    	reqApdu.setInner(inner);
    	
    	ApduReq reqApdu2 = new ApduReq();
    	ApduInner inner2 = new ApduInner();
    	inner2.setCmdType(1);
    	//车辆随机数卡片响应指令
    	inner2.setCmdValue("00A400009000");
    	reqApdu2.setInner(inner2);
    	info.put("0", reqApdu);
    	info.put("1", reqApdu2);
    	req.setResultInfo(info);
    	req.setChannelNo("11");
    	req.setOrderId("20190505262300347201455444");
    	//req.setOutBizNo("36190605152852344");
    	req.setType("0");
    	req.setCardNo("43011900232500052903");
    	req.setObuNo("4301190220052903");
    	BaseRequest baseReq = new BaseRequest();
    	baseReq.setBizContent(JSON.toJSONString(req));
    	baseReq.setCharset("UTF-8");
    	BaseResponse doService = HunanSecondIssueBussinessImpl.autoIssue(baseReq);
    	System.out.println("成功下发写系统信息指令");
    }
    
    @Test
    public void case8() {
    	//上报写系统信息指令结果
    	SecondIssueReq req = new SecondIssueReq();
    	Map<String,ApduReq> info = new HashMap<String,ApduReq>();
    	req.setCurStep(8);
    	ApduReq reqApdu = new ApduReq();
    	ApduInner inner = new ApduInner();
    	inner.setCmdType(1);
    	inner.setCmdValue("9000");
    	reqApdu.setInner(inner);
    	info.put("0", reqApdu);
    	req.setResultInfo(info);
    	req.setChannelNo("11");
    	req.setOrderId("2164319082822373941613");
    	//req.setOutBizNo("36190605152852344");
    	req.setType("0");
    	req.setCardNo("43011900232500052903");
    	req.setObuNo("4301190220052903");
    	BaseRequest baseReq = new BaseRequest();
    	baseReq.setBizContent(JSON.toJSONString(req));
    	baseReq.setCharset("UTF-8");
    	BaseResponse doService = HunanSecondIssueBussinessImpl.autoIssue(baseReq);
    	System.out.println("流程结束");
    	System.out.println(JSON.toJSONString(doService));
    }
    
    @Test
    public void urlTest() {
    	//新建二发订单
	
    }
    
    @Test
    public void cardQuery() {
    	String orderId ="430122";
    	QueryByCardNoReq reqQueryCard = new QueryByCardNoReq();
		reqQueryCard.setOwnerCode(4301);
		reqQueryCard.setCardNo("43011900232500052903");
        LogUtil.info(log, "", "查询卡信息请求", JSON.toJSONString(reqQueryCard));
        IssueEtcCardResp<IssueEtcCard> cardResp = IssueCenterApi.queryByCardNo(reqQueryCard);
        LogUtil.info(log, "", "查询卡信息响应", cardResp);
//        if(cardResp.getCode()!=0) {
//        	throw new WriteObuException("查询卡信息失败,请稍后重试");
//        }
		if(cardResp.getCode()==0&&cardResp.getResult()!=null) {
			IssueEtcCard result =cardResp.getResult();
			String orderNo = result.getOrderNo();
			if(!orderNo.equals(orderId)) {
				log.info("存在卡信息但是订单号不匹配");
				SecondIssueResp  resp  = new SecondIssueResp();
				resp.setErrorCode(InterfaceErrorCode.SYSTEM_ERROR.getValue());
				resp.setErrorMsg("卡号: "+result.getCardNo()+"已于车牌号 : "+result.getPlateNo()+" 绑定,请更换卡片重新绑定");
				resp.setSuccess(BaseResponseData.Success.FAILED.toString());
				throw new WriteObuException("卡号: "+result.getCardNo()+"已与车牌号 : "+result.getPlateNo()+" 绑定,请更换卡片重新绑定");
			}
        }
    }
    
    @Test
    public void orderQuery() {
    	try {
			
    		String orderNo ="4319062416004708971";
			SecondIssueOrderRequest secondIssueOrder = new SecondIssueOrderRequest();
			secondIssueOrder.setOwnerCode("4301");
			secondIssueOrder.setKind("0");
			secondIssueOrder.setOrderNo(orderNo);
			LogUtil.info(log, orderNo, "开始查询二发记录"+JSON.toJSONString(secondIssueOrder));
			SecondIssueOrderResponse orderQuery = IssueCenterApi.orderQuery(secondIssueOrder);
			LogUtil.info(log, orderNo, "查询二发记录响应"+JSON.toJSONString(orderQuery));
			if(orderQuery.getCode()==0&&orderQuery.getResult()!=null) {
				SecondIssueProcess iss =JSON.parseObject(orderQuery.getResult().toString(),SecondIssueProcess.class);
				if(StringUtils.isBlank(iss.getObuOutsideImageUrl())||StringUtils.isBlank(iss.getObuInnerImageUrl())) {
					
					log.info("OBU 车内或车外照为空");
					 
					
				}
			}
		
	}
	catch(Exception e) {
		log.error(e.getLocalizedMessage(),e.fillInStackTrace());
		
	}
    }
    
    @Test
    public void insertActiveOrder() {
    	
    	String orderNo ="4319062416004708971";
    	ActivationQueryReq actReq = new ActivationQueryReq();
		actReq.setKind("1");
		actReq.setOrderNo(orderNo);
		actReq.setOwnerCode("4301");
		LogUtil.info(log, orderNo, "查询激活记录请求 :"+JSON.toJSONString(actReq));
		ActivationQueryResp activationQuery = IssueCenterApi.activationQuery(actReq);
		LogUtil.info(log, orderNo, "查询激活记录响应 :"+JSON.toJSONString(activationQuery));
		
    }
    
    @Test
    public void updateEtcCard() {
    	
    	
    		
    		boolean isUpdate = true;
    		SecondIssueReq req = new SecondIssueReq();
    		req.setOrderId("4319062711563959606");
    		req.setCardNo("6666666666672");
    		req.setObuNo("4301190220052904");
          	//更新卡信息的前置是已经新建了卡片
    		/**
    		 * 第一种情况用户不知道之前订单卡片已经开卡成功这个时候就需要订单号去查询卡片，如果已经存在该卡那说明该卡片已经写0016成功不允许变更
    		 * 
    		 */

    		 QueryByOrderReq reqQueryCard = new QueryByOrderReq();
    		 reqQueryCard.setOrderNo(req.getOrderId());
    		 reqQueryCard.setOwnerCode(4301);
    		 LogUtil.info(log, req.getOrderId(), "查询卡信息请求", JSON.toJSONString(reqQueryCard));
             IssueEtcCardResp<IssueEtcCard> cardResp = IssueCenterApi.queryByOrderNo(reqQueryCard);
             LogUtil.info(log, req.getOrderId(), "查询卡信息响应", cardResp);
       		 if(cardResp.getCode()==0&&cardResp.getResult()!=null) {
       			IssueEtcCard result =cardResp.getResult();
       			String cardNo = result.getCardNo();
       			
       			if(!cardNo.equals(req.getCardNo())) {
       				LogUtil.info(log, req.getOrderId(), "通过订单号查询存在卡信息但是与请求卡号不匹配");
       				throw new WriteObuException("请使用正确的卡号,该车牌"+result.getPlateNo()+"已于卡号:"+cardNo+"绑定,请更换卡片重新激活");
       				//throw new WriteObuException("卡号: "+result.getCardNo()+"已与车牌号 : "+result.getPlateNo()+"绑定,请更换卡片重新激活");
       			}
       			if(StringUtils.isNotBlank(result.getObuCode())) {
       				if(!result.getObuCode().equals(req.getObuNo())) {
       					throw new WriteObuException("请使用正确的OBU设备,该车牌"+result.getPlateNo()+"已于OBU号:"+result.getObuCode()+"绑定,请更换OBU重新激活");
       				}
       			}
       			//更新OBU 号
       			if(isUpdate) {
       				result.setObuCode(req.getObuNo());
       				result.setUpdateTime(new Date());
       	   			LogUtil.info(log, req.getOrderId(), "更新卡信息请求:", JSON.toJSONString(result));
       	            GlobalResponse<NullResp> saveOrUpdateEtcCard = IssueCenterApi.saveOrUpdateEtcCard((JSONObject)JSON.toJSON(result));
       	            LogUtil.info(log, req.getOrderId(), "更新卡信息响应:", JSON.toJSONString(saveOrUpdateEtcCard));
       	            if(saveOrUpdateEtcCard.getCode()!=0) {
       	             	 throw new WriteObuException("更新卡信息失败,请稍后重试");
       	            }	
       			}
       			
              }
       		 else {
       			throw new WriteObuException("查询卡信息失败,请稍后重试");
       		 }
       	     
    	
    }
    
    @Test
    public void queryByObuNo() {
    	//通过OBU号查询卡信息
    	SecondIssueReq req = new SecondIssueReq();
    	req.setOrderId("");
    	req.setCardNo("88888888888");
    	req.setObuNo("430119022005212");
		JSONObject cardJson = new JSONObject();
		cardJson.put("owner_code", 4301);
		cardJson.put("obu_code", req.getObuNo());
		LogUtil.info(log, req.getOrderId(), "查询OBU信息请求", JSON.toJSONString(cardJson));
		GlobalResponse<List<IssueEtcCard>> queryCardByJson = IssueCenterApi.queryCardByJson(cardJson);
		LogUtil.info(log, req.getOrderId(), "查询OBU信息响应", queryCardByJson);
		if(queryCardByJson.getCode()!=0) {
        	throw new WriteObuException("查询OBU信息失败,请稍后重试");
        }
		if(queryCardByJson.getResult()!=null&&queryCardByJson.getResult().size()==1) {
			IssueEtcCard result =queryCardByJson.getResult().get(0);
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
    
    @Test
    public void doservice() {
    	ApplyOrderSyncReq applyOrderSyncReq = new ApplyOrderSyncReq();
    	applyOrderSyncReq.setOpType("");
    	applyOrderSyncReq.setOrderId("123456");
    	PickService.doService(applyOrderSyncReq, new ThirdPartner());
    }
    		
    
    
  
    
   public static void main(String[] args) {
		ActivationQueryReq actReq = new ActivationQueryReq();
		actReq.setKind("0");
		actReq.setOrderNo("1233");
		actReq.setOwnerCode("4301");
		System.out.println(JSON.toJSONString(actReq));
		
		ActivationCheckReq actCheckReq = new ActivationCheckReq();
		actCheckReq.setCardNo("43011900232500078011");
		actCheckReq.setObuNo("4301190220063014");
		actCheckReq.setKind("0");
		actCheckReq.setOwnerCode("4301");
		//{"random:" :"00A40090","":"vehicelContent":"","startTime":"","expireTime":""}
		actCheckReq.setRandom("00A4DB12");
		actCheckReq.setOrderNo("4319070821371480057");
//		if(org.apache.commons.lang3.StringUtils.isNotBlank(req.getOriginValue())) {
//			JSONObject obj = JSON.parseObject(req.getOriginValue());
//			ObuInfoApdu obuInfo = JSON.parseObject(obj.getString("obuInfo"), ObuInfoApdu.class);
//			CardInfoApdu cardInfo = JSON.parseObject(obj.getString("cardInfo"), CardInfoApdu.class);
			actCheckReq.setEsamStartTime("20190708");
			actCheckReq.setEsamExpireTime("20210708");
			actCheckReq.setEsamVersion("01");
		//}
		actCheckReq.setVehicleInfoOfEncrypt("");
		System.out.println("act:"+JSON.toJSONString(actCheckReq));
		
		SecondActiveUploadRequest activeUpload = new SecondActiveUploadRequest();
		activeUpload.setCardNo("43011900232500078011");
		activeUpload.setObuNo("4301190220063014");
		activeUpload.setPartnerId("");
		activeUpload.setKind(0);
		activeUpload.setOrderNo("4319070821371480057");
		activeUpload.setOwnerCode("4301");
		
		activeUpload.setActiveStatus(1);
		System.out.println(JSON.toJSONString(activeUpload));
   	}
}
