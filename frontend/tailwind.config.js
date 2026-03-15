/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        primary: {
          DEFAULT: '#4F46E5', // Primary - Синий
          50: '#eef2ff',
          100: '#e0e7ff',
          200: '#c7d2fe',
          300: '#a5b4fc',
          400: '#818cf8',
          500: '#6366f1',
          600: '#4f46e5',
          700: '#4338ca',
          800: '#3730a3',
          900: '#312e81',
        },
        secondary: {
          DEFAULT: '#60A5FA', // Secondary - Голубой
        },
        accent: {
          DEFAULT: '#34D399', // Accent - Зеленый
        },
        background: '#F9FAFB', // Background - Очень светлый
        text: '#111827', // Text - Темный
      },
    },
  },
  plugins: [],
}
