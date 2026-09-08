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

function PostEditPage() {
  const { postId } = useParams()
  const navigate = useNavigate()

  const {
    user,
    isLoggedIn,
    authLoading,
  } = useAuth()

  const [title, setTitle] =
    useState('')

  const [content, setContent] =
    useState('')

  const [loading, setLoading] =
    useState(true)

  const [submitting, setSubmitting] =
    useState(false)

  const [error, setError] =
    useState('')

  useEffect(() => {
    if (authLoading) {
      return
    }

    if (!isLoggedIn) {
      navigate('/login')
      return
    }

    const fetchPost = async () => {
      try {
        setLoading(true)
        setError('')

        const response = await api.get(
          `/posts/${postId}`,
        )

        const post = response.data

        if (
          String(post.userId) !==
          String(user.userId)
        ) {
          setError(
            '본인의 게시글만 수정할 수 있습니다.',
          )

          return
        }

        setTitle(post.title)
        setContent(post.content)
      } catch (err) {
        console.error(err)

        const message =
          err.response?.data?.message ||
          '게시글을 불러오지 못했습니다.'

        setError(message)
      } finally {
        setLoading(false)
      }
    }

    fetchPost()
  }, [
    authLoading,
    isLoggedIn,
    navigate,
    postId,
    user,
  ])

  const handleSubmit = async (event) => {
    event.preventDefault()

    if (!title.trim()) {
      setError(
        '제목을 입력해주세요.',
      )
      return
    }

    if (!content.trim()) {
      setError(
        '내용을 입력해주세요.',
      )
      return
    }

    try {
      setSubmitting(true)
      setError('')

      await api.patch(
        `/posts/${postId}`,
        {
          title: title.trim(),
          content: content.trim(),
        },
      )

      alert('게시글이 수정되었습니다.')

      navigate(`/posts/${postId}`)
    } catch (err) {
      console.error(err)

      const message =
        err.response?.data?.message ||
        '게시글 수정 중 오류가 발생했습니다.'

      setError(message)
    } finally {
      setSubmitting(false)
    }
  }

  if (authLoading || loading) {
    return (
      <section>
        <p>게시글을 불러오는 중입니다.</p>
      </section>
    )
  }

  return (
    <section className="post-form-page">
      <div className="page-header">
        <h1>게시글 수정</h1>
      </div>

      {error && (
        <p className="form-error post-form-error">
          {error}
        </p>
      )}

      {title && (
        <form
          className="post-form"
          onSubmit={handleSubmit}
        >
          <div className="post-form-field">
            <label htmlFor="edit-title">
              제목
            </label>

            <input
              id="edit-title"
              type="text"
              value={title}
              onChange={(event) =>
                setTitle(
                  event.target.value,
                )
              }
              maxLength={200}
              required
            />

            <span className="post-form-count">
              {title.length} / 200
            </span>
          </div>

          <div className="post-form-field">
            <label htmlFor="edit-content">
              내용
            </label>

            <textarea
              id="edit-content"
              value={content}
              onChange={(event) =>
                setContent(
                  event.target.value,
                )
              }
              maxLength={5000}
              required
            />

            <span className="post-form-count">
              {content.length} / 5000
            </span>
          </div>

          <div className="post-form-actions">
            <Link
              to={`/posts/${postId}`}
              className="community-secondary-button"
            >
              취소
            </Link>

            <button
              type="submit"
              className="community-primary-button"
              disabled={submitting}
            >
              {submitting
                ? '저장 중...'
                : '저장'}
            </button>
          </div>
        </form>
      )}
    </section>
  )
}

export default PostEditPage