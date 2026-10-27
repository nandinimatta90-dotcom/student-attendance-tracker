import React from 'react';
import { CheckCircle2, AlertCircle, AlertTriangle, X } from 'lucide-react';

export default function AlertBanner({ type = 'info', message, onClose }) {
  if (!message) return null;

  const isSuccess = type === 'success';
  const isError = type === 'error';
  const isWarning = type === 'warning';

  return (
    <div className="alert-container">
      <div className={`alert ${isSuccess ? 'alert-success' : isError ? 'alert-error' : 'alert-warning'}`}>
        {isSuccess && <CheckCircle2 size={18} className="shrink-0" />}
        {isError && <AlertCircle size={18} className="shrink-0" />}
        {isWarning && <AlertTriangle size={18} className="shrink-0" />}

        <div style={{ flex: 1 }}>{message}</div>

        {onClose && (
          <button className="alert-close" onClick={onClose} aria-label="Close alert">
            <X size={16} />
          </button>
        )}
      </div>
    </div>
  );
}
