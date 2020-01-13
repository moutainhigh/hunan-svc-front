package cn.trawe.etc.hunanfront.v2;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import cn.trawe.etc.hunanfront.EtcSvcFrontHunanBootstrap;
import cn.trawe.etc.hunanfront.service.QueryMonitorServiceImpl;
import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EtcSvcFrontHunanBootstrap.class)
@Slf4j
public class DingDingTalkTest {
	
	@Autowired
	 QueryMonitorServiceImpl queryMonitorServiceImpl;
	
	@Value("${spring.application.active}")
    protected  String active;
	
	
	@Test
	public void talk() {
		queryMonitorServiceImpl.send("userInfoUpload"+"--"+active, "服务降级");
	}

}
