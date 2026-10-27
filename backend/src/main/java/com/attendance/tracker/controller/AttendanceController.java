package com.attendance.tracker.controller;

import com.attendance.tracker.dto.AttendanceBatchRequest;
import com.attendance.tracker.dto.AttendanceRecordResponse;
import com.attendance.tracker.dto.DailyAttendanceOverviewResponse;
import com.attendance.tracker.entity.AttendanceStatus;
import com.attendance.tracker.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    /**
     * Loads the complete attendance overview for a date,
     * including all students and live count of present, absent, and unmarked.
     */
    @GetMapping("/overview")
    public ResponseEntity<DailyAttendanceOverviewResponse> getDailyOverview(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        DailyAttendanceOverviewResponse overview = attendanceService.getDailyOverview(date);
        return ResponseEntity.ok(overview);
    }

    /**
     * Retrieves saved attendance records for a specific date.
     */
    @GetMapping
    public ResponseEntity<List<AttendanceRecordResponse>> getRecordsByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<AttendanceRecordResponse> records = attendanceService.getRecordsByDate(date);
        return ResponseEntity.ok(records);
    }

    /**
     * Saves or updates attendance for a single student on a given date.
     */
    @PostMapping("/single")
    public ResponseEntity<AttendanceRecordResponse> saveOrUpdateSingle(
            @RequestParam("studentId") Long studentId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(value = "status", required = false) AttendanceStatus status) {
        AttendanceRecordResponse response = attendanceService.saveOrUpdateRecord(studentId, date, status);
        return ResponseEntity.ok(response);
    }

    /**
     * Saves or updates attendance in batch for a date.
     * Enforces single record per student/date, updating existing records.
     */
    @PostMapping("/batch")
    public ResponseEntity<DailyAttendanceOverviewResponse> saveBatch(
            @Valid @RequestBody AttendanceBatchRequest request) {
        DailyAttendanceOverviewResponse updatedOverview = attendanceService.saveOrUpdateBatch(request);
        return ResponseEntity.ok(updatedOverview);
    }
}
