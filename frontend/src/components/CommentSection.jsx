import {
  useCallback,
  useEffect,
  useState,
} from 'react'
import { Link } from 'react-router-dom'
import api from '../api/axios'
import { useAuth } from '../context/AuthContext'
import './comment.css'

function CommentSection({ postId }) {
  const {
    user,
    isLoggedIn,
    authLoading,
  } = useAuth()

  const [comments, setComments] =
    useState([])

  const [loading, setLoading] =
    useState(true)

  const [error, setError] =
    useState('')

  const [content, setContent] =
    useState('')

  const [submitting, setSubmitting] =
    useState(false)

  const [editingId, setEditingId] =
    useState(null)

  const [editContent, setEditContent] =
    useState('')

  const [
    actionLoadingId,
    setActionLoadingId,
  ] = useState(null)

  const fetchComments =
    useCallback(async () => {
      try {
        setLoading(true)
        setError('')

        const response = await api.get(
          `/posts/${postId}/comments`,
        )

        setComments(response.data)
      } catch (err) {
        console.error(err)

        const message =
          err.response?.data?.message ||
          '댓글을 불러오지 못했습니다.'

        setError(message)
      } finally {
        setLoading(false)
      }
    }, [postId])

  useEffect(() => {
    fetchComments()
  }, [fetchComments])

  const handleCreate = async (event) => {
    event.preventDefault()

    if (!isLoggedIn) {
      return
    }

    if (!content.trim()) {
      setError(
        '댓글 내용을 입력해주세요.',
      )
      return
    }

    try {
      setSubmitting(true)
      setError('')

      await api.post(
        `/posts/${postId}/comments`,
        {
          content: content.trim(),
        },
      )

      setContent('')

      await fetchComments()
    } catch (err) {
      console.error(err)

      const message =
        err.response?.data?.message ||
        '댓글 작성 중 오류가 발생했습니다.'

      setError(message)
    } finally {
      setSubmitting(false)
    }
  }

  const startEdit = (comment) => {
    setEditingId(comment.commentId)
    setEditContent(comment.content)
    setError('')
  }

  const cancelEdit = () => {
    setEditingId(null)
    setEditContent('')
  }

  const handleUpdate = async (
    commentId,
  ) => {
    if (!editContent.trim()) {
      setError(
        '댓글 내용을 입력해주세요.',
      )
      return
    }

    try {
      setActionLoadingId(commentId)
      setError('')

      await api.patch(
        `/posts/${postId}/comments/${commentId}`,
        {
          content: editContent.trim(),
        },
      )

      cancelEdit()

      await fetchComments()
    } catch (err) {
      console.error(err)

      const message =
        err.response?.data?.message ||
        '댓글 수정 중 오류가 발생했습니다.'

      setError(message)
    } finally {
      setActionLoadingId(null)
    }
  }

  const handleDelete = async (
    commentId,
  ) => {
    const confirmed = window.confirm(
      '정말 댓글을 삭제하시겠습니까?',
    )

    if (!confirmed) {
      return
    }

    try {
      setActionLoadingId(commentId)
      setError('')

      await api.delete(
        `/posts/${postId}/comments/${commentId}`,
      )

      await fetchComments()
    } catch (err) {
      console.error(err)

      const message =
        err.response?.data?.message ||
        '댓글 삭제 중 오류가 발생했습니다.'

      setError(message)
    } finally {
      setActionLoadingId(null)
    }
  }

  const formatDateTime = (dateTime) => {
    if (!dateTime) {
      return '-'
    }

    return dateTime
      .slice(0, 16)
      .replace('T', ' ')
  }

  const isUpdated = (comment) => {
    if (
      !comment.createdAt ||
      !comment.updatedAt
    ) {
      return false
    }

    return (
      comment.createdAt !==
      comment.updatedAt
    )
  }

  return (
    <section className="comment-section">
      <div className="comment-header">
        <h2>댓글</h2>

        {!loading && (
          <span>
            {comments.length}개
          </span>
        )}
      </div>

      {error && (
        <p className="form-error comment-error">
          {error}
        </p>
      )}

      {loading ? (
        <p>
          댓글을 불러오는 중입니다.
        </p>
      ) : comments.length === 0 ? (
        <div className="comment-empty">
          아직 작성된 댓글이 없습니다.
        </div>
      ) : (
        <div className="comment-list">
          {comments.map((comment) => {
            const isMine =
              user &&
              String(comment.userId) ===
                String(user.userId)

            const isEditing =
              editingId ===
              comment.commentId

            return (
              <article
                key={comment.commentId}
                className="comment-card"
              >
                <div className="comment-card-header">
                  <div>
                    <strong>
                      {comment.nickname}
                    </strong>

                    <span className="comment-date">
                      {formatDateTime(
                        comment.createdAt,
                      )}

                      {isUpdated(comment) &&
                        ' · 수정됨'}
                    </span>
                  </div>
                </div>

                {isEditing ? (
                  <div className="comment-edit-area">
                    <textarea
                      value={editContent}
                      onChange={(event) =>
                        setEditContent(
                          event.target.value,
                        )
                      }
                      maxLength={1000}
                    />

                    <div className="comment-edit-bottom">
                      <span>
                        {editContent.length}
                        {' / 1000'}
                      </span>

                      <div className="comment-edit-actions">
                        <button
                          type="button"
                          className="comment-primary-button"
                          onClick={() =>
                            handleUpdate(
                              comment.commentId,
                            )
                          }
                          disabled={
                            actionLoadingId ===
                            comment.commentId
                          }
                        >
                          {actionLoadingId ===
                          comment.commentId
                            ? '저장 중...'
                            : '저장'}
                        </button>

                        <button
                          type="button"
                          className="comment-secondary-button"
                          onClick={
                            cancelEdit
                          }
                        >
                          취소
                        </button>
                      </div>
                    </div>
                  </div>
                ) : (
                  <>
                    <p className="comment-content">
                      {comment.content}
                    </p>

                    {isMine && (
                      <div className="comment-actions">
                        <button
                          type="button"
                          onClick={() =>
                            startEdit(
                              comment,
                            )
                          }
                        >
                          수정
                        </button>

                        <button
                          type="button"
                          onClick={() =>
                            handleDelete(
                              comment.commentId,
                            )
                          }
                          disabled={
                            actionLoadingId ===
                            comment.commentId
                          }
                        >
                          {actionLoadingId ===
                          comment.commentId
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
        <div className="comment-write-area">
          {isLoggedIn ? (
            <form
              className="comment-form"
              onSubmit={handleCreate}
            >
              <h3>댓글 작성</h3>

              <textarea
                value={content}
                onChange={(event) =>
                  setContent(
                    event.target.value,
                  )
                }
                placeholder="댓글을 입력하세요."
                maxLength={1000}
                required
              />

              <div className="comment-form-bottom">
                <span>
                  {content.length} / 1000
                </span>

                <button
                  type="submit"
                  className="comment-primary-button"
                  disabled={submitting}
                >
                  {submitting
                    ? '작성 중...'
                    : '댓글 작성'}
                </button>
              </div>
            </form>
          ) : (
            <div className="comment-login-message">
              <p>
                댓글을 작성하려면 로그인이
                필요합니다.
              </p>

              <Link
                to="/login"
                className="comment-login-link"
              >
                로그인
              </Link>
            </div>
          )}
        </div>
      )}
    </section>
  )
}

export default CommentSection