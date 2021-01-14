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

  private EmployeeRepository employeeRepository;

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
