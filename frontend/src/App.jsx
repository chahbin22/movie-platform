import { Route, Routes } from 'react-router-dom'
import Header from './components/Header'
import HomePage from './pages/HomePage'
import LoginPage from './pages/LoginPage'
import SignupPage from './pages/SignupPage'
import MovieDetailPage from './pages/MovieDetailPage'
import SchedulePage from './pages/SchedulePage'
import SeatPage from './pages/SeatPage'
import MyReservationPage from './pages/MyReservationPage'
import PostListPage from './pages/PostListPage'
import PostDetailPage from './pages/PostDetailPage'
import PostWritePage from './pages/PostWritePage'
import PostEditPage from './pages/PostEditPage'
import './community.css'

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
            path="/my/reservations"
            element={<MyReservationPage />}
          />

          <Route
            path="/posts"
            element={<PostListPage />}
          />

          <Route
            path="/posts/new"
            element={<PostWritePage />}
          />

          <Route
            path="/posts/:postId"
            element={<PostDetailPage />}
          />

          <Route
            path="/posts/:postId/edit"
            element={<PostEditPage />}
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