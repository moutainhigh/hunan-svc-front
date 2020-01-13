//package cn.trawe.etc.hunanfront.api;
//
//import java.util.Map;
//
//import org.springframework.http.MediaType;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import cn.trawe.etc.hunanfront.expose.v2.BaseResponse;
///**
// * 网发平台接口
// * @author guzelin
// *
// */
////@RequestMapping("/etc")
//public interface HunanIssueOrderApi {
//
//	/**
//	 * 车牌校验
//	 * @param request
//	 * @return
//	 */
//	@PostMapping(value = "/precheck",consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
//	public BaseResponse precheck(@RequestParam Map<String, Object> params);
//	/**
//	 * 图片上传
//	 * @param mediaTransferRequest
//	 * @return
//	 */
//	@ResponseBody
//	@PostMapping(value = "/media_transfer",consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
//	public String mediaTransfer(@RequestParam Map<String, Object> params);
//	
//	/**
//	 * 订单提交
//	 * @param etcApplyOrderSync
//	 * @return
//	 */
//	@ResponseBody
//	@PostMapping(value = "/apply_order_sync",consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
//	public String applyOrderSync(@RequestParam Map<String, Object> params);
//	/**
//	 * 订单提交
//	 * @param etcApplyOrderSync
//	 * @return
//	 */
//	@ResponseBody
//	@PostMapping(value = "/second_issue",consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
//	public String secondIssue(@RequestParam Map<String, Object> params);
//}
