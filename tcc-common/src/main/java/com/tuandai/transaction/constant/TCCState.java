package com.tuandai.transaction.constant;
public enum TCCState {

    COMMIT(0, "提交"), CANCEL(1, "回滚"), UNKNOW(2, "未知");

    private final int value;

    private final String message;

    TCCState(int value, String message) {
        this.value = value;
        this.message = message;
    }

    public int value() {
        return value;
    }

    public String message() {
        return message;
    }

    public static TCCState findByValue(int value) {
        for (TCCState tccState : TCCState.values()) {
            if (tccState.value == value) {
                return tccState;
            }
        }
        return null;
    }
}