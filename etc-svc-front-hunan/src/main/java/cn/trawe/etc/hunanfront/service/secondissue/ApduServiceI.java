package cn.trawe.etc.hunanfront.service.secondissue;

import cn.trawe.etc.hunanfront.service.secondissue.apdu.CardInfoApdu;
import cn.trawe.etc.hunanfront.service.secondissue.apdu.ObuInfoApdu;
import cn.trawe.etc.hunanfront.service.secondissue.apdu.RandomInfoApdu;

public interface ApduServiceI {
	
	//解析卡实体
	public CardInfoApdu analysisCardInfo(String apdu);
	//解析OBU 实体
	public ObuInfoApdu analysisObuInfo(String apdu);
	//解析随机数返回指令
	public RandomInfoApdu analysisRandomInfo(String apdu);
	
	

}
