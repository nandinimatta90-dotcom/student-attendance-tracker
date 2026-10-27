package com.attendance.tracker.repository;

import com.attendance.tracker.entity.AttendanceRecord;
import com.attendance.tracker.entity.AttendanceStatus;
import com.attendance.tracker.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {

    Optional<AttendanceRecord> findByStudentAndAttendanceDate(Student student, LocalDate attendanceDate);

    Optional<AttendanceRecord> findByStudentIdAndAttendanceDate(Long studentId, LocalDate attendanceDate);

    List<AttendanceRecord> findByAttendanceDate(LocalDate attendanceDate);

    List<AttendanceRecord> findByAttendanceDateOrderByStudentRollNumberAsc(LocalDate attendanceDate);

    long countByAttendanceDateAndStatus(LocalDate attendanceDate, AttendanceStatus status);

    void deleteByStudentId(Long studentId);
}
