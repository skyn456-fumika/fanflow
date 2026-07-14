import { useEffect, useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { getSubscriptionFeed } from '../../api/feedApi'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || ''

const getImageUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return `${API_BASE_URL}${url}`
}

function FeedPostCard({ post }) {
  const channelPath = post.channelSlug
    ? `/channels/${post.channelSlug}`
    : '/channels'

  const boardPath = post.channelSlug
    ? `/channels/${post.channelSlug}/posts?boardCode=${post.boardCode}`
    : '/posts'

  return (
    <article className="home-post-card">
      {post.thumbnailUrl && (
        <Link to={`/posts/${post.postId}`} className="home-post-thumbnail">
          <img src={getImageUrl(post.thumbnailUrl)} alt="" />
        </Link>
      )}

      <div className="home-post-card-top">
        <div className="home-post-path">
          <Link to={channelPath}>{post.channelName || '채널'}</Link>
          <span>&gt;</span>
          <Link to={boardPath}>{post.boardName}</Link>
        </div>

        {post.notice && <span className="notice-badge">공지</span>}
      </div>

      <Link to={`/posts/${post.postId}`} className="home-post-title-link">
        <strong>{post.title}</strong>
      </Link>

      <div className="post-meta">
        <span>{post.writerNickname}</span>
        <span>조회 {post.viewCount}</span>
        <span>좋아요 {post.likeCount}</span>
        <span>댓글 {post.commentCount}</span>
      </div>
    </article>
  )
}

function FeedPage() {
  const navigate = useNavigate()
  const location = useLocation()

  const [posts, setPosts] = useState([])
  const [pageInfo, setPageInfo] = useState(null)
  const [page, setPage] = useState(0)

  const [loading, setLoading] = useState(false)
  const [errorMessage, setErrorMessage] = useState('')

  const loadFeed = async () => {
    const token = localStorage.getItem('accessToken')

    if (!token) {
      alert('로그인이 필요합니다.')
      navigate('/login', {
        replace: true,
        state: {
          from: location.pathname,
        },
      })
      return
    }

    try {
      setLoading(true)
      setErrorMessage('')

      const result = await getSubscriptionFeed({
        page,
        size: 10,
      })

      if (result.success) {
        setPosts(result.data.content)
        setPageInfo(result.data)
      } else {
        setErrorMessage(result.message || '구독 피드를 불러오지 못했습니다.')
      }
    } catch (error) {
      console.error(error)

      if (error.response?.status === 401) {
        localStorage.removeItem('accessToken')
        alert('로그인이 필요합니다.')
        navigate('/login', {
          replace: true,
          state: {
            from: location.pathname,
          },
        })
        return
      }

      setErrorMessage('구독 피드를 불러오지 못했습니다.')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadFeed()
  }, [page])

  if (loading) {
    return <p>불러오는 중...</p>
  }

  return (
    <div>
      <div className="page-title-row">
        <div>
          <h1>구독 피드</h1>
          <p>내가 구독한 채널의 최신 게시글을 모아봅니다.</p>
        </div>

        <Link to="/channels" className="secondary-button">
          채널 둘러보기
        </Link>
      </div>

      {errorMessage && <p className="error-message">{errorMessage}</p>}

      {posts.length === 0 ? (
        <div className="empty-box">
          구독한 채널의 게시글이 없습니다. 관심 있는 채널을 구독해보세요.
        </div>
      ) : (
        <div className="feed-post-list">
          {posts.map((post) => (
            <FeedPostCard key={post.postId} post={post} />
          ))}
        </div>
      )}

      {pageInfo && pageInfo.totalPages > 1 && (
        <div className="pagination">
          <button
            type="button"
            disabled={pageInfo.first}
            onClick={() => setPage((prev) => prev - 1)}
          >
            이전
          </button>

          <span>
            {pageInfo.page + 1} / {pageInfo.totalPages}
          </span>

          <button
            type="button"
            disabled={pageInfo.last}
            onClick={() => setPage((prev) => prev + 1)}
          >
            다음
          </button>
        </div>
      )}
    </div>
  )
}

export default FeedPage