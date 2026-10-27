package com.attendance.tracker.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.List;

public class DailyAttendanceOverviewResponse {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private int totalStudents;
    private int presentCount;
    private int absentCount;
    private int unmarkedCount;
    private double attendanceRatePercentage;
    private List<StudentAttendanceItemDto> students;

    public DailyAttendanceOverviewResponse() {
    }

    public DailyAttendanceOverviewResponse(LocalDate date, int totalStudents, int presentCount,
                                           int absentCount, int unmarkedCount,
                                           double attendanceRatePercentage,
                                           List<StudentAttendanceItemDto> students) {
        this.date = date;
        this.totalStudents = totalStudents;
        this.presentCount = presentCount;
        this.absentCount = absentCount;
        this.unmarkedCount = unmarkedCount;
        this.attendanceRatePercentage = attendanceRatePercentage;
        this.students = students;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(int totalStudents) {
        this.totalStudents = totalStudents;
    }

    public int getPresentCount() {
        return presentCount;
    }

    public void setPresentCount(int presentCount) {
        this.presentCount = presentCount;
    }

    public int getAbsentCount() {
        return absentCount;
    }

    public void setAbsentCount(int absentCount) {
        this.absentCount = absentCount;
    }

    public int getUnmarkedCount() {
        return unmarkedCount;
    }

    public void setUnmarkedCount(int unmarkedCount) {
        this.unmarkedCount = unmarkedCount;
    }

    public double getAttendanceRatePercentage() {
        return attendanceRatePercentage;
    }

    public void setAttendanceRatePercentage(double attendanceRatePercentage) {
        this.attendanceRatePercentage = attendanceRatePercentage;
    }

    public List<StudentAttendanceItemDto> getStudents() {
        return students;
    }

    public void setStudents(List<StudentAttendanceItemDto> students) {
        this.students = students;
    }
}
