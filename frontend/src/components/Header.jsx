import React from 'react';
import { UserCheck, UserPlus, Users } from 'lucide-react';

export default function Header({ onOpenAddStudent, onOpenDirectory, totalStudents = 0 }) {
  return (
    <header className="header-wrapper">
      <div className="brand-section">
        <div className="brand-icon-box">
          <UserCheck size={28} />
        </div>
        <div>
          <h1 className="brand-title">
            Student Attendance Tracker
          </h1>
          <p className="brand-subtitle">
            Trainer Daily Attendance Portal & Reporting
          </p>
        </div>
      </div>

      <div className="header-actions">
        <button
          className="btn btn-secondary"
          onClick={onOpenDirectory}
          title="View and manage enrolled students"
        >
          <Users size={16} />
          <span>Students ({totalStudents})</span>
        </button>

        <button
          className="btn btn-primary"
          onClick={onOpenAddStudent}
          title="Add a new student"
        >
          <UserPlus size={16} />
          <span>Add Student</span>
        </button>
      </div>
    </header>
  );
}
