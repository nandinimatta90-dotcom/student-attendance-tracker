import React, { useState, useEffect, useCallback, useMemo } from 'react';
import Header from './components/Header';
import StatsOverview from './components/StatsOverview';
import DateSelector from './components/DateSelector';
import AttendanceTable from './components/AttendanceTable';
import AddStudentModal from './components/AddStudentModal';
import StudentDirectoryModal from './components/StudentDirectoryModal';
import AlertBanner from './components/AlertBanner';
import {
  getDailyAttendanceOverview,
  saveBatchAttendance,
  addStudent,
} from './services/api';

function getTodayString() {
  const d = new Date();
  const yyyy = d.getFullYear();
  const mm = String(d.getMonth() + 1).padStart(2, '0');
  const dd = String(d.getDate()).padStart(2, '0');
  return `${yyyy}-${mm}-${dd}`;
}

export default function App() {
  const [selectedDate, setSelectedDate] = useState(getTodayString());
  const [students, setStudents] = useState([]);
  const [currentMarks, setCurrentMarks] = useState({});
  const [initialMarks, setInitialMarks] = useState({});

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [alert, setAlert] = useState(null);

  const [isAddStudentOpen, setIsAddStudentOpen] = useState(false);
  const [isDirectoryOpen, setIsDirectoryOpen] = useState(false);

  // Load attendance overview when selectedDate changes
  const loadDailyData = useCallback(async (date) => {
    setLoading(true);
    setAlert(null);
    try {
      const data = await getDailyAttendanceOverview(date);
      setStudents(data.students || []);

      const markMap = {};
      (data.students || []).forEach((item) => {
        markMap[item.studentId] = item.status || null;
      });

      setCurrentMarks(markMap);
      setInitialMarks({ ...markMap });
    } catch (err) {
      console.error('Error loading daily attendance:', err);
      setAlert({
        type: 'error',
        message: err.message || 'Unable to connect to the backend server. Please verify MySQL and Spring Boot are running.',
      });
      setStudents([]);
      setCurrentMarks({});
      setInitialMarks({});
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadDailyData(selectedDate);
  }, [selectedDate, loadDailyData]);

  // Check if there are unsaved marks
  const hasChanges = useMemo(() => {
    return Object.keys(currentMarks).some(
      (id) => (currentMarks[id] || null) !== (initialMarks[id] || null)
    );
  }, [currentMarks, initialMarks]);

  // Prompt if navigating away with unsaved changes
  const handleDateChange = (newDate) => {
    if (newDate === selectedDate) return;

    if (hasChanges) {
      const confirmed = window.confirm(
        'You have unsaved attendance changes for this date. Discard changes and switch date?'
      );
      if (!confirmed) {
        return;
      }
    }
    setSelectedDate(newDate);
  };

  // Mark single student
  const handleMarkChange = (studentId, status) => {
    setCurrentMarks((prev) => ({
      ...prev,
      [studentId]: status,
    }));
  };

  // Mark all students
  const handleMarkAll = (status) => {
    const updated = {};
    students.forEach((s) => {
      updated[s.studentId] = status;
    });
    setCurrentMarks(updated);
  };

  // Clear all marks
  const handleClearAll = () => {
    const updated = {};
    students.forEach((s) => {
      updated[s.studentId] = null;
    });
    setCurrentMarks(updated);
  };

  // Reset to last saved state
  const handleReset = () => {
    setCurrentMarks({ ...initialMarks });
    setAlert({
      type: 'warning',
      message: 'Reverted all unsaved changes to the last saved state.',
    });
  };

  // Save attendance to backend
  const handleSave = async () => {
    setSaving(true);
    setAlert(null);

    try {
      const recordsToSave = students.map((s) => ({
        studentId: s.studentId,
        status: currentMarks[s.studentId] || null,
      }));

      const updatedOverview = await saveBatchAttendance(selectedDate, recordsToSave);

      // Update state with confirmed saved records
      setStudents(updatedOverview.students || []);

      const newMarkMap = {};
      (updatedOverview.students || []).forEach((item) => {
        newMarkMap[item.studentId] = item.status || null;
      });

      setCurrentMarks(newMarkMap);
      setInitialMarks({ ...newMarkMap });

      setAlert({
        type: 'success',
        message: `Attendance for ${selectedDate} saved successfully! (Present: ${updatedOverview.presentCount}, Absent: ${updatedOverview.absentCount}, Unmarked: ${updatedOverview.unmarkedCount})`,
      });
    } catch (err) {
      console.error('Error saving attendance:', err);
      setAlert({
        type: 'error',
        message: err.message || 'Failed to save attendance. Please check your network and backend logs.',
      });
    } finally {
      setSaving(false);
    }
  };

  // Add new student handler
  const handleAddStudent = async ({ name, rollNumber }) => {
    const newStudent = await addStudent({ name, rollNumber });
    setAlert({
      type: 'success',
      message: `Student ${newStudent.name} (${newStudent.rollNumber}) added successfully!`,
    });
    // Refresh the attendance sheet
    await loadDailyData(selectedDate);
  };

  // Calculate live statistics based on current (staged) markings
  const liveStats = useMemo(() => {
    let present = 0;
    let absent = 0;
    let unmarked = 0;

    students.forEach((s) => {
      const status = currentMarks[s.studentId];
      if (status === 'PRESENT') {
        present++;
      } else if (status === 'ABSENT') {
        absent++;
      } else {
        // Requirement: Do not count unmarked students as absent!
        unmarked++;
      }
    });

    return {
      total: students.length,
      present,
      absent,
      unmarked,
    };
  }, [students, currentMarks]);

  return (
    <div className="app-container">
      {/* Top Header */}
      <Header
        onOpenAddStudent={() => setIsAddStudentOpen(true)}
        onOpenDirectory={() => setIsDirectoryOpen(true)}
        totalStudents={students.length}
      />

      {/* Global Alert Notification */}
      {alert && (
        <AlertBanner
          type={alert.type}
          message={alert.message}
          onClose={() => setAlert(null)}
        />
      )}

      {/* Date Selector and Navigation Bar */}
      <DateSelector
        selectedDate={selectedDate}
        onDateChange={handleDateChange}
        hasPendingChanges={hasChanges}
      />

      {/* Real-time Summary Cards */}
      <StatsOverview
        totalStudents={liveStats.total}
        presentCount={liveStats.present}
        absentCount={liveStats.absent}
        unmarkedCount={liveStats.unmarked}
      />

      {/* Attendance Sheet Table */}
      <AttendanceTable
        students={students}
        currentMarks={currentMarks}
        initialMarks={initialMarks}
        onMarkChange={handleMarkChange}
        onMarkAll={handleMarkAll}
        onClearAll={handleClearAll}
        onSave={handleSave}
        onReset={handleReset}
        isSaving={saving}
        hasChanges={hasChanges}
        loading={loading}
        onOpenAddStudent={() => setIsAddStudentOpen(true)}
        selectedDate={selectedDate}
      />

      {/* Add Student Modal */}
      <AddStudentModal
        isOpen={isAddStudentOpen}
        onClose={() => setIsAddStudentOpen(false)}
        onStudentAdded={handleAddStudent}
      />

      {/* Enrolled Students Directory Modal */}
      <StudentDirectoryModal
        isOpen={isDirectoryOpen}
        onClose={() => setIsDirectoryOpen(false)}
        onStudentsUpdated={() => loadDailyData(selectedDate)}
        onOpenAddStudent={() => setIsAddStudentOpen(true)}
      />
    </div>
  );
}
