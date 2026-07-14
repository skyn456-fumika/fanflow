import { useEffect, useState } from 'react'
import {
  Link,
  useLocation,
  useNavigate,
  useSearchParams,
} from 'react-router-dom'
import { getSubscriptionFeed } from '../../api/feedApi'
import { getMySubscribedChannels } from '../../api/channelApi'

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
  const [searchParams, setSearchParams] = useSearchParams()

  const initialPage = Math.max(
    Number.parseInt(searchParams.get('page') || '0', 10) || 0,
    0,
  )

  const initialChannelSlug = searchParams.get('channelSlug') || ''
  const initialSort =
    searchParams.get('sort') === 'popular' ? 'popular' : 'latest'

  const [posts, setPosts] = useState([])
  const [pageInfo, setPageInfo] = useState(null)

  const [subscribedChannels, setSubscribedChannels] = useState([])

  const [page, setPage] = useState(initialPage)
  const [channelSlug, setChannelSlug] = useState(initialChannelSlug)
  const [sort, setSort] = useState(initialSort)

  const [loading, setLoading] = useState(false)
  const [channelLoading, setChannelLoading] = useState(false)
  const [errorMessage, setErrorMessage] = useState('')

  const requireLogin = () => {
    localStorage.removeItem('accessToken')

    alert('로그인이 필요합니다.')

    navigate('/login', {
      replace: true,
      state: {
        from: `${location.pathname}${location.search}`,
      },
    })
  }

  const updateUrl = ({
    nextPage = page,
    nextChannelSlug = channelSlug,
    nextSort = sort,
  }) => {
    const params = {}

    if (nextPage > 0) {
      params.page = String(nextPage)
    }

    if (nextChannelSlug) {
      params.channelSlug = nextChannelSlug
    }

    if (nextSort !== 'latest') {
      params.sort = nextSort
    }

    setSearchParams(params, {
      replace: true,
    })
  }

  const loadSubscribedChannels = async () => {
    try {
      setChannelLoading(true)

      const result = await getMySubscribedChannels()

      if (result.success) {
        setSubscribedChannels(result.data || [])
      }
    } catch (error) {
      console.error(error)

      if (error.response?.status === 401) {
        requireLogin()
      }
    } finally {
      setChannelLoading(false)
    }
  }

  const loadFeed = async () => {
    const token = localStorage.getItem('accessToken')

    if (!token) {
      requireLogin()
      return
    }

    try {
      setLoading(true)
      setErrorMessage('')

      const result = await getSubscriptionFeed({
        page,
        size: 10,
        channelSlug,
        sort,
      })

      if (result.success) {
        setPosts(result.data.content || [])
        setPageInfo(result.data)
      } else {
        setErrorMessage(
          result.message || '구독 피드를 불러오지 못했습니다.',
        )
      }
    } catch (error) {
      console.error(error)

      if (error.response?.status === 401) {
        requireLogin()
        return
      }

      setErrorMessage('구독 피드를 불러오지 못했습니다.')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    const token = localStorage.getItem('accessToken')

    if (!token) {
      requireLogin()
      return
    }

    loadSubscribedChannels()
  }, [])

  useEffect(() => {
    updateUrl({
      nextPage: page,
      nextChannelSlug: channelSlug,
      nextSort: sort,
    })

    loadFeed()
  }, [page, channelSlug, sort])

  const handleChannelChange = (e) => {
    setChannelSlug(e.target.value)
    setPage(0)
  }

  const handleSortChange = (e) => {
    setSort(e.target.value)
    setPage(0)
  }

  const handlePreviousPage = () => {
    setPage((prev) => Math.max(prev - 1, 0))
  }

  const handleNextPage = () => {
    setPage((prev) => prev + 1)
  }

  return (
    <div>
      <div className="page-title-row">
        <div>
          <h1>구독 피드</h1>
          <p>내가 구독한 채널의 게시글을 한곳에서 모아봅니다.</p>
        </div>

        <Link to="/channels" className="secondary-button">
          채널 둘러보기
        </Link>
      </div>

      <div className="feed-filter-box">
        <div className="feed-filter-group">
          <label htmlFor="feedChannel">채널</label>

          <select
            id="feedChannel"
            value={channelSlug}
            onChange={handleChannelChange}
            disabled={channelLoading}
          >
            <option value="">전체 구독 채널</option>

            {subscribedChannels.map((channel) => (
              <option key={channel.channelId} value={channel.slug}>
                {channel.name}
              </option>
            ))}
          </select>
        </div>

        <div className="feed-filter-group">
          <label htmlFor="feedSort">정렬</label>

          <select
            id="feedSort"
            value={sort}
            onChange={handleSortChange}
          >
            <option value="latest">최신순</option>
            <option value="popular">인기순</option>
          </select>
        </div>
      </div>

      {errorMessage && <p className="error-message">{errorMessage}</p>}

      {loading ? (
        <p>불러오는 중...</p>
      ) : posts.length === 0 ? (
        <div className="empty-box">
          {channelSlug
            ? '선택한 채널에 표시할 게시글이 없습니다.'
            : '구독한 채널의 게시글이 없습니다. 관심 있는 채널을 구독해보세요.'}
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
            disabled={pageInfo.first || loading}
            onClick={handlePreviousPage}
          >
            이전
          </button>

          <span>
            {pageInfo.page + 1} / {pageInfo.totalPages}
          </span>

          <button
            type="button"
            disabled={pageInfo.last || loading}
            onClick={handleNextPage}
          >
            다음
          </button>
        </div>
      )}
    </div>
  )
}

export default FeedPage