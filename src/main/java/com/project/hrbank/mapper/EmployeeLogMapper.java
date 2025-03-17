package com.project.hrbank.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.project.hrbank.dto.requset.EmployeeLogRequest;
import com.project.hrbank.dto.response.EmployeeLogResponse;
import com.project.hrbank.entity.EmployeeLogs;

@Mapper
public interface EmployeeLogMapper {
	EmployeeLogMapper INSTANT = Mappers.getMapper(EmployeeLogMapper.class);

	EmployeeLogResponse toDto(EmployeeLogs employeeLogs);

	EmployeeLogs toEntity(EmployeeLogRequest request);
}
