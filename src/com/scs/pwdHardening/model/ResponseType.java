package com.scs.pwdHardening.model;

public enum ResponseType {
	CORRECT(0), INCORRECT(1), SKIPPED(2);
	ResponseType(int value){
		this.value = value;
	}
	public int value;
}
