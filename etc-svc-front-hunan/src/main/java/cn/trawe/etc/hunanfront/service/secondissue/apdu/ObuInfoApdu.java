package cn.trawe.etc.hunanfront.service.secondissue.apdu;

import lombok.Data;

@Data
public class ObuInfoApdu {
	
	
	private String providerCode;
	
	private String treatyType;
	
	private String contractVersion;
	
	private String contractNo;
	
	private String signedDate;
	
	private String expiredDate;
	
	private String disassembleStatus;
	
	/**
	 * if (!Array.isArray(res.data) || res.data.length === 0 || res.data[0].length !== 58) {
        // 失败
        typeof callBack.fail == 'function' &&
          callBack.fail({
            code: traweStatusCode.STATUS_FAIL,
            msg: '解析系统信息失败' + JSON.stringify(res),
            result_desc: '解析系统信息失败' + JSON.stringify(res)
          });
        return;
      }
      let systemInfo = {};
      systemInfo.providerCode = res.data[0].substring(0, 16); // 服务提供商编码
      systemInfo.treatyType = res.data[0].substring(16, 18); // 协约类型
      systemInfo.contractVersion = res.data[0].substring(18, 20);  // 合同版本
      systemInfo.contractNo = res.data[0].substring(20, 36); // obu号 合同序列号
      systemInfo.signedDate = res.data[0].substring(36, 44); // 合同签署日期 格式：CCYYMMDD
      systemInfo.expiredDate = res.data[0].substring(44, 52);  // 合同过期日期 格式：CCYYMMDD
      systemInfo.disassembleStatus = res.data[0].substring(52, 54);  // 拆卸状态
      res.systemInfo = systemInfo;
	 */
	
	
}
