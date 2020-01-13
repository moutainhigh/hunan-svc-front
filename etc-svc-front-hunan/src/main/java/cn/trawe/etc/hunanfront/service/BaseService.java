package cn.trawe.etc.hunanfront.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;

import cn.trawe.etc.hunanfront.config.InterfaceCenter;
import cn.trawe.etc.hunanfront.entity.ThirdPartner;
import cn.trawe.etc.hunanfront.expose.v2.BaseResponse;
import cn.trawe.etc.hunanfront.expose.v2.BaseResponseData;
import cn.trawe.etc.hunanfront.feign.v2.GatewayHunanApiImpl;
import cn.trawe.etc.hunanfront.feign.v2.IssueCenterApi;
import cn.trawe.etc.hunanfront.service.secondissue.apdu.CreateApduHeader;
import cn.trawe.etc.hunanfront.service.secondissue.bussiness.BaseBussinessService;
import cn.trawe.etc.hunanfront.service.secondissue.bussiness.BussinessCase10ServiceImpl;
import cn.trawe.etc.hunanfront.service.secondissue.bussiness.BussinessCase11ServiceImpl;
import cn.trawe.etc.hunanfront.service.secondissue.bussiness.BussinessCase1ServiceImpl;
import cn.trawe.etc.hunanfront.service.secondissue.bussiness.BussinessCase2ServiceImpl;
import cn.trawe.etc.hunanfront.service.secondissue.bussiness.BussinessCase3ServiceImpl;
import cn.trawe.etc.hunanfront.service.secondissue.bussiness.BussinessCase4ServiceImpl;
import cn.trawe.etc.hunanfront.service.secondissue.bussiness.BussinessCase5ServiceImpl;
import cn.trawe.etc.hunanfront.service.secondissue.bussiness.BussinessCase6ServiceImpl;
import cn.trawe.etc.hunanfront.service.secondissue.bussiness.BussinessCase7ServiceImpl;
import cn.trawe.etc.hunanfront.service.secondissue.bussiness.BussinessCase8ServiceImpl;
import cn.trawe.etc.hunanfront.service.secondissue.bussiness.BussinessCase9ServiceImpl;
import cn.trawe.etc.hunanfront.utils.SignUtil2;
import cn.trawe.pay.common.client.RedisClient;
import cn.trawe.pay.common.etcmsg.EtcObjectResponse;
import cn.trawe.pay.expose.entity.EtcIssueOrder;
import cn.trawe.pay.expose.request.issue.GetOrderReq;
import cn.trawe.pay.expose.response.issue.IssueSaveResp;
import cn.trawe.util.LogUtil;
import cn.trawe.utils.NumberUtils;
import cn.trawe.utils.ValidateUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Jiang Guangxing
 */
@Slf4j
public abstract class BaseService extends BaseBussinessService {

    @Value("${api.token}")
    protected String token;
    
    @Autowired
	protected GatewayHunanApiImpl gateWay;
	
    
    @Autowired
	protected  IssueCenterApi  IssueCenterApi;
    
    @Autowired
	protected BussinessCase1ServiceImpl case1;
	
	@Autowired
	protected BussinessCase2ServiceImpl case2;
	
	
	@Autowired
	protected BussinessCase3ServiceImpl case3;
	
	@Autowired
	protected BussinessCase4ServiceImpl case4;
	
	@Autowired
	protected BussinessCase5ServiceImpl case5;
	
	@Autowired
	protected BussinessCase6ServiceImpl case6;
	
	@Autowired
	protected BussinessCase7ServiceImpl case7;
	
	@Autowired
	protected BussinessCase8ServiceImpl case8;
	
	@Autowired
	protected BussinessCase9ServiceImpl case9;
	
	@Autowired
	protected BussinessCase10ServiceImpl case10;
	@Autowired
	protected BussinessCase11ServiceImpl case11;
	
	 @Autowired
	 protected CreateApduHeader CreateApduHeader;
	    
	

	
//	@Autowired
//	protected  IssueCardCenterApi  IssueCardCenterApi;
//	
	protected  String ISSUE_ORDER_SUBMIT_LOCKKEY = "hnfront:issue:order:submit:lockkey:";
	protected  String VEHICLE_ORDER_SUBMIT_LOCKKEY = "hnfront:vehicle:order:submit:lockkey:";
	protected  String VEHICLE_ORDER_SAVE_LOCKKEY = "hnfront:vehicle:order:save:lockkey:";
	protected  String ISSUE_ORDER_CANCEL_LOCKKEY = "hnfront:issue:order:cancel:lockkey:";
    protected  String ISSUE_ORDER_ACTIVE_STATUS_LOCKKEY = "hnfrontissue:order:activestatus:lockkey:";
    protected  int TIMEOUT_LOCK = 60;
    /**
     * 获取锁的次数
     */
    protected  int TRY_LOCK_COUNT = 3;
	
	@Autowired
	protected RedisClient redisClient;
	
	 @Autowired
	 protected QueryMonitorServiceImpl queryMonitorServiceImpl;
	 
	 
	@Value("${spring.application.active}")
    protected  String active;

    private static final String NUM_FMT = "00000";

