import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import {
  getUserComments,
  getUserPosts,
  getUserProfile,
} from '../../api/userApi'
import { getMyInfo } from '../../api/authApi'
import {
  blockUser,
  getUserBlockStatus,
  unblockUser,
} from '../../api/userBlockApi'

function UserProfilePage() {
  const navigate = useNavigate()

  const [me, setMe] = useState(null)
  const [blocked, setBlocked] = useState(false)
  const [blockLoading, setBlockLoading] = useState(false)

  const { userId } = useParams()

  const [profile, setProfile] = useState(null)
  const [activeTab, setActiveTab] = useState('posts')

  const [posts, setPosts] = useState([])
  const [postPageInfo, setPostPageInfo] = useState(null)
  const [postPage, setPostPage] = useState(0)

  const [comments, setComments] = useState([])
  const [commentPageInfo, setCommentPageInfo] = useState(null)
  const [commentPage, setCommentPage] = useState(0)

  const [loading, setLoading] = useState(false)
  const [activityLoading, setActivityLoading] = useState(false)
  const [errorMessage, setErrorMessage] = useState('')

  const getImageUrl = (imageUrl) => {
    if (!imageUrl) {
      return null
    }

    if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
      return imageUrl
    }

    return `${import.meta.env.VITE_API_BASE_URL}${imageUrl}`
  }

  const ActivityPath = ({ item }) => (
    <div className="activity-path">
      {item.channelName && (
        <>
          <span className="activity-path-channel">{item.channelName}</span>
          <span className="activity-path-separator">&gt;</span>
        </>
      )}

      <span className="activity-path-board">
        {item.boardName || '게시판'}
      </span>
    </div>
  )

  const loadProfile = async () => {
    try {
      setLoading(true)
      setErrorMessage('')

      const result = await getUserProfile(userId)

      if (result.success) {
        setProfile(result.data)
      } else {
        setErrorMessage(result.message || '프로필을 불러오지 못했습니다.')
      }
    } catch (error) {
      console.error(error)
      setErrorMessage('프로필을 불러오지 못했습니다.')
    } finally {
      setLoading(false)
    }
  }

  const loadPosts = async () => {
    try {
      setActivityLoading(true)

      const result = await getUserPosts({
        userId,
        page: postPage,
        size: 5,
      })

      if (result.success) {
        setPosts(result.data.content)
        setPostPageInfo(result.data)
      }
    } catch (error) {
      console.error(error)
    } finally {
      setActivityLoading(false)
    }
  }

  const loadComments = async () => {
    try {
      setActivityLoading(true)

      const result = await getUserComments({
        userId,
        page: commentPage,
        size: 5,
      })

      if (result.success) {
        setComments(result.data.content)
        setCommentPageInfo(result.data)
      }
    } catch (error) {
      console.error(error)
    } finally {
      setActivityLoading(false)
    }
  }

  const loadMyInfo = async () => {
    const token = localStorage.getItem('accessToken')

    if (!token) {
      setMe(null)
      setBlocked(false)
      return
    }

    try {
      const result = await getMyInfo()

      if (result.success) {
        setMe(result.data)
      }
    } catch (error) {
      console.error(error)
      setMe(null)
      setBlocked(false)
    }
  }

  const loadBlockStatus = async () => {
    const token = localStorage.getItem('accessToken')

    if (!token || !me || !profile) {
      setBlocked(false)
      return
    }

    if (me.userId === profile.userId) {
      setBlocked(false)
      return
    }

    try {
      const result = await getUserBlockStatus(profile.userId)

      if (result.success) {
        setBlocked(result.data.blocked)
      }
    } catch (error) {
      console.error(error)
      setBlocked(false)
    }
  }

  useEffect(() => {
    setProfile(null)
    setPosts([])
    setComments([])
    setPostPage(0)
    setCommentPage(0)
    setActiveTab('posts')
    setBlocked(false)

    loadProfile()
    loadMyInfo()
  }, [userId])

  useEffect(() => {
    if (!profile) {
      return
    }

    if (activeTab === 'posts') {
      loadPosts()
    }

    if (activeTab === 'comments') {
      loadComments()
    }
  }, [profile, activeTab, postPage, commentPage])

  useEffect(() => {
    if (!profile || !me) {
      return
    }

    loadBlockStatus()
  }, [profile, me])

  const handleTabChange = (tab) => {
    setActiveTab(tab)

    if (tab === 'posts') {
      setPostPage(0)
    }

    if (tab === 'comments') {
      setCommentPage(0)
    }
  }

  const handleBlockClick = async () => {
    const token = localStorage.getItem('accessToken')

    if (!token) {
      alert('로그인이 필요합니다.')

      navigate('/login', {
        state: {
          from: `/users/${userId}`,
        },
      })

      return
    }

    const confirmMessage = blocked
      ? '이 사용자의 차단을 해제하시겠습니까?'
      : '이 사용자를 차단하시겠습니까?'

    if (!window.confirm(confirmMessage)) {
      return
    }

    if (blockLoading) {
      return
    }

    try {
      setBlockLoading(true)

      const result = blocked
        ? await unblockUser(profile.userId)
        : await blockUser(profile.userId)

      if (result.success) {
        setBlocked(result.data.blocked)

        window.dispatchEvent(
          new Event('notification-change'),
        )
      } else {
        alert(
          result.message ||
            '사용자 차단 처리에 실패했습니다.',
        )
      }
    } catch (error) {
      console.error(error)

      if (error.response?.status === 401) {
        localStorage.removeItem('accessToken')
        alert('로그인이 필요합니다.')

        navigate('/login', {
          replace: true,
          state: {
            from: `/users/${userId}`,
          },
        })

        return
      }

      alert(
        error.response?.data?.message ||
          '사용자 차단 처리에 실패했습니다.',
      )
    } finally {
      setBlockLoading(false)
    }
  }

  if (loading) {
    return <p>불러오는 중...</p>
  }

  if (errorMessage) {
    return <p className="error-message">{errorMessage}</p>
  }

  if (!profile) {
    return null
  }

  return (
    <div>
      <div className="page-title-row">
        <div>
          <h1>유저 프로필</h1>
          <p>커뮤니티 활동 정보를 확인할 수 있습니다.</p>
        </div>
      </div>

      <div className="user-profile-box">
        <div className="profile-avatar large">
          {profile.profileImageUrl ? (
            <img src={getImageUrl(profile.profileImageUrl)} alt="프로필 이미지" />
          ) : (
            <span>{profile.nickname?.charAt(0) || '?'}</span>
          )}
        </div>

        <div className="user-profile-info">
          <div className="user-profile-info-header">
            <div>
              <div className="user-profile-name-row">
                <strong>{profile.nickname}</strong>

                {profile.streamer && (
                  <span className="streamer-badge">
                    공식 스트리머
                  </span>
                )}
              </div>

              <p>가입일 {profile.createdAt}</p>
            </div>

            {me && me.userId !== profile.userId && (
              <button
                type="button"
                className={
                  blocked
                    ? 'secondary-button'
                    : 'danger-button'
                }
                onClick={handleBlockClick}
                disabled={blockLoading}
              >
                {blockLoading
                  ? '처리 중...'
                  : blocked
                    ? '차단 해제'
                    : '사용자 차단'}
              </button>
            )}
          </div>

          <div className="user-profile-stats">
            <div>
              <span>{profile.postCount}</span>
              <p>작성 게시글</p>
            </div>

            <div>
              <span>{profile.commentCount}</span>
              <p>작성 댓글</p>
            </div>
          </div>
        </div>
      </div>

      {profile.streamer &&
        profile.ownedChannels?.length > 0 && (
          <section className="mypage-section user-owned-channel-section">
            <div className="home-section-title">
              <div>
                <h2>운영 중인 공식 채널</h2>
                <p>
                  이 사용자가 공식 스트리머로 등록된 채널입니다.
                </p>
              </div>
            </div>

            <div className="user-owned-channel-grid">
              {profile.ownedChannels.map((channel) => (
                <Link
                  key={channel.channelId}
                  to={`/channels/${channel.slug}`}
                  className="user-owned-channel-card"
                >
                  <div className="user-owned-channel-image">
                    {channel.profileImageUrl ? (
                      <img
                        src={getImageUrl(channel.profileImageUrl)}
                        alt={channel.name}
                      />
                    ) : (
                      <span>
                        {channel.name?.charAt(0) || '?'}
                      </span>
                    )}
                  </div>

                  <div>
                    <span className="streamer-badge">
                      공식 채널
                    </span>

                    <strong>{channel.name}</strong>

                    <p>
                      {channel.description ||
                        '채널 설명이 없습니다.'}
                    </p>
                  </div>
                </Link>
              ))}
            </div>
          </section>
        )}

      <div className="mypage-section">
        <h2>공개 활동</h2>

        <div className="mypage-tab-row">
          <button
            type="button"
            className={activeTab === 'posts' ? 'active' : ''}
            onClick={() => handleTabChange('posts')}
          >
            작성 게시글
          </button>

          <button
            type="button"
            className={activeTab === 'comments' ? 'active' : ''}
            onClick={() => handleTabChange('comments')}
          >
            작성 댓글
          </button>
        </div>

        {activityLoading ? (
          <p>불러오는 중...</p>
        ) : (
          <>
            {activeTab === 'posts' && (
              <div className="mypage-activity-list">
                {posts.length === 0 ? (
                  <div className="empty-box">공개된 게시글이 없습니다.</div>
                ) : (
                  posts.map((post) => (
                    <Link
                      to={`/posts/${post.postId}`}
                      key={post.postId}
                      className="mypage-activity-item"
                    >
                      <ActivityPath item={post} />

                      <div>
                        <span className="board-badge">{post.boardName}</span>
                        <strong>{post.title}</strong>
                      </div>

                      <div className="post-meta">
                        <span>조회 {post.viewCount}</span>
                        <span>좋아요 {post.likeCount}</span>
                        <span>댓글 {post.commentCount}</span>
                        <span>{post.createdAt}</span>
                      </div>
                    </Link>
                  ))
                )}

                {postPageInfo && !postPageInfo.empty && (
                  <div className="pagination">
                    <button
                      type="button"
                      disabled={postPageInfo.first}
                      onClick={() => setPostPage((prev) => prev - 1)}
                    >
                      이전
                    </button>

                    <span>
                      {postPageInfo.page + 1} / {postPageInfo.totalPages}
                    </span>

                    <button
                      type="button"
                      disabled={postPageInfo.last}
                      onClick={() => setPostPage((prev) => prev + 1)}
                    >
                      다음
                    </button>
                  </div>
                )}
              </div>
            )}

            {activeTab === 'comments' && (
              <div className="mypage-activity-list">
                {comments.length === 0 ? (
                  <div className="empty-box">공개된 댓글이 없습니다.</div>
                ) : (
                  comments.map((comment) => (
                    <Link
                      to={`/posts/${comment.postId}`}
                      key={comment.commentId}
                      className="mypage-activity-item"
                    >
                      <ActivityPath item={comment} />

                      <div>
                        <strong>{comment.content}</strong>
                      </div>

                      <div className="post-meta">
                        <span>{comment.createdAt}</span>
                      </div>
                    </Link>
                  ))
                )}

                {commentPageInfo && !commentPageInfo.empty && (
                  <div className="pagination">
                    <button
                      type="button"
                      disabled={commentPageInfo.first}
                      onClick={() => setCommentPage((prev) => prev - 1)}
                    >
                      이전
                    </button>

                    <span>
                      {commentPageInfo.page + 1} / {commentPageInfo.totalPages}
                    </span>

                    <button
                      type="button"
                      disabled={commentPageInfo.last}
                      onClick={() => setCommentPage((prev) => prev + 1)}
                    >
                      다음
                    </button>
                  </div>
                )}
              </div>
            )}
          </>
        )}
      </div>
    </div>
  )
}

export default UserProfilePage