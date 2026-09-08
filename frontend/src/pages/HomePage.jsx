import { useEffect, useState } from 'react'
import api from '../api/axios'
import MovieCard from '../components/MovieCard'

function HomePage() {
  const [movies, setMovies] = useState([])
  const [keyword, setKeyword] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const fetchMovies = async (searchKeyword = '') => {
    try {
      setLoading(true)
      setError('')

      const trimmedKeyword = searchKeyword.trim()

      const response = trimmedKeyword
        ? await api.get('/movies', {
            params: {
              keyword: trimmedKeyword,
            },
          })
        : await api.get('/movies')

      setMovies(response.data)
    } catch (err) {
      console.error(err)

      setError('영화 목록을 불러오지 못했습니다.')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchMovies()
  }, [])

  const handleSearch = (event) => {
    event.preventDefault()

    fetchMovies(keyword)
  }

  const handleReset = () => {
    setKeyword('')
    fetchMovies()
  }

  return (
    <section>
      <div className="page-header">
        <div>
          <h1>영화</h1>
          <p>원하는 영화를 검색해보세요.</p>
        </div>
      </div>

      <form
        className="movie-search-form"
        onSubmit={handleSearch}
      >
        <input
          type="text"
          value={keyword}
          onChange={(event) =>
            setKeyword(event.target.value)
          }
          placeholder="영화 제목을 입력하세요."
          className="movie-search-input"
        />

        <button
          type="submit"
          className="movie-search-button"
        >
          검색
        </button>

        <button
          type="button"
          className="movie-reset-button"
          onClick={handleReset}
        >
          전체 보기
        </button>
      </form>

      {loading ? (
        <p>영화 목록을 불러오는 중입니다.</p>
      ) : error ? (
        <p>{error}</p>
      ) : (
        <>
          <p className="movie-count">
            총 {movies.length}개의 영화가 있습니다.
          </p>

          {movies.length === 0 ? (
            <p>검색 결과가 없습니다.</p>
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
        </>
      )}
    </section>
  )
}

export default HomePage