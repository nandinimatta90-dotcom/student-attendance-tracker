import React from 'react';
import { Users, CheckCircle, XCircle, Clock, Percent } from 'lucide-react';

export default function StatsOverview({
  totalStudents = 0,
  presentCount = 0,
  absentCount = 0,
  unmarkedCount = 0,
}) {
  const markedTotal = presentCount + absentCount;
  const attendancePercentage = markedTotal > 0
    ? ((presentCount / markedTotal) * 100).toFixed(1)
    : '0.0';

  return (
    <div className="stats-grid">
      {/* Total Students */}
      <div className="stat-card">
        <div className="stat-header">
          <span className="stat-label">Total Students</span>
          <div className="stat-icon" style={{ backgroundColor: '#eff6ff', color: '#2563eb' }}>
            <Users size={18} />
          </div>
        </div>
        <div className="stat-value">{totalStudents}</div>
        <div className="stat-footer">Enrolled in class</div>
      </div>

      {/* Present Count */}
      <div className="stat-card">
        <div className="stat-header">
          <span className="stat-label">Present</span>
          <div className="stat-icon" style={{ backgroundColor: '#ecfdf5', color: '#059669' }}>
            <CheckCircle size={18} />
          </div>
        </div>
        <div className="stat-value" style={{ color: '#059669' }}>{presentCount}</div>
        <div className="stat-footer">Attended today</div>
      </div>

      {/* Absent Count */}
      <div className="stat-card">
        <div className="stat-header">
          <span className="stat-label">Absent</span>
          <div className="stat-icon" style={{ backgroundColor: '#fef2f2', color: '#dc2626' }}>
            <XCircle size={18} />
          </div>
        </div>
        <div className="stat-value" style={{ color: '#dc2626' }}>{absentCount}</div>
        <div className="stat-footer">Recorded absent</div>
      </div>

      {/* Unmarked Count */}
      <div className="stat-card">
        <div className="stat-header">
          <span className="stat-label">Unmarked</span>
          <div className="stat-icon" style={{ backgroundColor: '#f1f5f9', color: '#64748b' }}>
            <Clock size={18} />
          </div>
        </div>
        <div className="stat-value" style={{ color: '#64748b' }}>{unmarkedCount}</div>
        <div className="stat-footer">Pending mark (not absent)</div>
      </div>

      {/* Attendance Rate */}
      <div className="stat-card">
        <div className="stat-header">
          <span className="stat-label">Attendance Rate</span>
          <div className="stat-icon" style={{ backgroundColor: '#fdf4ff', color: '#a855f7' }}>
            <Percent size={18} />
          </div>
        </div>
        <div className="stat-value" style={{ color: '#7e22ce' }}>{attendancePercentage}%</div>
        <div className="stat-footer">
          {markedTotal} of {totalStudents} marked
        </div>
      </div>
    </div>
  );
}
