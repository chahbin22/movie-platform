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

function PostListPage() {
  const navigate = useNavigate()
  const { isLoggedIn } = useAuth()

  const [posts, setPosts] =
    useState([])

  const [loading, setLoading] =
    useState(true)

  const [error, setError] =
    useState('')

  useEffect(() => {
    const fetchPosts = async () => {
      try {
        setLoading(true)
        setError('')

        const response =
          await api.get('/posts')

        setPosts(response.data)
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

    fetchPosts()
  }, [])

  const handleWrite = () => {
    if (!isLoggedIn) {
      alert(
        '글을 작성하려면 로그인이 필요합니다.',
      )

      navigate('/login')
      return
    }

    navigate('/posts/new')
  }

  const formatDate = (dateTime) => {
    if (!dateTime) {
      return '-'
    }

    return dateTime.slice(0, 10)
  }

  if (loading) {
    return (
      <section>
        <h1>커뮤니티</h1>
        <p>게시글을 불러오는 중입니다.</p>
      </section>
    )
  }

  return (
    <section>
      <div className="community-header">
        <div>
          <h1>커뮤니티</h1>

          <p>
            영화에 대한 다양한 이야기를
            나눠보세요.
          </p>
        </div>

        <button
          type="button"
          className="community-primary-button"
          onClick={handleWrite}
        >
          글쓰기
        </button>
      </div>

      {error && (
        <p className="form-error">
          {error}
        </p>
      )}

      {posts.length === 0 ? (
        <div className="community-empty">
          아직 작성된 게시글이 없습니다.
        </div>
      ) : (
        <>
          <p className="community-count">
            총 {posts.length}개의 게시글이
            있습니다.
          </p>

          <div className="post-list">
            {posts.map((post) => (
              <Link
                key={post.postId}
                to={`/posts/${post.postId}`}
                className="post-list-item"
              >
                <div className="post-list-main">
                  <h2>{post.title}</h2>

                  <div className="post-list-meta">
                    <span>
                      {post.nickname}
                    </span>

                    <span>
                      {formatDate(
                        post.createdAt,
                      )}
                    </span>
                  </div>
                </div>

                <div className="post-view-count">
                  조회 {post.viewCount}
                </div>
              </Link>
            ))}
          </div>
        </>
      )}
    </section>
  )
}

export default PostListPage