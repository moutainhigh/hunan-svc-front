package cn.trawe.etc.hunanfront.config;

public enum InterfaceCenter {
	
	
	FAIL(1,"失败"),
	SUCCESS(0, "成功"),
	TIMEOUT(10,"服务降级");
	
	private int code;
    private String msg;
    InterfaceCenter(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
    
    

}
