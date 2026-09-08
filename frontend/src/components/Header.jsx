import { Link } from 'react-router-dom'

function Header() {
  return (
    <header className="header">
      <div className="header-inner">
        <Link to="/" className="logo">
          Movie Platform
        </Link>

        <nav className="nav">
          <Link to="/">영화</Link>
          <Link to="/login">로그인</Link>
          <Link to="/signup">회원가입</Link>
        </nav>
      </div>
    </header>
  )
}

export default Header