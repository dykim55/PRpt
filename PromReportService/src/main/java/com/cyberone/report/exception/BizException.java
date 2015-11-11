package com.cyberone.report.exception;

import org.springframework.validation.BindingResult;

import com.cyberone.report.utils.StringUtil;

/**
 * <pre>
 * 업무 비즈에러 Exception
 * </pre>
 * @author Administrator
 *
 */
public class BizException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	private String errorCode = "";
	private String errorMessage = "";
	
	// 생성자
	public BizException() {
		super();
	}
	public BizException(String message) {
		super(message);
	}
	public BizException(Throwable cause) {
		super(cause);
	}
	public BizException(String message, Throwable cause) {
		super(message, cause);
	}
	public BizException(BindingResult bindingResult) {
		this(bindingResult.getGlobalError().getCode(), bindingResult.getGlobalError().getDefaultMessage());
	}
	public BizException(BindingResult bindingResult, String errorMessage) {
		this(bindingResult.getGlobalError().getCode(), errorMessage);	
	}
	public BizException(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	// getter, setter
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMessage() {
		return StringUtil.isEmpty(errorMessage) ? getMessage() : errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
}
