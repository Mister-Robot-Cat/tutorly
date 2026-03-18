import { test, expect } from '@playwright/test'

test.describe('Authentication Flow', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/')
  })

  test('user can register successfully', async ({ page }) => {
    // Navigate to register page
    await page.click('text=Register')
    
    // Fill registration form
    await page.fill('input[name="email"]', 'e2e@test.com')
    await page.fill('input[name="password"]', 'password123')
    await page.fill('input[name="firstName"]', 'E2E')
    await page.fill('input[name="lastName"]', 'Test')
    await page.selectOption('select[name="role"]', 'STUDENT')
    
    // Submit form
    await page.click('button[type="submit"]')
    
    // Should redirect to login or dashboard
    await expect(page).toHaveURL(/\/(login|dashboard)/)
    
    // Check for success message or redirect to dashboard
    const successMessage = page.locator('text=Registration successful')
    const dashboardTitle = page.locator('text=Dashboard')
    
    await expect(successMessage.or(dashboardTitle)).toBeVisible()
  })

  test('user can login successfully', async ({ page }) => {
    // First register a user (or assume demo user exists)
    await page.click('text=Login')
    
    // Fill login form
    await page.fill('input[name="email"]', 'student1@example.com')
    await page.fill('input[name="password"]', 'password123')
    
    // Submit form
    await page.click('button[type="submit"]')
    
    // Should redirect to dashboard
    await expect(page).toHaveURL('/dashboard')
    
    // Check for dashboard elements
    await expect(page.locator('text=Dashboard')).toBeVisible()
    await expect(page.locator('text=Welcome')).toBeVisible()
  })

  test('login with invalid credentials shows error', async ({ page }) => {
    await page.click('text=Login')
    
    // Fill with invalid credentials
    await page.fill('input[name="email"]', 'invalid@test.com')
    await page.fill('input[name="password"]', 'wrongpassword')
    
    // Submit form
    await page.click('button[type="submit"]')
    
    // Should show error message
    await expect(page.locator('text=Invalid credentials')).toBeVisible()
    
    // Should stay on login page
    await expect(page).toHaveURL('/login')
  })

  test('registration validation works', async ({ page }) => {
    await page.click('text=Register')
    
    // Try to submit empty form
    await page.click('button[type="submit"]')
    
    // Should show validation errors
    await expect(page.locator('text=Email is required')).toBeVisible()
    await expect(page.locator('text=Password is required')).toBeVisible()
    
    // Test invalid email
    await page.fill('input[name="email"]', 'invalid-email')
    await page.click('button[type="submit"]')
    await expect(page.locator('text=Email should be valid')).toBeVisible()
    
    // Test short password
    await page.fill('input[name="email"]', 'test@example.com')
    await page.fill('input[name="password"]', '123')
    await page.click('button[type="submit"]')
    await expect(page.locator('text=Password must be at least 6 characters')).toBeVisible()
  })

  test('logout functionality works', async ({ page }) => {
    // Login first
    await page.click('text=Login')
    await page.fill('input[name="email"]', 'student1@example.com')
    await page.fill('input[name="password"]', 'password123')
    await page.click('button[type="submit"]')
    
    // Wait for dashboard
    await expect(page).toHaveURL('/dashboard')
    
    // Click logout
    await page.click('text=Logout')
    
    // Should redirect to home and show login/register buttons
    await expect(page).toHaveURL('/')
    await expect(page.locator('text=Login')).toBeVisible()
    await expect(page.locator('text=Register')).toBeVisible()
    await expect(page.locator('text=Dashboard')).not.toBeVisible()
  })

  test('protected routes redirect to login when not authenticated', async ({ page }) => {
    // Try to access dashboard directly
    await page.goto('/dashboard')
    
    // Should redirect to login
    await expect(page).toHaveURL('/login')
  })

  test('admin routes are protected for non-admin users', async ({ page }) => {
    // Login as regular student
    await page.click('text=Login')
    await page.fill('input[name="email"]', 'student1@example.com')
    await page.fill('input[name="password"]', 'password123')
    await page.click('button[type="submit"]')
    
    // Try to access admin panel
    await page.goto('/admin')
    
    // Should show forbidden or redirect
    await expect(page.locator('text=403').or(page.locator('text=Forbidden')).or(page.locator('text=Login'))).toBeVisible()
  })
})

test.describe('Navigation', () => {
  test('navigation links work correctly', async ({ page }) => {
    await page.goto('/')
    
    // Test Find Tutors link
    await page.click('text=Find Tutors')
    await expect(page).toHaveURL('/tutors')
    await expect(page.locator('text=Available Tutors')).toBeVisible()
    
    // Test brand logo
    await page.click('text=Tutorly')
    await expect(page).toHaveURL('/')
  })

  test('mobile navigation works', async ({ page }) => {
    // Set mobile viewport
    await page.setViewportSize({ width: 375, height: 667 })
    await page.goto('/')
    
    // Menu should be collapsed initially
    await expect(page.locator('text=Login')).not.toBeVisible()
    
    // Click menu button
    await page.click('button[aria-label="Toggle menu"]')
    
    // Menu should be visible
    await expect(page.locator('text=Login')).toBeVisible()
    await expect(page.locator('text=Register')).toBeVisible()
  })
})
