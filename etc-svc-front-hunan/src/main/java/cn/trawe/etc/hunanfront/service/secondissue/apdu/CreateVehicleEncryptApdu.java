package cn.trawe.etc.hunanfront.service.secondissue.apdu;

import java.util.HashMap;
import java.util.Map;

public class CreateVehicleEncryptApdu {
	
	/**
	 * 读取车辆信息
	 * @param {string} randomNo 随机数
	 * @param {{fail:function,success:function}} callBack 回调
	 */
//	export function readCarInfo(randomNo, callBack) {
//	  let cmd = [];
//	  cmd[0] = '00A4000002DF01';
//	  cmd[1] = '00B400000A' + randomNo + '3B00';
	            //00B400000A   bc1184b7        3B00
//	  sendAndReceiveCheckSW(CMD_TYPE_ESAM, cmd, {
//	    success: (res) => {
//	      console.log('车辆密文是', res.data[1]);
//	      if (Array.isArray(res.data) && res.data.length === 2 && res.data[1].length >= 148
//	        && hexUtil.getSw1Sw2(res.data[1]) === traweStatusCode.SW1SW2_SUCCESS) {
//	        res.msg = '读取车辆信息成功';
//	        res.data = res.data[1].substr(res.data[1].length - 148, 144);
//	        typeof callBack.success == 'function' && callBack.success(res);
//	      } else {
//	        res.code = traweStatusCode.STATUS_FAIL;
//	        res.msg = '读取车辆信息失败';
//	        typeof callBack.fail == 'function' && callBack.fail(res);
//	      }
//	    },
//	    fail: callBack.fail
//	  });
//	}
	
	public Map<String,Object> createHeaderReadObuAndCard(){
		
		Map<String,Object> apduList = new HashMap<String,Object>();
//		  cmd[0] = '00A4000002DF01';
//		  cmd[1] = '00B400000A' + randomNo + '3B00';
//		apduList.put("0", obuRespChoose3F00);
//		apduList.put("1", obuRespReadSystem);
//		apduList.put("2", cardRespChoose3F00);
//		apduList.put("3", cardRespChoose1001);
//		apduList.put("4", cardRespReadCard);
//		return apduList;
		return apduList;
		
	}

}
