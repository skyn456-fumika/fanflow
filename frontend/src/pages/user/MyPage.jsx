import { useEffect, useRef, useState } from 'react'
import { Link, useNavigate, useLocation } from 'react-router-dom'
import {
  deleteMe,
  getMyComments,
  getMyInfo,
  getMyLikedPosts,
  getMyPosts,
  updateNickname,
  updatePassword,
  updateProfileImage,
} from '../../api/authApi'
import {
  getMySubscribedChannels,
  updateChannelNotification,
} from '../../api/channelApi'

function MyPage() {
  const location = useLocation()
  const navigate = useNavigate()

  const alertShownRef = useRef(false)

  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(false)
  const [errorMessage, setErrorMessage] = useState('')

  const [nickname, setNickname] = useState('')
  const [currentPassword, setCurrentPassword] = useState('')
  const [newPassword, setNewPassword] = useState('')
  const [newPasswordConfirm, setNewPasswordConfirm] = useState('')
  const [deletePassword, setDeletePassword] = useState('')

  const [activeTab, setActiveTab] = useState('posts')

  const [myPosts, setMyPosts] = useState([])
  const [myPostsPageInfo, setMyPostsPageInfo] = useState(null)
  const [myPostsPage, setMyPostsPage] = useState(0)

  const [myComments, setMyComments] = useState([])
  const [myCommentsPageInfo, setMyCommentsPageInfo] = useState(null)
  const [myCommentsPage, setMyCommentsPage] = useState(0)

  const [myLikedPosts, setMyLikedPosts] = useState([])
  const [myLikedPostsPageInfo, setMyLikedPostsPageInfo] = useState(null)
  const [myLikedPostsPage, setMyLikedPostsPage] = useState(0)

  const [activityLoading, setActivityLoading] = useState(false)

  const [mySubscribedChannels, setMySubscribedChannels] = useState([])

  const [notificationUpdatingChannelId, setNotificationUpdatingChannelId] =
  useState(null)

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

  const requireLogin = () => {
    if (!alertShownRef.current) {
      alertShownRef.current = true
      alert('로그인이 필요합니다.')
    }

    navigate('/login', {
      replace: true,
      state: {
        from: location.pathname,
      },
    })
  }

  const loadMyInfo = async () => {
    const token = localStorage.getItem('accessToken')

    if (!token) {
      requireLogin()
      return
    }

    try {
      setLoading(true)
      setErrorMessage('')

      const result = await getMyInfo()

      if (result.success) {
        setUser(result.data)
        setNickname(result.data.nickname)
      } else {
        setErrorMessage(result.message || '내 정보를 불러오지 못했습니다.')
      }
    } catch (error) {
      console.error(error)

      if (error.response?.status === 401) {
        localStorage.removeItem('accessToken')
        requireLogin()
        return
      }

      setErrorMessage('내 정보를 불러오지 못했습니다.')
    } finally {
      setLoading(false)
    }
  }

  const loadMyPosts = async () => {
    try {
      setActivityLoading(true)

      const result = await getMyPosts({
        page: myPostsPage,
        size: 5,
      })

      if (result.success) {
        setMyPosts(result.data.content)
        setMyPostsPageInfo(result.data)
      }
    } catch (error) {
      console.error(error)
    } finally {
      setActivityLoading(false)
    }
  }

  const loadMyComments = async () => {
    try {
      setActivityLoading(true)

      const result = await getMyComments({
        page: myCommentsPage,
        size: 5,
      })

      if (result.success) {
        setMyComments(result.data.content)
        setMyCommentsPageInfo(result.data)
      }
    } catch (error) {
      console.error(error)
    } finally {
      setActivityLoading(false)
    }
  }

  const loadMyLikedPosts = async () => {
    try {
      setActivityLoading(true)

      const result = await getMyLikedPosts({
        page: myLikedPostsPage,
        size: 5,
      })

      if (result.success) {
        setMyLikedPosts(result.data.content)
        setMyLikedPostsPageInfo(result.data)
      }
    } catch (error) {
      console.error(error)
    } finally {
      setActivityLoading(false)
    }
  }

  const loadMySubscribedChannels = async () => {
    try {
      setActivityLoading(true)

      const result = await getMySubscribedChannels()

      if (result.success) {
        setMySubscribedChannels(result.data)
      }
    } catch (error) {
      console.error(error)
    } finally {
      setActivityLoading(false)
    }
  }

  const handleChannelNotificationChange = async (
    channel,
    notificationEnabled,
  ) => {
    try {
      setNotificationUpdatingChannelId(channel.channelId)

      const result = await updateChannelNotification(
        channel.slug,
        notificationEnabled,
      )

      if (result.success) {
        setMySubscribedChannels((prev) =>
          prev.map((item) =>
            item.channelId === channel.channelId
              ? {
                  ...item,
                  notificationEnabled:
                    result.data.notificationEnabled,
                }
              : item,
          ),
        )
      } else {
        alert(result.message || '새 글 알림 설정 변경에 실패했습니다.')
      }
    } catch (error) {
      console.error(error)

      alert(
        error.response?.data?.message ||
          '새 글 알림 설정 변경에 실패했습니다.',
      )
    } finally {
      setNotificationUpdatingChannelId(null)
    }
  }

  useEffect(() => {
    loadMyInfo()
  }, [])

  useEffect(() => {
    if (!user) {
      return
    }

    if (activeTab === 'posts') {
      loadMyPosts()
    }

    if (activeTab === 'comments') {
      loadMyComments()
    }

    if (activeTab === 'likes') {
      loadMyLikedPosts()
    }

    if (activeTab === 'channels') {
      loadMySubscribedChannels()
    }
  }, [user, activeTab, myPostsPage, myCommentsPage, myLikedPostsPage])

  const handleNicknameSubmit = async (e) => {
    e.preventDefault()

    const trimmedNickname = nickname.trim()

    if (!trimmedNickname) {
      alert('닉네임을 입력해주세요.')
      return
    }

    if (trimmedNickname.length < 2 || trimmedNickname.length > 20) {
      alert('닉네임은 2자 이상 20자 이하로 입력해주세요.')
      return
    }

    try {
      const result = await updateNickname({
        nickname: trimmedNickname,
      })

      if (result.success) {
        alert('닉네임이 변경되었습니다.')
        setUser(result.data)
        setNickname(result.data.nickname)
      } else {
        alert(result.message || '닉네임 변경에 실패했습니다.')
      }
    } catch (error) {
      console.error(error)

      const message =
        error.response?.data?.message || '닉네임 변경에 실패했습니다.'

      alert(message)
    }
  }

  const handlePasswordSubmit = async (e) => {
    e.preventDefault()

    if (!currentPassword.trim()) {
      alert('현재 비밀번호를 입력해주세요.')
      return
    }

    if (!newPassword.trim()) {
      alert('새 비밀번호를 입력해주세요.')
      return
    }

    if (newPassword.length < 8 || newPassword.length > 20) {
      alert('새 비밀번호는 8자 이상 20자 이하로 입력해주세요.')
      return
    }

    if (newPassword !== newPasswordConfirm) {
      alert('새 비밀번호 확인이 일치하지 않습니다.')
      return
    }

    try {
      const result = await updatePassword({
        currentPassword,
        newPassword,
      })

      if (result.success) {
        alert('비밀번호가 변경되었습니다.')
        setCurrentPassword('')
        setNewPassword('')
        setNewPasswordConfirm('')
      } else {
        alert(result.message || '비밀번호 변경에 실패했습니다.')
      }
    } catch (error) {
      console.error(error)

      const message =
        error.response?.data?.message || '비밀번호 변경에 실패했습니다.'

      alert(message)
    }
  }

  const handleDeleteMe = async (e) => {
    e.preventDefault()

    if (!deletePassword.trim()) {
      alert('비밀번호를 입력해주세요.')
      return
    }

    if (!window.confirm('정말 회원 탈퇴하시겠습니까?')) {
      return
    }

    try {
      const result = await deleteMe({
        password: deletePassword,
      })

      if (result.success) {
        alert('회원 탈퇴가 완료되었습니다.')
        localStorage.removeItem('accessToken')
        navigate('/posts', { replace: true })
      } else {
        alert(result.message || '회원 탈퇴에 실패했습니다.')
      }
    } catch (error) {
      console.error(error)

      const message =
        error.response?.data?.message || '회원 탈퇴에 실패했습니다.'

      alert(message)
    }
  }

  const handleTabChange = (tab) => {
    setActiveTab(tab)

    if (tab === 'posts') {
      setMyPostsPage(0)
    }

    if (tab === 'comments') {
      setMyCommentsPage(0)
    }

    if (tab === 'likes') {
      setMyLikedPostsPage(0)
    }

    if (tab === 'channels') {
      setMySubscribedChannels([])
    }
  }

  const handleProfileImageChange = async (e) => {
    const file = e.target.files?.[0]

    if (!file) {
      return
    }

    if (!file.type.startsWith('image/')) {
      alert('이미지 파일만 업로드할 수 있습니다.')
      e.target.value = ''
      return
    }

    try {
      const result = await updateProfileImage(file)

      if (result.success) {
        setUser((prev) => ({
          ...prev,
          profileImageUrl: result.data.profileImageUrl,
        }))

        alert('프로필 이미지가 변경되었습니다.')
      } else {
        alert(result.message || '프로필 이미지 변경에 실패했습니다.')
      }
    } catch (error) {
      console.error(error)
      alert(error.response?.data?.message || '프로필 이미지 변경에 실패했습니다.')
    } finally {
      e.target.value = ''
    }
  }

  if (loading) {
    return <p>불러오는 중...</p>
  }

  if (errorMessage) {
    return <p className="error-message">{errorMessage}</p>
  }

  if (!user) {
    return null
  }

  return (
    <div>
      <div className="page-title-row">
        <div>
          <h1>마이페이지</h1>
          <p>내 계정 정보를 확인하고 수정할 수 있습니다.</p>
        </div>
      </div>

      <div className="mypage-box">
        <div className="mypage-profile-box">
          <div className="profile-avatar large">
            {user.profileImageUrl ? (
              <img src={getImageUrl(user.profileImageUrl)} alt="프로필 이미지" />
            ) : (
              <span>{user.nickname?.charAt(0) || '?'}</span>
            )}
          </div>

          <div>
            <strong>{user.nickname}</strong>
            <p>프로필 이미지를 설정해 팬 커뮤니티에서 나를 표현해보세요.</p>

            <label className="secondary-button profile-upload-label">
              이미지 변경
              <input
                type="file"
                accept="image/*"
                onChange={handleProfileImageChange}
                hidden
              />
            </label>
          </div>
        </div>

        <div className="mypage-row">
          <span className="mypage-label">회원 번호</span>
          <span className="mypage-value">{user.userId}</span>
        </div>

        <div className="mypage-row">
          <span className="mypage-label">이메일</span>
          <span className="mypage-value">{user.email}</span>
        </div>

        <div className="mypage-row">
          <span className="mypage-label">닉네임</span>
          <span className="mypage-value">{user.nickname}</span>
        </div>

        <div className="mypage-row">
          <span className="mypage-label">권한</span>
          <span className="mypage-value">{user.role}</span>
        </div>

        <div className="mypage-row">
          <span className="mypage-label">상태</span>
          <span className="mypage-value">{user.status}</span>
        </div>

        <div className="mypage-row">
          <span className="mypage-label">가입일</span>
          <span className="mypage-value">{user.createdAt}</span>
        </div>
      </div>

      <div className="mypage-section">
        <h2>닉네임 변경</h2>

        <form onSubmit={handleNicknameSubmit} className="mypage-form">
          <div className="form-group">
            <label htmlFor="nickname">새 닉네임</label>
            <input
              id="nickname"
              type="text"
              value={nickname}
              onChange={(e) => setNickname(e.target.value)}
              placeholder="2자 이상 20자 이하"
            />
          </div>

          <button type="submit" className="primary-button">
            닉네임 변경
          </button>
        </form>
      </div>

      <div className="mypage-section">
        <h2>비밀번호 변경</h2>

        <form onSubmit={handlePasswordSubmit} className="mypage-form">
          <div className="form-group">
            <label htmlFor="currentPassword">현재 비밀번호</label>
            <input
              id="currentPassword"
              type="password"
              value={currentPassword}
              onChange={(e) => setCurrentPassword(e.target.value)}
              placeholder="현재 비밀번호"
            />
          </div>

          <div className="form-group">
            <label htmlFor="newPassword">새 비밀번호</label>
            <input
              id="newPassword"
              type="password"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              placeholder="8자 이상 20자 이하"
            />
          </div>

          <div className="form-group">
            <label htmlFor="newPasswordConfirm">새 비밀번호 확인</label>
            <input
              id="newPasswordConfirm"
              type="password"
              value={newPasswordConfirm}
              onChange={(e) => setNewPasswordConfirm(e.target.value)}
              placeholder="새 비밀번호를 다시 입력하세요"
            />
          </div>

          <button type="submit" className="primary-button">
            비밀번호 변경
          </button>
        </form>
      </div>

      <div className="mypage-section danger-section">
        <h2>회원 탈퇴</h2>
        <p>회원 탈퇴 시 계정은 탈퇴 상태로 변경되며 로그인이 제한됩니다.</p>

        <form onSubmit={handleDeleteMe} className="mypage-form">
          <div className="form-group">
            <label htmlFor="deletePassword">비밀번호 확인</label>
            <input
              id="deletePassword"
              type="password"
              value={deletePassword}
              onChange={(e) => setDeletePassword(e.target.value)}
              placeholder="비밀번호를 입력하세요"
            />
          </div>

          <button type="submit" className="danger-button">
            회원 탈퇴
          </button>
        </form>
      </div>

      <div className="mypage-section">
        <h2>내 활동</h2>

        <div className="mypage-tab-row">
          <button
            type="button"
            className={activeTab === 'posts' ? 'active' : ''}
            onClick={() => handleTabChange('posts')}
          >
            내가 쓴 게시글
          </button>

          <button
            type="button"
            className={activeTab === 'comments' ? 'active' : ''}
            onClick={() => handleTabChange('comments')}
          >
            내가 쓴 댓글
          </button>

          <button
            type="button"
            className={activeTab === 'likes' ? 'active' : ''}
            onClick={() => handleTabChange('likes')}
          >
            좋아요한 게시글
          </button>

          <button
            type="button"
            className={activeTab === 'channels' ? 'active' : ''}
            onClick={() => handleTabChange('channels')}
          >
            구독 채널
          </button>
        </div>

        {activityLoading ? (
          <p>불러오는 중...</p>
        ) : (
          <>
            {activeTab === 'posts' && (
              <div className="mypage-activity-list">
                {myPosts.length === 0 ? (
                  <div className="empty-box">작성한 게시글이 없습니다.</div>
                ) : (
                  myPosts.map((post) => (
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
                      </div>
                    </Link>
                  ))
                )}

                {myPostsPageInfo && !myPostsPageInfo.empty && (
                  <div className="pagination">
                    <button
                      type="button"
                      disabled={myPostsPageInfo.first}
                      onClick={() => setMyPostsPage((prev) => prev - 1)}
                    >
                      이전
                    </button>

                    <span>
                      {myPostsPageInfo.page + 1} / {myPostsPageInfo.totalPages}
                    </span>

                    <button
                      type="button"
                      disabled={myPostsPageInfo.last}
                      onClick={() => setMyPostsPage((prev) => prev + 1)}
                    >
                      다음
                    </button>
                  </div>
                )}
              </div>
            )}

            {activeTab === 'comments' && (
              <div className="mypage-activity-list">
                {myComments.length === 0 ? (
                  <div className="empty-box">작성한 댓글이 없습니다.</div>
                ) : (
                  myComments.map((comment) => (
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
                        <span>{comment.writerNickname}</span>
                        <span>{comment.createdAt}</span>
                      </div>
                    </Link>
                  ))
                )}

                {myCommentsPageInfo && !myCommentsPageInfo.empty && (
                  <div className="pagination">
                    <button
                      type="button"
                      disabled={myCommentsPageInfo.first}
                      onClick={() => setMyCommentsPage((prev) => prev - 1)}
                    >
                      이전
                    </button>

                    <span>
                      {myCommentsPageInfo.page + 1} / {myCommentsPageInfo.totalPages}
                    </span>

                    <button
                      type="button"
                      disabled={myCommentsPageInfo.last}
                      onClick={() => setMyCommentsPage((prev) => prev + 1)}
                    >
                      다음
                    </button>
                  </div>
                )}
              </div>
            )}

            {activeTab === 'likes' && (
              <div className="mypage-activity-list">
                {myLikedPosts.length === 0 ? (
                  <div className="empty-box">좋아요한 게시글이 없습니다.</div>
                ) : (
                  myLikedPosts.map((post) => (
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
                        <span>{post.writerNickname}</span>
                        <span>조회 {post.viewCount}</span>
                        <span>좋아요 {post.likeCount}</span>
                        <span>댓글 {post.commentCount}</span>
                      </div>
                    </Link>
                  ))
                )}

                {myLikedPostsPageInfo && !myLikedPostsPageInfo.empty && (
                  <div className="pagination">
                    <button
                      type="button"
                      disabled={myLikedPostsPageInfo.first}
                      onClick={() => setMyLikedPostsPage((prev) => prev - 1)}
                    >
                      이전
                    </button>

                    <span>
                      {myLikedPostsPageInfo.page + 1} / {myLikedPostsPageInfo.totalPages}
                    </span>

                    <button
                      type="button"
                      disabled={myLikedPostsPageInfo.last}
                      onClick={() => setMyLikedPostsPage((prev) => prev + 1)}
                    >
                      다음
                    </button>
                  </div>
                )}
              </div>
            )}

            {activeTab === 'channels' && (
              <div className="channel-list-grid">
                {mySubscribedChannels.length === 0 ? (
                  <div className="empty-box">구독한 채널이 없습니다.</div>
                ) : (
                  mySubscribedChannels.map((channel) => (
                    <div
                      key={channel.channelId}
                      className="channel-list-card"
                    >
                      <Link
                        to={`/channels/${channel.slug}`}
                        className="channel-list-card-link"
                      >
                        {channel.bannerImageUrl && (
                          <div className="channel-list-banner">
                            <img
                              src={getImageUrl(channel.bannerImageUrl)}
                              alt=""
                            />
                          </div>
                        )}

                        <div className="channel-list-body">
                          <div className="channel-list-profile">
                            {channel.profileImageUrl ? (
                              <img
                                src={getImageUrl(channel.profileImageUrl)}
                                alt={channel.name}
                              />
                            ) : (
                              <span>{channel.name?.charAt(0) || '?'}</span>
                            )}
                          </div>

                          <div className="channel-list-info">
                            <span className="home-eyebrow">
                              Subscribed
                            </span>

                            <strong>{channel.name}</strong>

                            <p>
                              {channel.description ||
                                '채널 설명이 없습니다.'}
                            </p>

                            <div className="channel-list-meta">
                              <span>
                                구독자 {channel.subscriberCount || 0}
                              </span>
                              <span>구독 중</span>
                            </div>
                          </div>
                        </div>
                      </Link>

                      <div className="channel-list-notification-row">
                        <span>새 글 알림</span>

                        <button
                          type="button"
                          className={
                            channel.notificationEnabled
                              ? 'secondary-button'
                              : 'secondary-button muted'
                          }
                          disabled={
                            notificationUpdatingChannelId ===
                            channel.channelId
                          }
                          onClick={() =>
                            handleChannelNotificationChange(
                              channel,
                              !channel.notificationEnabled,
                            )
                          }
                        >
                          {channel.notificationEnabled ? 'ON' : 'OFF'}
                        </button>
                      </div>
                    </div>
                  ))
                )}
              </div>
            )}
          </>
        )}
      </div>
    </div>
  )
}

export default MyPage