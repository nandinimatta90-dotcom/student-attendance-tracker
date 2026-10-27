import React, { useState } from 'react';
import { X, UserPlus, Loader2 } from 'lucide-react';

export default function AddStudentModal({ isOpen, onClose, onStudentAdded }) {
  const [name, setName] = useState('');
  const [rollNumber, setRollNumber] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  if (!isOpen) return null;

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);

    const trimmedName = name.trim();
    const trimmedRoll = rollNumber.trim().toUpperCase();

    if (!trimmedName) {
      setError('Student name is required.');
      return;
    }
    if (!trimmedRoll) {
      setError('Roll number is required.');
      return;
    }

    setLoading(true);
    try {
      await onStudentAdded({ name: trimmedName, rollNumber: trimmedRoll });
      setName('');
      setRollNumber('');
      onClose();
    } catch (err) {
      setError(err.message || 'Failed to add student');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-backdrop" onClick={onClose}>
      <div className="modal-dialog" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <UserPlus size={20} color="#2563eb" />
            <h2 className="modal-title">Add New Student</h2>
          </div>
          <button className="alert-close" onClick={onClose} aria-label="Close modal">
            <X size={18} />
          </button>
        </div>

        <form onSubmit={handleSubmit}>
          <div className="modal-body">
            {error && (
              <div className="alert alert-error" style={{ marginBottom: '16px' }}>
                {error}
              </div>
            )}

            <div className="form-group">
              <label className="form-label" htmlFor="student-roll">
                Roll Number / ID *
              </label>
              <input
                id="student-roll"
                type="text"
                className={`form-input ${error && !rollNumber ? 'input-error' : ''}`}
                placeholder="e.g. CS-101, 2026-A12"
                value={rollNumber}
                onChange={(e) => setRollNumber(e.target.value)}
                autoFocus
                disabled={loading}
              />
              <p className="form-hint">
                Must be unique across all students. Duplicates will be prevented.
              </p>
            </div>

            <div className="form-group">
              <label className="form-label" htmlFor="student-name">
                Full Name *
              </label>
              <input
                id="student-name"
                type="text"
                className={`form-input ${error && !name ? 'input-error' : ''}`}
                placeholder="e.g. Nandini Matta"
                value={name}
                onChange={(e) => setName(e.target.value)}
                disabled={loading}
              />
            </div>
          </div>

          <div className="modal-footer">
            <button
              type="button"
              className="btn btn-secondary"
              onClick={onClose}
              disabled={loading}
            >
              Cancel
            </button>
            <button
              type="submit"
              className="btn btn-primary"
              disabled={loading}
            >
              {loading && <Loader2 size={16} className="spinner" />}
              <span>{loading ? 'Adding...' : 'Add Student'}</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
