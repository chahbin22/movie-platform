import {
  useCallback,
  useEffect,
  useState,
} from 'react'
import { Link } from 'react-router-dom'
import api from '../api/axios'
import { useAuth } from '../context/AuthContext'
import './review.css'

function ReviewSection({ movieId }) {
  const {
    user,
    isLoggedIn,
    authLoading,
  } = useAuth()

  const [reviews, setReviews] =
    useState([])

  const [loading, setLoading] =
    useState(true)

  const [error, setError] =
    useState('')

  const [rating, setRating] =
    useState(5)

  const [content, setContent] =
    useState('')

  const [submitting, setSubmitting] =
    useState(false)

  const [editingId, setEditingId] =
    useState(null)

  const [editRating, setEditRating] =
    useState(5)

  const [editContent, setEditContent] =
    useState('')

  const [actionLoadingId, setActionLoadingId] =
    useState(null)

  const fetchReviews =
    useCallback(async () => {
      try {
        setLoading(true)
        setError('')

        const response = await api.get(
          `/movies/${movieId}/reviews`,
        )

        setReviews(response.data)
      } catch (err) {
        console.error(err)

        const message =
          err.response?.data?.message ||
          '리뷰를 불러오지 못했습니다.'

        setError(message)
      } finally {
        setLoading(false)
      }
    }, [movieId])

  useEffect(() => {
    fetchReviews()
  }, [fetchReviews])

  const myReview = reviews.find(
    (review) =>
      user &&
      String(review.userId) ===
        String(user.userId),
  )

  const handleCreate = async (event) => {
    event.preventDefault()

    if (!isLoggedIn) {
      return
    }

    if (!content.trim()) {
      setError(
        '리뷰 내용을 입력해주세요.',
      )
      return
    }

    try {
      setSubmitting(true)
      setError('')

      await api.post(
        `/movies/${movieId}/reviews`,
        {
          rating: Number(rating),
          content: content.trim(),
        },
      )

      setRating(5)
      setContent('')

      await fetchReviews()
    } catch (err) {
      console.error(err)

      const message =
        err.response?.data?.message ||
        '리뷰 작성 중 오류가 발생했습니다.'

      setError(message)
    } finally {
      setSubmitting(false)
    }
  }

  const startEdit = (review) => {
    setEditingId(review.reviewId)
    setEditRating(review.rating)
    setEditContent(review.content)
    setError('')
  }

  const cancelEdit = () => {
    setEditingId(null)
    setEditRating(5)
    setEditContent('')
  }

  const handleUpdate = async (
    reviewId,
  ) => {
    if (!editContent.trim()) {
      setError(
        '리뷰 내용을 입력해주세요.',
      )
      return
    }

    try {
      setActionLoadingId(reviewId)
      setError('')

      await api.patch(
        `/movies/${movieId}/reviews/${reviewId}`,
        {
          rating: Number(editRating),
          content: editContent.trim(),
        },
      )

      cancelEdit()

      await fetchReviews()
    } catch (err) {
      console.error(err)

      const message =
        err.response?.data?.message ||
        '리뷰 수정 중 오류가 발생했습니다.'

      setError(message)
    } finally {
      setActionLoadingId(null)
    }
  }

  const handleDelete = async (
    reviewId,
  ) => {
    const confirmed = window.confirm(
      '정말 리뷰를 삭제하시겠습니까?',
    )

    if (!confirmed) {
      return
    }

    try {
      setActionLoadingId(reviewId)
      setError('')

      await api.delete(
        `/movies/${movieId}/reviews/${reviewId}`,
      )

      await fetchReviews()
    } catch (err) {
      console.error(err)

      const message =
        err.response?.data?.message ||
        '리뷰 삭제 중 오류가 발생했습니다.'

      setError(message)
    } finally {
      setActionLoadingId(null)
    }
  }

  const formatDate = (dateTime) => {
    if (!dateTime) {
      return ''
    }

    return dateTime.slice(0, 10)
  }

  const renderStars = (value) => {
    return (
      '★'.repeat(value) +
      '☆'.repeat(5 - value)
    )
  }

  return (
    <section className="review-section">
      <div className="review-header">
        <div>
          <h2>리뷰</h2>

          <p>
            이 영화에 대한 감상을
            확인해보세요.
          </p>
        </div>

        {!loading && (
          <span className="review-count">
            {reviews.length}개
          </span>
        )}
      </div>

      {error && (
        <p className="form-error review-error">
          {error}
        </p>
      )}

      {loading ? (
        <p>리뷰를 불러오는 중입니다.</p>
      ) : reviews.length === 0 ? (
        <div className="review-empty">
          아직 작성된 리뷰가 없습니다.
        </div>
      ) : (
        <div className="review-list">
          {reviews.map((review) => {
            const isMine =
              user &&
              String(review.userId) ===
                String(user.userId)

            const isEditing =
              editingId ===
              review.reviewId

            return (
              <article
                key={review.reviewId}
                className="review-card"
              >
                <div className="review-card-header">
                  <div>
                    <strong className="review-nickname">
                      {review.nickname}
                    </strong>

                    <div className="review-stars">
                      {renderStars(
                        review.rating,
                      )}
                    </div>
                  </div>

                  <span className="review-date">
                    {formatDate(
                      review.createdAt,
                    )}
                  </span>
                </div>

                {isEditing ? (
                  <div className="review-edit-form">
                    <div className="review-rating-field">
                      <label
                        htmlFor={`edit-rating-${review.reviewId}`}
                      >
                        평점
                      </label>

                      <select
                        id={`edit-rating-${review.reviewId}`}
                        value={editRating}
                        onChange={(event) =>
                          setEditRating(
                            Number(
                              event.target
                                .value,
                            ),
                          )
                        }
                      >
                        <option value={5}>
                          5점
                        </option>
                        <option value={4}>
                          4점
                        </option>
                        <option value={3}>
                          3점
                        </option>
                        <option value={2}>
                          2점
                        </option>
                        <option value={1}>
                          1점
                        </option>
                      </select>
                    </div>

                    <textarea
                      value={editContent}
                      onChange={(event) =>
                        setEditContent(
                          event.target.value,
                        )
                      }
                      maxLength={2000}
                    />

                    <div className="review-edit-actions">
                      <button
                        type="button"
                        className="review-primary-button"
                        onClick={() =>
                          handleUpdate(
                            review.reviewId,
                          )
                        }
                        disabled={
                          actionLoadingId ===
                          review.reviewId
                        }
                      >
                        {actionLoadingId ===
                        review.reviewId
                          ? '저장 중...'
                          : '저장'}
                      </button>

                      <button
                        type="button"
                        className="review-secondary-button"
                        onClick={
                          cancelEdit
                        }
                      >
                        취소
                      </button>
                    </div>
                  </div>
                ) : (
                  <>
                    <p className="review-content">
                      {review.content}
                    </p>

                    {isMine && (
                      <div className="review-actions">
                        <button
                          type="button"
                          onClick={() =>
                            startEdit(
                              review,
                            )
                          }
                        >
                          수정
                        </button>

                        <button
                          type="button"
                          onClick={() =>
                            handleDelete(
                              review.reviewId,
                            )
                          }
                          disabled={
                            actionLoadingId ===
                            review.reviewId
                          }
                        >
                          {actionLoadingId ===
                          review.reviewId
                            ? '처리 중...'
                            : '삭제'}
                        </button>
                      </div>
                    )}
                  </>
                )}
              </article>
            )
          })}
        </div>
      )}

      {!authLoading && (
        <div className="review-write-area">
          {!isLoggedIn ? (
            <div className="review-login-message">
              <p>
                리뷰를 작성하려면
                로그인이 필요합니다.
              </p>

              <Link
                to="/login"
                className="review-login-link"
              >
                로그인
              </Link>
            </div>
          ) : myReview ? (
            <p className="review-my-message">
              이미 이 영화에 리뷰를
              작성했습니다. 작성한 리뷰에서
              수정 또는 삭제할 수 있습니다.
            </p>
          ) : (
            <form
              className="review-form"
              onSubmit={handleCreate}
            >
              <h3>리뷰 작성</h3>

              <div className="review-rating-field">
                <label htmlFor="review-rating">
                  평점
                </label>

                <select
                  id="review-rating"
                  value={rating}
                  onChange={(event) =>
                    setRating(
                      Number(
                        event.target.value,
                      ),
                    )
                  }
                >
                  <option value={5}>
                    5점
                  </option>
                  <option value={4}>
                    4점
                  </option>
                  <option value={3}>
                    3점
                  </option>
                  <option value={2}>
                    2점
                  </option>
                  <option value={1}>
                    1점
                  </option>
                </select>
              </div>

              <textarea
                value={content}
                onChange={(event) =>
                  setContent(
                    event.target.value,
                  )
                }
                placeholder="영화에 대한 감상을 작성해주세요."
                maxLength={2000}
                required
              />

              <div className="review-form-bottom">
                <span>
                  {content.length} / 2000
                </span>

                <button
                  type="submit"
                  className="review-primary-button"
                  disabled={submitting}
                >
                  {submitting
                    ? '작성 중...'
                    : '리뷰 작성'}
                </button>
              </div>
            </form>
          )}
        </div>
      )}
    </section>
  )
}

export default ReviewSection