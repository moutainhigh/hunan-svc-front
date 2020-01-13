package cn.trawe.etc.hunanfront.enums;

/*
 * 接口通信常量类
 */
public enum InterfaceErrorCode {
	
	RESULT_OK("100"),
	RESULT_OK_INFO("成功"),
	SYSTEM_ERROR("102"),
	SYSTEM_ERROR_INFO("系统异常"),
	CUSTOMER_SERVICE("0001"),
	CUSTOMER_SERVICE_INFO("联系客服");
	private String value;
	
	private InterfaceErrorCode(String value) {
		this.value =value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
	
	

}
