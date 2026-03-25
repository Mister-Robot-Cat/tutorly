import { useState, useEffect } from 'react'
import { MessageSquare, User, Clock } from 'lucide-react'
import api from '../services/api'
import LoadingSpinner from '../components/LoadingSpinner'
import ChatWidget from '../components/ChatWidget'

const Inbox = () => {
    const [rooms, setRooms] = useState([])
    const [loading, setLoading] = useState(true)
    const [activeRoom, setActiveRoom] = useState(null)

    useEffect(() => {
        fetchRooms()
    }, [])

    const fetchRooms = async () => {
        try {
            const response = await api.get('/chat/rooms')
            setRooms(response.data)
        } catch (error) {
            console.error('Error fetching chat rooms:', error)
        } finally {
            setLoading(false)
        }
    }

    const openChat = (room) => {
        setActiveRoom(room)
    }

    if (loading) return <LoadingSpinner message="Loading messages..." />

    return (
        <div className="max-w-4xl mx-auto py-8 px-4">
            <h1 className="text-2xl font-bold text-gray-900 dark:text-white mb-6 flex items-center">
                <MessageSquare className="mr-3 h-6 w-6 text-primary-600" />
                Messages
            </h1>

            {rooms.length === 0 ? (
                <div className="bg-white dark:bg-gray-800 rounded-lg shadow p-8 text-center">
                    <MessageSquare className="mx-auto h-12 w-12 text-gray-400 mb-4" />
                    <h3 className="text-lg font-medium text-gray-900 dark:text-white mb-1">No messages yet</h3>
                    <p className="text-gray-500 dark:text-gray-400">
                        When you contact a tutor or student, your conversations will appear here.
                    </p>
                </div>
            ) : (
                <div className="bg-white dark:bg-gray-800 rounded-lg shadow overflow-hidden divide-y divide-gray-200 dark:divide-gray-700">
                    {rooms.map(room => (
                        <div 
                            key={room.id}
                            onClick={() => openChat(room)}
                            className="p-4 hover:bg-gray-50 dark:hover:bg-gray-700/50 cursor-pointer transition-colors flex items-center justify-between"
                        >
                            <div className="flex items-center space-x-4">
                                <div className="w-12 h-12 bg-primary-100 dark:bg-primary-900/30 rounded-full flex items-center justify-center text-primary-700 dark:text-primary-300 font-bold text-lg">
                                    {room.otherUserName.charAt(0)}
                                </div>
                                <div>
                                    <div className="flex items-center gap-2">
                                        <h4 className="text-md font-semibold text-gray-900 dark:text-white">
                                            {room.otherUserName}
                                        </h4>
                                        <span className="text-xs px-2 py-0.5 rounded-full bg-gray-100 dark:bg-gray-700 text-gray-600 dark:text-gray-300">
                                            {room.otherUserRole}
                                        </span>
                                    </div>
                                    <p className="text-sm text-gray-500 dark:text-gray-400 line-clamp-1 mt-0.5">
                                        {room.lastMessage || 'No messages yet'}
                                    </p>
                                </div>
                            </div>
                            <div className="flex flex-col items-end space-y-1">
                                {room.lastMessageTime && (
                                    <span className="text-xs text-gray-400 flex items-center">
                                        <Clock className="w-3 h-3 mr-1" />
                                        {new Date(room.lastMessageTime).toLocaleDateString(undefined, { month: 'short', day: 'numeric' })}
                                    </span>
                                )}
                                {room.unreadCount > 0 && (
                                    <span className="bg-primary-600 text-white text-xs font-bold px-2 py-1 rounded-full">
                                        {room.unreadCount}
                                    </span>
                                )}
                            </div>
                        </div>
                    ))}
                </div>
            )}

            {/* Render the chat widget if a room is selected */}
            {activeRoom && (
                <ChatWidget 
                    room={activeRoom} 
                    onClose={() => {
                        setActiveRoom(null)
                        fetchRooms() // Refresh to update unread counts and last message
                    }} 
                />
            )}
        </div>
    )
}

export default Inbox
