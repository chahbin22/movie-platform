import {
  createContext,
  useContext,
  useEffect,
  useState,
} from 'react'
import api from '../api/axios'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [authLoading, setAuthLoading] =
    useState(true)

  useEffect(() => {
    const checkAuthentication = async () => {
      const accessToken =
        localStorage.getItem('accessToken')

      if (!accessToken) {
        setAuthLoading(false)
        return
      }

      try {
        const response =
          await api.get('/users/me')

        setUser(response.data)
      } catch (error) {
        console.error(error)

        localStorage.removeItem(
          'accessToken',
        )

        setUser(null)
      } finally {
        setAuthLoading(false)
      }
    }

    checkAuthentication()
  }, [])

  const login = async (accessToken) => {
    localStorage.setItem(
      'accessToken',
      accessToken,
    )

    try {
      const response =
        await api.get('/users/me')

      setUser(response.data)
    } catch (error) {
      localStorage.removeItem(
        'accessToken',
      )

      setUser(null)

      throw error
    }
  }

  const logout = () => {
    localStorage.removeItem('accessToken')
    setUser(null)
  }

  const value = {
    user,
    authLoading,
    isLoggedIn: Boolean(user),
    login,
    logout,
  }

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const context = useContext(AuthContext)

  if (!context) {
    throw new Error(
      'useAuth는 AuthProvider 내부에서 사용해야 합니다.',
    )
  }

  return context
}