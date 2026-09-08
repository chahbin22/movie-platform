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
import { useAuth } from '../context/AuthContext'

function SeatPage() {
  const { scheduleId } = useParams()
  const navigate = useNavigate()

  const { isLoggedIn } = useAuth()

  const [seats, setSeats] =
    useState([])

  const [selectedSeatIds, setSelectedSeatIds] =
    useState([])

  const [loading, setLoading] =
    useState(true)

  const [reservationLoading, setReservationLoading] =
    useState(false)

  const [error, setError] =
    useState('')

  const fetchSeats = async () => {
    try {
      setLoading(true)
      setError('')

      const response = await api.get(
        `/schedules/${scheduleId}/seats`,
      )

      setSeats(response.data)
    } catch (err) {
      console.error(err)

      const message =
        err.response?.data?.message ||
        '좌석 정보를 불러오지 못했습니다.'

      setError(message)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchSeats()
  }, [scheduleId])

  const handleSeatClick = (seat) => {
    if (seat.status !== 'AVAILABLE') {
      return
    }

    setSelectedSeatIds((current) => {
      if (
        current.includes(
          seat.scheduleSeatId,
        )
      ) {
        return current.filter(
          (id) =>
            id !== seat.scheduleSeatId,
        )
      }

      return [
        ...current,
        seat.scheduleSeatId,
      ]
    })
  }

  const selectedSeats = seats.filter(
    (seat) =>
      selectedSeatIds.includes(
        seat.scheduleSeatId,
      ),
  )

  const totalPrice =
    selectedSeats.reduce(
      (sum, seat) =>
        sum + seat.price,
      0,
    )

  const seatRows = seats.reduce(
    (rows, seat) => {
      if (!rows[seat.seatRow]) {
        rows[seat.seatRow] = []
      }

      rows[seat.seatRow].push(seat)

      return rows
    },
    {},
  )

  Object.values(seatRows).forEach(
    (rowSeats) => {
      rowSeats.sort(
        (a, b) =>
          a.seatNumber -
          b.seatNumber,
      )
    },
  )

  const getSeatClassName = (seat) => {
    if (seat.status !== 'AVAILABLE') {
      return 'seat seat-reserved'
    }

    if (
      selectedSeatIds.includes(
        seat.scheduleSeatId,
      )
    ) {
      return 'seat seat-selected'
    }

    return 'seat seat-available'
  }

  const handleReservation = async () => {
    if (!isLoggedIn) {
      alert(
        '로그인이 필요한 기능입니다.',
      )

      navigate('/login')
      return
    }

    if (selectedSeatIds.length === 0) {
      alert('좌석을 선택해주세요.')
      return
    }

    try {
      setReservationLoading(true)
      setError('')

      await api.post('/reservations', {
        scheduleId: Number(scheduleId),
        scheduleSeatIds:
          selectedSeatIds,
      })

      alert('예매가 완료되었습니다.')

      setSelectedSeatIds([])

      await fetchSeats()
    } catch (err) {
      console.error(err)

      const message =
        err.response?.data?.message ||
        '예매 중 오류가 발생했습니다.'

      setError(message)
    } finally {
      setReservationLoading(false)
    }
  }

  if (loading) {
    return (
      <section>
        <h1>좌석 선택</h1>

        <p>
          좌석 정보를 불러오는 중입니다.
        </p>
      </section>
    )
  }

  if (error && seats.length === 0) {
    return (
      <section>
        <h1>좌석 선택</h1>

        <p className="form-error">
          {error}
        </p>
      </section>
    )
  }

  return (
    <section>
      <div className="page-header">
        <h1>좌석 선택</h1>

        <p>
          상영 일정 #{scheduleId}
        </p>
      </div>

      {error && (
        <p className="form-error seat-error">
          {error}
        </p>
      )}

      {seats.length === 0 ? (
        <div className="empty-seat-message">
          <p>
            등록된 좌석 정보가 없습니다.
          </p>
        </div>
      ) : (
        <>
          <div className="seat-container">
            <div className="screen">
              SCREEN
            </div>

            <div className="seat-map">
              {Object.entries(
                seatRows,
              ).map(
                ([
                  rowName,
                  rowSeats,
                ]) => (
                  <div
                    key={rowName}
                    className="seat-row"
                  >
                    <span className="seat-row-name">
                      {rowName}
                    </span>

                    <div className="seat-row-seats">
                      {rowSeats.map(
                        (seat) => (
                          <button
                            key={
                              seat.scheduleSeatId
                            }
                            type="button"
                            className={
                              getSeatClassName(
                                seat,
                              )
                            }
                            disabled={
                              seat.status !==
                              'AVAILABLE'
                            }
                            onClick={() =>
                              handleSeatClick(
                                seat,
                              )
                            }
                            title={`${seat.seatRow}${seat.seatNumber} / ${seat.price.toLocaleString('ko-KR')}원`}
                          >
                            {
                              seat.seatNumber
                            }
                          </button>
                        ),
                      )}
                    </div>
                  </div>
                ),
              )}
            </div>

            <div className="seat-legend">
              <div>
                <span className="legend-box legend-available" />
                선택 가능
              </div>

              <div>
                <span className="legend-box legend-selected" />
                선택됨
              </div>

              <div>
                <span className="legend-box legend-reserved" />
                예약 완료
              </div>
            </div>
          </div>

          <div className="reservation-summary">
            <div>
              <h2>예매 정보</h2>

              <p>
                선택 좌석:{' '}
                <strong>
                  {selectedSeats.length === 0
                    ? '없음'
                    : selectedSeats
                        .map(
                          (seat) =>
                            `${seat.seatRow}${seat.seatNumber}`,
                        )
                        .join(', ')}
                </strong>
              </p>

              <p>
                선택 좌석 수:{' '}
                <strong>
                  {
                    selectedSeats.length
                  }개
                </strong>
              </p>

              <p className="total-price">
                총 금액:{' '}
                <strong>
                  {totalPrice.toLocaleString(
                    'ko-KR',
                  )}
                  원
                </strong>
              </p>
            </div>

            <button
              type="button"
              className="reservation-submit-button"
              onClick={handleReservation}
              disabled={
                reservationLoading ||
                selectedSeatIds.length ===
                  0
              }
            >
              {reservationLoading
                ? '예매 처리 중...'
                : '예매하기'}
            </button>
          </div>
        </>
      )}

      <Link
        to="/"
        className="back-link"
      >
        ← 영화 목록으로 돌아가기
      </Link>
    </section>
  )
}

export default SeatPage