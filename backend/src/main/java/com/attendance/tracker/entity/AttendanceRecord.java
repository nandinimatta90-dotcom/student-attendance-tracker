package com.attendance.tracker.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing an attendance record for a student on a specific date.
 * Enforces unique constraint per student per date.
 */
@Entity
@Table(
    name = "attendance_records",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_student_attendance_date",
            columnNames = {"student_id", "attendance_date"}
        )
    }
)
public class AttendanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(
        name = "student_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_attendance_student")
    )
    private Student student;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AttendanceStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public AttendanceRecord() {
    }

    public AttendanceRecord(Student student, LocalDate attendanceDate, AttendanceStatus status) {
        this.student = student;
        this.attendanceDate = attendanceDate;
        this.status = status;
    }

    public AttendanceRecord(Long id, Student student, LocalDate attendanceDate, AttendanceStatus status) {
        this.id = id;
        this.student = student;
        this.attendanceDate = attendanceDate;
        this.status = status;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (this.createdAt == null) {
            this.createdAt = now;
        }
        if (this.updatedAt == null) {
            this.updatedAt = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttendanceRecord that = (AttendanceRecord) o;
        return Objects.equals(id, that.id) ||
                (Objects.equals(student, that.student) && Objects.equals(attendanceDate, that.attendanceDate));
    }

    @Override
    public int hashCode() {
        return Objects.hash(student, attendanceDate);
    }

    @Override
    public String toString() {
        return "AttendanceRecord{" +
                "id=" + id +
                ", student=" + (student != null ? student.getRollNumber() : "null") +
                ", attendanceDate=" + attendanceDate +
                ", status=" + status +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
