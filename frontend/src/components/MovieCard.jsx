import { Link } from 'react-router-dom'

function MovieCard({ movie }) {
  return (
    <Link
      to={`/movies/${movie.movieId}`}
      className="movie-card-link"
    >
      <article className="movie-card">
        <div className="movie-poster-wrapper">
          {movie.posterUrl ? (
            <img
              src={movie.posterUrl}
              alt={`${movie.title} 포스터`}
              className="movie-poster"
            />
          ) : (
            <div className="movie-poster-placeholder">
              포스터 없음
            </div>
          )}
        </div>

        <div className="movie-info">
          <h2 className="movie-title">
            {movie.title}
          </h2>

          <p className="movie-genre">
            {movie.genre || '장르 정보 없음'}
          </p>

          <div className="movie-meta">
            {movie.runningTime && (
              <span>{movie.runningTime}분</span>
            )}

            {movie.releaseDate && (
              <span>{movie.releaseDate}</span>
            )}
          </div>
        </div>
      </article>
    </Link>
  )
}

export default MovieCard