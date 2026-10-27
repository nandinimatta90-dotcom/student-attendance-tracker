package com.attendance.tracker.service;

import com.attendance.tracker.dto.StudentRequest;
import com.attendance.tracker.dto.StudentResponse;
import com.attendance.tracker.entity.Student;
import com.attendance.tracker.exception.DuplicateResourceException;
import com.attendance.tracker.exception.ResourceNotFoundException;
import com.attendance.tracker.repository.AttendanceRecordRepository;
import com.attendance.tracker.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private AttendanceRecordRepository attendanceRecordRepository;

    @InjectMocks
    private StudentService studentService;

    private Student student1;
    private Student student2;

    @BeforeEach
    void setUp() {
        student1 = new Student(1L, "Alice Johnson", "CS-101");
        student2 = new Student(2L, "Bob Smith", "CS-102");
    }

    @Test
    @DisplayName("Should successfully register a new student with unique roll number")
    void shouldAddStudentSuccessfully() {
        StudentRequest request = new StudentRequest("Alice Johnson", "CS-101");
        when(studentRepository.existsByRollNumberIgnoreCase("CS-101")).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenReturn(student1);

        StudentResponse response = studentService.addStudent(request);

        assertNotNull(response);
        assertEquals("Alice Johnson", response.getName());
        assertEquals("CS-101", response.getRollNumber());
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when roll number already exists")
    void shouldThrowExceptionWhenDuplicateRollNumber() {
        StudentRequest request = new StudentRequest("Another Alice", "CS-101");
        when(studentRepository.existsByRollNumberIgnoreCase("CS-101")).thenReturn(true);

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> studentService.addStudent(request)
        );

        assertTrue(exception.getMessage().contains("CS-101"));
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("Should return all students ordered by roll number")
    void shouldReturnAllStudents() {
        when(studentRepository.findAllByOrderByRollNumberAsc()).thenReturn(List.of(student1, student2));

        List<StudentResponse> students = studentService.getAllStudents();

        assertEquals(2, students.size());
        assertEquals("CS-101", students.get(0).getRollNumber());
        assertEquals("CS-102", students.get(1).getRollNumber());
    }

    @Test
    @DisplayName("Should find student by ID or throw ResourceNotFoundException")
    void shouldFindStudentById() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student1));
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        StudentResponse response = studentService.getStudentById(1L);
        assertNotNull(response);
        assertEquals("Alice Johnson", response.getName());

        assertThrows(ResourceNotFoundException.class, () -> studentService.getStudentById(999L));
    }
}
