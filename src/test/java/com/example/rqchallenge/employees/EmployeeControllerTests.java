package com.example.rqchallenge.employees;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.MethodMode;
import org.springframework.test.web.servlet.MockMvc;
import com.example.rqchallenge.domain.Employee;
import com.example.rqchallenge.employees.exceptions.EmployeeNotFoundException;
import com.example.rqchallenge.employees.exceptions.EmployeeValidationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

@SpringBootTest
@AutoConfigureMockMvc
class EmployeeControllerTests {

  private static final String AGE_KEY = "age";

  private static final String SALARY_KEY = "salary";

  private static final String NAME_KEY = "name";

  @Autowired
  private MockMvc mockMvc;

  ObjectMapper objectMapper = new ObjectMapper();

  @DirtiesContext(methodMode = MethodMode.BEFORE_METHOD)
  @Test
  void getAllEmployees() throws Exception {
    // Setup two employees. Assertions on creating Employees are done in another test.
    Map<String, Object> input = createDefaultEmployeeInput();
    createEmployeeForTest(input);
    createEmployeeForTest(input);

    MockHttpServletResponse response = runGetRequest("/");
    List<Employee> actual = objectMapper.readValue(response.getContentAsString(),
        new TypeReference<List<Employee>>() {});

    assertNotNull(actual);
    assertEquals(2, actual.size());
  }

  @Test
  void getEmployeesByNameSearch() throws JsonProcessingException, Exception {
    Map<String, Object> expected = createDefaultEmployeeInput();
    expected.put(NAME_KEY, "Find Me");
    createEmployeeForTest(expected);
    Map<String, Object> additionalEmployee = createDefaultEmployeeInput();
    createEmployeeForTest(additionalEmployee);

    // search with a partial match and different case
    MockHttpServletResponse response = runGetRequest("/search/IND");
    List<Employee> actual = objectMapper.readValue(response.getContentAsString(),
        new TypeReference<List<Employee>>() {});

    assertNotNull(actual);
    assertEquals(1, actual.size());
    assertEquals(expected.get(NAME_KEY), actual.get(0).getName());
  }

  @Test
  void getEmployeeById() throws Exception {
    Map<String, Object> expectedInput = createDefaultEmployeeInput();
    Employee expected = createEmployeeForTest(expectedInput);
    Map<String, Object> additionalEmployee = createDefaultEmployeeInput();
    createEmployeeForTest(additionalEmployee);

    // search with a partial match and different case
    String uri = String.format("/%s", expected.getId());
    MockHttpServletResponse response = runGetRequest(uri);
    Employee actual = objectMapper.readValue(response.getContentAsString(), Employee.class);

    assertEquals(expected, actual);
  }

  @Test
  void failGetEmployeeByWrongId() throws Exception {
    mockMvc.perform(get("/{id}", "Bad Id")).andExpect(status().isBadRequest()).andExpect(
        result -> assertTrue(result.getResolvedException() instanceof EmployeeNotFoundException));

  }

  @Test
  void getHighestSalaryOfEmployees() throws JsonProcessingException, Exception {
    Map<String, Object> matchingEmployee = createDefaultEmployeeInput();
    int expectedValue = 9999999;
    matchingEmployee.put(SALARY_KEY, expectedValue);
    createEmployeeForTest(matchingEmployee);
    Map<String, Object> additionalEmployee = createDefaultEmployeeInput();
    createEmployeeForTest(additionalEmployee);

    MockHttpServletResponse response = runGetRequest("/highestSalary");
    Integer actualValue = Integer.valueOf(response.getContentAsString());

    assertEquals(expectedValue, actualValue);

  }

  @DirtiesContext(methodMode = MethodMode.BEFORE_METHOD)
  @Test
  void getTopTenHighestEarningEmployeeNames() throws Exception {
    IntStream.range(0, 11).forEach(idx -> {
      Map<String, Object> input = createDefaultEmployeeInput();
      input.put(SALARY_KEY, idx);
      input.put(NAME_KEY, String.valueOf(idx));
      createEmployeeForTest(input);
    });

    MockHttpServletResponse response = runGetRequest("/topTenHighestEarningEmployeeNames");
    List<String> actual =
        objectMapper.readValue(response.getContentAsString(), new TypeReference<List<String>>() {});

    List<String> expected = Lists.newArrayList("10", "9", "8", "7", "6", "5", "4", "3", "2", "1");
    assertEquals(expected, actual);
  }

  @Test
  void createEmployee() throws Exception {
    Map<String, Object> input = createDefaultEmployeeInput();

    MockHttpServletResponse response = runPostRequest("/", input);
    Employee actual = objectMapper.readValue(response.getContentAsString(), Employee.class);

    assertNotNull(actual);
    assertNotNull(actual.getId());
    // reset id to make comparison easier
    actual.setId(null);
    Employee expected = objectMapper.convertValue(input, Employee.class);
    assertEquals(expected, actual);
  }

  @Test
  void failInvalidEmployeInput() throws JsonProcessingException, Exception {
    Map<String, Object> input = new HashMap<>();
    input.put(NAME_KEY, "Test Name");
    input.put(SALARY_KEY, "Wrong");
    input.put(AGE_KEY, "30");

    mockMvc
        .perform(post("/").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input)).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest()).andExpect(result -> assertTrue(
            result.getResolvedException() instanceof EmployeeValidationException));

  }

  @DirtiesContext(methodMode = MethodMode.BEFORE_METHOD)
  @Test
  void deleteEmployeeById() throws Exception {
    Map<String, Object> matchingEmployeeInput = createDefaultEmployeeInput();
    String expectedName = "Delete Me";
    matchingEmployeeInput.put(NAME_KEY, expectedName);
    Employee matchingEmployee = createEmployeeForTest(matchingEmployeeInput);
    Map<String, Object> additionalEmployee = createDefaultEmployeeInput();
    createEmployeeForTest(additionalEmployee);
    // confirm setup before removing an entry
    assertNumEmployees(2);


    String actual = mockMvc.perform(delete("/{id}", matchingEmployee.getId()))
        .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

    assertEquals(expectedName, actual);
    assertNumEmployees(1);
  }

  private void assertNumEmployees(int size) {
    try {
      MockHttpServletResponse response = runGetRequest("/");
      List<Employee> allEmployees = objectMapper.readValue(response.getContentAsString(),
          new TypeReference<List<Employee>>() {});
      assertNotNull(allEmployees);
      assertEquals(size, allEmployees.size());
    } catch (Exception e) {
      fail(e.getMessage());
    }


  }

  private Map<String, Object> createDefaultEmployeeInput() {
    Map<String, Object> input = new HashMap<>();
    input.put(NAME_KEY, "Test Name");
    input.put(SALARY_KEY, 50000);
    input.put(AGE_KEY, "30");
    return input;
  }

  private Employee createEmployeeForTest(Map<String, Object> input) {
    try {
      MockHttpServletResponse response = runPostRequest("/", input);
      return objectMapper.readValue(response.getContentAsString(), Employee.class);
    } catch (Exception e) {
      return null;
    }
  }

  private MockHttpServletResponse runPostRequest(String uri, Object body) throws Exception {
    return mockMvc
        .perform(post(uri).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()).andReturn().getResponse();
  }

  private MockHttpServletResponse runGetRequest(String uri) throws Exception {
    return mockMvc.perform(get(uri)).andExpect(status().isOk()).andReturn().getResponse();
  }

}
