import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import {
  getChannelHome,
  getChannelSubscription,
  subscribeChannel,
  unsubscribeChannel,
  updateChannelNotification,
} from '../../api/channelApi'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || ''

const getImageUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return `${API_BASE_URL}${url}`
}

function ChannelHomePostCard({ post }) {
  return (
    <Link
      key={post.postId}
      to={`/posts/${post.postId}`}
      className="home-post-card"
    >
      {post.thumbnailUrl && (
        <div className="home-post-thumbnail">
          <img src={getImageUrl(post.thumbnailUrl)} alt="" />
        </div>
      )}

      <div className="home-post-card-top">
        <div className="post-path">
          <span className="post-path-board">{post.boardName}</span>
        </div>

        {post.notice && <span className="notice-badge">공지</span>}
      </div>

      <strong>{post.title}</strong>

      <div className="post-meta">
        <span>{post.writerNickname}</span>
        <span>조회 {post.viewCount}</span>
        <span>좋아요 {post.likeCount}</span>
        <span>댓글 {post.commentCount}</span>
      </div>
    </Link>
  )
}

function ChannelHomePostSection({ title, description, posts, emptyText, moreLink }) {
  return (
    <section className="home-section">
      <div className="home-section-title">
        <div>
          <h2>{title}</h2>
          <p>{description}</p>
        </div>

        {moreLink && (
          <Link to={moreLink} className="secondary-button">
            더보기
          </Link>
        )}
      </div>

      {posts.length === 0 ? (
        <div className="empty-box">{emptyText}</div>
      ) : (
        <div className="home-post-list">
          {posts.map((post) => (
            <ChannelHomePostCard key={post.postId} post={post} />
          ))}
        </div>
      )}
    </section>
  )
}

