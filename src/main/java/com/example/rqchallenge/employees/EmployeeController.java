package com.example.rqchallenge.employees;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import com.example.rqchallenge.domain.Employee;
import com.example.rqchallenge.employees.exceptions.EmployeeNotFoundException;
import com.example.rqchallenge.employees.exceptions.EmployeeValidationException;
import lombok.extern.log4j.Log4j2;

@RestController
@Log4j2
public class EmployeeController implements IEmployeeController {

  @Autowired
  private EmployeeHandler employeeHandler;

  @Override
  public ResponseEntity<List<Employee>> getAllEmployees() {
    log.info("Retrieving all employees");
    return employeeHandler.getAllEmployees();
  }

  @Override
  public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
    log.info("Retrieving employees with name containing: {}", searchString);
    return employeeHandler.getEmployeesByNameSearch(searchString);
  }

  @Override
  public ResponseEntity<Employee> getEmployeeById(String id) throws EmployeeNotFoundException {
    log.info("Retrieving employee with ID: {}", id);
    return employeeHandler.getEmployeeById(id);
  }

  @Override
  public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
    log.info("Retrieving highest salary.");
    return employeeHandler.getHighestSalaryOfEmployees();
  }

  @Override
  public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
    log.info("Retrieving names of top ten highest earning employees.");
    return employeeHandler.getTopTenHighestEarningEmployeeNames();
  }


  @Override
  public ResponseEntity<Employee> createEmployee(Map<String, Object> employeeInput)
      throws EmployeeValidationException {
    log.info("Creating employee with attributes: {}", employeeInput);
    return employeeHandler.createEmployee(employeeInput);
  }

  @Override
  public ResponseEntity<String> deleteEmployeeById(String id) throws EmployeeNotFoundException {
    log.info("Deleting employe with ID: {}", id);
    return employeeHandler.deleteEmployeeById(id);
  }
}