    public static String getOrderNo(String ownerCode) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmmss");
		Date date = new Date();
		String rand = NumberUtils.format(NumberUtils.randInt(99999), NUM_FMT);

		return ownerCode.substring(0, 2) + simpleDateFormat.format(date) + rand;
	}
    
    @Value("${trawe.privateKey}")
    private String trawePrivateKey;
    
    protected EtcIssueOrder  getOrder(String orderNo,String outOrderId){
		GetOrderReq getOrder = new GetOrderReq();
		if(StringUtils.isNotBlank(outOrderId)) {
			getOrder.setOutOrderId(outOrderId);
		}
		if(StringUtils.isNotBlank(orderNo)) {
			getOrder.setOrderNo(orderNo);
		}
    	LogUtil.info(log, orderNo, "中台订单查询请求:"+JSON.toJSONString(getOrder));
		EtcObjectResponse<EtcIssueOrder> getOrderResp = IssueCenterApi.getOrder(getOrder);
		LogUtil.info(log, orderNo, "中台订单查询响应:"+JSON.toJSONString(getOrderResp));
		if(InterfaceCenter.TIMEOUT.getCode()==getOrderResp.getCode()) {
			throw new RuntimeException(getOrderResp.getMsg());
			
		}
		if(InterfaceCenter.SUCCESS.getCode()!=getOrderResp.getCode()) {
			throw new RuntimeException(getOrderResp.getMsg());
		}
		
	    if (ValidateUtil.isEmpty(getOrderResp.getData())) {
	    	throw new RuntimeException("订单不存在");
	    }    
	    EtcIssueOrder order =getOrderResp.getData();
		return order;
	}
	
	protected void modifyOrder(EtcIssueOrder order) {
		
	    
		LogUtil.info(log, order.getOrderNo(), "中台修改订单请求:"+JSON.toJSONString(order));
		IssueSaveResp respCenter = IssueCenterApi.edit((JSONObject)JSON.toJSON(order));
		LogUtil.info(log,order.getOrderNo(), "中台修改订单响应:"+JSON.toJSONString(respCenter));
		if(InterfaceCenter.SUCCESS.getCode()!=respCenter.getCode()) {
			throw new RuntimeException(respCenter.getMsg());
		}
	}

    
    
    

    protected BaseResponse paramsError(String errorMsg, String charset,ThirdPartner ThirdPartner)  {
        log.error("响应失败:【{}】", errorMsg);
        BaseResponse res = new BaseResponse<BaseResponseData>().setResponse(successed().setErrorCode(BaseResponseData.ErrorCode.PARAMS_ERROR.toString()).setErrorMsg(errorMsg));
        
        try {
			res.setSign(SignUtil2.signResponse(res.getResponse(),trawePrivateKey,charset,true));
		} catch (AlipayApiException e) {
			log.error(e.getMessage(),e.fillInStackTrace());
		}
        log.info("响应报文:"+JSON.toJSONString(res));
        return res;
    }

    protected BaseResponse systemError(String charset,ThirdPartner ThirdPartner) {
        log.error("响应失败:【系统内部错误】");
        BaseResponse res = new BaseResponse<BaseResponseData>().setResponse(failed().setErrorCode(BaseResponseData.ErrorCode.SYSTEM_ERROR.toString()).setErrorMsg("系统内部错误"));
        try {
			res.setSign(SignUtil2.signResponse(res.getResponse(),trawePrivateKey,charset,true));
		} catch (AlipayApiException e) {
			log.error(e.getMessage(),e.fillInStackTrace());
		}
        log.info("响应报文:"+JSON.toJSONString(res));
        return res;
    }

    protected BaseResponse systemErrorCancelFail(String charset,ThirdPartner ThirdPartner,String msg) {
        log.error("取消响应:【"+msg+"】");
        BaseResponse res = new BaseResponse<BaseResponseData>().setResponse(failed().setErrorCode(BaseResponseData.ErrorCode.SYSTEM_ERROR.toString()).setErrorMsg(msg));
        try {
            res.setSign(SignUtil2.signResponse(res.getResponse(),trawePrivateKey,charset,true));
        } catch (AlipayApiException e) {
            log.error(e.getMessage(),e.fillInStackTrace());
        }
        log.info("响应报文:"+JSON.toJSONString(res));
        return res;
    }
    

    protected BaseResponse otherError(String errorMsg, String charset,ThirdPartner ThirdPartner) {
        log.error("响应失败:【{}】", errorMsg);
        BaseResponse res = new BaseResponse<BaseResponseData>().setResponse(successed().setErrorCode(BaseResponseData.ErrorCode.OTHER_ERROR.toString()).setErrorMsg(errorMsg));
        try {
			res.setSign(SignUtil2.signResponse(res.getResponse(),trawePrivateKey,charset,true));
		} catch (AlipayApiException e) {
			log.error(e.getMessage(),e.fillInStackTrace());
		}
        log.info("响应报文:"+JSON.toJSONString(res));
        return res;
    }
    

    public static BaseResponseData failed() {
        return new BaseResponseData().setSuccess(BaseResponseData.Success.FAILED.toString());
    }
    
    public static BaseResponseData successed() {
        return new BaseResponseData().setSuccess(BaseResponseData.Success.SUCCEED.toString());
    }
}
