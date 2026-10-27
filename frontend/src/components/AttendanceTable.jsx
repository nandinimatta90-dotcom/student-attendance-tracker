import React, { useState } from 'react';
import {
  Check,
  X,
  RotateCcw,
  Search,
  Save,
  CheckCircle2,
  XCircle,
  Clock,
  UserPlus,
  Loader2,
  AlertCircle
} from 'lucide-react';

export default function AttendanceTable({
  students = [],
  currentMarks = {},
  initialMarks = {},
  onMarkChange,
  onMarkAll,
  onClearAll,
  onSave,
  onReset,
  isSaving,
  hasChanges,
  loading,
  onOpenAddStudent,
  selectedDate,
}) {
  const [search, setSearch] = useState('');

  // Filter students by search query
  const filteredStudents = students.filter((s) => {
    const q = search.toLowerCase();
    return s.name.toLowerCase().includes(q) || s.rollNumber.toLowerCase().includes(q);
  });

  if (loading) {
    return (
      <div className="main-card">
        <div className="loading-container">
          <Loader2 size={32} className="spinner" color="#2563eb" />
          <p style={{ fontWeight: 600, color: 'var(--color-gray-700)' }}>
            Loading attendance records for {selectedDate}...
          </p>
        </div>
      </div>
    );
  }

  if (students.length === 0) {
    return (
      <div className="main-card">
        <div className="empty-state">
          <div className="empty-icon-box">
            <UserPlus size={32} />
          </div>
          <h3 className="empty-title">No Students Registered Yet</h3>
          <p className="empty-description">
            Get started by adding your students with their name and roll number. Once added, you can track their daily attendance here.
          </p>
          <button className="btn btn-primary" onClick={onOpenAddStudent}>
            <UserPlus size={16} />
            <span>Add First Student</span>
          </button>
        </div>
      </div>
    );
  }

  // Count modified entries
  const modifiedCount = students.filter(
    (s) => (currentMarks[s.studentId] || null) !== (initialMarks[s.studentId] || null)
  ).length;

  return (
    <div className="main-card">
      {/* Table Toolbar */}
      <div className="card-toolbar">
        <div className="search-box">
          <Search size={16} color="#64748b" />
          <input
            type="text"
            placeholder="Search student or roll no..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>

        <div className="batch-actions-wrapper">
          <span style={{ fontSize: '0.8125rem', fontWeight: 600, color: 'var(--color-gray-500)', marginRight: '4px' }}>
            Batch:
          </span>
          <button
            className="btn btn-sm btn-secondary"
            onClick={() => onMarkAll('PRESENT')}
            title="Mark all displayed students present"
          >
            <Check size={14} color="#059669" />
            <span>All Present</span>
          </button>
          <button
            className="btn btn-sm btn-secondary"
            onClick={() => onMarkAll('ABSENT')}
            title="Mark all displayed students absent"
          >
            <X size={14} color="#dc2626" />
            <span>All Absent</span>
          </button>
          <button
            className="btn btn-sm btn-secondary"
            onClick={onClearAll}
            title="Clear markings for all students"
          >
            <RotateCcw size={14} />
            <span>Clear All</span>
          </button>
        </div>
      </div>

      {/* Table Content */}
      <div className="table-responsive">
        <table className="attendance-table">
          <thead>
            <tr>
              <th style={{ width: '130px' }}>Roll No</th>
              <th>Student Name</th>
              <th style={{ width: '160px' }}>Status</th>
              <th style={{ width: '220px', textAlign: 'center' }}>Mark Attendance</th>
            </tr>
          </thead>
          <tbody>
            {filteredStudents.length === 0 ? (
              <tr>
                <td colSpan={4} style={{ textAlign: 'center', padding: '36px', color: '#64748b' }}>
                  No students found matching "{search}".
                </td>
              </tr>
            ) : (
              filteredStudents.map((student) => {
                const mark = currentMarks[student.studentId] || null;
                const isPresent = mark === 'PRESENT';
                const isAbsent = mark === 'ABSENT';
                const isUnmarked = !mark;
                const isRowModified = (mark || null) !== (initialMarks[student.studentId] || null);

                return (
                  <tr key={student.studentId} className={isRowModified ? 'row-unsaved' : ''}>
                    <td>
                      <span className="roll-badge">{student.rollNumber}</span>
                    </td>
                    <td>
                      <div className="student-name">{student.name}</div>
                      {isRowModified && (
                        <div style={{ fontSize: '0.6875rem', color: '#d97706', fontWeight: 600 }}>
                          • Pending save
                        </div>
                      )}
                    </td>
                    <td>
                      {isPresent && (
                        <span className="status-pill status-present">
                          <CheckCircle2 size={13} />
                          <span>Present</span>
                        </span>
                      )}
                      {isAbsent && (
                        <span className="status-pill status-absent">
                          <XCircle size={13} />
                          <span>Absent</span>
                        </span>
                      )}
                      {isUnmarked && (
                        <span className="status-pill status-unmarked">
                          <Clock size={13} />
                          <span>Unmarked</span>
                        </span>
                      )}
                    </td>
                    <td style={{ textAlign: 'center' }}>
                      <div className="status-toggle-group">
                        <button
                          type="button"
                          className={`toggle-btn toggle-btn-present ${isPresent ? 'active' : ''}`}
                          onClick={() => onMarkChange(student.studentId, isPresent ? null : 'PRESENT')}
                          title="Mark Present"
                        >
                          <Check size={14} />
                          <span>P</span>
                        </button>
                        <button
                          type="button"
                          className={`toggle-btn toggle-btn-absent ${isAbsent ? 'active' : ''}`}
                          onClick={() => onMarkChange(student.studentId, isAbsent ? null : 'ABSENT')}
                          title="Mark Absent"
                        >
                          <X size={14} />
                          <span>A</span>
                        </button>
                        <button
                          type="button"
                          className={`toggle-btn toggle-btn-clear ${isUnmarked ? 'active' : ''}`}
                          onClick={() => onMarkChange(student.studentId, null)}
                          title="Clear / Unmark"
                        >
                          <span>Clear</span>
                        </button>
                      </div>
                    </td>
                  </tr>
                );
              })
            )}
          </tbody>
        </table>
      </div>

      {/* Save Bar Footer */}
      <div className="save-bar-footer">
        <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
          <span className="table-stats-pill">
            Showing {filteredStudents.length} of {students.length} students
          </span>
          {modifiedCount > 0 && (
            <div className="pending-notice">
              <AlertCircle size={16} />
              <span>{modifiedCount} unsaved change{modifiedCount > 1 ? 's' : ''}</span>
            </div>
          )}
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
          {modifiedCount > 0 && (
            <button
              className="btn btn-secondary"
              onClick={onReset}
              disabled={isSaving}
            >
              Reset
            </button>
          )}

          <button
            className="btn btn-primary"
            onClick={onSave}
            disabled={isSaving || (!hasChanges && modifiedCount === 0)}
          >
            {isSaving ? (
              <>
                <Loader2 size={16} className="spinner" />
                <span>Saving to Database...</span>
              </>
            ) : (
              <>
                <Save size={16} />
                <span>Save Attendance</span>
              </>
            )}
          </button>
        </div>
      </div>
    </div>
  );
}
