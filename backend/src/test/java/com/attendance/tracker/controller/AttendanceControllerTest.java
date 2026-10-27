package com.attendance.tracker.controller;

import com.attendance.tracker.dto.AttendanceBatchRequest;
import com.attendance.tracker.dto.AttendanceStatusRequest;
import com.attendance.tracker.dto.DailyAttendanceOverviewResponse;
import com.attendance.tracker.dto.StudentAttendanceItemDto;
import com.attendance.tracker.entity.AttendanceStatus;
import com.attendance.tracker.service.AttendanceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AttendanceController.class)
class AttendanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AttendanceService attendanceService;

    @Test
    @DisplayName("GET /api/attendance/overview should return complete overview for requested date")
    void shouldReturnDailyOverview() throws Exception {
        LocalDate date = LocalDate.of(2026, 10, 27);
        DailyAttendanceOverviewResponse overview = new DailyAttendanceOverviewResponse(
                date,
                2,
                1,
                1,
                0,
                50.0,
                List.of(
                        new StudentAttendanceItemDto(1L, "Alice Johnson", "CS-101", 101L, AttendanceStatus.PRESENT, null),
                        new StudentAttendanceItemDto(2L, "Bob Smith", "CS-102", 102L, AttendanceStatus.ABSENT, null)
                )
        );

        when(attendanceService.getDailyOverview(eq(date))).thenReturn(overview);

        mockMvc.perform(get("/api/attendance/overview")
                        .param("date", "2026-10-27"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value("2026-10-27"))
                .andExpect(jsonPath("$.totalStudents").value(2))
                .andExpect(jsonPath("$.presentCount").value(1))
                .andExpect(jsonPath("$.absentCount").value(1))
                .andExpect(jsonPath("$.unmarkedCount").value(0))
                .andExpect(jsonPath("$.students.length()").value(2));
    }

    @Test
    @DisplayName("POST /api/attendance/batch should save attendance and return updated overview")
    void shouldSaveBatchAttendance() throws Exception {
        LocalDate date = LocalDate.of(2026, 10, 27);
        AttendanceBatchRequest batchRequest = new AttendanceBatchRequest(
                date,
                List.of(
                        new AttendanceStatusRequest(1L, AttendanceStatus.PRESENT),
                        new AttendanceStatusRequest(2L, AttendanceStatus.ABSENT)
                )
        );

        DailyAttendanceOverviewResponse response = new DailyAttendanceOverviewResponse(
                date,
                2,
                1,
                1,
                0,
                50.0,
                List.of(
                        new StudentAttendanceItemDto(1L, "Alice Johnson", "CS-101", 101L, AttendanceStatus.PRESENT, null),
                        new StudentAttendanceItemDto(2L, "Bob Smith", "CS-102", 102L, AttendanceStatus.ABSENT, null)
                )
        );

        when(attendanceService.saveOrUpdateBatch(any(AttendanceBatchRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/attendance/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(batchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.presentCount").value(1))
                .andExpect(jsonPath("$.absentCount").value(1));
    }
}
