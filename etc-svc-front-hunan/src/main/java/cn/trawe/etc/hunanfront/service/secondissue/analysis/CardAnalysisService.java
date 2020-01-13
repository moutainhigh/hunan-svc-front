package cn.trawe.etc.hunanfront.service.secondissue.analysis;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import cn.trawe.etc.hunanfront.exception.AnalySisException;
import cn.trawe.etc.hunanfront.service.secondissue.apdu.CardInfoApdu;
import lombok.extern.slf4j.Slf4j;
/**
 *          console.log('读取卡片信息返回是', JSON.stringify(res));
	          if (!Array.isArray(res.data) || res.data.length === 0 || res.data[0].length !== 86) {
	            // 失败
	            typeof callBack.fail == 'function' &&
	              callBack.fail({
	                code: traweStatusCode.STATUS_FAIL,
	                msg: '解析卡信息失败' + JSON.stringify(res),
	                result_desc: '解析卡信息失败' + JSON.stringify(res)
	              });
	            return;
	          }
	          let resp = { code: traweStatusCode.STATUS_SUCCESS, msg: '成功', data: { info: {} } };
	          resp.data.info.filedata = res.data[0];
	          resp.data.info.distributionId = res.data[0].substr(0, 16);  //发卡方标识
	          resp.data.info.cardType = res.data[0].substr(16, 2);        //卡片类型
	          resp.data.info.cardVersion = res.data[0].substr(18, 2);    //卡片版本号
	          resp.data.info.cardNetNumber = res.data[0].substr(20, 4);//卡片网络编号
	          resp.data.info.cardNumber = res.data[0].substr(24, 16);//CPU卡内部编号
	          resp.data.info.startTime = res.data[0].substr(40, 8);//启用时间
	          resp.data.info.expirationTime = res.data[0].substr(48, 8);//到期时间
	          resp.data.info.plateNumber = res.data[0].substr(56, 24);//车牌号码
	          resp.data.info.userType = res.data[0].substr(80, 2);//用户类型
	          resp.data.info.plateColor = res.data[0].substr(82, 4);// 车牌颜色
	          resp.data.info.cardId = res.data[0].substr(20, 20);//卡号
 * @author jianjun.chai
 *
 */
@Service
@Slf4j
public class CardAnalysisService {

	
	public CardInfoApdu analysis(String apdu) {
		
		 if (apdu.length() == 0 || apdu.length() != 86 || !apdu.endsWith("9000")) {
	            // 失败
			    log.info("卡片解析指令失败,指定内容:"+apdu);
			    throw new AnalySisException("卡片解析指令失败,指定内容:"+apdu);
	     }
		CardInfoApdu  resp = new CardInfoApdu();
		resp.setDistributionId( apdu.substring(0, 16));
		resp.setCardType(apdu.substring(16, 18));
		resp.setCardVersion(apdu.substring(18, 20));
		//TODO
		resp.setCardNetNumber(apdu.substring(20, 24));
		//resp.setCardNetNumber("4301");
		resp.setCardNumber(apdu.substring(24, 40));
		//resp.setCardNumber("1900232500052902");
		resp.setStartTime(apdu.substring(40, 48));
		resp.setExpirationTime(apdu.substring(48, 56));
		resp.setPlateNumber(apdu.substring(56, 80));
		resp.setUserType(apdu.substring(80,82));
		//resp.setPlateColor(apdu.substring(82, 86));
		log.info("解析卡片信息为 : "+JSON.toJSONString(resp));
		return resp;
		
	}
	
	public static void main(String[] args) {
		System.out.println("01234567890123456789".substring(0, 16));
	}
	
}
