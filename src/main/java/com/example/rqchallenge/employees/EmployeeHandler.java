package com.example.rqchallenge.employees;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import com.example.rqchallenge.domain.Employee;
import com.example.rqchallenge.employees.exceptions.EmployeeNotFoundException;
import com.example.rqchallenge.employees.exceptions.EmployeeValidationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EmployeeHandler {

  @Autowired
  private EmployeeRepository employeeRepository;

  private ObjectMapper objectMapper = new ObjectMapper();

  /**
   * @return all employees
   */
  public ResponseEntity<List<Employee>> getAllEmployees() {
    List<Employee> employees = employeeRepository.findAll();
    log.info("Found {} total employees.", employees.size());
    return ResponseEntity.ok(employees);
  }

  /**
   * @param searchString the employee name search input (case-insensitive)
   * @return all employees whose name contains or matches the string input provided
   */
  public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
    List<Employee> employees = employeeRepository.findByNameContainsIgnoreCase(searchString);
    log.info("Found {} employees containing name: {}", employees.size(), searchString);
    return ResponseEntity.ok(employees);
  }

  /**
   * @param id the employee ID
   * @return the employee with the provided ID
   * @throws EmployeeNotFoundException if no employees have the provided ID
   */
  public ResponseEntity<Employee> getEmployeeById(String id) throws EmployeeNotFoundException {
    Employee employee = findEmployeeById(id);
    log.info("Found employee {} with ID {}.", employee.getName(), id);
    return ResponseEntity.ok(employee);
  }

  /**
   * @return the highest salary of all employees
   */
  public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
    Employee employee = employeeRepository.findFirstByOrderBySalaryDesc();
    Integer salary = employee.getSalary();
    log.info("Found highest earning salary {} from employee {}", salary, employee.getId());
    return ResponseEntity.ok(salary);
  }

  /**
   * @return the names of the top 10 highest earning employees
   */
  public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
    List<Employee> employees = employeeRepository.findFirst10ByOrderBySalaryDesc();
    List<String> employeeNames =
        employees.stream().map(Employee::getName).collect(Collectors.toList());
    log.info("Found 10 highest earning employees: {}", employeeNames);
    return ResponseEntity.ok(employeeNames);
  }

  /**
   * Creates an employee with the provided attributes. An employee ID will be generated.
   * 
   * @param employeeInput the employee attributes
   * @return the created employee description
   * @throws EmployeeValidationException
   */
  public ResponseEntity<Employee> createEmployee(Map<String, Object> employeeInput)
      throws EmployeeValidationException {
    // The params in the README prompt are different from the provided interface, so I went with
    // this implementation because it's cleaner. This method could be used with a wrapper if we did
    // need an endpoint with individual params and a success/failure string response.
    try {
      Employee employee = objectMapper.convertValue(employeeInput, Employee.class);
      String id = UUID.randomUUID().toString();
      employee.setId(id);
      employeeRepository.save(employee);
      log.info("Created employee: {}", employee);
      return ResponseEntity.ok(employee);
    } catch (IllegalArgumentException e) {
      String errorMsg =
          String.format("Cannot create employee with invalid input: %s", employeeInput.toString());
      log.info(errorMsg);
      throw new EmployeeValidationException(errorMsg);
    }

  }

  /**
   * Permanently removes the employee with the provided ID
   * 
   * @param id the ID of the employee to be deleted
   * @return the name of the deleted employee
   * @throws EmployeeNotFoundException
   */
  public ResponseEntity<String> deleteEmployeeById(String id) throws EmployeeNotFoundException {
    // retrieve the employee to confirm the entry exists and get its name
    Employee employee = findEmployeeById(id);
    employeeRepository.deleteById(id);
    log.info("Deleted employee {}: {}", id, employee);
    return ResponseEntity.ok(employee.getName());
  }

  private Employee findEmployeeById(String id) throws EmployeeNotFoundException {
    return employeeRepository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
  }

}
