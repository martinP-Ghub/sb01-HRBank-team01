package com.project.hrbank.entity.enums;

public enum EmployeeStatus {
	ACTIVE("재직중"), ON_LEAVE("휴직중"), RESIGNED("퇴사");

	private final String description;

	EmployeeStatus(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
