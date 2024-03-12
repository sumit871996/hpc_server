package com.app.entities;

public enum buildStatusEnum {

	SUCCESS("1"), // this sets a property to respective enum
	FAILURE("-1"), 
	UNSTABLE("2"), 
	INPROGRESS("0");

	private String value;

	private buildStatusEnum(String value) {
		this.value = value;
	}

	public String toString() {
		return this.value; // will return , or ' instead of COMMA or APOSTROPHE
	}

}
