package cn.trawe.etc.hunanfront.v2;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSON;

import cn.trawe.etc.hunanfront.EtcSvcFrontHunanBootstrap;
import cn.trawe.etc.hunanfront.entity.ThirdPartner;
import cn.trawe.etc.hunanfront.expose.v2.req.IssueOrderCancelReq;
import cn.trawe.etc.hunanfront.expose.v2.req.IssueOrderQueryReq;
import cn.trawe.etc.hunanfront.expose.v2.req.IssueOrderSubmitReq;
import cn.trawe.etc.hunanfront.expose.v2.resp.IssueOrderCancelResp;
import cn.trawe.etc.hunanfront.expose.v2.resp.IssueOrderQueryResp;
import cn.trawe.etc.hunanfront.expose.v2.resp.IssueOrderSubmitResp;
import cn.trawe.etc.hunanfront.feign.v2.IssueCenterApi;
import cn.trawe.etc.hunanfront.service.v2.IssueServiceI;
import cn.trawe.pay.expose.request.issue.ThirdOutOrderSaveReq;
import cn.trawe.pay.expose.response.issue.ThirdOutOrderSaveResp;
import cn.trawe.util.LogUtil;
import lombok.extern.slf4j.Slf4j;
@RunWith(SpringRunner.class)
@SpringBootTest(classes = EtcSvcFrontHunanBootstrap.class)
@Slf4j
public class IssueServiceTest {
	
	@Autowired
	private IssueServiceI  issueService;
	
	String orderId ="20190827";
	
	@Autowired
	private IssueCenterApi IssueCenterApi;
	
	/**
	 * 发行订单提交
	 */
	@Test
	public void orderSubmit() {
		IssueOrderSubmitReq req = new IssueOrderSubmitReq();
		req.setAccountNo("twtest03");
		req.setPassword("5f426917ca218cba79ba8ecda6b99ef5");
		req.setChannelNo("216");
		req.setAddress("外运大厦");
		req.setProvinceName("北京市");
		req.setCityName("北京市");
		req.setDistrictName("朝阳区");
		req.setContactName("柴建军");
		req.setInstallStatus("1");
		req.setOrderId(orderId);
		req.setSignCardType(1);
		//req.setSignAccount("23232323232***12121");
		req.setSignChannel("001");
		req.setUserNo("43010119062980012");
		req.setVehicleId("672df6e4bc4a4c20b97f0c4286f24bbd");
		req.setContactTel("18001226357");
		
		IssueOrderSubmitResp issueOrderSubmit = issueService.issueOrderSubmit(req, new ThirdPartner());
		System.out.println(JSON.toJSONString(issueOrderSubmit));
	}
	
	/**
	 * 发行订单取消
	 */
	@Test
	public void orderCancel() {
		
		IssueOrderCancelReq req = new IssueOrderCancelReq();
		req.setOrderId(orderId);
		IssueOrderCancelResp issueOrderCancel = issueService.issueOrderCancel(req, new ThirdPartner());
	}
	
	/**
	 * 发行订单查询
	 */
	@Test
	public void orderQuery() {
		IssueOrderQueryReq req = new IssueOrderQueryReq();
		req.setOrderId(orderId);
		IssueOrderQueryResp issueOrderQuery = issueService.issueOrderQuery(req, new ThirdPartner());
	}
	
	/**
	 * 保存第三方映射
	 */
	@Test
	public void saveThirdOutOrder() {
	  	
	  	//关联渠道ID
		String orderNo ="4319082714163222119";
		String outOrderId="20190827";
		Long channelId =216L;
	      ThirdOutOrderSaveReq req = new ThirdOutOrderSaveReq();
	      //网发平台订单号
	      req.setOrderNo(orderNo);
	      //银行渠道的订单号
	      req.setOutOrderId(outOrderId);
	      req.setOutType(1);
	      req.setThirdId(channelId);
	      //req.setBankCode(channelId.toString());
	      LogUtil.info(log, outOrderId, "保存订单映射信息请求", req);
	      ThirdOutOrderSaveResp res = IssueCenterApi.saveThirdOutOrder(req);
	      LogUtil.info(log, outOrderId, "保存订单映射信息响应", res);
	      if (res == null)
	          throw new RuntimeException("保存订单映射信息响应失败,res为空");
	      if (res.getCode() != 0)
	          throw new RuntimeException("保存订单映射信息响应失败,code不为0");
	  }


}
