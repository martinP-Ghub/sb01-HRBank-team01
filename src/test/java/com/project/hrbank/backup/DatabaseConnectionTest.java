package com.project.hrbank.backup;

import static org.assertj.core.api.Assertions.*;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DatabaseConnectionTest {

	private static final String DATABASE_NAME = "hrbank";
	@Autowired
	private DataSource dataSource;

	@Test
	@DisplayName("데이터베이스 로컬 postgre sql 접속 테스트")
	void database_connection() {
		try (Connection connection = dataSource.getConnection()) {
			assertThat(connection).isNotNull();
			assertThat(connection.getCatalog()).isEqualTo(DATABASE_NAME);
		} catch (SQLException exception) {
			throw new RuntimeException(exception);
		}
	}
}
