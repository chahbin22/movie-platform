import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import api from '../api/axios'

function SignupPage() {
  const navigate = useNavigate()

  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [nickname, setNickname] = useState('')

  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const handleSubmit = async (event) => {
    event.preventDefault()

    try {
      setLoading(true)
      setError('')

      await api.post('/auth/signup', {
        email,
        password,
        nickname,
      })

      alert('회원가입이 완료되었습니다.')

      navigate('/login')
    } catch (err) {
      console.error(err)

      const message =
        err.response?.data?.message ||
        '회원가입 중 오류가 발생했습니다.'

      setError(message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <section>
      <h1>회원가입</h1>

      <form
        className="form"
        onSubmit={handleSubmit}
      >
        <div className="form-group">
          <label htmlFor="signup-email">
            이메일
          </label>

          <input
            id="signup-email"
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
          <label htmlFor="signup-password">
            비밀번호
          </label>

          <input
            id="signup-password"
            type="password"
            value={password}
            onChange={(event) =>
              setPassword(event.target.value)
            }
            placeholder="8자 이상의 비밀번호를 입력하세요."
            minLength={8}
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="signup-nickname">
            닉네임
          </label>

          <input
            id="signup-nickname"
            type="text"
            value={nickname}
            onChange={(event) =>
              setNickname(event.target.value)
            }
            placeholder="닉네임을 입력하세요."
            minLength={2}
            maxLength={20}
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
            ? '회원가입 중...'
            : '회원가입'}
        </button>
      </form>
    </section>
  )
}

export default SignupPage