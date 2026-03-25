import { useState, useRef, useEffect } from 'react'
import { Send, X, Loader2 } from 'lucide-react'
import { useAuth } from '../context/AuthContext'
import { useChat } from '../services/useChat'

const ChatWidget = ({ room, onClose }) => {
    const { user } = useAuth()
    const { messages, sendMessage, isConnected, loading } = useChat(room?.id)
    const [newMessage, setNewMessage] = useState('')
    const messagesEndRef = useRef(null)

    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' })
    }

    useEffect(() => {
        scrollToBottom()
    }, [messages])

    const handleSend = (e) => {
        e.preventDefault()
        if (newMessage.trim() && isConnected) {
            sendMessage(newMessage)
            setNewMessage('')
        }
    }

    if (!room) return null

    return (
        <div className="fixed bottom-4 right-4 w-96 h-[500px] bg-white dark:bg-gray-800 rounded-lg shadow-2xl flex flex-col overflow-hidden border border-gray-200 dark:border-gray-700 z-50">
            {/* Header */}
            <div className="bg-primary-600 px-4 py-3 flex items-center justify-between">
                <div>
                    <h3 className="text-white font-semibold flex items-center gap-2">
                        {room.otherUserName}
                        <span className="text-xs bg-white/20 px-2 py-0.5 rounded-full">
                            {room.otherUserRole}
                        </span>
                    </h3>
                    <div className="text-primary-100 text-xs flex items-center gap-1 mt-0.5">
                        <div className={`w-2 h-2 rounded-full ${isConnected ? 'bg-green-400' : 'bg-red-400'}`}></div>
                        {isConnected ? 'Connected' : 'Disconnected'}
                    </div>
                </div>
                <button 
                    onClick={onClose}
                    className="text-white hover:text-primary-100 transition-colors"
                >
                    <X className="h-5 w-5" />
                </button>
            </div>

            {/* Messages Area */}
            <div className="flex-1 overflow-y-auto p-4 space-y-4 bg-gray-50 dark:bg-gray-900/50">
                {loading ? (
                    <div className="flex justify-center items-center h-full">
                        <Loader2 className="h-6 w-6 animate-spin text-primary-600" />
                    </div>
                ) : messages.length === 0 ? (
                    <div className="text-center text-gray-500 dark:text-gray-400 mt-10">
                        <p>No messages yet.</p>
                        <p className="text-sm">Start the conversation!</p>
                    </div>
                ) : (
                    messages.map((msg, idx) => {
                        const isMe = msg.senderId === user?.id
                        return (
                            <div 
                                key={msg.id || idx} 
                                className={`flex flex-col ${isMe ? 'items-end' : 'items-start'}`}
                            >
                                <div 
                                    className={`max-w-[80%] px-4 py-2 rounded-2xl ${
                                        isMe 
                                            ? 'bg-primary-600 text-white rounded-br-none' 
                                            : 'bg-gray-200 dark:bg-gray-700 text-gray-900 dark:text-white rounded-bl-none'
                                    }`}
                                >
                                    <p className="text-sm break-words">{msg.content}</p>
                                </div>
                                <span className="text-xs text-gray-500 dark:text-gray-400 mt-1 mx-1">
                                    {new Date(msg.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                                </span>
                            </div>
                        )
                    })
                )}
                <div ref={messagesEndRef} />
            </div>

            {/* Input Area */}
            <div className="p-3 bg-white dark:bg-gray-800 border-t border-gray-200 dark:border-gray-700">
                <form onSubmit={handleSend} className="flex items-center gap-2">
                    <input
                        type="text"
                        value={newMessage}
                        onChange={(e) => setNewMessage(e.target.value)}
                        placeholder="Type a message..."
                        disabled={!isConnected}
                        className="flex-1 bg-gray-100 dark:bg-gray-700 border-transparent rounded-full px-4 py-2 text-sm focus:border-primary-500 focus:bg-white dark:focus:bg-gray-800 focus:ring-0 text-gray-900 dark:text-white disabled:opacity-50"
                    />
                    <button
                        type="submit"
                        disabled={!newMessage.trim() || !isConnected}
                        className="p-2 bg-primary-600 text-white rounded-full hover:bg-primary-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                        <Send className="h-4 w-4" />
                    </button>
                </form>
            </div>
        </div>
    )
}

export default ChatWidget
