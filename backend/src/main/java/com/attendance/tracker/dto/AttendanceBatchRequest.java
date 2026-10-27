package com.attendance.tracker.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public class AttendanceBatchRequest {

    @NotNull(message = "Attendance date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @NotNull(message = "Records list cannot be null")
    @NotEmpty(message = "At least one attendance record is required")
    @Valid
    private List<AttendanceStatusRequest> records;

    public AttendanceBatchRequest() {
    }

    public AttendanceBatchRequest(LocalDate date, List<AttendanceStatusRequest> records) {
        this.date = date;
        this.records = records;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<AttendanceStatusRequest> getRecords() {
        return records;
    }

    public void setRecords(List<AttendanceStatusRequest> records) {
        this.records = records;
    }
}
