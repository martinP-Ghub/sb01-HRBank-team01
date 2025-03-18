package com.project.hrbank.backup.provider;

import com.project.hrbank.entity.Employee;
import com.project.hrbank.repository.EmployeeRepository;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CsvProvider {

    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    public static final String BACKUP_FILE_NAME = "backup_employee_";

    private final EmployeeRepository employeeRepository;

    public String generateFileName() {
        String formattedDateTime = LocalDateTime.now().format(TIME_FORMAT);
        return BACKUP_FILE_NAME + formattedDateTime;
    }

    public byte[] loadEmployeeData() {
        PrintWriter printWriter = new PrintWriter(new StringWriter());
        printWriter.println("ID,직원번호,이름,부서,직급,입사일,상태");
        List<Employee> employees = employeeRepository.findAll();
        employees.forEach(employee -> {
            writeEmployeeInfo(employee, printWriter);
        });
        return printWriter.toString().getBytes(StandardCharsets.UTF_8);
    }

    private void writeEmployeeInfo(Employee employee, PrintWriter printWriter) {
        printWriter.printf("%d, %s, %s, %s, %s, %s, %s\n", employee.getEmployeeId(), employee.getEmployeeNumber(),
                employee.getName(),
                employee.getDepartmentId(), employee.getPosition(), employee.getHireDate(), employee.getStatus());
    }
}
