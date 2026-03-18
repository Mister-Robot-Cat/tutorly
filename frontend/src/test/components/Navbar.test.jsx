import { render, screen, fireEvent } from '@testing-library/react'
import { BrowserRouter } from 'react-router-dom'
import { AuthProvider } from '../context/AuthContext'
import Navbar from '../components/Navbar'

// Mock the API service
vi.mock('../services/api', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn()
  }
}))

const renderWithProviders = (component) => {
  return render(
    <BrowserRouter>
      <AuthProvider>
        {component}
      </AuthProvider>
    </BrowserRouter>
  )
}

describe('Navbar Component', () => {
  beforeEach(() => {
    // Clear localStorage before each test
    localStorage.clear()
  })

  test('renders login and register links when user is not authenticated', () => {
    renderWithProviders(<Navbar />)
    
    expect(screen.getByText('Login')).toBeInTheDocument()
    expect(screen.getByText('Register')).toBeInTheDocument()
    expect(screen.queryByText('Dashboard')).not.toBeInTheDocument()
    expect(screen.queryByText('Logout')).not.toBeInTheDocument()
  })

  test('renders dashboard and logout links when user is authenticated', () => {
    // Mock authenticated user
    localStorage.setItem('token', 'mock-token')
    localStorage.setItem('user', JSON.stringify({
      id: 1,
      email: 'test@example.com',
      role: 'STUDENT'
    }))

    renderWithProviders(<Navbar />)
    
    expect(screen.getByText('Dashboard')).toBeInTheDocument()
    expect(screen.getByText('Logout')).toBeInTheDocument()
    expect(screen.queryByText('Login')).not.toBeInTheDocument()
    expect(screen.queryByText('Register')).not.toBeInTheDocument()
  })

  test('shows admin link for admin users', () => {
    // Mock admin user
    localStorage.setItem('token', 'mock-token')
    localStorage.setItem('user', JSON.stringify({
      id: 1,
      email: 'admin@example.com',
      role: 'ADMIN'
    }))

    renderWithProviders(<Navbar />)
    
    expect(screen.getByText('Dashboard')).toBeInTheDocument()
    expect(screen.getByText('Admin')).toBeInTheDocument()
    expect(screen.getByText('Logout')).toBeInTheDocument()
  })

  test('logout functionality works correctly', () => {
    // Mock authenticated user
    localStorage.setItem('token', 'mock-token')
    localStorage.setItem('user', JSON.stringify({
      id: 1,
      email: 'test@example.com',
      role: 'STUDENT'
    }))

    renderWithProviders(<Navbar />)
    
    const logoutButton = screen.getByText('Logout')
    fireEvent.click(logoutButton)
    
    // Check that localStorage is cleared
    expect(localStorage.getItem('token')).toBeNull()
    expect(localStorage.getItem('user')).toBeNull()
  })

  test('mobile menu toggle works', () => {
    renderWithProviders(<Navbar />)
    
    // Find mobile menu button (hamburger icon)
    const menuButton = screen.getByRole('button', { name: /menu/i })
    
    // Initially mobile menu should be hidden
    expect(screen.queryByText('Login')).not.toBeVisible()
    
    // Click menu button to open
    fireEvent.click(menuButton)
    
    // Now menu items should be visible
    expect(screen.getByText('Login')).toBeVisible()
    expect(screen.getByText('Register')).toBeVisible()
  })

  test('brand link navigates to home', () => {
    renderWithProviders(<Navbar />)
    
    const brandLink = screen.getByText('Tutorly')
    expect(brandLink.closest('a')).toHaveAttribute('href', '/')
  })

  test('tutor link navigates to tutors page', () => {
    renderWithProviders(<Navbar />)
    
    const tutorsLink = screen.getByText('Find Tutors')
    expect(tutorsLink.closest('a')).toHaveAttribute('href', '/tutors')
  })
})
