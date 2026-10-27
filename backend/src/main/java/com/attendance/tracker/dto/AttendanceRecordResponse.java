package com.attendance.tracker.dto;

import com.attendance.tracker.entity.AttendanceRecord;
import com.attendance.tracker.entity.AttendanceStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AttendanceRecordResponse {

    private Long id;
    private Long studentId;
    private String studentName;
    private String studentRollNumber;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate attendanceDate;

    private AttendanceStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AttendanceRecordResponse() {
    }

    public AttendanceRecordResponse(Long id, Long studentId, String studentName, String studentRollNumber,
                                    LocalDate attendanceDate, AttendanceStatus status,
                                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentRollNumber = studentRollNumber;
        this.attendanceDate = attendanceDate;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static AttendanceRecordResponse fromEntity(AttendanceRecord record) {
        return new AttendanceRecordResponse(
                record.getId(),
                record.getStudent().getId(),
                record.getStudent().getName(),
                record.getStudent().getRollNumber(),
                record.getAttendanceDate(),
                record.getStatus(),
                record.getCreatedAt(),
                record.getUpdatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentRollNumber() {
        return studentRollNumber;
    }

    public void setStudentRollNumber(String studentRollNumber) {
        this.studentRollNumber = studentRollNumber;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
