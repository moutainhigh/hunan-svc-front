//package cn.trawe.etc.hunanfront;
//
//import java.util.Collections;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.web.client.RestTemplate;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//
//import cn.trawe.etc.hunanfront.EtcSvcFrontHunanBootstrap;
//import cn.trawe.etc.hunanfront.dao.ThirdOrderSycnRecordCompletedDao;
//import cn.trawe.etc.hunanfront.dao.ThirdOrderSycnRecordDao;
//import cn.trawe.etc.hunanfront.entity.ThirdOrderSycnRecordCompleted;
//import cn.trawe.etc.hunanfront.enums.AuditType;
//import cn.trawe.etc.hunanfront.feign.ApiClient;
//import cn.trawe.etc.hunanfront.feign.EtcCoreWithholdClient;
//import cn.trawe.etc.hunanfront.feign.ExpressInfoClient;
//import cn.trawe.etc.hunanfront.feign.ImageClient;
//import cn.trawe.etc.hunanfront.request.ApplyOrderSyncReq;
//import cn.trawe.etc.hunanfront.request.UserBlacklistSyncRequest;
//import cn.trawe.etc.hunanfront.rocketmq.listener.ApplyOrderSyncListener;
//import cn.trawe.etc.hunanfront.rocketmq.sender.RocketMqSender;
//import cn.trawe.etc.hunanfront.service.ExpressInfoService;
//import cn.trawe.etc.hunanfront.service.ThirdOrderSycnRecordService;
//import cn.trawe.etc.hunanfront.service.ThirdPartnerService;
//import cn.trawe.etc.hunanfront.service.applysync.ApplySyncFactory;
//import cn.trawe.pay.expose.entity.IssueEtcCard;
//import cn.trawe.pay.expose.enums.ChannelType;
//import cn.trawe.pay.expose.request.UserReq;
//import cn.trawe.pay.expose.request.issue.CheckPlateNoReq;
//import cn.trawe.pay.expose.request.issue.IssueOrderQueryReq;
//import cn.trawe.pay.expose.request.issue.IssueOrderSubmitReq;
//import cn.trawe.pay.expose.request.issue.QueryByOwnerReq;
//import cn.trawe.pay.expose.response.issue.IssueEtcCardResp;
//import cn.trawe.pay.expose.response.issue.IssueOrderQueryResp;
//import cn.trawe.pay.expose.response.issue.IssueOrderSubmitResp;
//import cn.trawe.util.LogUtil;
//import lombok.extern.slf4j.Slf4j;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = EtcSvcFrontHunanBootstrap.class)
//@Slf4j
//public class ApplicationTests {
//    @Autowired
//    private ApiClient apiClient;
//    @Autowired
//    private EtcCoreWithholdClient coreWithholdClient;
//    @Autowired
//    private RocketMqSender rocketMqSender;
//    @Autowired
//    private EtcCoreWithholdClient etcCoreWithholdClient;
//    @Value("${api.token}")
//    private String token;
//    @Autowired
//    private ThirdOrderSycnRecordDao thirdOrderSycnRecordDao;
//    @Autowired
//    private ApplyOrderSyncListener applyOrderSyncListener;
//    @Autowired
//    private ExpressInfoClient expressInfoClient;
//    @Autowired
//    private ExpressInfoService expressInfoService;
//    @Autowired
//    private ImageClient imageClient;
//    @Autowired
//    private ApplySyncFactory applySyncFactory;
//    @Autowired
//    private ThirdOrderSycnRecordService recordService;
//    @Autowired
//    private ThirdOrderSycnRecordCompletedDao recordCompletedDao;
//    @Autowired
//    ThirdPartnerService ThirdPartnerService;
//    @Test
//    public void testThridParternDao() {
//    	//ThirdPartnerService.getPartner(11);
//    }
//    
//    @Test
//    public void testRecordCompletedDao() {
//        ThirdOrderSycnRecordCompleted recordCompleted = new ThirdOrderSycnRecordCompleted();
//        recordCompletedDao.save(recordCompleted);
//    }
//
//    @Test
//    public void testRecordService() {
//        recordService.valid(444);
//    }
//
//    @Test
//    public void testApplySyncFactory() {
//        System.out.println(applySyncFactory.getApplyCancelStrategy("1"));
//        System.out.println(applySyncFactory.getApplyCancelStrategy("1"));
//        System.out.println(applySyncFactory.getApplyCancelStrategy("2"));
//        System.out.println(applySyncFactory.getApplyCancelStrategy("3"));
//        System.out.println(applySyncFactory.getApplyCancelStrategy("4"));
//    }
//
//    @Test
//    public void testPlateNoCheck() {
//        String no = "冀B78Z36";
//        CheckPlateNoReq req = new CheckPlateNoReq();
//        req.setVehicleLicenseColor("0");
//        req.setVehicleLicensePlate(no);
//        JSONObject  partnerId  = new JSONObject();
//        partnerId.put("orgId", "308");
//        partnerId.put("channelId", "1828");
//        req.setPartnerId(partnerId.toJSONString());
//        req.setOwnerCode(4301);
//        System.out.println(JSON.toJSONString(apiClient.checkVehicleLicense(req, token)));
//    }
//
//    @Test
//    public void test() {
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("mobile", "13687667656");
//        System.out.println(JSON.toJSONString(apiClient.saveOrder(jsonObject, "test", token)));
//    }
//
//    @Test
//    public void testSaveUser() {
//        UserReq req = new UserReq();
//        req.setCertNo("2331434237552");
//        req.setRealName("test2");
//        req.setChannelType(ChannelType.ALIPAY);
//        System.out.println(JSON.toJSONString(apiClient.saveOrUpdateUser(req, "test", token)));
//    }
//
//    @Test
//    public void testSendMsg() {
//        rocketMqSender.sendStringMsg("测试消息");
//    }
//
//    @Test
//    public void testBlack() {
//        UserBlacklistSyncRequest request = new UserBlacklistSyncRequest();
//        request.setAddUsers(Collections.emptyList());
//        System.out.println(JSON.toJSONString(etcCoreWithholdClient.userBlacklistSync(request, token)));
//    }
//
//    @Test
//    public void testOwner() {
//        System.out.println(JSON.toJSONString(apiClient.owner(3201, token)));
//    }
//
//    @Test
//    public void testSubmitOrder() {
//        String auditType = AuditType.getAuditType("3201", true);
//        String orderNo = "3219051120490772473";
//        IssueOrderSubmitReq req = new IssueOrderSubmitReq();
//        req.setOrderNo(orderNo);
//        req.setAuditType(auditType);
//        LogUtil.info(log, orderNo, "提交订单信息请求", req);
//        IssueOrderSubmitResp res = apiClient.orderSubmit(req, token);
//        LogUtil.info(log, orderNo, "提交订单信息响应", res);
//    }
//
//    @Test
//    public void testBlk() {
//        UserBlacklistSyncRequest request = new UserBlacklistSyncRequest();
//        request.setAddUsers(Collections.emptyList());
//        System.out.println(JSON.toJSONString(coreWithholdClient.userBlacklistSync(request, token)));
//    }
//
//    @Test
//    public void testRecord() {
//        thirdOrderSycnRecordDao.addRetryTimes(57L);
//    }
//
//    @Test
//    public void applySync() {
//        String json = "{\"agreementNo\":\"20195313001509417889\",\"buyerUid\":\"2088302542824896\",\"cardType\":\"1\",\"deliveryAmount\":\"1000\",\"deliveryInfo\":{\"address\":\"新吴区无锡市新吴区国家软件园天鹅座B\",\"cityCode\":\"0510\",\"cityName\":\"无锡市\",\"contactName\":\"赵洋洋4\",\"contactTel\":\"17712362386\",\"deliveryCode\":\"11111\",\"districtName\":\"新吴区\",\"provinceName\":\"江苏省\"},\"deviceAmount\":\"1000\",\"deviceStatus\":\"0\",\"invoiceInfo\":{\"dutyNo\":\"343243\",\"email\":\"fdfd@xxx.com\",\"invoiceTitle\":\"上海4xxxx有限公司\",\"invoiceTitleType\":\"2\",\"invoiceType\":\"null\",\"needInvoice\":\"1\",\"phone\":\"15026663623\"},\"merchantPid\":\"2088301537607123\",\"opType\":\"1\",\"orderCreateTime\":\"2019-05-14 15:20:47\",\"orderId\":\"20190614152047123248924878\",\"orderStatus\":\"1\",\"orderUpdateTime\":\"2019-05-14 15:14:07\",\"payStatus\":\"1\",\"recordId\":0,\"sellerAgreement\":\"1\",\"sellerAgreementVersion\":\"\",\"sellerId\":\"4301\",\"sellerName\":\"fdadfd\",\"serviceAmount\":\"0\",\"totalAmount\":\"13000\",\"viInfo\":{\"engineNo\":\"161860477\",\"viAc\":\"5\",\"viGrantTime\":\"2016-09-05\",\"viImgAngle\":\"A*U9QHSbBOivqWz6lImJOxUwBjAQAAAA\",\"viLicenseImgBack\":\"A*Mj6lRo6MBXMHjIRBpFYWfABjAQAAAA\",\"viLicenseImgFront\":\"A*Mj6lRo6MBXMHjIRBpFYWfABjAQAAAA\",\"viModelName\":\"雪佛兰牌SGM7154DAAA\",\"viNumber\":\"沪G78Z45\",\"viOwnerName\":\"卢衡5\",\"viPlateColor\":\"0\",\"viStartTime\":\"2016-09-05\",\"viType\":\"小型轿车\",\"viUseType\":\"1\",\"viVin\":\"LSGBE5447GG166736\"},\"viOwnerInfo\":{\"ownerCertImgBack\":\"A*gb3SRL9y7xlWaZZBtVCgnwBjAQAAAA\",\"ownerCertImgFront\":\"A*gb3SRL9y7xlWaZZBtVCgnwBjAQAAAA\",\"viOwnerCertNo\":\"321282199211081819\",\"viOwnerCertType\":\"0\",\"viOwnerName\":\"柴建军\",\"viOwnerType\":\"0\"}}";
//        ApplyOrderSyncReq req = JSON.parseObject(json, ApplyOrderSyncReq.class);
//        applyOrderSyncListener.saveOrUpdateOrderInfo(req);
//    }
//
//    @Test
//    public void testUidQuery() {
//        String uid = "test";
//        IssueOrderQueryReq queryReq = new IssueOrderQueryReq();
//        LogUtil.info(log, uid, "根据uid查询订单请求");
//        IssueOrderQueryResp queryResp = apiClient.orderQuery(queryReq, token, uid);
//        LogUtil.info(log, uid, "根据uid查询订单响应", queryResp);
////
////        uid = "2088712840577395";
////        LogUtil.info(log, uid, "根据uid查询订单请求");
////        queryResp = apiClient.orderQuery(queryReq, token, uid);
////        LogUtil.info(log, uid, "根据uid查询订单响应", queryResp);
////
////        IssueOrderQueryReq issueOrderQueryReq = new IssueOrderQueryReq();
////        issueOrderQueryReq.setOrderNo("1219051517403379214");
////        issueOrderQueryReq.setPageNo(1);
////        issueOrderQueryReq.setPageSize(1);
////        queryResp = apiClient.orderQuery(issueOrderQueryReq, token, "");
////        LogUtil.info(log, uid, "查询订单响应", queryResp);
//    }
//
//    @Test
//    public void testCard() {
//        String uid = "2088352542824893";
//        LogUtil.info(log, uid, "查询卡信息请求");
//        QueryByOwnerReq queryByOwnerReq = new QueryByOwnerReq();
//        queryByOwnerReq.setOwnerCode(3201);
//        IssueEtcCardResp<IssueEtcCard> cardResp = apiClient.queryCardByAlipayUserId(uid, queryByOwnerReq, token);
//        LogUtil.info(log, uid, "查询卡信息响应", cardResp);
//    }
//
//    @Test
//    public void testExpressInfos() {
//        LogUtil.info(log, "", "查询快递信息", expressInfoClient.expressInfos());
//    }
//
//    @Test
//    public void testUidQueryAndExpressInfos() {
//        testUidQuery();
//        testExpressInfos();
//    }
//
//    @Test
//    public void testCode() {
//        System.out.println(expressInfoService.getDeliveryCode("申2通test"));
//        System.out.println(expressInfoService.getDeliveryCode("申2通test"));
//        System.out.println(expressInfoService.getDeliveryCode("京东"));
//        System.out.println(expressInfoService.getDeliveryCode("京东"));
//    }
//
//    public static void main(String[] args) {
//        RestTemplate restTemplate = new RestTemplate();
//        System.out.println(restTemplate.getForObject("https://detail.i56.taobao.com/xml/cpcode_detail_list.xml", String.class));
//    }
//
//    @Test
//    public void testImage() {
//        ImageClient.UploadImagesReq req = new ImageClient.UploadImagesReq();
//        req.setOrderId("20190518152047123248924875");
//        System.out.println(JSON.toJSONString(imageClient.uploadImages(req)));
//    }
//}
