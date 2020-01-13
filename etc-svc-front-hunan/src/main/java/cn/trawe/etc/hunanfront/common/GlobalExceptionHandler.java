package cn.trawe.etc.hunanfront.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;

import cn.trawe.etc.hunanfront.config.ParamModel;
import cn.trawe.etc.hunanfront.entity.ThirdPartner;
import cn.trawe.etc.hunanfront.exception.ActivationObuException;
import cn.trawe.etc.hunanfront.exception.AnalySisException;
import cn.trawe.etc.hunanfront.exception.NoticeException;
import cn.trawe.etc.hunanfront.exception.WriteObuException;
import cn.trawe.etc.hunanfront.expose.v2.BaseRequest;
import cn.trawe.etc.hunanfront.expose.v2.BaseResponse;
import cn.trawe.etc.hunanfront.expose.v2.BaseResponseData;
import cn.trawe.etc.hunanfront.service.BaseService;
import cn.trawe.etc.hunanfront.service.ThirdPartnerService;
import cn.trawe.etc.hunanfront.utils.SignUtil2;
import cn.trawe.util.LogUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 全局异常处理
 *
 * @author Jiang Guangxing
 * @see ResponseEntityExceptionHandler
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @Autowired
    private SignUtil2 signUtil;
    
    @Value("${trawe.privateKey}")
    private String trawePrivateKey;
    
    @Autowired
    private ThirdPartnerService thirdPartnerService;
    
    
    
	

    /**
     * 处理controller外部异常
     *
     * @param ex      异常
     * @param body    响应体
     * @param headers 响应头
     * @param status  响应状态码s
     * @param request 请求
     * @return 异常响应
     */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object body,
                                                             HttpHeaders headers, HttpStatus status, WebRequest request) {
    	ThirdPartner thirdPartner = new ThirdPartner(trawePrivateKey);
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(systemError(request.getParameter("charset"),thirdPartner,ex), headers, HttpStatus.OK);
    }

    /**
     * 处理controller内部异常
     *
     * @param e 异常
     * @return 异常响应
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public BaseResponse errorHandler(@ParamModel BaseRequest req, Exception e) {
    	//log.info("特微私钥 : " +trawePrivateKey);
    	ThirdPartner thirdPartner = new ThirdPartner(trawePrivateKey);
    	//ThirdPartner partner = thirdPartnerService.getPartner(req);
        LogUtil.error(log, "湖南网发平台前置网关服务全局异常"+e.getLocalizedMessage(), e.getMessage(), e);
        return systemError(req.getCharset(),thirdPartner,e);
    }
    
    /**
     * 处理controller内部异常
     *
     * @param e 异常
     * @return 异常响应
     */
    @ExceptionHandler(value = ActivationObuException.class)
    @ResponseBody
    public BaseResponse ActivationObuException(@ParamModel BaseRequest req, Exception e) {
    	//log.info("特微私钥 : " +trawePrivateKey);
    	ThirdPartner thirdPartner = new ThirdPartner(trawePrivateKey);
    	//ThirdPartner partner = thirdPartnerService.getPartner(req);
        LogUtil.error(log, "湖南网发平台前置网关服务全局异常"+e.getLocalizedMessage(), e.getMessage(), e);
        return systemError(req.getCharset(),thirdPartner,e);
    }
    
    /**
     * 处理controller内部异常
     *
     * @param e 异常
     * @return 异常响应
     */
    @ExceptionHandler(value = AnalySisException.class)
    @ResponseBody
    public BaseResponse AnalySisException(@ParamModel BaseRequest req, Exception e) {
    	//log.info("特微私钥 : " +trawePrivateKey);
    	ThirdPartner thirdPartner = new ThirdPartner(trawePrivateKey);
    	//ThirdPartner partner = thirdPartnerService.getPartner(req);
        LogUtil.error(log, "湖南网发平台前置网关服务全局异常"+e.getLocalizedMessage(), e.getMessage(), e);
        return systemError(req.getCharset(),thirdPartner,e);
    }
    
    /**
     * 处理controller内部异常
     *
     * @param e 异常
     * @return 异常响应
     */
    @ExceptionHandler(value = NoticeException.class)
    @ResponseBody
    public BaseResponse NoticeException(@ParamModel BaseRequest req, Exception e) {
    	//log.info("特微私钥 : " +trawePrivateKey);
    	ThirdPartner thirdPartner = new ThirdPartner(trawePrivateKey);
    	//ThirdPartner partner = thirdPartnerService.getPartner(req);
        LogUtil.error(log, "湖南网发平台前置网关服务全局异常"+e.getLocalizedMessage(), e.getMessage(), e);
        return systemError(req.getCharset(),thirdPartner,e);
    }
    
    /**
     * 处理controller内部异常
     *
     * @param e 异常
     * @return 异常响应
     */
    @ExceptionHandler(value = WriteObuException.class)
    @ResponseBody
    public BaseResponse WriteObuException(@ParamModel BaseRequest req, Exception e) {
    	//log.info("特微私钥 : " +trawePrivateKey);
    	ThirdPartner thirdPartner = new ThirdPartner(trawePrivateKey);
    	//ThirdPartner partner = thirdPartnerService.getPartner(req);
        LogUtil.error(log, "湖南网发平台前置网关服务全局异常"+e.getLocalizedMessage(), e.getMessage(), e.fillInStackTrace());
        return systemError(req.getCharset(),thirdPartner,e);
    }

    private BaseResponse systemError(String charset,ThirdPartner ThirdPartner,Exception e) {
    	
  
    	
        BaseResponse res = new BaseResponse<BaseResponseData>().setResponse(BaseService.successed().setErrorCode(BaseResponseData.ErrorCode.SYSTEM_ERROR.toString()).setErrorMsg(e.getLocalizedMessage()!= null&!"".equals(e.getLocalizedMessage())?e.getLocalizedMessage():"系统异常,请稍后重试"));
        try {
			res.setSign(SignUtil2.signResponse(res.getResponse(),ThirdPartner.getTrawePrivateKey(),charset,true));
		} catch (AlipayApiException e1) {
			log.error(e1.getMessage(),e1.fillInStackTrace());
		}
        catch (Exception e1) {
			log.error(e1.getMessage(),e1.fillInStackTrace());
		}
        LogUtil.error(log, "systemError","湖南网发平台前置网关服务返回"+JSON.toJSONString(res));
        return res;
    
    }
    
  
    

    
  
}
