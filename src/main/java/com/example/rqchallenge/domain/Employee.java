package com.example.rqchallenge.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Employee {

  @Id
  private String id;
  /**
   * It would be better to split name into different parts and calculate display names as needed.
   * I'm assuming name has been combined here for the sake of time.
   */
  private String name;
  private Integer salary;
  private String age;
}
