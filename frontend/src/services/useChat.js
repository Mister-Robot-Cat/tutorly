import { useState, useEffect, useCallback } from 'react'
import { Client } from '@stomp/stompjs'
import api from './api'
import { useAuth } from '../context/AuthContext'
import toast from 'react-hot-toast'

// Helper to get WS URL from standard API URL
const getWsUrl = () => {
    const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8081/api'
    return apiUrl.replace('/api', '/ws')
}

export const useChat = (roomId) => {
    const [messages, setMessages] = useState([])
    const [stompClient, setStompClient] = useState(null)
    const [isConnected, setIsConnected] = useState(false)
    const [loading, setLoading] = useState(false)
    const { user } = useAuth()

    const fetchMessages = useCallback(async () => {
        if (!roomId) return
        setLoading(true)
        try {
            const response = await api.get(`/chat/rooms/${roomId}/messages`)
            // Backend returns Page<ChatMessageDTO> ordered by date desc
            // We want oldest first in the chat window, so reverse it
            const fetchedMessages = response.data.content.reverse()
            setMessages(fetchedMessages)
            
            // Mark as read
            await api.patch(`/chat/rooms/${roomId}/read`)
        } catch (error) {
            console.error('Error fetching messages', error)
            toast.error('Failed to load chat history')
        } finally {
            setLoading(false)
        }
    }, [roomId])

    useEffect(() => {
        if (roomId) {
            fetchMessages()
        }
    }, [roomId, fetchMessages])

    useEffect(() => {
        if (!user || !roomId) return

        let client = null
        let cancelled = false

        const initializeChat = async () => {
            try {
                if (typeof global === 'undefined') {
                    window.global = window
                }

                const { default: SockJS } = await import('sockjs-client')
                if (cancelled) return

                const token = localStorage.getItem('token')
                client = new Client({
                    webSocketFactory: () => new SockJS(getWsUrl()),
                    connectHeaders: {
                        Authorization: `Bearer ${token}`
                    },
                    debug: () => {},
                    reconnectDelay: 5000,
                    heartbeatIncoming: 4000,
                    heartbeatOutgoing: 4000,
                })

                client.onConnect = () => {
                    setIsConnected(true)
                    client.subscribe(`/topic/room/${roomId}`, (message) => {
                        if (message.body) {
                            const newMessage = JSON.parse(message.body)
                            setMessages(prev => {
                                if (prev.some(m => m.id === newMessage.id)) return prev
                                return [...prev, newMessage]
                            })
                        }
                    })
                }

                client.onStompError = (frame) => {
                    console.error('Broker reported error: ' + frame.headers['message'])
                    console.error('Additional details: ' + frame.body)
                }

                client.activate()
                setStompClient(client)
            } catch (error) {
                console.error('Failed to initialize chat connection', error)
            }
        }

        initializeChat()

        return () => {
            cancelled = true
            setIsConnected(false)
            if (client?.active) {
                client.deactivate()
            }
        }
    }, [roomId, user])

    const sendMessage = useCallback((content) => {
        if (stompClient && isConnected && content.trim() && user) {
            const chatMessageDTO = {
                roomId: roomId,
                senderId: user.id,
                content: content.trim()
            }
            stompClient.publish({
                destination: '/app/chat.send',
                body: JSON.stringify(chatMessageDTO)
            })
        }
    }, [stompClient, isConnected, roomId, user])

    return {
        messages,
        sendMessage,
        isConnected,
        loading
    }
}
