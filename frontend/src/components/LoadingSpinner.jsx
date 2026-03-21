import { Loader2 } from 'lucide-react'

const LoadingSpinner = ({ message = 'Loading...', size = 'default' }) => {
  const sizeClasses = {
    small: 'h-4 w-4',
    default: 'h-8 w-8',
    large: 'h-12 w-12'
  }

  return (
    <div className="flex flex-col items-center justify-center min-h-[200px] w-full">
      <Loader2 className={`animate-spin text-primary-600 ${sizeClasses[size]}`} />
      <p className="mt-4 text-sm text-gray-500 dark:text-gray-400 font-medium">
        {message}
      </p>
    </div>
  )
}

export default LoadingSpinner
