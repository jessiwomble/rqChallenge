package com.example.rqchallenge.employees.exceptions;

public class EmployeeNotFoundException extends Exception {

  private static final long serialVersionUID = -2338192372172234829L;

  public EmployeeNotFoundException(String employeeIdentifier) {
    super(String.format("Could not find employee %s.", employeeIdentifier));
  }
}
