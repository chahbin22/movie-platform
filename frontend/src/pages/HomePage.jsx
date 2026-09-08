import { useEffect, useState } from 'react'
import api from '../api/axios'
import MovieCard from '../components/MovieCard'

function HomePage() {
  const [movies, setMovies] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    const fetchMovies = async () => {
      try {
        const response = await api.get('/movies')

        setMovies(response.data)
      } catch (err) {
        console.error(err)

        setError('영화 목록을 불러오지 못했습니다.')
      } finally {
        setLoading(false)
      }
    }

    fetchMovies()
  }, [])

  if (loading) {
    return (
      <section>
        <h1>영화</h1>
        <p>영화 목록을 불러오는 중입니다.</p>
      </section>
    )
  }

  if (error) {
    return (
      <section>
        <h1>영화</h1>
        <p>{error}</p>
      </section>
    )
  }

  return (
    <section>
      <div className="page-header">
        <div>
          <h1>영화</h1>
          <p>총 {movies.length}개의 영화가 있습니다.</p>
        </div>
      </div>

      {movies.length === 0 ? (
        <p>저장된 영화가 없습니다.</p>
      ) : (
        <div className="movie-grid">
          {movies.map((movie) => (
            <MovieCard
              key={movie.movieId}
              movie={movie}
            />
          ))}
        </div>
      )}
    </section>
  )
}

export default HomePage