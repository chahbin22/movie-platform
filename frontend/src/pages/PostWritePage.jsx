import {
  useEffect,
  useState,
} from 'react'
import {
  Link,
  useNavigate,
} from 'react-router-dom'
import api from '../api/axios'
import { useAuth } from '../context/AuthContext'

function PostWritePage() {
  const navigate = useNavigate()

  const {
    isLoggedIn,
    authLoading,
  } = useAuth()

  const [title, setTitle] =
    useState('')

  const [content, setContent] =
    useState('')

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
    }
  }, [
    authLoading,
    isLoggedIn,
    navigate,
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

      const response = await api.post(
        '/posts',
        {
          title: title.trim(),
          content: content.trim(),
        },
      )

      alert('게시글이 작성되었습니다.')

      navigate(
        `/posts/${response.data.postId}`,
      )
    } catch (err) {
      console.error(err)

      const message =
        err.response?.data?.message ||
        '게시글 작성 중 오류가 발생했습니다.'

      setError(message)
    } finally {
      setSubmitting(false)
    }
  }

  if (authLoading) {
    return (
      <section>
        <p>로그인 정보를 확인하는 중입니다.</p>
      </section>
    )
  }

  return (
    <section className="post-form-page">
      <div className="page-header">
        <h1>글쓰기</h1>

        <p>
          커뮤니티에 새로운 글을
          작성합니다.
        </p>
      </div>

      {error && (
        <p className="form-error post-form-error">
          {error}
        </p>
      )}

      <form
        className="post-form"
        onSubmit={handleSubmit}
      >
        <div className="post-form-field">
          <label htmlFor="post-title">
            제목
          </label>

          <input
            id="post-title"
            type="text"
            value={title}
            onChange={(event) =>
              setTitle(event.target.value)
            }
            maxLength={200}
            placeholder="제목을 입력하세요."
            required
          />

          <span className="post-form-count">
            {title.length} / 200
          </span>
        </div>

        <div className="post-form-field">
          <label htmlFor="post-content">
            내용
          </label>

          <textarea
            id="post-content"
            value={content}
            onChange={(event) =>
              setContent(event.target.value)
            }
            maxLength={5000}
            placeholder="내용을 입력하세요."
            required
          />

          <span className="post-form-count">
            {content.length} / 5000
          </span>
        </div>

        <div className="post-form-actions">
          <Link
            to="/posts"
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
              ? '작성 중...'
              : '작성'}
          </button>
        </div>
      </form>
    </section>
  )
}

export default PostWritePage