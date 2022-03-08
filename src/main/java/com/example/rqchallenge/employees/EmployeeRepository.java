package com.example.rqchallenge.employees;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.rqchallenge.domain.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, String> {

  /**
   * @param searchString the partial name search input
   * @return all employees whose name contains the provided string (case-insensitive)
   */
  public List<Employee> findByNameContainsIgnoreCase(String searchString);

  /**
   * @return the employee with the highest salary
   */
  public Employee findFirstByOrderBySalaryDesc();

  /**
   * @return the top 10 highest earning employees
   */
  public List<Employee> findFirst10ByOrderBySalaryDesc();
}
