import { useState, useEffect } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { tutorAPI, subjectAPI } from '../services/api'
import { Search, Filter, Star, DollarSign, BookOpen, ChevronLeft, ChevronRight } from 'lucide-react'
import LoadingSpinner from '../components/LoadingSpinner'

const Tutors = () => {
  const [searchParams, setSearchParams] = useSearchParams()
  const [tutors, setTutors] = useState([])
  const [subjects, setSubjects] = useState([])
  const [loading, setLoading] = useState(true)
  const [pagination, setPagination] = useState({
    page: 0,
    size: 10,
    totalPages: 0,
    totalElements: 0,
  })
  
  const [filters, setFilters] = useState({
    subject: searchParams.get('subject') || '',
    minPrice: '',
    maxPrice: '',
    minRating: '',
  })

  useEffect(() => {
    fetchSubjects()
    fetchTutors(pagination.page)
  }, [pagination.page]) // Refetch when page changes

  const fetchSubjects = async () => {
    try {
      const response = await subjectAPI.getAll()
      setSubjects(response.data)
    } catch (error) {
      console.error('Error fetching subjects:', error)
    }
  }

  const fetchTutors = async (pageIndex = 0) => {
    setLoading(true)
    try {
      const isFiltering = filters.subject || filters.minPrice || filters.maxPrice || filters.minRating
      
      let response;
      if (isFiltering) {
        // Use standard getAll if we are applying specific filters 
        // (In a real production app, the paged endpoint should also support all these filters)
        const params = {}
        if (filters.subject) params.subject = filters.subject
        if (filters.minPrice) params.minPrice = filters.minPrice
        if (filters.maxPrice) params.maxPrice = filters.maxPrice
        if (filters.minRating) params.minRating = filters.minRating
        response = await tutorAPI.getAll(params)
        setTutors(response.data)
        setPagination(prev => ({ ...prev, totalPages: 1, totalElements: response.data.length }))
      } else {
        // Use paginated endpoint when no filters are active
        response = await tutorAPI.getPaged(pageIndex, pagination.size, 'rating')
        setTutors(response.data.content)
        setPagination({
          page: response.data.number,
          size: response.data.size,
          totalPages: response.data.totalPages,
          totalElements: response.data.totalElements,
        })
      }
    } catch (error) {
      console.error('Error fetching tutors:', error)
    } finally {
      setLoading(false)
    }
  }

  const handleFilterChange = (name, value) => {
    setFilters({ ...filters, [name]: value })
  }

  const applyFilters = () => {
    // Reset to first page when applying new filters
    setPagination(prev => ({ ...prev, page: 0 }))
    fetchTutors(0)
  }

  const clearFilters = () => {
    setFilters({ subject: '', minPrice: '', maxPrice: '', minRating: '' })
    setSearchParams({})
    setPagination(prev => ({ ...prev, page: 0 }))
    fetchTutors(0) // Will fetch un-filtered paginated results
  }

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900 py-8 px-4">
      <div className="max-w-7xl mx-auto">
        <div className="mb-8">
          <h1 className="text-4xl font-bold text-gray-900 dark:text-white mb-2">
            Find Your Perfect Tutor
          </h1>
          <p className="text-gray-600 dark:text-gray-400">
            Browse {pagination.totalElements > 0 ? pagination.totalElements : tutors.length} expert tutors across all subjects
          </p>
        </div>

        <div className="grid lg:grid-cols-4 gap-8">
          {/* Filters Sidebar */}
          <div className="lg:col-span-1">
            <div className="bg-white dark:bg-gray-800 rounded-lg shadow-lg p-6 sticky top-24">
              <div className="flex items-center justify-between mb-6">
                <h2 className="text-lg font-semibold text-gray-900 dark:text-white flex items-center">
                  <Filter className="h-5 w-5 mr-2" />
                  Filters
                </h2>
                <button
                  onClick={clearFilters}
                  className="text-sm text-primary-600 hover:text-primary-700 dark:text-primary-400"
                >
                  Clear
                </button>
              </div>

              <div className="space-y-6">
                <div>
                  <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                    Subject
                  </label>
                  <select
                    value={filters.subject}
                    onChange={(e) => handleFilterChange('subject', e.target.value)}
                    className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-primary-500"
                  >
                    <option value="">All Subjects</option>
                    {subjects.map((subject) => (
                      <option key={subject.id} value={subject.name}>
                        {subject.icon} {subject.name}
                      </option>
                    ))}
                  </select>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                    Price Range ($/hour)
                  </label>
                  <div className="grid grid-cols-2 gap-2">
                    <input
                      type="number"
                      placeholder="Min"
                      value={filters.minPrice}
                      onChange={(e) => handleFilterChange('minPrice', e.target.value)}
                      className="px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-primary-500"
                    />
                    <input
                      type="number"
                      placeholder="Max"
                      value={filters.maxPrice}
                      onChange={(e) => handleFilterChange('maxPrice', e.target.value)}
                      className="px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-primary-500"
                    />
                  </div>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                    Minimum Rating
                  </label>
                  <select
                    value={filters.minRating}
                    onChange={(e) => handleFilterChange('minRating', e.target.value)}
                    className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-primary-500"
                  >
                    <option value="">Any Rating</option>
                    <option value="4">4+ Stars</option>
                    <option value="4.5">4.5+ Stars</option>
                  </select>
                </div>

                <button
                  onClick={applyFilters}
                  className="w-full bg-primary-600 hover:bg-primary-700 text-white px-4 py-2 rounded-lg font-medium transition-colors"
                >
                  Apply Filters
                </button>
              </div>
            </div>
          </div>

          {/* Tutors Grid */}
          <div className="lg:col-span-3">
            {loading ? (
              <LoadingSpinner message="Finding the best tutors for you..." size="large" />
            ) : tutors.length === 0 ? (
              <div className="text-center py-12">
                <Search className="h-16 w-16 text-gray-400 mx-auto mb-4" />
                <h3 className="text-xl font-semibold text-gray-900 dark:text-white mb-2">
                  No tutors found
                </h3>
                <p className="text-gray-600 dark:text-gray-400">
                  Try adjusting your filters
                </p>
              </div>
            ) : (
              <>
                <div className="grid md:grid-cols-2 gap-6 mb-8">
                  {tutors.map((tutor) => (
                    <Link
                      key={tutor.id}
                      to={`/tutors/${tutor.id}`}
                      className="group"
                    >
                      <div className="bg-white dark:bg-gray-800 rounded-lg shadow-lg hover:shadow-2xl transition-all transform hover:-translate-y-1 overflow-hidden h-full flex flex-col">
                        <div className="p-6 flex-grow">
                          <div className="flex items-start space-x-4">
                            <div className="flex-shrink-0">
                              <div className="w-16 h-16 bg-gradient-to-br from-primary-400 to-primary-600 rounded-full flex items-center justify-center text-white text-2xl font-bold">
                                {tutor.user?.firstName?.[0] || ''}{tutor.user?.lastName?.[0] || ''}
                              </div>
                            </div>
                            <div className="flex-1 min-w-0">
                              <div className="flex items-center justify-between">
                                <h3 className="text-lg font-semibold text-gray-900 dark:text-white group-hover:text-primary-600 dark:group-hover:text-primary-400 transition-colors truncate">
                                  {tutor.user?.firstName || 'Unknown'} {tutor.user?.lastName || 'Tutor'}
                                </h3>
                                {tutor.isVerified && (
                                  <span className="text-xs bg-green-100 dark:bg-green-900/30 text-green-800 dark:text-green-400 px-2 py-1 rounded-full flex-shrink-0 ml-2">
                                    ✓ Verified
                                  </span>
                                )}
                              </div>
                              <div className="flex items-center mt-1 text-sm text-gray-600 dark:text-gray-400">
                                <Star className="h-4 w-4 text-yellow-400 fill-current mr-1" />
                                <span className="font-medium">{tutor.rating?.toFixed(1) || '0.0'}</span>
                                <span className="mx-1">•</span>
                                <span>{tutor.totalReviews || 0} reviews</span>
                              </div>
                            </div>
                          </div>

                          <p className="mt-4 text-sm text-gray-600 dark:text-gray-400 line-clamp-2">
                            {tutor.description || 'No description provided.'}
                          </p>

                          <div className="mt-4 flex flex-wrap gap-2">
                            {tutor.subjects?.slice(0, 3).map((subject) => (
                              <span
                                key={subject.id}
                                className="inline-flex items-center px-3 py-1 rounded-full text-xs font-medium bg-primary-100 dark:bg-primary-900/30 text-primary-800 dark:text-primary-300"
                              >
                                {subject.icon} {subject.name}
                              </span>
                            ))}
                          </div>
                        </div>

                        <div className="mt-auto px-6 py-4 bg-gray-50 dark:bg-gray-700/50 border-t border-gray-200 dark:border-gray-700 flex items-center justify-between">
                          <div className="flex items-center text-gray-600 dark:text-gray-400">
                            <BookOpen className="h-4 w-4 mr-1" />
                            <span className="text-sm">{tutor.totalLessons || 0} lessons</span>
                          </div>
                          <div className="flex items-center text-lg font-bold text-gray-900 dark:text-white">
                            <DollarSign className="h-5 w-5" />
                            {tutor.hourlyRate}/hr
                          </div>
                        </div>
                      </div>
                    </Link>
                  ))}
                </div>

                {/* Pagination Controls */}
                {pagination.totalPages > 1 && (
                  <div className="flex items-center justify-center space-x-4 border-t border-gray-200 dark:border-gray-700 pt-6">
                    <button
                      onClick={() => setPagination(prev => ({ ...prev, page: prev.page - 1 }))}
                      disabled={pagination.page === 0}
                      className="p-2 rounded-md bg-white dark:bg-gray-800 text-gray-500 disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50 dark:hover:bg-gray-700 border border-gray-300 dark:border-gray-600 transition-colors"
                    >
                      <ChevronLeft className="h-5 w-5" />
                    </button>
                    
                    <span className="text-sm font-medium text-gray-700 dark:text-gray-300">
                      Page {pagination.page + 1} of {pagination.totalPages}
                    </span>

                    <button
                      onClick={() => setPagination(prev => ({ ...prev, page: prev.page + 1 }))}
                      disabled={pagination.page >= pagination.totalPages - 1}
                      className="p-2 rounded-md bg-white dark:bg-gray-800 text-gray-500 disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50 dark:hover:bg-gray-700 border border-gray-300 dark:border-gray-600 transition-colors"
                    >
                      <ChevronRight className="h-5 w-5" />
                    </button>
                  </div>
                )}
              </>
            )}
          </div>
        </div>
      </div>
    </div>
  )
}

export default Tutors