function ChannelHomePage() {
  const { channelSlug } = useParams()
  const navigate = useNavigate()

  const [channel, setChannel] = useState(null)
  const [boards, setBoards] = useState([])
  const [noticePosts, setNoticePosts] = useState([])
  const [popularPosts, setPopularPosts] = useState([])
  const [recentPosts, setRecentPosts] = useState([])

  const [loading, setLoading] = useState(false)
  const [errorMessage, setErrorMessage] = useState('')

  const [subscribed, setSubscribed] = useState(false)
  const [subscriberCount, setSubscriberCount] = useState(0)
  const [subscriptionLoading, setSubscriptionLoading] = useState(false)

  const [notificationEnabled, setNotificationEnabled] = useState(false)
  const [notificationLoading, setNotificationLoading] = useState(false)

  const loadChannelHome = async () => {
    try {
      setLoading(true)
      setErrorMessage('')

      const result = await getChannelHome(channelSlug)

      if (!result.success) {
        setErrorMessage(result.message || '채널 홈을 불러오지 못했습니다.')
        return
      }

      setChannel(result.data.channel)
      setBoards(result.data.boards || [])
      setNoticePosts(result.data.noticePosts || [])
      setPopularPosts(result.data.popularPosts || [])
      setRecentPosts(result.data.recentPosts || [])
      
      setSubscribed(result.data.channel.subscribed || false)
      setSubscriberCount(result.data.channel.subscriberCount || 0)

      const token = localStorage.getItem('accessToken')

      if (token) {
        try {
          const subscriptionResult = await getChannelSubscription(channelSlug)

          if (subscriptionResult.success) {
            setSubscribed(subscriptionResult.data.subscribed)
            setSubscriberCount(subscriptionResult.data.subscriberCount)
            setNotificationEnabled(
              subscriptionResult.data.notificationEnabled || false,
            )
          }
        } catch (error) {
          console.error(error)
        }
      }
    } catch (error) {
      console.error(error)

      if (error.response?.status === 404) {
        setErrorMessage('채널을 찾을 수 없습니다.')
        return
      }

      setErrorMessage('채널 홈을 불러오지 못했습니다.')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    if (!channelSlug) {
      navigate('/', { replace: true })
      return
    }

    loadChannelHome()
  }, [channelSlug])

  const handleToggleSubscription = async () => {
    const token = localStorage.getItem('accessToken')

    if (!token) {
      alert('로그인이 필요합니다.')
      navigate('/login', {
        state: {
          from: `/channels/${channelSlug}`,
        },
      })
      return
    }

    try {
      setSubscriptionLoading(true)

      const result = subscribed
        ? await unsubscribeChannel(channelSlug)
        : await subscribeChannel(channelSlug)

      if (result.success) {
        setSubscribed(result.data.subscribed)
        setSubscriberCount(result.data.subscriberCount)
        setNotificationEnabled(
          result.data.notificationEnabled || false,
        )
      } else {
        alert(result.message || '채널 구독 처리에 실패했습니다.')
      }
    } catch (error) {
      console.error(error)
      alert(error.response?.data?.message || '채널 구독 처리에 실패했습니다.')
    } finally {
      setSubscriptionLoading(false)
    }
  }

  const handleToggleNotification = async () => {
    try {
      setNotificationLoading(true)

      const result = await updateChannelNotification(
        channelSlug,
        !notificationEnabled,
      )

      if (result.success) {
        setNotificationEnabled(result.data.notificationEnabled)
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
      setNotificationLoading(false)
    }
  }

  if (loading) {
    return <p>불러오는 중...</p>
  }

  if (errorMessage) {
    return <p className="error-message">{errorMessage}</p>
  }

  if (!channel) {
    return null
  }

  return (
    <div className="channel-home-page">
      <section className="channel-hero">
        {channel.bannerImageUrl && (
          <div className="channel-banner">
            <img src={getImageUrl(channel.bannerImageUrl)} alt="" />
          </div>
        )}

        <div className="channel-hero-content">
          <div className="channel-profile-image">
            {channel.profileImageUrl ? (
              <img src={getImageUrl(channel.profileImageUrl)} alt={channel.name} />
            ) : (
              <span>{channel.name?.charAt(0) || '?'}</span>
            )}
          </div>

          <div className="channel-info">
            <span className="home-eyebrow">Fan Channel</span>
            <h1>{channel.name}</h1>
            <p>{channel.description || '채널 설명이 없습니다.'}</p>

            <div className="channel-subscription-row">
              <span>구독자 {subscriberCount}</span>

              <div className="channel-subscription-actions">
                <button
                  type="button"
                  className={subscribed ? 'secondary-button' : 'primary-button'}
                  onClick={handleToggleSubscription}
                  disabled={subscriptionLoading}
                >
                  {subscribed ? '구독 중' : '구독하기'}
                </button>

                {subscribed && (
                  <button
                    type="button"
                    className={
                      notificationEnabled
                        ? 'secondary-button'
                        : 'secondary-button muted'
                    }
                    onClick={handleToggleNotification}
                    disabled={notificationLoading}
                  >
                    {notificationEnabled
                      ? '새 글 알림 ON'
                      : '새 글 알림 OFF'}
                  </button>
                )}
              </div>
            </div>

            <div className="channel-action-row">
              <Link
                to={`/channels/${channel.slug}/posts`}
                className="primary-button"
              >
                게시글 보기
              </Link>

              <Link
                to={`/channels/${channel.slug}/posts/write`}
                className="secondary-button"
              >
                글쓰기
              </Link>
            </div>
          </div>
        </div>
      </section>

      <section className="home-section">
        <div className="home-section-title">
          <div>
            <h2>채널 게시판</h2>
            <p>이 채널에서 운영 중인 게시판입니다.</p>
          </div>
        </div>

        {boards.length === 0 ? (
          <div className="empty-box">게시판이 없습니다.</div>
        ) : (
          <div className="channel-board-grid">
            {boards.map((board) => (
              <Link
                key={board.boardId}
                to={`/channels/${channel.slug}/posts?boardCode=${board.code}`}
                className="channel-board-card"
              >
                <span className="board-badge">{board.code}</span>
                <strong>{board.name}</strong>
                <p>{board.description || '게시판 설명이 없습니다.'}</p>
              </Link>
            ))}
          </div>
        )}
      </section>

      <ChannelHomePostSection
        title="공지사항"
        description="이 채널에서 꼭 확인해야 할 공지입니다."
        posts={noticePosts}
        emptyText="등록된 공지사항이 없습니다."
        moreLink={`/channels/${channel.slug}/posts?boardCode=NOTICE`}
      />

      <div className="home-grid">
        <ChannelHomePostSection
          title="인기글"
          description="팬들이 많이 반응한 글입니다."
          posts={popularPosts}
          emptyText="아직 인기글이 없습니다."
          moreLink={`/channels/${channel.slug}/posts`}
        />

        <ChannelHomePostSection
          title="최신글"
          description="최근 올라온 팬들의 이야기입니다."
          posts={recentPosts}
          emptyText="아직 게시글이 없습니다."
          moreLink={`/channels/${channel.slug}/posts`}
        />
      </div>
    </div>
  )
}

export default ChannelHomePage