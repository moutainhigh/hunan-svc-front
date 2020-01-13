package cn.trawe.etc.hunanfront.service.secondissue.analysis;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import cn.trawe.etc.hunanfront.exception.AnalySisException;
import cn.trawe.etc.hunanfront.service.secondissue.apdu.ObuInfoApdu;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ObuAnalysisService {
	
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
	 * @param apdu
	 * @return
	 */
	
	public ObuInfoApdu analysis(String apdu) {
		ObuInfoApdu resp = new ObuInfoApdu();
		
		if (apdu.length() == 0 ||!apdu.endsWith("9000")) {
	        // 失败
			throw new AnalySisException("卡OBU系统信息解析指令失败,指定内容:"+apdu);
	      }
		resp.setProviderCode(apdu.substring(0, 16));
		resp.setTreatyType(apdu.substring(16, 18));
		resp.setContractVersion(apdu.substring(18, 20));
		//TODO
		resp.setContractNo(apdu.substring(20, 36));
		//resp.setContractNo("4301190220052902");
		
		resp.setSignedDate(apdu.substring(36,44));
		resp.setExpiredDate(apdu.substring(44, 52));
		resp.setDisassembleStatus(apdu.substring(52, 54));
		log.info("解析OBU信息为 :" +JSON.toJSONString(resp));
		return resp;
	}
	
	

}
