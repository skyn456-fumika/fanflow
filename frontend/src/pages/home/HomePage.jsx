import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { getMain } from '../../api/mainApi'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || ''

const getImageUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return `${API_BASE_URL}${url}`
}

function HomePostCard({ post }) {
  return (
    <Link to={`/posts/${post.postId}`} className="home-post-card">
      {post.thumbnailUrl && (
        <div className="home-post-thumbnail">
          <img src={getImageUrl(post.thumbnailUrl)} alt="" />
        </div>
      )}

      <div className="home-post-card-top">
        <span className="board-badge">{post.boardName}</span>
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
  const [mainData, setMainData] = useState({
    noticePosts: [],
    popularPosts: [],
    recentPosts: [],
    commentedPosts: [],
  })

  const [loading, setLoading] = useState(false)
  const [errorMessage, setErrorMessage] = useState('')

  const loadMain = async () => {
    try {
      setLoading(true)
      setErrorMessage('')

      const result = await getMain()

      if (result.success) {
        setMainData(result.data)
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

            <Link to="/channels/fumika" className="secondary-button">
              기본 채널
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