import axios from 'axios'

// Use environment variable for API URL, fallback to localhost for development
const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api'

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: false, // Set to true if using cookies
})

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

export const authAPI = {
  register: (data) => api.post('/auth/register', data),
  login: (data) => api.post('/auth/login', data),
  getCurrentUser: () => api.get('/auth/me'),
}

export const tutorAPI = {
  getAll: (params) => api.get('/tutors', { params }),
  getById: (id) => api.get(`/tutors/${id}`),
  updateProfile: (data) => api.put('/tutors/profile', data),
}

export const subjectAPI = {
  getAll: () => api.get('/subjects'),
  create: (data) => api.post('/subjects', data),
  delete: (id) => api.delete(`/subjects/${id}`),
}

export const bookingAPI = {
  create: (data) => api.post('/bookings', data),
  getMyBookings: () => api.get('/bookings/my-bookings'),
  updateStatus: (id, status) => api.patch(`/bookings/${id}/status`, { status }),
  updatePayment: (id, paymentStatus) => api.patch(`/bookings/${id}/payment`, { paymentStatus }),
}

export const reviewAPI = {
  create: (data) => api.post('/reviews', data),
  getTutorReviews: (tutorId) => api.get(`/reviews/tutor/${tutorId}`),
}

export const adminAPI = {
  getUsers: () => api.get('/admin/users'),
  deleteUser: (id) => api.delete(`/admin/users/${id}`),
  getStats: () => api.get('/admin/stats'),
  verifyTutor: (id) => api.patch(`/admin/tutors/${id}/verify`),
  setTutorActive: (id, isActive) => api.patch(`/admin/tutors/${id}/active`, { isActive }),
}

export default api
