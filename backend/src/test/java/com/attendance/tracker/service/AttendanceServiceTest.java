package com.attendance.tracker.service;

import com.attendance.tracker.dto.AttendanceBatchRequest;
import com.attendance.tracker.dto.AttendanceRecordResponse;
import com.attendance.tracker.dto.AttendanceStatusRequest;
import com.attendance.tracker.dto.DailyAttendanceOverviewResponse;
import com.attendance.tracker.entity.AttendanceRecord;
import com.attendance.tracker.entity.AttendanceStatus;
import com.attendance.tracker.entity.Student;
import com.attendance.tracker.repository.AttendanceRecordRepository;
import com.attendance.tracker.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest {

    @Mock
    private AttendanceRecordRepository attendanceRecordRepository;

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private AttendanceService attendanceService;

    private Student student1;
    private Student student2;
    private Student student3;
    private LocalDate testDate;

    @BeforeEach
    void setUp() {
        student1 = new Student(1L, "Alice Johnson", "CS-101");
        student2 = new Student(2L, "Bob Smith", "CS-102");
        student3 = new Student(3L, "Charlie Brown", "CS-103");
        testDate = LocalDate.of(2026, 10, 27);
    }

    @Test
    @DisplayName("Should accurately calculate present, absent, and unmarked counts without counting unmarked as absent")
    void shouldCalculateAttendanceOverviewAccurately() {
        // student1 is PRESENT, student2 is ABSENT, student3 is UNMARKED (no record)
        AttendanceRecord record1 = new AttendanceRecord(101L, student1, testDate, AttendanceStatus.PRESENT);
        AttendanceRecord record2 = new AttendanceRecord(102L, student2, testDate, AttendanceStatus.ABSENT);

        when(studentRepository.findAllByOrderByRollNumberAsc()).thenReturn(List.of(student1, student2, student3));
        when(attendanceRecordRepository.findByAttendanceDate(testDate)).thenReturn(List.of(record1, record2));

        DailyAttendanceOverviewResponse overview = attendanceService.getDailyOverview(testDate);

        assertNotNull(overview);
        assertEquals(testDate, overview.getDate());
        assertEquals(3, overview.getTotalStudents());
        assertEquals(1, overview.getPresentCount());
        assertEquals(1, overview.getAbsentCount());
        assertEquals(1, overview.getUnmarkedCount(), "Unmarked students must NOT be counted as absent!");
        assertEquals(50.0, overview.getAttendanceRatePercentage());

        assertEquals(3, overview.getStudents().size());
        assertEquals(AttendanceStatus.PRESENT, overview.getStudents().get(0).getStatus());
        assertEquals(AttendanceStatus.ABSENT, overview.getStudents().get(1).getStatus());
        assertNull(overview.getStudents().get(2).getStatus(), "Student 3 should be unmarked (null status)");
    }

    @Test
    @DisplayName("Should create new record when none exists for the student and date")
    void shouldCreateNewRecordWhenNoneExists() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student1));
        when(attendanceRecordRepository.findByStudentIdAndAttendanceDate(1L, testDate)).thenReturn(Optional.empty());

        AttendanceRecord newRecord = new AttendanceRecord(201L, student1, testDate, AttendanceStatus.PRESENT);
        when(attendanceRecordRepository.save(any(AttendanceRecord.class))).thenReturn(newRecord);

        AttendanceRecordResponse response = attendanceService.saveOrUpdateRecord(1L, testDate, AttendanceStatus.PRESENT);

        assertNotNull(response);
        assertEquals(1L, response.getStudentId());
        assertEquals(AttendanceStatus.PRESENT, response.getStatus());
        verify(attendanceRecordRepository, times(1)).save(any(AttendanceRecord.class));
    }

    @Test
    @DisplayName("Should update existing record when one already exists for the student and date (no duplicate)")
    void shouldUpdateExistingRecordInsteadOfDuplicate() {
        AttendanceRecord existingRecord = new AttendanceRecord(201L, student1, testDate, AttendanceStatus.PRESENT);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student1));
        when(attendanceRecordRepository.findByStudentIdAndAttendanceDate(1L, testDate)).thenReturn(Optional.of(existingRecord));
        when(attendanceRecordRepository.save(existingRecord)).thenReturn(existingRecord);

        // Update to ABSENT
        AttendanceRecordResponse response = attendanceService.saveOrUpdateRecord(1L, testDate, AttendanceStatus.ABSENT);

        assertNotNull(response);
        assertEquals(AttendanceStatus.ABSENT, existingRecord.getStatus());
        verify(attendanceRecordRepository, times(1)).save(existingRecord);
    }

    @Test
    @DisplayName("Should batch save and return updated overview")
    void shouldSaveBatchAttendance() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student1));
        when(studentRepository.findById(2L)).thenReturn(Optional.of(student2));
        when(attendanceRecordRepository.findByStudentIdAndAttendanceDate(1L, testDate)).thenReturn(Optional.empty());
        when(attendanceRecordRepository.findByStudentIdAndAttendanceDate(2L, testDate)).thenReturn(Optional.empty());

        AttendanceRecord rec1 = new AttendanceRecord(101L, student1, testDate, AttendanceStatus.PRESENT);
        AttendanceRecord rec2 = new AttendanceRecord(102L, student2, testDate, AttendanceStatus.ABSENT);

        when(attendanceRecordRepository.save(any(AttendanceRecord.class))).thenReturn(rec1, rec2);

        when(studentRepository.findAllByOrderByRollNumberAsc()).thenReturn(List.of(student1, student2));
        when(attendanceRecordRepository.findByAttendanceDate(testDate)).thenReturn(List.of(rec1, rec2));

        AttendanceBatchRequest batchRequest = new AttendanceBatchRequest(
                testDate,
                List.of(
                        new AttendanceStatusRequest(1L, AttendanceStatus.PRESENT),
                        new AttendanceStatusRequest(2L, AttendanceStatus.ABSENT)
                )
        );

        DailyAttendanceOverviewResponse response = attendanceService.saveOrUpdateBatch(batchRequest);

        assertNotNull(response);
        assertEquals(2, response.getTotalStudents());
        assertEquals(1, response.getPresentCount());
        assertEquals(1, response.getAbsentCount());
        assertEquals(0, response.getUnmarkedCount());
    }
}
