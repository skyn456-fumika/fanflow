import { useEffect, useState } from 'react'
import { Link, useParams, useSearchParams } from 'react-router-dom'
import { getChannelPosts, getPosts } from '../../api/postApi'
import { getChannelBoards, getBoards } from '../../api/boardApi'

const GALLERY_BOARD_CODES = ['FAN_ART']

function PostListPage() {
  const { channelSlug } = useParams()
  const [searchParams, setSearchParams] = useSearchParams()

  const DEFAULT_CHANNEL_SLUG = 'fumika'
  const currentChannelSlug = channelSlug || DEFAULT_CHANNEL_SLUG

  const [boards, setBoards] = useState([])
  const [posts, setPosts] = useState([])
  const [pageInfo, setPageInfo] = useState(null)

  const [boardCode, setBoardCode] = useState(searchParams.get('boardCode') || '')
  const [keyword, setKeyword] = useState(searchParams.get('keyword') || '')
  const [page, setPage] = useState(0)

  const [loading, setLoading] = useState(false)
  const [errorMessage, setErrorMessage] = useState('')

  const size = 10
  const isGalleryBoard = GALLERY_BOARD_CODES.includes(boardCode)

  const getImageUrl = (imageUrl) => {
    if (!imageUrl) {
      return ''
    }

    if (imageUrl.startsWith('http')) {
      return imageUrl
    }

    return `${import.meta.env.VITE_API_BASE_URL}${imageUrl}`
  }

  const renderPostPath = (post) => (
    <div className="post-path">
      {post.channelName && (
        <>
          <span className="post-path-channel">
            {post.channelName}
          </span>

          <span className="post-path-separator">&gt;</span>
        </>
      )}

      <span className="post-path-board">
        {post.boardName}
      </span>
    </div>
  )

  const loadBoards = async () => {
    try {
      const result = channelSlug
        ? await getChannelBoards(currentChannelSlug)
        : await getBoards()

      if (result.success) {
        setBoards(result.data)
      }
    } catch (error) {
      console.error(error)
      setErrorMessage('게시판 목록을 불러오지 못했습니다.')
    }
  }

  const loadPosts = async () => {
    try {
      setLoading(true)
      setErrorMessage('')

      const result = channelSlug
        ? await getChannelPosts({
            channelSlug: currentChannelSlug,
            boardCode,
            keyword,
            page,
            size,
          })
        : await getPosts({
            boardCode,
            keyword,
            page,
            size,
          })
      if (result.success) {
        setPosts(result.data.content)
        setPageInfo(result.data)
      }
    } catch (error) {
      console.error(error)
      setErrorMessage('게시글 목록을 불러오지 못했습니다.')
    } finally {
      setLoading(false)
    }
  }

  const updateSearchParams = ({ nextBoardCode, nextKeyword }) => {
    const params = {}

    if (nextBoardCode) {
      params.boardCode = nextBoardCode
    }

    if (nextKeyword) {
      params.keyword = nextKeyword
    }

    setSearchParams(params)
  }

  useEffect(() => {
    loadBoards()
  }, [channelSlug])

  useEffect(() => {
    const queryBoardCode = searchParams.get('boardCode') || ''
    const queryKeyword = searchParams.get('keyword') || ''

    setBoardCode(queryBoardCode)
    setKeyword(queryKeyword)
    setPage(0)
  }, [searchParams])

  useEffect(() => {
    loadPosts()
  }, [channelSlug, boardCode, page])

  const handleSearch = (e) => {
    e.preventDefault()

    const nextKeyword = keyword.trim()

    setPage(0)

    updateSearchParams({
      nextBoardCode: boardCode,
      nextKeyword,
    })

    loadPosts()
  }

  const handleBoardChange = (e) => {
    const nextBoardCode = e.target.value

    setBoardCode(nextBoardCode)
    setPage(0)

    updateSearchParams({
      nextBoardCode,
      nextKeyword: keyword.trim(),
    })
  }

  return (
    <div>
      <div className="page-title-row">
        <div>
          <h1>게시글 목록</h1>
          <p>
            {channelSlug
              ? '선택한 채널의 게시글을 확인합니다.'
              : '전체 채널의 게시글을 확인합니다.'}
          </p>
        </div>

        <Link to={channelSlug ? `/channels/${currentChannelSlug}/posts/write` : '/posts/write'} className="primary-button">
          글쓰기
        </Link>
      </div>

      <div className="filter-box">
        <select value={boardCode} onChange={handleBoardChange}>
          <option value="">전체 게시판</option>
          {boards.map((board) => (
            <option key={board.boardId} value={board.code}>
              {board.name}
            </option>
          ))}
        </select>

        <form onSubmit={handleSearch} className="search-form">
          <input
            type="text"
            value={keyword}
            placeholder="제목 또는 내용 검색"
            onChange={(e) => setKeyword(e.target.value)}
          />
          <button type="submit">검색</button>
        </form>
      </div>

      {errorMessage && <p className="error-message">{errorMessage}</p>}

      {loading ? (
        <p>불러오는 중...</p>
      ) : posts.length === 0 ? (
        <div className="empty-box">게시글이 없습니다.</div>
      ) : isGalleryBoard ? (
        <div className="gallery-post-grid">
          {posts.map((post) => (
            <Link
              to={`/posts/${post.postId}`}
              key={post.postId}
              className="gallery-post-card"
            >
              {post.thumbnailUrl ? (
                <div className="gallery-post-thumbnail">
                  <img src={post.thumbnailUrl} alt="" />
                </div>
              ) : (
                <div className="gallery-post-thumbnail gallery-post-thumbnail-empty">
                  이미지가 등록되지 않았습니다
                </div>
              )}

              <div className="gallery-post-body">
                <div className="home-post-card-top">
                  {renderPostPath(post)}
                  {post.notice && <span className="notice-badge">공지</span>}
                </div>

                <strong>{post.title}</strong>

                <div className="post-meta">
                  <span>{post.writerNickname}</span>
                  <span>조회 {post.viewCount}</span>
                  <span>좋아요 {post.likeCount}</span>
                  <span>댓글 {post.commentCount}</span>
                </div>
              </div>
            </Link>
          ))}
        </div>
      ) : (
        <div className="post-list">
          {posts.map((post) => (
            <Link
              to={`/posts/${post.postId}`}
              key={post.postId}
              className="post-list-item"
            >
              {post.thumbnailUrl && (
                <div className="post-thumbnail">
                  <img src={getImageUrl(post.thumbnailUrl)} alt="" />
                </div>
              )}

              <div>
                <div className="post-main">
                  <div className="post-title-block">
                    <div className="post-title-top">
                      {renderPostPath(post)}
                      {post.notice && <span className="notice-badge">공지</span>}
                    </div>

                    <strong>{post.title}</strong>
                  </div>
                </div>

                <div className="post-meta">
                  <span>{post.writerNickname}</span>
                  <span>조회 {post.viewCount}</span>
                  <span>좋아요 {post.likeCount}</span>
                  <span>댓글 {post.commentCount}</span>
                </div>
              </div>
            </Link>
          ))}
        </div>
      )}

      {pageInfo && !pageInfo.empty && (
        <div className="pagination">
          <button
            disabled={pageInfo.first}
            onClick={() => setPage((prev) => prev - 1)}
          >
            이전
          </button>

          <span>
            {pageInfo.page + 1} / {pageInfo.totalPages}
          </span>

          <button
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

export default PostListPage