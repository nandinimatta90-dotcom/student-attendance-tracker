/**
 * API service for communicating with the Spring Boot backend.
 */

const BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api';

/**
 * Helper to handle fetch responses and parse error messages from Spring Boot ErrorResponse
 */
async function handleResponse(response) {
  if (!response.ok) {
    let errorMessage = `HTTP error ${response.status}: ${response.statusText}`;
    try {
      const errorData = await response.json();
      if (errorData.message) {
        errorMessage = errorData.message;
      }
      if (errorData.validationErrors) {
        const details = Object.entries(errorData.validationErrors)
          .map(([k, v]) => `${k}: ${v}`)
          .join(', ');
        errorMessage += ` (${details})`;
      }
    } catch {
      // Fallback if not JSON
    }
    const error = new Error(errorMessage);
    error.status = response.status;
    throw error;
  }

  // Handle 204 No Content
  if (response.status === 204) {
    return null;
  }

  return response.json();
}

/**
 * Fetch daily attendance overview for a specific date (YYYY-MM-DD).
 */
export async function getDailyAttendanceOverview(date) {
  const response = await fetch(`${BASE_URL}/attendance/overview?date=${encodeURIComponent(date)}`, {
    headers: {
      'Accept': 'application/json',
    },
  });
  return handleResponse(response);
}

/**
 * Batch save attendance for a specific date.
 * records: Array of { studentId: number, status: 'PRESENT' | 'ABSENT' | null }
 */
export async function saveBatchAttendance(date, records) {
  const response = await fetch(`${BASE_URL}/attendance/batch`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    },
    body: JSON.stringify({
      date,
      records,
    }),
  });
  return handleResponse(response);
}

/**
 * Fetch all registered students.
 */
export async function getStudents() {
  const response = await fetch(`${BASE_URL}/students`, {
    headers: {
      'Accept': 'application/json',
    },
  });
  return handleResponse(response);
}

/**
 * Add a new student.
 * data: { name: string, rollNumber: string }
 */
export async function addStudent(data) {
  const response = await fetch(`${BASE_URL}/students`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    },
    body: JSON.stringify(data),
  });
  return handleResponse(response);
}

/**
 * Delete a student and their attendance records.
 */
export async function deleteStudent(id) {
  const response = await fetch(`${BASE_URL}/students/${id}`, {
    method: 'DELETE',
    headers: {
      'Accept': 'application/json',
    },
  });
  return handleResponse(response);
}
