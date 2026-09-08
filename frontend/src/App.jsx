import { Route, Routes } from 'react-router-dom'
import Header from './components/Header'
import HomePage from './pages/HomePage'
import LoginPage from './pages/LoginPage'
import SignupPage from './pages/SignupPage'
import MovieDetailPage from './pages/MovieDetailPage'
import SchedulePage from './pages/SchedulePage'
import SeatPage from './pages/SeatPage'

function App() {
  return (
    <>
      <Header />

      <main className="container">
        <Routes>
          <Route
            path="/"
            element={<HomePage />}
          />

          <Route
            path="/movies/:movieId"
            element={<MovieDetailPage />}
          />

          <Route
            path="/movies/:movieId/schedules"
            element={<SchedulePage />}
          />

          <Route
            path="/schedules/:scheduleId/seats"
            element={<SeatPage />}
          />

          <Route
            path="/login"
            element={<LoginPage />}
          />

          <Route
            path="/signup"
            element={<SignupPage />}
          />
        </Routes>
      </main>
    </>
  )
}

export default App