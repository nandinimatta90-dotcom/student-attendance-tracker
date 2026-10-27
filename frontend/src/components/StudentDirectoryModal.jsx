import React, { useState, useEffect } from 'react';
import { X, Users, Search, Trash2, Loader2, UserPlus } from 'lucide-react';
import { getStudents, deleteStudent } from '../services/api';

export default function StudentDirectoryModal({
  isOpen,
  onClose,
  onStudentsUpdated,
  onOpenAddStudent,
}) {
  const [students, setStudents] = useState([]);
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(false);
  const [deletingId, setDeletingId] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (isOpen) {
      loadStudents();
    }
  }, [isOpen]);

  const loadStudents = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await getStudents();
      setStudents(data);
    } catch (err) {
      setError(err.message || 'Failed to load students');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id, rollNumber) => {
    if (!window.confirm(`Are you sure you want to remove student ${rollNumber}? All associated attendance history for this student will also be removed.`)) {
      return;
    }

    setDeletingId(id);
    try {
      await deleteStudent(id);
      setStudents((prev) => prev.filter((s) => s.id !== id));
      if (onStudentsUpdated) {
        onStudentsUpdated();
      }
    } catch (err) {
      setError(err.message || 'Failed to delete student');
    } finally {
      setDeletingId(null);
    }
  };

  if (!isOpen) return null;

  const filtered = students.filter(
    (s) =>
      s.name.toLowerCase().includes(search.toLowerCase()) ||
      s.rollNumber.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="modal-backdrop" onClick={onClose}>
      <div
        className="modal-dialog"
        style={{ maxWidth: '640px' }}
        onClick={(e) => e.stopPropagation()}
      >
        <div className="modal-header">
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <Users size={20} color="#2563eb" />
            <h2 className="modal-title">Enrolled Students Directory</h2>
          </div>
          <button className="alert-close" onClick={onClose} aria-label="Close modal">
            <X size={18} />
          </button>
        </div>

        <div className="modal-body" style={{ maxHeight: '60vh', overflowY: 'auto' }}>
          {error && (
            <div className="alert alert-error" style={{ marginBottom: '16px' }}>
              {error}
            </div>
          )}

          <div
            style={{
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center',
              gap: '12px',
              marginBottom: '16px',
            }}
          >
            <div className="search-box" style={{ width: '100%' }}>
              <Search size={16} color="#64748b" />
              <input
                type="text"
                placeholder="Search by name or roll number..."
                value={search}
                onChange={(e) => setSearch(e.target.value)}
              />
            </div>
            <button
              className="btn btn-primary btn-sm"
              onClick={() => {
                onClose();
                onOpenAddStudent();
              }}
            >
              <UserPlus size={14} />
              <span>New</span>
            </button>
          </div>

          {loading ? (
            <div className="loading-container">
              <Loader2 size={24} className="spinner" />
              <span>Loading students...</span>
            </div>
          ) : filtered.length === 0 ? (
            <div style={{ padding: '32px 16px', textAlign: 'center', color: '#64748b' }}>
              {search ? 'No students match your search.' : 'No students enrolled yet.'}
            </div>
          ) : (
            <div className="table-responsive">
              <table className="attendance-table">
                <thead>
                  <tr>
                    <th>Roll No</th>
                    <th>Student Name</th>
                    <th style={{ textAlign: 'right' }}>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {filtered.map((student) => (
                    <tr key={student.id}>
                      <td style={{ width: '120px' }}>
                        <span className="roll-badge">{student.rollNumber}</span>
                      </td>
                      <td>
                        <span className="student-name">{student.name}</span>
                      </td>
                      <td style={{ textAlign: 'right' }}>
                        <button
                          className="btn btn-outline-danger btn-sm"
                          onClick={() => handleDelete(student.id, student.rollNumber)}
                          disabled={deletingId === student.id}
                          title="Delete student"
                        >
                          {deletingId === student.id ? (
                            <Loader2 size={14} className="spinner" />
                          ) : (
                            <Trash2 size={14} />
                          )}
                          <span>Remove</span>
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>

        <div className="modal-footer">
          <div style={{ marginRight: 'auto', fontSize: '0.8125rem', color: '#64748b' }}>
            Total: {students.length} student{students.length !== 1 ? 's' : ''}
          </div>
          <button className="btn btn-secondary" onClick={onClose}>
            Close
          </button>
        </div>
      </div>
    </div>
  );
}
