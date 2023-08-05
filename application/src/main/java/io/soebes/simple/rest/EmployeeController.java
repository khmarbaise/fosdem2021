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
package io.soebes.simple.rest;

import io.soebes.simple.jpa.Employee;
import io.soebes.simple.repositories.EmployeeRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class EmployeeController {

  private final EmployeeRepository employeeRepository;

  public EmployeeController(EmployeeRepository employeeRepository) {
    this.employeeRepository = employeeRepository;
  }

  @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Void> create() {
    Employee employee = new Employee();
    employee.setDescription("Description");
    employee.setPublished(Boolean.FALSE);
    employee.setTitle("the Title.");
    this.employeeRepository.save(employee);
    return ResponseEntity.noContent().build();
  }

  @GetMapping(value = "lists", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<List<Employee>> lists() {
    return ResponseEntity.ok(this.employeeRepository.findAll());
  }
}
