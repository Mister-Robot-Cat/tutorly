# Tutorly Frontend

Modern React frontend for the Tutorly tutoring marketplace platform.

## 🚀 Tech Stack

- **React 18** - UI library
- **Vite** - Build tool and dev server
- **React Router** - Client-side routing
- **TailwindCSS** - Utility-first CSS framework
- **Axios** - HTTP client
- **Lucide React** - Modern icon library
- **date-fns** - Date formatting

## 📦 Installation

```bash
cd frontend
npm install
```

## 🏃 Running the App

```bash
# Development server
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview
```

The app will be available at `http://localhost:5173`

## 🎨 Features

- **Modern UI/UX** - Clean, responsive design with dark mode support
- **Authentication** - Login and registration with JWT
- **Tutor Discovery** - Browse and filter tutors by subject, price, rating
- **Booking System** - Schedule lessons with tutors
- **Student Dashboard** - Manage bookings and track lessons
- **Admin Panel** - User management and platform statistics
- **Real-time Updates** - Dynamic data fetching and state management

## 📁 Project Structure

```
src/
├── components/      # Reusable UI components
│   ├── Navbar.jsx
│   └── ProtectedRoute.jsx
├── context/         # React context providers
│   ├── AuthContext.jsx
│   └── ThemeContext.jsx
├── pages/           # Page components
│   ├── Home.jsx
│   ├── Login.jsx
│   ├── Register.jsx
│   ├── Tutors.jsx
│   ├── TutorDetail.jsx
│   ├── StudentDashboard.jsx
│   └── AdminDashboard.jsx
├── services/        # API integration
│   └── api.js
├── App.jsx          # Main app component
├── main.jsx         # Entry point
└── index.css        # Global styles
```

## 🔧 Configuration

The frontend is configured to proxy API requests to the backend:

```javascript
// vite.config.js
proxy: {
  '/api': {
    target: 'http://localhost:8080',
    changeOrigin: true,
  }
}
```

## 🎯 Key Pages

- `/` - Landing page with features and subjects
- `/login` - User authentication
- `/register` - New user registration
- `/tutors` - Browse all tutors with filters
- `/tutors/:id` - Tutor profile and booking
- `/dashboard/student` - Student bookings dashboard
- `/dashboard/admin` - Admin panel (admin only)

## 🌙 Dark Mode

The app supports dark mode with automatic system preference detection and manual toggle.

## 📱 Responsive Design

Fully responsive design that works on:
- Desktop (1024px+)
- Tablet (768px - 1023px)
- Mobile (< 768px)

## 🔐 Authentication

JWT tokens are stored in localStorage and automatically included in API requests via Axios interceptors.

## 🚀 Deployment

Build the production bundle:

```bash
npm run build
```

Deploy the `dist` folder to:
- Vercel
- Netlify
- GitHub Pages
- Any static hosting service

---

**Built with ❤️ using React and TailwindCSS**
