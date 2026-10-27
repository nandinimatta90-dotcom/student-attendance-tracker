package com.attendance.tracker.dto;

import com.attendance.tracker.entity.AttendanceStatus;

import java.time.LocalDateTime;

public class StudentAttendanceItemDto {

    private Long studentId;
    private String name;
    private String rollNumber;
    private Long attendanceRecordId;
    private AttendanceStatus status;
    private LocalDateTime updatedAt;

    public StudentAttendanceItemDto() {
    }

    public StudentAttendanceItemDto(Long studentId, String name, String rollNumber,
                                  Long attendanceRecordId, AttendanceStatus status,
                                  LocalDateTime updatedAt) {
        this.studentId = studentId;
        this.name = name;
        this.rollNumber = rollNumber;
        this.attendanceRecordId = attendanceRecordId;
        this.status = status;
        this.updatedAt = updatedAt;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
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

    public Long getAttendanceRecordId() {
        return attendanceRecordId;
    }

    public void setAttendanceRecordId(Long attendanceRecordId) {
        this.attendanceRecordId = attendanceRecordId;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
