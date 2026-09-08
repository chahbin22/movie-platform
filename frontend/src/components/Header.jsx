import {
  Link,
  useNavigate,
} from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

function Header() {
  const navigate = useNavigate()

  const {
    user,
    authLoading,
    isLoggedIn,
    logout,
  } = useAuth()

  const handleLogout = () => {
    logout()
    navigate('/')
  }

  return (
    <header className="header">
      <div className="header-inner">
        <Link
          to="/"
          className="logo"
        >
          Movie Platform
        </Link>

        <nav className="nav">
          <Link to="/">
            영화
          </Link>

          <Link to="/posts">
            커뮤니티
          </Link>

          {!authLoading && (
            <>
              {isLoggedIn ? (
                <>
                  <Link to="/my/reservations">
                    내 예매
                  </Link>

                  <span>
                    {user.nickname}
                  </span>

                  <button
                    type="button"
                    onClick={handleLogout}
                    className="nav-logout-button"
                  >
                    로그아웃
                  </button>
                </>
              ) : (
                <>
                  <Link to="/login">
                    로그인
                  </Link>

                  <Link to="/signup">
                    회원가입
                  </Link>
                </>
              )}
            </>
          )}
        </nav>
      </div>
    </header>
  )
}

export default Header