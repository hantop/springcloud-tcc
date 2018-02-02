package com.tuandai.architecture.domain;

import com.tuandai.transaction.constant.TCCState;

public enum TableType {

	TCC_START(0, "TCC开始表"), TCC_END(1, "TCC结束表");

	private final int value;

	private final String message;

	TableType(int value, String message) {
		this.value = value;
		this.message = message;
	}

	public int value() {
		return value;
	}

	public String message() {
		return message;
	}

	public static TableType findByValue(int value) {
		for (TableType tccState : TableType.values()) {
			if (tccState.value == value) {
				return tccState;
			}
		}
		return null;
	}
}
