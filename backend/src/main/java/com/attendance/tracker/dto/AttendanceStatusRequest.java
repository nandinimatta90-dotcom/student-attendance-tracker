package com.attendance.tracker.dto;

import com.attendance.tracker.entity.AttendanceStatus;
import jakarta.validation.constraints.NotNull;

public class AttendanceStatusRequest {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    /**
     * Attendance status: PRESENT or ABSENT.
     * Can be null if the intention is to unmark an existing attendance record.
     */
    private AttendanceStatus status;

    public AttendanceStatusRequest() {
    }

    public AttendanceStatusRequest(Long studentId, AttendanceStatus status) {
        this.studentId = studentId;
        this.status = status;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }
}
