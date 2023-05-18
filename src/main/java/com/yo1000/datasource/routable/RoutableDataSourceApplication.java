package com.yo1000.datasource.routable;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@SpringBootApplication
public class RoutableDataSourceApplication {
	public static void main(String[] args) {
		SpringApplication.run(RoutableDataSourceApplication.class, args);
	}

	@RestController
	@RequestMapping("/")
	public static class UuidRestController {
		private final NamedParameterJdbcOperations jdbcOps;

		public UuidRestController(NamedParameterJdbcOperations jdbcOps) {
			this.jdbcOps = jdbcOps;
		}

		@GetMapping
		@Transactional(readOnly = true)
		public Object get() {
			return jdbcOps.query(
					"SELECT * FROM uuid",
					DataClassRowMapper.newInstance(Uuid.class)
			);
		}

		@PostMapping
		@Transactional
		public Object post() {
			return jdbcOps.update(
					"INSERT INTO uuid(id) VALUES(:id)",
					Map.ofEntries(Map.entry("id", UUID.randomUUID()))
			);
		}
	}

	public record Uuid(
			String id
	) {}
}
