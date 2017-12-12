package com.tuandai.architecture.constant;

/**
 * 
 */
public enum TransState {

	PENDING(0, "处理中"),

    CONFIRM(1, "事务确认提交"),
    
    CANCEL(2, "事务取消回滚"),
    
    UNKNOW(-1, "未知");

    private final int code;

    private final String message;


    TransState(int code, String message) {
        this.code = code;
        this.message = message;
    }


    public int code() {
        return code;
    }

    public String message() {
        return message;
    }

}
