import { useEffect, useState } from 'react'
import {
  useNavigate,
  useParams,
} from 'react-router-dom'
import api from '../api/axios'

function MovieDetailPage() {
  const { movieId } = useParams()
  const navigate = useNavigate()

  const [movie, setMovie] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    const fetchMovie = async () => {
      try {
        const response = await api.get(
          `/movies/${movieId}`,
        )

        setMovie(response.data)
      } catch (err) {
        console.error(err)

        setError(
          '영화 정보를 불러오지 못했습니다.',
        )
      } finally {
        setLoading(false)
      }
    }

    fetchMovie()
  }, [movieId])

  const handleReservation = () => {
    navigate(
      `/movies/${movieId}/schedules`,
    )
  }

  if (loading) {
    return (
      <section>
        <p>
          영화 정보를 불러오는 중입니다.
        </p>
      </section>
    )
  }

  if (error) {
    return (
      <section>
        <p>{error}</p>
      </section>
    )
  }

  if (!movie) {
    return (
      <section>
        <p>영화 정보가 없습니다.</p>
      </section>
    )
  }

  return (
    <section className="movie-detail">
      <div className="movie-detail-poster">
        {movie.posterUrl ? (
          <img
            src={movie.posterUrl}
            alt={`${movie.title} 포스터`}
          />
        ) : (
          <div className="movie-detail-placeholder">
            포스터 없음
          </div>
        )}
      </div>

      <div className="movie-detail-info">
        <h1>{movie.title}</h1>

        <div className="movie-detail-meta">
          {movie.genre && (
            <p>
              <strong>장르</strong>
              <span>{movie.genre}</span>
            </p>
          )}

          {movie.runningTime && (
            <p>
              <strong>러닝타임</strong>
              <span>
                {movie.runningTime}분
              </span>
            </p>
          )}

          {movie.releaseDate && (
            <p>
              <strong>개봉일</strong>
              <span>
                {movie.releaseDate}
              </span>
            </p>
          )}

          {movie.director && (
            <p>
              <strong>감독</strong>
              <span>{movie.director}</span>
            </p>
          )}

          {movie.ageRating && (
            <p>
              <strong>관람등급</strong>
              <span>
                {movie.ageRating}
              </span>
            </p>
          )}
        </div>

        <div className="movie-description">
          <h2>줄거리</h2>

          <p>
            {movie.description ||
              '등록된 줄거리가 없습니다.'}
          </p>
        </div>

        <button
          type="button"
          className="reservation-button"
          onClick={handleReservation}
        >
          예매하기
        </button>
      </div>
    </section>
  )
}

export default MovieDetailPage