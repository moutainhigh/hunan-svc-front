package cn.trawe.etc.hunanfront.request.secondissue;

import java.util.Map;

import cn.trawe.etc.hunanfront.expose.v2.BaseResp;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class SecondIssueResp  extends BaseResp {
	
	private Integer totalStep;
	
	private Integer curStep;
	
	private String cardNo;
	
	private String obuNo;
	
	//是否下发读取卡片信息，读取OBU 信息指令
	private String apduFlag;
	//下发参数需要前端原样传递过来
	private String originValue;
	
	private String message;
	
	private Map<String,Object> respInfo;

	@Override
	public String toString() {
		return "SecondIssueResp [totalStep=" + totalStep + ", curStep=" + curStep + ", cardNo=" + cardNo + ", obuNo="
				+ obuNo + ", apduFlag=" + apduFlag + ", originValue=" + originValue + ", message=" + message
				+ ", respInfo=" + respInfo + ", toString()=" + super.toString() + "]";
	}
	
	
	

}
