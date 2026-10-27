import React from 'react';
import { Calendar, ChevronLeft, ChevronRight } from 'lucide-react';

export default function DateSelector({
  selectedDate,
  onDateChange,
  hasPendingChanges,
}) {
  const getTodayString = () => {
    const today = new Date();
    return today.toISOString().split('T')[0];
  };

  const getYesterdayString = () => {
    const d = new Date();
    d.setDate(d.getDate() - 1);
    return d.toISOString().split('T')[0];
  };

  const shiftDate = (days) => {
    const parts = selectedDate.split('-');
    const d = new Date(Number(parts[0]), Number(parts[1]) - 1, Number(parts[2]));
    d.setDate(d.getDate() + days);
    const yyyy = d.getFullYear();
    const mm = String(d.getMonth() + 1).padStart(2, '0');
    const dd = String(d.getDate()).padStart(2, '0');
    onDateChange(`${yyyy}-${mm}-${dd}`);
  };

  const formattedDate = (() => {
    try {
      const parts = selectedDate.split('-');
      const d = new Date(Number(parts[0]), Number(parts[1]) - 1, Number(parts[2]));
      return d.toLocaleDateString(undefined, {
        weekday: 'short',
        year: 'numeric',
        month: 'short',
        day: 'numeric',
      });
    } catch {
      return selectedDate;
    }
  })();

  const isToday = selectedDate === getTodayString();
  const isYesterday = selectedDate === getYesterdayString();

  return (
    <div className="controls-bar">
      <div className="date-controls">
        <div className="date-input-wrapper">
          <Calendar size={18} color="#2563eb" />
          <input
            type="date"
            value={selectedDate}
            onChange={(e) => {
              if (e.target.value) {
                onDateChange(e.target.value);
              }
            }}
            aria-label="Attendance Date"
          />
        </div>

        <button
          className="btn btn-secondary btn-icon-only"
          onClick={() => shiftDate(-1)}
          title="Previous day"
          aria-label="Previous day"
        >
          <ChevronLeft size={16} />
        </button>

        <button
          className="btn btn-secondary btn-icon-only"
          onClick={() => shiftDate(1)}
          title="Next day"
          aria-label="Next day"
        >
          <ChevronRight size={16} />
        </button>

        <button
          className={`btn btn-sm ${isToday ? 'btn-primary' : 'btn-secondary'}`}
          onClick={() => onDateChange(getTodayString())}
        >
          Today
        </button>

        <button
          className={`btn btn-sm ${isYesterday ? 'btn-primary' : 'btn-secondary'}`}
          onClick={() => onDateChange(getYesterdayString())}
        >
          Yesterday
        </button>
      </div>

      <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
        <div style={{ fontWeight: 700, color: 'var(--color-gray-800)', fontSize: '0.9375rem' }}>
          {formattedDate}
        </div>
        {hasPendingChanges && (
          <span
            style={{
              fontSize: '0.75rem',
              fontWeight: 700,
              backgroundColor: 'var(--color-warning-light)',
              color: 'var(--color-warning)',
              border: '1px solid #fde68a',
              padding: '3px 8px',
              borderRadius: '9999px',
            }}
          >
            Unsaved Changes
          </span>
        )}
      </div>
    </div>
  );
}
