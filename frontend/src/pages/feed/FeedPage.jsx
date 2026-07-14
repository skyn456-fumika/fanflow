import { useEffect, useState } from 'react'
import {
  Link,
  useLocation,
  useNavigate,
  useSearchParams,
} from 'react-router-dom'
import { getSubscriptionFeed } from '../../api/feedApi'
import {
  getChannelHome,
  getMySubscribedChannels,
} from '../../api/channelApi'

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

  const initialBoardCode = searchParams.get('boardCode') || ''
  const [boards, setBoards] = useState([])
  const [boardCode, setBoardCode] = useState(initialBoardCode)
  const [boardLoading, setBoardLoading] = useState(false)

  const initialKeyword = searchParams.get('keyword') || ''
  const [keyword, setKeyword] = useState(initialKeyword)
  const [keywordInput, setKeywordInput] = useState(initialKeyword)

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
        boardCode,
        keyword,
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

  const loadChannelBoards = async (selectedChannelSlug) => {
    if (!selectedChannelSlug) {
      setBoards([])
      setBoardCode('')
      return
    }

    try {
      setBoardLoading(true)

      const result = await getChannelHome(selectedChannelSlug)

      if (result.success) {
        const loadedBoards = result.data.boards || []

        setBoards(loadedBoards)

        const boardExists =
          !boardCode ||
          loadedBoards.some((board) => board.code === boardCode)

        if (!boardExists) {
          setSearchParams(
            createFeedSearchParams({
              nextPage: 0,
              nextChannelSlug: selectedChannelSlug,
              nextBoardCode: '',
              nextKeyword: keyword,
              nextSort: sort,
            }),
            {
              replace: true,
            },
          )
        }
      } else {
        setBoards([])
        setBoardCode('')
      }
    } catch (error) {
      console.error(error)
      setBoards([])
      setBoardCode('')
    } finally {
      setBoardLoading(false)
    }
  }

  const createFeedSearchParams = ({
    nextPage = page,
    nextChannelSlug = channelSlug,
    nextBoardCode = boardCode,
    nextKeyword = keyword,
    nextSort = sort,
  }) => {
    const params = new URLSearchParams()

    if (nextPage > 0) {
      params.set('page', String(nextPage))
    }

    if (nextChannelSlug) {
      params.set('channelSlug', nextChannelSlug)
    }

    if (nextChannelSlug && nextBoardCode) {
      params.set('boardCode', nextBoardCode)
    }

    if (nextKeyword) {
      params.set('keyword', nextKeyword)
    }

    if (nextSort !== 'latest') {
      params.set('sort', nextSort)
    }

    return params
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
    loadChannelBoards(channelSlug)
  }, [channelSlug])

  useEffect(() => {
    const nextPage = Math.max(
      Number.parseInt(searchParams.get('page') || '0', 10) || 0,
      0,
    )

    const nextChannelSlug = searchParams.get('channelSlug') || ''
    const nextBoardCode = nextChannelSlug
      ? searchParams.get('boardCode') || ''
      : ''

    const nextKeyword = searchParams.get('keyword') || ''

    const nextSort =
      searchParams.get('sort') === 'popular'
        ? 'popular'
        : 'latest'

    setPage(nextPage)
    setChannelSlug(nextChannelSlug)
    setBoardCode(nextBoardCode)
    setKeyword(nextKeyword)
    setKeywordInput(nextKeyword)
    setSort(nextSort)
  }, [searchParams])

  useEffect(() => {
    loadFeed()
  }, [page, channelSlug, boardCode, keyword, sort])

  const handleChannelChange = (e) => {
    const nextChannelSlug = e.target.value

    setSearchParams(
      createFeedSearchParams({
        nextPage: 0,
        nextChannelSlug,
        nextBoardCode: '',
        nextKeyword: keyword,
        nextSort: sort,
      }),
    )
  }

  const handleSortChange = (e) => {
    const nextSort = e.target.value

    setSearchParams(
      createFeedSearchParams({
        nextPage: 0,
        nextChannelSlug: channelSlug,
        nextBoardCode: boardCode,
        nextKeyword: keyword,
        nextSort,
      }),
    )
  }

  const handlePreviousPage = () => {
    const nextPage = Math.max(page - 1, 0)

    setSearchParams(
      createFeedSearchParams({
        nextPage,
        nextChannelSlug: channelSlug,
        nextBoardCode: boardCode,
        nextKeyword: keyword,
        nextSort: sort,
      }),
    )
  }

  const handleNextPage = () => {
    const nextPage = page + 1

    setSearchParams(
      createFeedSearchParams({
        nextPage,
        nextChannelSlug: channelSlug,
        nextBoardCode: boardCode,
        nextKeyword: keyword,
        nextSort: sort,
      }),
    )
  }

  const handleBoardChange = (e) => {
    const nextBoardCode = e.target.value

    setSearchParams(
      createFeedSearchParams({
        nextPage: 0,
        nextChannelSlug: channelSlug,
        nextBoardCode,
        nextKeyword: keyword,
        nextSort: sort,
      }),
    )
  }

  const handleSearchSubmit = (e) => {
    e.preventDefault()

    const nextKeyword = keywordInput.trim()

    setSearchParams(
      createFeedSearchParams({
        nextPage: 0,
        nextChannelSlug: channelSlug,
        nextBoardCode: boardCode,
        nextKeyword,
        nextSort: sort,
      }),
    )
  }

  const handleSearchReset = () => {
    setKeywordInput('')

    setSearchParams(
      createFeedSearchParams({
        nextPage: 0,
        nextChannelSlug: channelSlug,
        nextBoardCode: boardCode,
        nextKeyword: '',
        nextSort: sort,
      }),
    )
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
          <label htmlFor="feedBoard">게시판</label>

          <select
            id="feedBoard"
            value={boardCode}
            onChange={handleBoardChange}
            disabled={!channelSlug || boardLoading}
          >
            <option value="">
              {!channelSlug
                ? '채널을 먼저 선택하세요'
                : '전체 게시판'}
            </option>

            {boards.map((board) => (
              <option key={board.boardId} value={board.code}>
                {board.name}
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

      <form className="feed-search-form" onSubmit={handleSearchSubmit}>
        <input
          type="search"
          value={keywordInput}
          onChange={(e) => setKeywordInput(e.target.value)}
          placeholder="구독 피드에서 제목이나 내용을 검색하세요."
        />

        <button type="submit" className="primary-button">
          검색
        </button>

        {keyword && (
          <button
            type="button"
            className="secondary-button"
            onClick={handleSearchReset}
          >
            초기화
          </button>
        )}
      </form>

      {errorMessage && <p className="error-message">{errorMessage}</p>}

      {loading ? (
        <p>불러오는 중...</p>
      ) : posts.length === 0 ? (
        <div className="empty-box">
          {keyword
            ? `"${keyword}" 검색 결과가 없습니다.`
            : boardCode
              ? '선택한 게시판에 표시할 게시글이 없습니다.'
              : channelSlug
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