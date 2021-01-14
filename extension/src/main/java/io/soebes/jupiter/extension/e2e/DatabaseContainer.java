package io.soebes.jupiter.extension.e2e;

import org.testcontainers.containers.GenericContainer;

import java.time.Duration;
import java.util.Map;

public class DatabaseContainer {
  private static final int DATABASE_PORT = 5432;

  private static final String POSTGRESQL_IMAGE = "postgres:9.6.20-alpine";

  private final String database;
  private final String username;
  private final String password;

  private final String hibernateDialect;

  private GenericContainer<?> postgresContainer;

  DatabaseContainer() {
    this.username = "postgres";
    this.password = "123";
    this.database = "testdb";
    this.hibernateDialect = "org.hibernate.dialect.PostgreSQLDialect";
  }

  void start() {
    this.postgresContainer = new GenericContainer<>(POSTGRESQL_IMAGE)
        .withExposedPorts(DATABASE_PORT)
        .withStartupAttempts(5)
        .withStartupTimeout(Duration.ofSeconds(60))
        .withEnv(Map.of(
            "POSTGRES_USER", this.username,
            "POSTGRES_PASSWORD", this.password,
            "POSTGRES_DB", this.database
        ));
    this.postgresContainer.start();
  }

  void stop() {
    this.postgresContainer.stop();
  }

  public String dataSourceUrl() {
    return "jdbc:postgresql://localhost:" + this.postgresContainer.getMappedPort(DATABASE_PORT) + "/" + this.database;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getHibernateDialect() {
    return hibernateDialect;
  }
}
