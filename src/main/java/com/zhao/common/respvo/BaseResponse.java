package com.zhao.common.respvo;

/**
 * 统一响应基类
 * @author Administrator
 *
 * @param <T>
 */
public class BaseResponse<T> {

	private int code;
	private T content;
	private String msg;
	
	private BaseResponse() {}
	
	private BaseResponse(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}
	
	public static BaseResponse<?> getError() {
		return new BaseResponse<Object>();
	}
	
	public static <T> BaseResponse<T> ERROR(String msg) {
		BaseResponse<T> res = new BaseResponse<T>();
		res.code = 500;
		res.msg = msg;
		return res;
	}
	
	public static <T> BaseResponse<T> SUCCESS(T t) {
		BaseResponse<T> res = new BaseResponse<T>();
		res.code = 0;
		res.content = t;
		res.msg = "success";
		return res;
	}
	
	public static <T> BaseResponse<T> SUCCESS(int code, T t) {
		BaseResponse<T> res = new BaseResponse<T>();
		res.code = code;
		res.content = t;
		res.msg = "error";
		return res;
	}
	
	public static <T> BaseResponse<T> SUCCESS() {
		BaseResponse<T> res = new BaseResponse<T>();
		res.code = 0;
		res.content = null;
		res.msg = "success";
		return res;
	}
	
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public T getContent() {
		return content;
	}
	public void setContent(T content) {
		this.content = content;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
}
