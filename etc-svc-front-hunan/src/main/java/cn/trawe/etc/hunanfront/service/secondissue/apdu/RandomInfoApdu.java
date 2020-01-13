package cn.trawe.etc.hunanfront.service.secondissue.apdu;

import lombok.Data;

@Data
public class RandomInfoApdu {
	
	/**
	 *  if (res.data.length === 2 && res.data[1].length >= 12) {
        let random = res.data[1].substr(res.data[1].length - 12, 8);
        res.data = random;
        res.msg = '获取随机数成功';
        typeof callBack.success == 'function' && callBack.success(res);
      } else {
        res.code = traweStatusCode.STATUS_FAIL;
        res.msg = '获取随机数失败';
        typeof callBack.fail == 'function' && callBack.fail(res);
      }
	 */
	
	private String random;

}
