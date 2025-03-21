package com.project.hrbank.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FileExtension {

	CSV("csv"),
	LOG("log"),
	IMAGE("image"),
	;
	private final String description;
}
