import {
  useCallback,
  useEffect,
  useState,
} from 'react'
import {
  Link,
  useNavigate,
} from 'react-router-dom'
import api from '../api/axios'
import { useAuth } from '../context/AuthContext'

function MyReservationPage() {
  const navigate = useNavigate()

  const {
    isLoggedIn,
    authLoading,
  } = useAuth()

  const [reservations, setReservations] =
    useState([])

  const [loading, setLoading] =
    useState(true)

  const [error, setError] =
    useState('')

  const [cancelingId, setCancelingId] =
    useState(null)

  const fetchReservations =
    useCallback(async () => {
      try {
        setLoading(true)
        setError('')

        const response = await api.get(
          '/reservations/me',
        )

        setReservations(response.data)
      } catch (err) {
        console.error(err)

        const message =
          err.response?.data?.message ||
          '예매 정보를 불러오지 못했습니다.'

        setError(message)
      } finally {
        setLoading(false)
      }
    }, [])

  useEffect(() => {
    if (authLoading) {
      return
    }

    if (!isLoggedIn) {
      navigate('/login')
      return
    }

    fetchReservations()
  }, [
    authLoading,
    isLoggedIn,
    navigate,
    fetchReservations,
  ])

  const handleCancel = async (
    reservationId,
  ) => {
    const confirmed = window.confirm(
      '정말 예매를 취소하시겠습니까?',
    )

    if (!confirmed) {
      return
    }

    try {
      setCancelingId(reservationId)
      setError('')

      await api.patch(
        `/reservations/${reservationId}/cancel`,
      )

      alert('예매가 취소되었습니다.')

      await fetchReservations()
    } catch (err) {
      console.error(err)

      const message =
        err.response?.data?.message ||
        '예매 취소 중 오류가 발생했습니다.'

      setError(message)
    } finally {
      setCancelingId(null)
    }
  }

  const formatDateTime = (startTime) => {
    if (!startTime) {
      return '-'
    }

    const date =
      startTime.slice(0, 10)

    const time =
      startTime.slice(11, 16)

    return `${date} ${time}`
  }

  const formatSeats = (seats) => {
    if (!seats || seats.length === 0) {
      return '좌석 정보 없음'
    }

    return seats
      .map(
        (seat) =>
          `${seat.seatRow}${seat.seatNumber}`,
      )
      .join(', ')
  }

  const formatPrice = (price) => {
    if (price == null) {
      return '0'
    }

    return price.toLocaleString('ko-KR')
  }

  if (authLoading || loading) {
    return (
      <section>
        <h1>내 예매</h1>

        <p>
          예매 정보를 불러오는 중입니다.
        </p>
      </section>
    )
  }

  return (
    <section>
      <div className="page-header">
        <h1>내 예매</h1>

        <p>
          예매한 영화와 좌석을 확인할 수
          있습니다.
        </p>
      </div>

      {error && (
        <p className="form-error reservation-error">
          {error}
        </p>
      )}

      {reservations.length === 0 ? (
        <div className="empty-reservation">
          <p>
            아직 예매 내역이 없습니다.
          </p>

          <Link
            to="/"
            className="reservation-movie-link"
          >
            영화 보러가기
          </Link>
        </div>
      ) : (
        <>
          <p className="reservation-count">
            총 {reservations.length}개의
            예매가 있습니다.
          </p>

          <div className="reservation-list">
            {reservations.map(
              (reservation) => {
                const isCanceled =
                  reservation.status ===
                  'CANCELED'

                return (
                  <article
                    key={
                      reservation.reservationId
                    }
                    className={`reservation-card ${
                      isCanceled
                        ? 'reservation-card-canceled'
                        : ''
                    }`}
                  >
                    <div className="reservation-card-top">
                      <div>
                        <h2>
                          {
                            reservation.movieTitle
                          }
                        </h2>

                        <span
                          className={`reservation-status ${
                            isCanceled
                              ? 'reservation-status-canceled'
                              : 'reservation-status-reserved'
                          }`}
                        >
                          {isCanceled
                            ? '예매 취소'
                            : '예매 완료'}
                        </span>
                      </div>

                      <span className="reservation-number">
                        예매 #
                        {
                          reservation.reservationId
                        }
                      </span>
                    </div>

                    <div className="reservation-detail-grid">
                      <div className="reservation-detail-item">
                        <span className="reservation-detail-label">
                          영화관
                        </span>

                        <strong>
                          {
                            reservation.theaterName
                          }
                        </strong>
                      </div>

                      <div className="reservation-detail-item">
                        <span className="reservation-detail-label">
                          상영관
                        </span>

                        <strong>
                          {
                            reservation.screenName
                          }
                        </strong>
                      </div>

                      <div className="reservation-detail-item">
                        <span className="reservation-detail-label">
                          상영 시간
                        </span>

                        <strong>
                          {formatDateTime(
                            reservation.startTime,
                          )}
                        </strong>
                      </div>

                      <div className="reservation-detail-item">
                        <span className="reservation-detail-label">
                          좌석
                        </span>

                        <strong>
                          {formatSeats(
                            reservation.seats,
                          )}
                        </strong>
                      </div>
                    </div>

                    <div className="reservation-card-bottom">
                      <div className="reservation-total">
                        <span>
                          총 결제 금액
                        </span>

                        <strong>
                          {formatPrice(
                            reservation.totalPrice,
                          )}
                          원
                        </strong>
                      </div>

                      {!isCanceled && (
                        <button
                          type="button"
                          className="reservation-cancel-button"
                          onClick={() =>
                            handleCancel(
                              reservation.reservationId,
                            )
                          }
                          disabled={
                            cancelingId ===
                            reservation.reservationId
                          }
                        >
                          {cancelingId ===
                          reservation.reservationId
                            ? '취소 처리 중...'
                            : '예매 취소'}
                        </button>
                      )}
                    </div>
                  </article>
                )
              },
            )}
          </div>
        </>
      )}
    </section>
  )
}

export default MyReservationPage