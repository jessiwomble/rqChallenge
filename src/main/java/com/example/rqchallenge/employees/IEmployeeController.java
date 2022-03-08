package com.example.rqchallenge.employees;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.rqchallenge.domain.Employee;
import com.example.rqchallenge.employees.exceptions.EmployeeNotFoundException;
import com.example.rqchallenge.employees.exceptions.EmployeeValidationException;

@RestController
public interface IEmployeeController {

  /**
   * @return all employees in the repository
   * @throws IOException
   */
  @GetMapping()
  ResponseEntity<List<Employee>> getAllEmployees() throws IOException;

  /**
   * @param searchString the employee name search input (case-insensitive)
   * @return all employees whose name contains or matches the string input provided
   */
  @GetMapping("/search/{searchString}")
  ResponseEntity<List<Employee>> getEmployeesByNameSearch(@PathVariable String searchString);

  /**
   * @param id the employee ID
   * @return the employee with the provided ID
   * @throws EmployeeNotFoundException if no employees have the provided ID
   */
  @GetMapping("/{id}")
  ResponseEntity<Employee> getEmployeeById(@PathVariable String id)
      throws EmployeeNotFoundException;

  /**
   * @return the highest salary of all employees
   */
  @GetMapping("/highestSalary")
  ResponseEntity<Integer> getHighestSalaryOfEmployees();

  /**
   * @return the names of the top 10 highest earning employees
   */
  @GetMapping("/topTenHighestEarningEmployeeNames")
  ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames();

  /**
   * Creates an employee with the provided attributes. An employee ID will be generated.
   * 
   * @param employeeInput the employee attributes
   * @return the created employee description
   * @throws EmployeeValidationException if any of the provided employee attributes are the wrong
   *         type.
   */
  @PostMapping()
  ResponseEntity<Employee> createEmployee(
      @RequestBody(required = true) Map<String, Object> employeeInput)
      throws EmployeeValidationException;

  /**
   * Permanently removes the employee with the provided ID
   * 
   * @param id the ID of the employee to be deleted
   * @return the name of the deleted employee
   * @throws EmployeeNotFoundException if the provided ID doesn't exist.
   */
  @DeleteMapping("/{id}")
  ResponseEntity<String> deleteEmployeeById(@PathVariable String id)
      throws EmployeeNotFoundException;

}
