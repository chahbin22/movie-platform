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
import CommentSection from '../components/CommentSection'

function PostDetailPage() {
  const { postId } = useParams()
  const navigate = useNavigate()

  const {
    user,
    authLoading,
  } = useAuth()

  const [post, setPost] =
    useState(null)

  const [loading, setLoading] =
    useState(true)

  const [deleting, setDeleting] =
    useState(false)

  const [error, setError] =
    useState('')

  useEffect(() => {
    const fetchPost = async () => {
      try {
        setLoading(true)
        setError('')

        const response = await api.get(
          `/posts/${postId}`,
        )

        setPost(response.data)
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
  }, [postId])

  const isMine =
    !authLoading &&
    user &&
    post &&
    String(user.userId) ===
      String(post.userId)

  const handleDelete = async () => {
    const confirmed =
      window.confirm(
        '정말 게시글을 삭제하시겠습니까?',
      )

    if (!confirmed) {
      return
    }

    try {
      setDeleting(true)
      setError('')

      await api.delete(
        `/posts/${postId}`,
      )

      alert('게시글이 삭제되었습니다.')

      navigate('/posts')
    } catch (err) {
      console.error(err)

      const message =
        err.response?.data?.message ||
        '게시글 삭제 중 오류가 발생했습니다.'

      setError(message)
    } finally {
      setDeleting(false)
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

  if (loading) {
    return (
      <section>
        <p>
          게시글을 불러오는 중입니다.
        </p>
      </section>
    )
  }

  if (error && !post) {
    return (
      <section>
        <p className="form-error">
          {error}
        </p>

        <Link
          to="/posts"
          className="back-link"
        >
          ← 목록으로
        </Link>
      </section>
    )
  }

  if (!post) {
    return null
  }

  return (
    <section>
      <article className="post-detail">
        <div className="post-detail-header">
          <h1>
            {post.title}
          </h1>

          <div className="post-detail-meta">
            <span>
              {post.nickname}
            </span>

            <span>
              {formatDateTime(
                post.createdAt,
              )}
            </span>

            <span>
              조회 {post.viewCount}
            </span>
          </div>
        </div>

        {error && (
          <p className="form-error post-detail-error">
            {error}
          </p>
        )}

        <div className="post-detail-content">
          {post.content}
        </div>

        <div className="post-detail-actions">
          <Link
            to="/posts"
            className="community-secondary-button"
          >
            목록
          </Link>

          {isMine && (
            <div className="post-owner-actions">
              <Link
                to={`/posts/${postId}/edit`}
                className="community-secondary-button"
              >
                수정
              </Link>

              <button
                type="button"
                className="community-danger-button"
                onClick={handleDelete}
                disabled={deleting}
              >
                {deleting
                  ? '삭제 중...'
                  : '삭제'}
              </button>
            </div>
          )}
        </div>
      </article>

      <CommentSection
        postId={postId}
      />
    </section>
  )
}

export default PostDetailPage