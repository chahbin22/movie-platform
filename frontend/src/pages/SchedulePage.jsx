import {
  useEffect,
  useState,
} from 'react'
import {
  Link,
  useNavigate,
  useParams,
} from 'react-router-dom'
import api from '../api/axios'

function SchedulePage() {
  const { movieId } = useParams()
  const navigate = useNavigate()

  const [movie, setMovie] = useState(null)
  const [theaters, setTheaters] =
    useState([])

  const [theaterId, setTheaterId] =
    useState('')

  const [date, setDate] =
    useState('')

  const [schedules, setSchedules] =
    useState([])

  const [loading, setLoading] =
    useState(true)

  const [scheduleLoading, setScheduleLoading] =
    useState(false)

  const [error, setError] =
    useState('')

  const [searched, setSearched] =
    useState(false)

  useEffect(() => {
    const fetchInitialData = async () => {
      try {
        const [
          movieResponse,
          theaterResponse,
        ] = await Promise.all([
          api.get(`/movies/${movieId}`),
          api.get('/theaters'),
        ])

        setMovie(movieResponse.data)
        setTheaters(theaterResponse.data)
      } catch (err) {
        console.error(err)

        setError(
          '예매 정보를 불러오지 못했습니다.',
        )
      } finally {
        setLoading(false)
      }
    }

    fetchInitialData()
  }, [movieId])

  const handleSearch = async (event) => {
    event.preventDefault()

    if (!theaterId || !date) {
      setError(
        '영화관과 날짜를 모두 선택해주세요.',
      )

      return
    }

    try {
      setScheduleLoading(true)
      setError('')
      setSearched(true)

      const response = await api.get(
        '/schedules',
        {
          params: {
            movieId,
            theaterId,
            date,
          },
        },
      )

      setSchedules(response.data)
    } catch (err) {
      console.error(err)

      setSchedules([])

      const message =
        err.response?.data?.message ||
        '상영 일정을 불러오지 못했습니다.'

      setError(message)
    } finally {
      setScheduleLoading(false)
    }
  }

  const handleScheduleSelect = (
    scheduleId,
  ) => {
    navigate(
      `/schedules/${scheduleId}/seats`,
    )
  }

  const formatTime = (startTime) => {
    if (!startTime) {
      return ''
    }

    return startTime.slice(11, 16)
  }

  const formatPrice = (price) => {
    return price.toLocaleString('ko-KR')
  }

  if (loading) {
    return (
      <section>
        <p>
          예매 정보를 불러오는 중입니다.
        </p>
      </section>
    )
  }

  return (
    <section>
      <div className="page-header">
        <h1>상영 일정</h1>

        {movie && (
          <p>
            {movie.title}
          </p>
        )}
      </div>

      <form
        className="schedule-search-form"
        onSubmit={handleSearch}
      >
        <div className="schedule-field">
          <label htmlFor="theater">
            영화관
          </label>

          <select
            id="theater"
            value={theaterId}
            onChange={(event) =>
              setTheaterId(
                event.target.value,
              )
            }
          >
            <option value="">
              영화관을 선택하세요.
            </option>

            {theaters.map((theater) => (
              <option
                key={theater.theaterId}
                value={theater.theaterId}
              >
                {theater.brand
                  ? `${theater.brand} ${theater.name}`
                  : theater.name}
              </option>
            ))}
          </select>
        </div>

        <div className="schedule-field">
          <label htmlFor="date">
            날짜
          </label>

          <input
            id="date"
            type="date"
            value={date}
            onChange={(event) =>
              setDate(event.target.value)
            }
          />
        </div>

        <button
          type="submit"
          className="schedule-search-button"
          disabled={scheduleLoading}
        >
          {scheduleLoading
            ? '조회 중...'
            : '상영 일정 조회'}
        </button>
      </form>

      {error && (
        <p className="form-error">
          {error}
        </p>
      )}

      {searched &&
        !scheduleLoading &&
        !error && (
          <div className="schedule-result">
            <h2>조회 결과</h2>

            {schedules.length === 0 ? (
              <p>
                해당 조건의 상영 일정이
                없습니다.
              </p>
            ) : (
              <>
                <p className="schedule-count">
                  총 {schedules.length}개의
                  상영 일정이 있습니다.
                </p>

                <div className="schedule-list">
                  {schedules.map(
                    (schedule) => (
                      <button
                        key={
                          schedule.scheduleId
                        }
                        type="button"
                        className="schedule-card"
                        onClick={() =>
                          handleScheduleSelect(
                            schedule.scheduleId,
                          )
                        }
                      >
                        <div className="schedule-card-header">
                          <strong>
                            {
                              schedule.theaterName
                            }
                          </strong>

                          <span>
                            {
                              schedule.screenName
                            }
                          </span>
                        </div>

                        <div className="schedule-time">
                          {formatTime(
                            schedule.startTime,
                          )}
                        </div>

                        <div className="schedule-price">
                          {formatPrice(
                            schedule.basePrice,
                          )}
                          원
                        </div>
                      </button>
                    ),
                  )}
                </div>
              </>
            )}
          </div>
        )}

      <Link
        to={`/movies/${movieId}`}
        className="back-link"
      >
        ← 영화 상세로 돌아가기
      </Link>
    </section>
  )
}

export default SchedulePage