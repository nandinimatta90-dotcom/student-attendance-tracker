package com.attendance.tracker.service;

import com.attendance.tracker.dto.StudentRequest;
import com.attendance.tracker.dto.StudentResponse;
import com.attendance.tracker.entity.Student;
import com.attendance.tracker.exception.DuplicateResourceException;
import com.attendance.tracker.exception.ResourceNotFoundException;
import com.attendance.tracker.repository.AttendanceRecordRepository;
import com.attendance.tracker.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class StudentService {

    private static final Logger log = LoggerFactory.getLogger(StudentService.class);

    private final StudentRepository studentRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;

    public StudentService(StudentRepository studentRepository, AttendanceRecordRepository attendanceRecordRepository) {
        this.studentRepository = studentRepository;
        this.attendanceRecordRepository = attendanceRecordRepository;
    }

    public StudentResponse addStudent(StudentRequest request) {
        String trimmedName = request.getName().trim();
        String trimmedRollNumber = request.getRollNumber().trim().toUpperCase();

        if (studentRepository.existsByRollNumberIgnoreCase(trimmedRollNumber)) {
            log.warn("Attempt to register student with duplicate roll number: {}", trimmedRollNumber);
            throw new DuplicateResourceException("Student with roll number '" + trimmedRollNumber + "' already exists.");
        }

        Student student = new Student(trimmedName, trimmedRollNumber);
        Student saved = studentRepository.save(student);
        log.info("Registered new student: id={}, rollNumber={}, name={}", saved.getId(), saved.getRollNumber(), saved.getName());
        return StudentResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> getAllStudents() {
        return studentRepository.findAllByOrderByRollNumberAsc()
                .stream()
                .map(StudentResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public Student getStudentEntityById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public StudentResponse getStudentById(Long id) {
        return StudentResponse.fromEntity(getStudentEntityById(id));
    }

    public void deleteStudent(Long id) {
        Student student = getStudentEntityById(id);
        attendanceRecordRepository.deleteByStudentId(id);
        studentRepository.delete(student);
        log.info("Deleted student id={} and associated attendance records", id);
    }
}
