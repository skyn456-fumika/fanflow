import { useEffect, useState } from 'react'
import { Link, useLocation } from 'react-router-dom'
import { getMain } from '../../api/mainApi'
import { getSubscriptionFeed } from '../../api/feedApi'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || ''

const getImageUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return `${API_BASE_URL}${url}`
}

function HomePostCard({ post }) {
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
          <Link to={channelPath}>
            {post.channelName || '채널'}
          </Link>

          <span>&gt;</span>

          <Link to={boardPath}>
            {post.boardName}
          </Link>
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

function HomeSection({ title, description, posts, emptyText }) {
  return (
    <section className="home-section">
      <div className="home-section-title">
        <div>
          <h2>{title}</h2>
          <p>{description}</p>
        </div>
      </div>

      {posts.length === 0 ? (
        <div className="empty-box">{emptyText}</div>
      ) : (
        <div className="home-post-list">
          {posts.map((post) => (
            <HomePostCard key={post.postId} post={post} />
          ))}
        </div>
      )}
    </section>
  )
}

function HomePage() {
  const location = useLocation()

  const [isLoggedIn, setIsLoggedIn] = useState(false)

  const [mainData, setMainData] = useState({
    noticePosts: [],
    popularPosts: [],
    recentPosts: [],
    commentedPosts: [],
    subscriptionPosts: [],
  })

  const [loading, setLoading] = useState(false)
  const [errorMessage, setErrorMessage] = useState('')

  const loadMain = async () => {
    try {
      setLoading(true)
      setErrorMessage('')

      const token = localStorage.getItem('accessToken')
      const loggedIn = !!token

      setIsLoggedIn(loggedIn)

      const result = await getMain()

      if (result.success) {
        let subscriptionPosts = []

        if (loggedIn) {
          try {
            const feedResult = await getSubscriptionFeed({
              page: 0,
              size: 5,
            })

            if (feedResult.success) {
              subscriptionPosts = feedResult.data.content
            }
          } catch (error) {
            console.error(error)
          }
        }

        setMainData({
          ...result.data,
          subscriptionPosts,
        })
      } else {
        setErrorMessage(result.message || '메인 페이지를 불러오지 못했습니다.')
      }
    } catch (error) {
      console.error(error)
      setErrorMessage('메인 페이지를 불러오지 못했습니다.')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadMain()
  }, [location.pathname])

  useEffect(() => {
    const handleAuthChange = () => {
      loadMain()
    }

    window.addEventListener('auth-change', handleAuthChange)

    return () => {
      window.removeEventListener('auth-change', handleAuthChange)
    }
  }, [])

  if (loading) {
    return <p>불러오는 중...</p>
  }

  if (errorMessage) {
    return <p className="error-message">{errorMessage}</p>
  }

  return (
    <div className="home-page">
      <section className="home-hero">
        <div>
          <span className="home-eyebrow">Fan Community</span>
          <h1>팬들의 이야기가 흐르는 공간, FanFlow</h1>
          <p>
            좋아하는 순간을 나누고, 응원과 이야기를 쌓아가는 팬 커뮤니티입니다.
          </p>

          <div className="home-hero-actions">
            <Link to="/channels" className="primary-button">
              채널 둘러보기
            </Link>
          </div>
        </div>

        <div className="home-hero-card">
          <strong>Today&apos;s FanFlow</strong>
          <p>공지, 인기글, 최신글을 한눈에 확인하세요.</p>

          <div className="home-stat-grid">
            <div>
              <span>{mainData.noticePosts.length}</span>
              <p>공지</p>
            </div>
            <div>
              <span>{mainData.popularPosts.length}</span>
              <p>인기글</p>
            </div>
            <div>
              <span>{mainData.recentPosts.length}</span>
              <p>최신글</p>
            </div>
          </div>
        </div>
      </section>

      {isLoggedIn && (
        <section className="home-section">
          <div className="home-section-title">
            <div>
              <h2>구독 채널 최신글</h2>
              <p>내가 구독한 채널에서 올라온 새 글입니다.</p>
            </div>

            <Link to="/feed" className="secondary-button">
              피드 보기
            </Link>
          </div>

          {mainData.subscriptionPosts.length === 0 ? (
            <div className="empty-box">
              구독 채널의 최신글이 없습니다. 관심 있는 채널을 구독해보세요.
            </div>
          ) : (
            <div className="home-post-list">
              {mainData.subscriptionPosts.map((post) => (
                <HomePostCard key={post.postId} post={post} />
              ))}
            </div>
          )}
        </section>
      )}

      <HomeSection
        title="공지사항"
        description="팬 커뮤니티에서 꼭 확인해야 할 소식입니다."
        posts={mainData.noticePosts}
        emptyText="등록된 공지사항이 없습니다."
      />

      <div className="home-grid">
        <HomeSection
          title="인기글"
          description="팬들이 많이 반응한 글입니다."
          posts={mainData.popularPosts}
          emptyText="아직 인기글이 없습니다."
        />

        <HomeSection
          title="최신글"
          description="방금 올라온 팬들의 이야기입니다."
          posts={mainData.recentPosts}
          emptyText="아직 게시글이 없습니다."
        />
      </div>

      <HomeSection
        title="댓글 많은 글"
        description="지금 이야기가 활발히 이어지고 있는 글입니다."
        posts={mainData.commentedPosts}
        emptyText="아직 댓글이 많은 글이 없습니다."
      />
    </div>
  )
}

export default HomePage