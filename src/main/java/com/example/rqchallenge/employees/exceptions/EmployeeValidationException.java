package com.example.rqchallenge.employees.exceptions;

public class EmployeeValidationException extends Exception {

  private static final long serialVersionUID = -8617397018103136630L;

  public EmployeeValidationException(String errorMessage) {
    super(errorMessage);
  }
}
