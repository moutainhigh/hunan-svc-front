//package cn.trawe.etc.hunanfront;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//
//import com.alibaba.fastjson.JSON;
//
//import cn.trawe.etc.hunanfront.controller.FrontHunanController;
//import cn.trawe.etc.hunanfront.expose.v2.BaseRequest;
//import cn.trawe.etc.hunanfront.expose.v2.BaseResponse;
//import cn.trawe.etc.hunanfront.rocketmq.listener.ApplyOrderSyncListener;
//import cn.trawe.etc.hunanfront.service.ApplyOrderQueryService;
//import cn.trawe.etc.hunanfront.service.ApplyOrderSyncService;
//import cn.trawe.etc.hunanfront.service.MediaTransferService;
//import cn.trawe.etc.hunanfront.service.PrecheckService;
//import cn.trawe.etc.hunanfront.service.UserBlacklistSyncService;
//import cn.trawe.etc.hunanfront.service.secondissue.HunanSecondIssueBussinessImpl;
//
//@RunWith(SpringRunner.class)
//@WebMvcTest(FrontHunanController.class)
//public class FrontHunanControllerTest {
//
//    @Autowired
//    private MockMvc mvc;
//    
//    @MockBean
//    private PrecheckService precheckService;
//
//    @MockBean
//    private MediaTransferService mediaTransferService;
//
//    @MockBean
//    private ApplyOrderSyncService applyOrderSyncService;
//
//    @MockBean
//    private ApplyOrderQueryService applyOrderQueryService;
//
//    @MockBean
//    private UserBlacklistSyncService userBlacklistSyncService;
//    @MockBean
//    private ApplyOrderSyncListener applyOrderSyncListener;
//    @MockBean
//    HunanSecondIssueBussinessImpl  HunanSecondIssueBussinessImpl;
//
//    @Test
//    public void testIndex() throws Exception {
//        this.mvc.perform(MockMvcRequestBuilders.get("/"))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().string("Just for Spring Boot unit test!"));
//    }
//
//    @Test
//    public void testprecheck() throws Exception {
//    	BaseRequest request = new BaseRequest();
//    	BaseResponse response = null; 
//        
//        this.mvc.perform(MockMvcRequestBuilders.post("/precheck").param("req", JSON.toJSONString(request)))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().string(JSON.toJSONString(response)));
//    }
//}