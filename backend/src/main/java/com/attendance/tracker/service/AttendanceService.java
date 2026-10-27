package com.attendance.tracker.service;

import com.attendance.tracker.dto.*;
import com.attendance.tracker.entity.AttendanceRecord;
import com.attendance.tracker.entity.AttendanceStatus;
import com.attendance.tracker.entity.Student;
import com.attendance.tracker.exception.ResourceNotFoundException;
import com.attendance.tracker.repository.AttendanceRecordRepository;
import com.attendance.tracker.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class AttendanceService {

    private static final Logger log = LoggerFactory.getLogger(AttendanceService.class);

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final StudentRepository studentRepository;

    public AttendanceService(AttendanceRecordRepository attendanceRecordRepository, StudentRepository studentRepository) {
        this.attendanceRecordRepository = attendanceRecordRepository;
        this.studentRepository = studentRepository;
    }

    /**
     * Retrieves the daily attendance overview for a specific date.
     * Includes all registered students, their attendance status (if marked),
     * and accurate summary counts. Unmarked students are not counted as absent.
     */
    @Transactional(readOnly = true)
    public DailyAttendanceOverviewResponse getDailyOverview(LocalDate date) {
        List<Student> allStudents = studentRepository.findAllByOrderByRollNumberAsc();
        List<AttendanceRecord> records = attendanceRecordRepository.findByAttendanceDate(date);

        Map<Long, AttendanceRecord> recordMap = records.stream()
                .collect(Collectors.toMap(r -> r.getStudent().getId(), Function.identity(), (a, b) -> b));

        int presentCount = 0;
        int absentCount = 0;
        List<StudentAttendanceItemDto> studentItems = new ArrayList<>();

        for (Student student : allStudents) {
            AttendanceRecord record = recordMap.get(student.getId());
            AttendanceStatus status = (record != null) ? record.getStatus() : null;
            Long recordId = (record != null) ? record.getId() : null;
            var updatedAt = (record != null) ? record.getUpdatedAt() : null;

            if (status == AttendanceStatus.PRESENT) {
                presentCount++;
            } else if (status == AttendanceStatus.ABSENT) {
                absentCount++;
            }

            studentItems.add(new StudentAttendanceItemDto(
                    student.getId(),
                    student.getName(),
                    student.getRollNumber(),
                    recordId,
                    status,
                    updatedAt
            ));
        }

        int totalStudents = allStudents.size();
        int unmarkedCount = totalStudents - (presentCount + absentCount);
        int markedCount = presentCount + absentCount;
        double rate = (markedCount > 0) ? Math.round(((double) presentCount / markedCount) * 1000.0) / 10.0 : 0.0;

        return new DailyAttendanceOverviewResponse(
                date,
                totalStudents,
                presentCount,
                absentCount,
                unmarkedCount,
                rate,
                studentItems
        );
    }

    /**
     * Saves or updates a single attendance record for a student on a specific date.
     * Enforces the constraint that a student has at most one record per date.
     */
    public AttendanceRecordResponse saveOrUpdateRecord(Long studentId, LocalDate date, AttendanceStatus status) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));

        Optional<AttendanceRecord> existing = attendanceRecordRepository.findByStudentIdAndAttendanceDate(studentId, date);

        if (status == null) {
            // If status is null, delete existing record if present (unmarking)
            existing.ifPresent(attendanceRecordRepository::delete);
            log.info("Cleared attendance for studentId={} on date={}", studentId, date);
            return null;
        }

        AttendanceRecord record;
        if (existing.isPresent()) {
            record = existing.get();
            record.setStatus(status);
            log.info("Updated existing attendance record id={} for studentId={} to {}", record.getId(), studentId, status);
        } else {
            record = new AttendanceRecord(student, date, status);
            log.info("Created new attendance record for studentId={} on {} as {}", studentId, date, status);
        }

        AttendanceRecord saved = attendanceRecordRepository.save(record);
        return AttendanceRecordResponse.fromEntity(saved);
    }

    /**
     * Saves or updates attendance in batch for multiple students on a specific date.
     * Saving again for the same date updates existing records.
     */
    public DailyAttendanceOverviewResponse saveOrUpdateBatch(AttendanceBatchRequest request) {
        LocalDate date = request.getDate();
        log.info("Processing batch attendance submission for date={} with {} student entries", date, request.getRecords().size());

        for (AttendanceStatusRequest entry : request.getRecords()) {
            if (entry.getStudentId() == null) {
                continue;
            }
            saveOrUpdateRecord(entry.getStudentId(), date, entry.getStatus());
        }

        // Return updated daily overview
        return getDailyOverview(date);
    }

    /**
     * Returns all saved attendance records for a given date.
     */
    @Transactional(readOnly = true)
    public List<AttendanceRecordResponse> getRecordsByDate(LocalDate date) {
        return attendanceRecordRepository.findByAttendanceDateOrderByStudentRollNumberAsc(date)
                .stream()
                .map(AttendanceRecordResponse::fromEntity)
                .toList();
    }
}
