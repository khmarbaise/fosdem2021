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
package io.soebes.tests.e2e;

import io.soebes.jupiter.extension.e2e.ApplicationPorts;
import io.soebes.jupiter.extension.e2e.DatabaseContainer;
import io.soebes.jupiter.extension.e2e.E2ETest;
import io.soebes.simple.jpa.Employee;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@E2ETest
class ExampleE2E {

  private static final Logger LOG = LoggerFactory.getLogger(ExampleE2E.class);

  @BeforeEach
  void beforeEach(ApplicationPorts ports) {
    System.out.println("--> ExampleE2E.beforeEach");
    int applicationPort = ports.getApplicationPort();

    RestTemplate restTemplate = new RestTemplateBuilder().build();
    for (int i = 0; i < 10; i++) {
      ResponseEntity<Void> voidResponseEntity = restTemplate
          .postForEntity(URI.create("http://localhost:" + applicationPort + "/create"), null, Void.class);
      System.out.printf("[%d] = voidResponseEntity.getStatusCode() = %s\n", i, voidResponseEntity.getStatusCode());
    }
  }

  @AfterEach
  void afterEach(ApplicationPorts ports) {
    LOG.info("Hello World.");
    System.out.println("--> ExampleE2E.afterEach");
    int applicationPort = ports.getApplicationPort();
    RestTemplate restTemplate = new RestTemplateBuilder().build();
    Employee[] forObject = restTemplate.getForObject("http://localhost:" + applicationPort + "/lists", Employee[].class);
    for (Employee employee : forObject) {
      System.out.println("employee = " + employee);
    }
  }

  @Test
  void first_test(DatabaseContainer db) {
    System.out.println("--> ExampleE2E.first_test");
    System.out.println("db.dataSourceUrl() = " + db.dataSourceUrl());
    System.out.println("db.getPassword() = " + db.getPassword());
    System.out.println("db.getUsername() = " + db.getUsername());
  }

  @Test
  void second_test(DatabaseContainer db) {
    System.out.println("ExampleE2E.second_test");
    System.out.println("db.dataSourceUrl() = " + db.dataSourceUrl());
    System.out.println("db.getPassword() = " + db.getPassword());
    System.out.println("db.getUsername() = " + db.getUsername());
  }

  @Test
  void third_test(DatabaseContainer db) {
    System.out.println("ExampleE2E.third_test");
    System.out.println("db.dataSourceUrl() = " + db.dataSourceUrl());
    System.out.println("db.getPassword() = " + db.getPassword());
    System.out.println("db.getUsername() = " + db.getUsername());
  }

  @Test
  void forth_test(DatabaseContainer db) {
    System.out.println("ExampleE2E.forth_test");
    System.out.println("db.dataSourceUrl() = " + db.dataSourceUrl());
    System.out.println("db.getPassword() = " + db.getPassword());
    System.out.println("db.getUsername() = " + db.getUsername());
  }

}
