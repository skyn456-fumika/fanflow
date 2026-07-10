import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { getBoards } from '../../api/boardApi'
import { getPosts } from '../../api/postApi'

function PostListPage() {
  const [boards, setBoards] = useState([])
  const [posts, setPosts] = useState([])
  const [pageInfo, setPageInfo] = useState(null)

  const [boardCode, setBoardCode] = useState('')
  const [keyword, setKeyword] = useState('')
  const [page, setPage] = useState(0)

  const [loading, setLoading] = useState(false)
  const [errorMessage, setErrorMessage] = useState('')

  const size = 10

  const loadBoards = async () => {
    try {
      const result = await getBoards()

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

      const result = await getPosts({
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

  useEffect(() => {
    loadBoards()
  }, [])

  useEffect(() => {
    loadPosts()
  }, [boardCode, page])

  const handleSearch = (e) => {
    e.preventDefault()
    setPage(0)
    loadPosts()
  }

  const handleBoardChange = (e) => {
    setBoardCode(e.target.value)
    setPage(0)
  }

  return (
    <div>
      <div className="page-title-row">
        <div>
          <h1>게시글 목록</h1>
          <p>FanFlow 커뮤니티 게시글입니다.</p>
        </div>

        <Link to="/posts/write" className="primary-button">
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
      ) : (
        <div className="post-list">
          {posts.length === 0 ? (
            <div className="empty-box">게시글이 없습니다.</div>
          ) : (
            posts.map((post) => (
              <Link
                to={`/posts/${post.postId}`}
                key={post.postId}
                className="post-list-item"
              >
                <div className="post-main">
                  <span className="board-badge">{post.boardName}</span>
                  {post.notice && <span className="notice-badge">공지</span>}
                  <strong>{post.title}</strong>
                </div>

                <div className="post-meta">
                  <span>{post.writerNickname}</span>
                  <span>조회 {post.viewCount}</span>
                  <span>좋아요 {post.likeCount}</span>
                  <span>댓글 {post.commentCount}</span>
                </div>
              </Link>
            ))
          )}
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