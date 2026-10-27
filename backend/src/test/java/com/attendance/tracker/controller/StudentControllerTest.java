package com.attendance.tracker.controller;

import com.attendance.tracker.dto.StudentRequest;
import com.attendance.tracker.dto.StudentResponse;
import com.attendance.tracker.exception.DuplicateResourceException;
import com.attendance.tracker.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudentService studentService;

    @Test
    @DisplayName("POST /api/students should create student and return 201 Created")
    void shouldCreateStudent() throws Exception {
        StudentRequest request = new StudentRequest("Alice Johnson", "CS-101");
        StudentResponse response = new StudentResponse(1L, "Alice Johnson", "CS-101", LocalDateTime.now());

        when(studentService.addStudent(any(StudentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Alice Johnson"))
                .andExpect(jsonPath("$.rollNumber").value("CS-101"));
    }

    @Test
    @DisplayName("POST /api/students should return 409 Conflict when roll number is duplicate")
    void shouldReturnConflictWhenDuplicateRollNumber() throws Exception {
        StudentRequest request = new StudentRequest("Alice Johnson", "CS-101");

        when(studentService.addStudent(any(StudentRequest.class)))
                .thenThrow(new DuplicateResourceException("Student with roll number 'CS-101' already exists."));

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Student with roll number 'CS-101' already exists."));
    }

    @Test
    @DisplayName("POST /api/students should return 400 Bad Request when validation fails")
    void shouldReturnBadRequestWhenNameOrRollBlank() throws Exception {
        StudentRequest request = new StudentRequest("", "");

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.name").exists())
                .andExpect(jsonPath("$.validationErrors.rollNumber").exists());
    }

    @Test
    @DisplayName("GET /api/students should return list of students")
    void shouldReturnAllStudents() throws Exception {
        StudentResponse s1 = new StudentResponse(1L, "Alice Johnson", "CS-101", LocalDateTime.now());
        StudentResponse s2 = new StudentResponse(2L, "Bob Smith", "CS-102", LocalDateTime.now());

        when(studentService.getAllStudents()).thenReturn(List.of(s1, s2));

        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].rollNumber").value("CS-101"))
                .andExpect(jsonPath("$[1].rollNumber").value("CS-102"));
    }
}
