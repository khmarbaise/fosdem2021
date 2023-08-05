/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
