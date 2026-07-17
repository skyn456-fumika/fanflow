import { useEffect, useState } from 'react'
import {
  Link,
  useNavigate,
  useParams,
} from 'react-router-dom'
import { getChannelHome } from '../../api/channelApi'
import {
  getBlindChannelComments,
  getBlindChannelPosts,
  unblindModeratedComment,
  unblindModeratedPost,
} from '../../api/channelModerationApi'

function ChannelModerationPage() {
  const { channelSlug } = useParams()
  const navigate = useNavigate()

  const [channel, setChannel] = useState(null)
  const [posts, setPosts] = useState([])
  const [comments, setComments] = useState([])
  const [activeTab, setActiveTab] =
    useState('posts')

  const [loading, setLoading] = useState(true)
  const [errorMessage, setErrorMessage] =
    useState('')

  const loadBlindPosts = async () => {
    const result =
      await getBlindChannelPosts(channelSlug)

    if (result.success) {
      setPosts(result.data || [])
    }
  }

  const loadBlindComments = async () => {
    const result =
      await getBlindChannelComments(channelSlug)

    if (result.success) {
      setComments(result.data || [])
    }
  }

  const initialize = async () => {
    const token = localStorage.getItem('accessToken')

    if (!token) {
      alert('로그인이 필요합니다.')

      navigate('/login', {
        replace: true,
        state: {
          from: `/channels/${channelSlug}/moderation`,
        },
      })

      return
    }

    try {
      setLoading(true)
      setErrorMessage('')

      const channelResult =
        await getChannelHome(channelSlug)

      if (!channelResult.success) {
        setErrorMessage(
          channelResult.message ||
            '채널 정보를 불러오지 못했습니다.',
        )
        return
      }

      const loadedChannel =
        channelResult.data.channel

      const role = loadedChannel.myChannelRole

      if (role !== 'OWNER' && role !== 'MANAGER') {
        alert(
          '채널 운영진만 접근할 수 있습니다.',
        )

        navigate(`/channels/${channelSlug}`, {
          replace: true,
        })

        return
      }

      setChannel(loadedChannel)

      await Promise.all([
        loadBlindPosts(),
        loadBlindComments(),
      ])
    } catch (error) {
      console.error(error)

      if (error.response?.status === 401) {
        localStorage.removeItem('accessToken')
        alert('로그인이 필요합니다.')

        navigate('/login', {
          replace: true,
          state: {
            from: `/channels/${channelSlug}/moderation`,
          },
        })

        return
      }

      if (error.response?.status === 403) {
        alert(
          '채널 운영진만 접근할 수 있습니다.',
        )

        navigate(`/channels/${channelSlug}`, {
          replace: true,
        })

        return
      }

      setErrorMessage(
        error.response?.data?.message ||
          '콘텐츠 관리 정보를 불러오지 못했습니다.',
      )
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    initialize()
  }, [channelSlug])

  const handleUnblindPost = async (post) => {
    if (
      !window.confirm(
        `"${post.title}" 게시글의 블라인드를 해제하시겠습니까?`,
      )
    ) {
      return
    }

    try {
      const result =
        await unblindModeratedPost(post.postId)

      if (result.success) {
        alert('게시글 블라인드가 해제되었습니다.')
        await loadBlindPosts()
      } else {
        alert(
          result.message ||
            '게시글 블라인드 해제에 실패했습니다.',
        )
      }
    } catch (error) {
      console.error(error)

      alert(
        error.response?.data?.message ||
          '게시글 블라인드 해제에 실패했습니다.',
      )
    }
  }

  const handleUnblindComment = async (
    comment,
  ) => {
    if (
      !window.confirm(
        '이 댓글의 블라인드를 해제하시겠습니까?',
      )
    ) {
      return
    }

    try {
      const result =
        await unblindModeratedComment(
          comment.commentId,
        )

      if (result.success) {
        alert('댓글 블라인드가 해제되었습니다.')
        await loadBlindComments()
      } else {
        alert(
          result.message ||
            '댓글 블라인드 해제에 실패했습니다.',
        )
      }
    } catch (error) {
      console.error(error)

      alert(
        error.response?.data?.message ||
          '댓글 블라인드 해제에 실패했습니다.',
      )
    }
  }

  if (loading) {
    return <p>불러오는 중...</p>
  }

  if (errorMessage) {
    return (
      <p className="error-message">
        {errorMessage}
      </p>
    )
  }

  if (!channel) {
    return null
  }

  return (
    <div>
      <div className="page-title-row">
        <div>
          <h1>{channel.name} 콘텐츠 관리</h1>

          <p>
            운영진에 의해 블라인드 처리된
            게시글과 댓글을 관리합니다.
          </p>
        </div>

        <Link
          to={`/channels/${channel.slug}`}
          className="secondary-button"
        >
          채널로 돌아가기
        </Link>
      </div>

      <div className="moderation-summary-grid">
        <div className="moderation-summary-card">
          <span>블라인드 게시글</span>
          <strong>{posts.length}</strong>
        </div>

        <div className="moderation-summary-card">
          <span>블라인드 댓글</span>
          <strong>{comments.length}</strong>
        </div>
      </div>

      <div className="admin-tab-row">
        <button
          type="button"
          className={
            activeTab === 'posts' ? 'active' : ''
          }
          onClick={() => setActiveTab('posts')}
        >
          게시글 {posts.length}
        </button>

        <button
          type="button"
          className={
            activeTab === 'comments'
              ? 'active'
              : ''
          }
          onClick={() =>
            setActiveTab('comments')
          }
        >
          댓글 {comments.length}
        </button>
      </div>

      {activeTab === 'posts' ? (
        <section className="admin-section">
          <h2>블라인드 게시글</h2>

          {posts.length === 0 ? (
            <div className="empty-box">
              블라인드 처리된 게시글이 없습니다.
            </div>
          ) : (
            <div className="moderation-content-list">
              {posts.map((post) => (
                <div
                  key={post.postId}
                  className="moderation-content-item"
                >
                  <div className="moderation-content-main">
                    <div className="moderation-content-path">
                      <span className="board-badge">
                        {post.boardName}
                      </span>

                      {post.notice && (
                        <span className="notice-badge">
                          공지
                        </span>
                      )}
                    </div>

                    <strong>{post.title}</strong>

                    <div className="post-meta">
                      <span>
                        작성자 {post.writerNickname}
                      </span>
                      <span>{post.createdAt}</span>
                    </div>
                  </div>

                  <div className="moderation-content-actions">
                    <Link
                      to={`/posts/${post.postId}`}
                      className="secondary-button"
                    >
                      상세 확인
                    </Link>

                    <button
                      type="button"
                      className="moderation-restore-button"
                      onClick={() =>
                        handleUnblindPost(post)
                      }
                    >
                      블라인드 해제
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </section>
      ) : (
        <section className="admin-section">
          <h2>블라인드 댓글</h2>

          {comments.length === 0 ? (
            <div className="empty-box">
              블라인드 처리된 댓글이 없습니다.
            </div>
          ) : (
            <div className="moderation-content-list">
              {comments.map((comment) => (
                <div
                  key={comment.commentId}
                  className="moderation-content-item"
                >
                  <div className="moderation-content-main">
                    <div className="moderation-content-path">
                      <span className="board-badge">
                        {comment.boardName}
                      </span>

                      {comment.reply && (
                        <span className="moderation-type-badge">
                          답글
                        </span>
                      )}
                    </div>

                    <strong>
                      {comment.postTitle}
                    </strong>

                    <p className="moderation-comment-preview">
                      {comment.content}
                    </p>

                    <div className="post-meta">
                      <span>
                        작성자 {comment.writerNickname}
                      </span>
                      <span>{comment.createdAt}</span>
                    </div>
                  </div>

                  <div className="moderation-content-actions">
                    <Link
                      to={`/posts/${comment.postId}#comment-${comment.commentId}`}
                      className="secondary-button"
                    >
                      원문 확인
                    </Link>

                    <button
                      type="button"
                      className="moderation-restore-button"
                      onClick={() =>
                        handleUnblindComment(comment)
                      }
                    >
                      블라인드 해제
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </section>
      )}
    </div>
  )
}

export default ChannelModerationPage