package com.zhao.common.exception;

import com.zhao.common.respvo.ResponseStatus;

public class BusinessException extends RuntimeException {
	private static final long serialVersionUID = 3076501183921908289L;

	private int code;
	
	public BusinessException() {
		super();
	}

	public BusinessException(String msg) {
		super(msg);
		this.code = 500;
	}
	public BusinessException(ResponseStatus status) {
		super(status.getMsg());
		this.code = status.getCode();
	}
	public BusinessException(int code, String msg) {
		super(msg);
		this.code = code;
	}

	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
}
