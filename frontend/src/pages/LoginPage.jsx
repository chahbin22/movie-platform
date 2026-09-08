import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import api from '../api/axios'
import { useAuth } from '../context/AuthContext'

function LoginPage() {
  const navigate = useNavigate()
  const { login } = useAuth()

  const [email, setEmail] = useState('')
  const [password, setPassword] =
    useState('')

  const [loading, setLoading] =
    useState(false)

  const [error, setError] =
    useState('')

  const handleSubmit = async (event) => {
    event.preventDefault()

    try {
      setLoading(true)
      setError('')

      const response = await api.post(
        '/auth/login',
        {
          email,
          password,
        },
      )

      const accessToken =
        response.data.accessToken

      await login(accessToken)

      alert('로그인되었습니다.')

      navigate('/')
    } catch (err) {
      console.error(err)

      const message =
        err.response?.data?.message ||
        '로그인 중 오류가 발생했습니다.'

      setError(message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <section>
      <h1>로그인</h1>

      <form
        className="form"
        onSubmit={handleSubmit}
      >
        <div className="form-group">
          <label htmlFor="login-email">
            이메일
          </label>

          <input
            id="login-email"
            type="email"
            value={email}
            onChange={(event) =>
              setEmail(event.target.value)
            }
            placeholder="이메일을 입력하세요."
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="login-password">
            비밀번호
          </label>

          <input
            id="login-password"
            type="password"
            value={password}
            onChange={(event) =>
              setPassword(
                event.target.value,
              )
            }
            placeholder="비밀번호를 입력하세요."
            required
          />
        </div>

        {error && (
          <p className="form-error">
            {error}
          </p>
        )}

        <button
          type="submit"
          disabled={loading}
        >
          {loading
            ? '로그인 중...'
            : '로그인'}
        </button>
      </form>
    </section>
  )
}

export default LoginPage