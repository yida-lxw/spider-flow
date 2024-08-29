package org.spiderflow.model;

public class JsonBean<T> {
	public static final int SUCCESS_CODE = 200;
	public static final int ERROR_CODE = 500;

	public static final String SUCCESS_MESSAGE = "ok";
	public static final String ERROR_MESSAGE = "error";

	private Integer code = 200;

	private String message = "执行成功";

	private T data;

	public JsonBean(Integer code, String message, T data) {
		this.code = code;
		this.message = message;
		this.data = data;
	}

	public JsonBean(Integer code, String message) {
		this.code = code;
		this.message = message;
	}

	public JsonBean(T data) {
		this.data = data;
	}

	public static <T> JsonBean success(int code, String message, T data) {
		return new JsonBean(code, message, data);
	}

	public static JsonBean success(int code, String message) {
		return success(code, message, null);
	}

	public static JsonBean success(String message) {
		return success(SUCCESS_CODE, message);
	}

	public static <T> JsonBean success(T data) {
		return success(SUCCESS_CODE, SUCCESS_MESSAGE, data);
	}

	public static JsonBean success() {
		return success(SUCCESS_MESSAGE);
	}

	public static JsonBean error(String message) {
		return error(ERROR_CODE, message);
	}

	public static JsonBean error(int code, String message) {
		return new JsonBean(code, message);
	}

	public static JsonBean error() {
		return error(ERROR_MESSAGE);
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}
