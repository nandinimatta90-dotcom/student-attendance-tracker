package com.attendance.tracker.dto;

import com.attendance.tracker.entity.Student;
import java.time.LocalDateTime;

public class StudentResponse {

    private Long id;
    private String name;
    private String rollNumber;
    private LocalDateTime createdAt;

    public StudentResponse() {
    }

    public StudentResponse(Long id, String name, String rollNumber, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.rollNumber = rollNumber;
        this.createdAt = createdAt;
    }

    public static StudentResponse fromEntity(Student student) {
        return new StudentResponse(
                student.getId(),
                student.getName(),
                student.getRollNumber(),
                student.getCreatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
