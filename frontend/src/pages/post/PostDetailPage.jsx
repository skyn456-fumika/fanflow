import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { deletePost, getPostDetail } from '../../api/postApi'
import {
  createComment,
  deleteComment,
  getComments,
} from '../../api/commentApi'
import { getMyLikeStatus, likePost, unlikePost } from '../../api/likeApi'
import { getMyInfo } from '../../api/authApi'
import { createReport } from '../../api/reportApi'
import {
  addBookmark,
  getBookmarkStatus,
  removeBookmark,
} from '../../api/bookmarkApi'
import { saveRecentPost } from '../../utils/recentPostStorage'

function PostDetailPage() {
  const { postId } = useParams()
  const navigate = useNavigate()

  const [post, setPost] = useState(null)
  const [comments, setComments] = useState([])
  const [commentContent, setCommentContent] = useState('')
  const [liked, setLiked] = useState(false)
  const [bookmarked, setBookmarked] = useState(false)
  const [bookmarkLoading, setBookmarkLoading] = useState(false)
  const [me, setMe] = useState(null)

  const [loading, setLoading] = useState(false)
  const [errorMessage, setErrorMessage] = useState('')

  const loadPostDetail = async () => {
    const result = await getPostDetail(postId)

    if (result.success) {
      setPost(result.data)
      saveRecentPost(result.data)
    }
  }
  
  const loadComments = async () => {
    const result = await getComments(postId)

    if (result.success) {
      setComments(result.data)
    }
  }

  const loadLikeStatus = async () => {
    try {
      const result = await getMyLikeStatus(postId)

      if (result.success) {
        setLiked(result.data.liked)
      }
    } catch (error) {
      // 비로그인 사용자는 좋아요 상태 조회에서 401이 발생할 수 있음
      setLiked(false)
    }
  }

  const loadMyInfo = async () => {
    try {
      const token = localStorage.getItem('accessToken')

      if (!token) {
        setMe(null)
        return
      }

      const result = await getMyInfo()

      if (result.success) {
        setMe(result.data)
      }
    } catch (error) {
      setMe(null)
    }
  }

  const loadPageData = async () => {
    try {
      setLoading(true)
      setErrorMessage('')

      await loadPostDetail()
      await loadComments()
      await loadLikeStatus()
      await loadBookmarkStatus()
      await loadMyInfo()
    } catch (error) {
      console.error(error)
      setErrorMessage('삭제되었거나 블라인드 처리된 게시글입니다.')
    } finally {
      setLoading(false)
    }
  }

  const loadBookmarkStatus = async () => {
    const token = localStorage.getItem('accessToken')

    if (!token) {
      setBookmarked(false)
      return
    }

    try {
      const result = await getBookmarkStatus(postId)

      if (result.success) {
        setBookmarked(result.data.bookmarked)
      }
    } catch (error) {
      console.error(error)

      if (error.response?.status === 401) {
        setBookmarked(false)
        return
      }

      setBookmarked(false)
    }
  }

  const handleSharePost = async () => {
    const shareUrl = window.location.href

    const shareData = {
      title: post.title,
      text: `${post.title} - FanFlow`,
      url: shareUrl,
    }

    try {
      if (navigator.share) {
        await navigator.share(shareData)
        return
      }

      if (navigator.clipboard?.writeText) {
        await navigator.clipboard.writeText(shareUrl)
        alert('게시글 주소가 복사되었습니다.')
        return
      }

      const textarea = document.createElement('textarea')
      textarea.value = shareUrl
      textarea.style.position = 'fixed'
      textarea.style.opacity = '0'

      document.body.appendChild(textarea)
      textarea.focus()
      textarea.select()

      const copied = document.execCommand('copy')

      document.body.removeChild(textarea)

      if (copied) {
        alert('게시글 주소가 복사되었습니다.')
      } else {
        window.prompt('아래 주소를 복사해주세요.', shareUrl)
      }
    } catch (error) {
      if (error.name === 'AbortError') {
        return
      }

      console.error(error)

      try {
        await navigator.clipboard.writeText(shareUrl)
        alert('게시글 주소가 복사되었습니다.')
      } catch (clipboardError) {
        console.error(clipboardError)
        window.prompt('아래 주소를 복사해주세요.', shareUrl)
      }
    }
  }

  const getImageUrl = (imageUrl) => {
    if (!imageUrl) {
      return null
    }

    if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
      return imageUrl
    }

    return `${import.meta.env.VITE_API_BASE_URL}${imageUrl}`
  }

  useEffect(() => {
    loadPageData()
  }, [postId])

  if (loading) {
    return <p>불러오는 중...</p>
  }

  if (errorMessage) {
    return <p className="error-message">{errorMessage}</p>
  }

  if (!post) {
    return <div className="empty-box">게시글이 없습니다.</div>
  }

  const isWriter = me && post.writerId === me.userId

  const listPath = post.channelSlug
    ? `/channels/${post.channelSlug}/posts?boardCode=${post.boardCode}`
    : '/posts'

  const handleDeletePost = async () => {
    if (!window.confirm('게시글을 삭제하시겠습니까?')) {
      return
    }

    try {
      const result = await deletePost(postId)

      if (result.success) {
        //alert('게시글이 삭제되었습니다.')
        navigate(listPath, { replace: true })
      } else {
        alert(result.message || '게시글 삭제에 실패했습니다.')
      }
    } catch (error) {
      console.error(error)

      if (error.response?.status === 401) {
        localStorage.removeItem('accessToken')
        alert('로그인이 필요합니다.')
        navigate('/login', { replace: true })
        return
      }

      const message =
        error.response?.data?.message || '게시글 삭제에 실패했습니다.'

      alert(message)
    }
  }

  const handleLikeClick = async () => {
    const token = localStorage.getItem('accessToken')

    if (!token) {
      alert('로그인이 필요합니다.')
      navigate('/login', { replace: true })
      return
    }

    try {
      const result = liked ? await unlikePost(postId) : await likePost(postId)

      if (result.success) {
        setLiked(result.data.liked)

        setPost((prev) => ({
          ...prev,
          likeCount: result.data.likeCount,
        }))
      } else {
        alert(result.message || '좋아요 처리에 실패했습니다.')
      }
    } catch (error) {
      console.error(error)

      if (error.response?.status === 401) {
        localStorage.removeItem('accessToken')
        alert('로그인이 필요합니다.')
        navigate('/login', { replace: true })
        return
      }

      const message =
        error.response?.data?.message || '좋아요 처리에 실패했습니다.'

      alert(message)
    }
  }

  const handleCommentSubmit = async (e) => {
    e.preventDefault()

    const token = localStorage.getItem('accessToken')

    if (!token) {
      alert('로그인이 필요합니다.')
      navigate('/login', { replace: true })
      return
    }

    const content = commentContent.trim()

    if (!content) {
      alert('댓글 내용을 입력해주세요.')
      return
    }

    try {
      const result = await createComment(postId, {
        content,
      })

      if (result.success) {
        setCommentContent('')
        await loadComments()

        setPost((prev) => ({
          ...prev,
          commentCount: (prev.commentCount || 0) + 1,
        }))
      } else {
        alert(result.message || '댓글 작성에 실패했습니다.')
      }
    } catch (error) {
      console.error(error)

      if (error.response?.status === 401) {
        localStorage.removeItem('accessToken')
        alert('로그인이 필요합니다.')
        navigate('/login', { replace: true })
        return
      }

      const message =
        error.response?.data?.message || '댓글 작성에 실패했습니다.'

      alert(message)
    }
  }

  const handleDeleteComment = async (commentId) => {
    if (!window.confirm('댓글을 삭제하시겠습니까?')) {
      return
    }

    try {
      const result = await deleteComment(commentId)

      if (result.success) {
        await loadComments()

        setPost((prev) => ({
          ...prev,
          commentCount: Math.max((prev.commentCount || 0) - 1, 0),
        }))
      } else {
        alert(result.message || '댓글 삭제에 실패했습니다.')
      }
    } catch (error) {
      console.error(error)

      if (error.response?.status === 401) {
        localStorage.removeItem('accessToken')
        alert('로그인이 필요합니다.')
        navigate('/login', { replace: true })
        return
      }

      const message =
        error.response?.data?.message || '댓글 삭제에 실패했습니다.'

      alert(message)
    }
  }

  const handleReportPost = async () => {
    const token = localStorage.getItem('accessToken')

    if (!token) {
      alert('로그인이 필요합니다.')
      navigate('/login', { replace: true })
      return
    }

    const reason = window.prompt('신고 사유를 입력해주세요.')

    if (!reason || !reason.trim()) {
      return
    }

    try {
      const result = await createReport({
        targetType: 'POST',
        targetId: post.postId,
        reason: reason.trim(),
      })

      alert(result.message || '신고가 접수되었습니다.')
    } catch (error) {
      console.error(error)
      alert(error.response?.data?.message || '신고 처리 중 오류가 발생했습니다.')
    }
  }

  const handleReportComment = async (commentId) => {
    const token = localStorage.getItem('accessToken')

    if (!token) {
      alert('로그인이 필요합니다.')
      navigate('/login', { replace: true })
      return
    }

    const reason = window.prompt('신고 사유를 입력해주세요.')

    if (!reason || !reason.trim()) {
      return
    }

    try {
      const result = await createReport({
        targetType: 'COMMENT',
        targetId: commentId,
        reason: reason.trim(),
      })

      alert(result.message || '신고가 접수되었습니다.')
    } catch (error) {
      console.error(error)
      alert(error.response?.data?.message || '신고 처리 중 오류가 발생했습니다.')
    }
  }

  const handleBookmarkClick = async () => {
    const token = localStorage.getItem('accessToken')

    if (!token) {
      alert('로그인이 필요합니다.')

      navigate('/login', {
        state: {
          from: `/posts/${postId}`,
        },
      })

      return
    }

    if (bookmarkLoading) {
      return
    }

    try {
      setBookmarkLoading(true)

      const result = bookmarked
        ? await removeBookmark(postId)
        : await addBookmark(postId)

      if (result.success) {
        setBookmarked(result.data.bookmarked)
      } else {
        alert(result.message || '북마크 처리에 실패했습니다.')
      }
    } catch (error) {
      console.error(error)

      if (error.response?.status === 401) {
        localStorage.removeItem('accessToken')
        alert('로그인이 필요합니다.')

        navigate('/login', {
          replace: true,
          state: {
            from: `/posts/${postId}`,
          },
        })

        return
      }

      alert(
        error.response?.data?.message ||
          '북마크 처리에 실패했습니다.',
      )
    } finally {
      setBookmarkLoading(false)
    }
  }

  return (
    <div>
      <div className="page-title-row">
        <div>
          <h1>{post.title}</h1>
          <p>
            {post.channelName
              ? `${post.channelName} 채널의 게시글입니다.`
              : '게시글 상세 페이지입니다.'}
          </p>
        </div>

        <Link to={listPath} className="secondary-button">
          목록
        </Link>
      </div>

      <div className="post-detail-box">
        <div className="post-main">
          {post.channelSlug ? (
            <div className="post-breadcrumb">
              <Link to={`/channels/${post.channelSlug}`}>
                {post.channelName || '채널'}
              </Link>

              <span>&gt;</span>

              <Link to={`/channels/${post.channelSlug}/posts?boardCode=${post.boardCode}`}>
                {post.boardName}
              </Link>
            </div>
          ) : (
            <span className="board-badge">{post.boardName}</span>
          )}

          {post.notice && <span className="notice-badge">공지</span>}
        </div>

        <div className="post-author-row">
          <Link to={`/users/${post.writerId}`} className="post-author-avatar-link">
            <div className="profile-avatar">
              {post.writerProfileImageUrl ? (
                <img src={getImageUrl(post.writerProfileImageUrl)} alt="작성자 프로필" />
              ) : (
                <span>{post.writerNickname?.charAt(0) || '?'}</span>
              )}
            </div>
          </Link>

          <div className="post-author-info">
            <Link to={`/users/${post.writerId}`} className="post-author-name-link">
              {post.writerNickname}
            </Link>

            <div className="post-meta">
              <span>조회 {post.viewCount}</span>
              <span>좋아요 {post.likeCount}</span>
              <span>댓글 {post.commentCount}</span>
              <span>{post.createdAt}</span>
            </div>
          </div>
        </div>

        <hr />

        {post.imageUrl && (
          <div className="post-image-box">
            <img src={post.imageUrl} alt="게시글 이미지" />
          </div>
        )}

        <div
          className="post-content"
          dangerouslySetInnerHTML={{ __html: post.content }}
        />

        <div className="post-action-row">
          <button type="button" onClick={handleLikeClick}>
            {liked ? '좋아요 취소' : '좋아요'}
          </button>

          <button
            type="button"
            className={bookmarked ? 'bookmark-button active' : 'bookmark-button'}
            onClick={handleBookmarkClick}
            disabled={bookmarkLoading}
          >
            {bookmarkLoading
              ? '처리 중...'
              : bookmarked
                ? '북마크 해제'
                : '북마크'}
          </button>

          <button type="button" onClick={handleSharePost}>
            공유
          </button>

          {isWriter && (
            <>
              <button
                type="button"
                onClick={() => navigate(`/posts/${postId}/edit`)}
              >
                수정
              </button>

              <button type="button" onClick={handleDeletePost}>
                삭제
              </button>
            </>
          )}

          <button type="button" onClick={handleReportPost}>
            신고
          </button>
        </div>
      </div>

      <section className="comment-section">
        <h2>댓글</h2>

        <form onSubmit={handleCommentSubmit} className="comment-form">
          <textarea
            value={commentContent}
            onChange={(e) => setCommentContent(e.target.value)}
            placeholder="댓글을 입력하세요."
            rows={4}
          />

          <div className="comment-form-action">
            <button type="submit" className="primary-button">
              댓글 등록
            </button>
          </div>
        </form>

        {comments.length === 0 ? (
          <div className="empty-box">등록된 댓글이 없습니다.</div>
        ) : (
          <div className="comment-list">
            {comments.map((comment) => {
              const isCommentWriter = me && comment.writerId === me.userId

              return (
                <div key={comment.commentId} className="comment-item">
                  <div className="comment-header">
                    <Link to={`/users/${comment.writerId}`} className="comment-writer-box comment-author-link">
                      <div className="profile-avatar small">
                        {comment.writerProfileImageUrl ? (
                          <img
                            src={getImageUrl(comment.writerProfileImageUrl)}
                            alt="댓글 작성자 프로필"
                          />
                        ) : (
                          <span>{comment.writerNickname?.charAt(0) || '?'}</span>
                        )}
                      </div>

                      <div className="comment-writer-info">
                        <strong>{comment.writerNickname}</strong>
                        <span>{comment.createdAt}</span>
                      </div>
                    </Link>
                  </div>

                  <p>{comment.content}</p>

                  <div className="comment-action-row">
                    {isCommentWriter && (
                      <button
                        type="button"
                        onClick={() => handleDeleteComment(comment.commentId)}
                      >
                        삭제
                      </button>
                    )}

                    <button
                      type="button"
                      onClick={() => handleReportComment(comment.commentId)}
                    >
                      신고
                    </button>
                  </div>
                </div>
              )
            })}
          </div>
        )}
      </section>
    </div>
  )
}

export default PostDetailPage