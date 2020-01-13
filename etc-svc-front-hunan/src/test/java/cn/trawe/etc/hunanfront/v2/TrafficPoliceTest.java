package cn.trawe.etc.hunanfront.v2;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.aliyun.openservices.shade.com.alibaba.fastjson.JSON;

import cn.trawe.etc.hunanfront.controller.v2.TrafficPoliceController;
import cn.trawe.etc.hunanfront.expose.v2.BaseRequest;
import cn.trawe.etc.hunanfront.expose.v2.BaseResponse;
import cn.trawe.etc.hunanfront.feign.entity.hunan.CarServiceReq;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TrafficPoliceTest {
	
	@Autowired
	private TrafficPoliceController  TrafficPoliceController;
    /**
     * 拓展业务提交订单  套装
     */
	@Test
	public void second_issue_replace_order_submit() {
		BaseRequest  req = new BaseRequest();
		CarServiceReq reqData = new CarServiceReq();
 		reqData.setVehPlate("湘A8B885");
		reqData.setVehPlateColor(1);
		reqData.setViVehicleType(2);
		req.setBizContent(JSON.toJSONString(reqData));
		
		BaseResponse resp = TrafficPoliceController.carServiceQuery(req);
		System.out.println(JSON.toJSONString(resp));
		
	}

}
