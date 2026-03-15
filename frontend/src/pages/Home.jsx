import { Link } from 'react-router-dom'
import { GraduationCap, Search, Star, Users, BookOpen, Shield, TrendingUp } from 'lucide-react'

const Home = () => {
  const subjects = [
    { name: 'Programming', icon: '💻', color: 'bg-blue-500' },
    { name: 'Mathematics', icon: '📐', color: 'bg-purple-500' },
    { name: 'Physics', icon: '⚛️', color: 'bg-green-500' },
    { name: 'English', icon: '📚', color: 'bg-yellow-500' },
    { name: 'Business', icon: '💼', color: 'bg-red-500' },
    { name: 'Design', icon: '🎨', color: 'bg-pink-500' },
  ]

  const features = [
    {
      icon: <Search className="h-8 w-8" />,
      title: 'Find Perfect Tutors',
      description: 'Search and filter through hundreds of qualified tutors',
    },
    {
      icon: <Star className="h-8 w-8" />,
      title: 'Verified Reviews',
      description: 'Read authentic reviews from real students',
    },
    {
      icon: <BookOpen className="h-8 w-8" />,
      title: 'Flexible Scheduling',
      description: 'Book lessons at times that work for you',
    },
    {
      icon: <Shield className="h-8 w-8" />,
      title: 'Secure Payments',
      description: 'Safe and secure payment processing',
    },
  ]

  return (
    <div className="min-h-screen">
      {/* Hero Section */}
      <section className="relative bg-gradient-to-br from-primary-600 via-primary-700 to-primary-900 text-white py-20 px-4">
        <div className="max-w-7xl mx-auto">
          <div className="text-center">
            <h1 className="text-5xl md:text-6xl font-bold mb-6 animate-fade-in">
              Find Your Perfect Tutor
            </h1>
            <p className="text-xl md:text-2xl mb-8 text-primary-100">
              Learn from expert tutors across all subjects
            </p>
            <div className="flex flex-col sm:flex-row gap-4 justify-center">
              <Link
                to="/tutors"
                className="bg-white text-primary-600 px-8 py-4 rounded-lg text-lg font-semibold hover:bg-gray-100 transition-all transform hover:scale-105 shadow-lg"
              >
                Browse Tutors
              </Link>
              <Link
                to="/register"
                className="bg-primary-500 text-white px-8 py-4 rounded-lg text-lg font-semibold hover:bg-primary-400 transition-all transform hover:scale-105 shadow-lg border-2 border-white"
              >
                Get Started
              </Link>
            </div>
          </div>
        </div>
        
        {/* Decorative elements */}
        <div className="absolute top-0 left-0 w-full h-full overflow-hidden opacity-10">
          <div className="absolute top-10 left-10 w-72 h-72 bg-white rounded-full blur-3xl"></div>
          <div className="absolute bottom-10 right-10 w-96 h-96 bg-white rounded-full blur-3xl"></div>
        </div>
      </section>

      {/* Popular Subjects */}
      <section className="py-16 px-4 bg-white dark:bg-gray-900">
        <div className="max-w-7xl mx-auto">
          <h2 className="text-3xl md:text-4xl font-bold text-center mb-12 text-gray-900 dark:text-white">
            Popular Subjects
          </h2>
          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-6">
            {subjects.map((subject) => (
              <Link
                key={subject.name}
                to={`/tutors?subject=${subject.name}`}
                className="group"
              >
                <div className="bg-gray-50 dark:bg-gray-800 rounded-xl p-6 text-center hover:shadow-xl transition-all transform hover:scale-105 cursor-pointer border-2 border-transparent hover:border-primary-500">
                  <div className={`${subject.color} w-16 h-16 rounded-full flex items-center justify-center text-3xl mx-auto mb-3 group-hover:scale-110 transition-transform`}>
                    {subject.icon}
                  </div>
                  <h3 className="font-semibold text-gray-900 dark:text-white">
                    {subject.name}
                  </h3>
                </div>
              </Link>
            ))}
          </div>
        </div>
      </section>

      {/* Features */}
      <section className="py-16 px-4 bg-gray-50 dark:bg-gray-800">
        <div className="max-w-7xl mx-auto">
          <h2 className="text-3xl md:text-4xl font-bold text-center mb-12 text-gray-900 dark:text-white">
            Why Choose Tutorly?
          </h2>
          <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-8">
            {features.map((feature, index) => (
              <div
                key={index}
                className="bg-white dark:bg-gray-900 rounded-xl p-6 shadow-lg hover:shadow-2xl transition-all transform hover:-translate-y-2"
              >
                <div className="text-primary-600 dark:text-primary-400 mb-4">
                  {feature.icon}
                </div>
                <h3 className="text-xl font-semibold mb-2 text-gray-900 dark:text-white">
                  {feature.title}
                </h3>
                <p className="text-gray-600 dark:text-gray-400">
                  {feature.description}
                </p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Stats */}
      <section className="py-16 px-4 bg-white dark:bg-gray-900">
        <div className="max-w-7xl mx-auto">
          <div className="grid md:grid-cols-3 gap-8 text-center">
            <div className="p-6">
              <div className="flex items-center justify-center mb-4">
                <Users className="h-12 w-12 text-primary-600" />
              </div>
              <div className="text-4xl font-bold text-gray-900 dark:text-white mb-2">
                500+
              </div>
              <div className="text-gray-600 dark:text-gray-400">
                Expert Tutors
              </div>
            </div>
            <div className="p-6">
              <div className="flex items-center justify-center mb-4">
                <BookOpen className="h-12 w-12 text-primary-600" />
              </div>
              <div className="text-4xl font-bold text-gray-900 dark:text-white mb-2">
                10,000+
              </div>
              <div className="text-gray-600 dark:text-gray-400">
                Lessons Completed
              </div>
            </div>
            <div className="p-6">
              <div className="flex items-center justify-center mb-4">
                <TrendingUp className="h-12 w-12 text-primary-600" />
              </div>
              <div className="text-4xl font-bold text-gray-900 dark:text-white mb-2">
                4.8/5
              </div>
              <div className="text-gray-600 dark:text-gray-400">
                Average Rating
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* CTA */}
      <section className="py-16 px-4 bg-gradient-to-r from-primary-600 to-primary-800 text-white">
        <div className="max-w-4xl mx-auto text-center">
          <h2 className="text-3xl md:text-4xl font-bold mb-6">
            Ready to Start Learning?
          </h2>
          <p className="text-xl mb-8 text-primary-100">
            Join thousands of students improving their skills with expert tutors
          </p>
          <Link
            to="/register"
            className="inline-block bg-white text-primary-600 px-8 py-4 rounded-lg text-lg font-semibold hover:bg-gray-100 transition-all transform hover:scale-105 shadow-lg"
          >
            Sign Up Now
          </Link>
        </div>
      </section>
    </div>
  )
}

export default Home
