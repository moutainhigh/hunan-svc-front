package cn.trawe.etc.hunanfront.service.http;

import lombok.Data;

@Data
public class HttpResult {
	
	private int code;
	
	private String body;

	public HttpResult(int code, String body) {
		super();
		this.code = code;
		this.body = body;
	}

	public HttpResult() {
		super();
	}

	@Override
	public String toString() {
		return "HttpResult [code=" + code + ", body=" + body + "]";
	}

    

}