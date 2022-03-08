package com.example.rqchallenge;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.rqchallenge.employees.EmployeeController;

@SpringBootTest
class RqChallengeApplicationTests {

  @Autowired
  private EmployeeController employeeController;

  @Test
  void contextLoads() {
    assertThat(employeeController).isNotNull();
  }

}
