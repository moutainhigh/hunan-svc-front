package cn.trawe.etc.hunanfront;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import cn.trawe.etc.hunanfront.EtcSvcFrontHunanBootstrap;
import cn.trawe.etc.hunanfront.service.ApplyHunanSyncService;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = EtcSvcFrontHunanBootstrap.class)
public class ApplySyncTest {
	
	    @Autowired
	    ApplyHunanSyncService ApplyHunanSyncService;
	    @Test
	    public void case1() {
	    	ApplyHunanSyncService.applySync("4319062416004708971");
	 }

  
}
