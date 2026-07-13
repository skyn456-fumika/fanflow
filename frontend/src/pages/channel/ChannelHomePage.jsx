import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { getChannel } from '../../api/channelApi'
import { getChannelBoards } from '../../api/boardApi'
import { getChannelPosts } from '../../api/postApi'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || ''

const getImageUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return `${API_BASE_URL}${url}`
}

function ChannelHomePage() {
  const { channelSlug } = useParams()
  const navigate = useNavigate()

  const [channel, setChannel] = useState(null)
  const [boards, setBoards] = useState([])
  const [recentPosts, setRecentPosts] = useState([])

  const [loading, setLoading] = useState(false)
  const [errorMessage, setErrorMessage] = useState('')

  const loadChannelHome = async () => {
    try {
      setLoading(true)
      setErrorMessage('')

      const [channelResult, boardsResult, postsResult] = await Promise.all([
        getChannel(channelSlug),
        getChannelBoards(channelSlug),
        getChannelPosts({
          channelSlug,
          page: 0,
          size: 6,
        }),
      ])

      if (!channelResult.success) {
        setErrorMessage(channelResult.message || '채널 정보를 불러오지 못했습니다.')
        return
      }

      setChannel(channelResult.data)

      if (boardsResult.success) {
        setBoards(boardsResult.data)
      }

      if (postsResult.success) {
        setRecentPosts(postsResult.data.content)
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

      <section className="home-section">
        <div className="home-section-title">
          <div>
            <h2>최근 게시글</h2>
            <p>이 채널에 최근 올라온 팬들의 이야기입니다.</p>
          </div>

          <Link
            to={`/channels/${channel.slug}/posts`}
            className="secondary-button"
          >
            더보기
          </Link>
        </div>

        {recentPosts.length === 0 ? (
          <div className="empty-box">아직 게시글이 없습니다.</div>
        ) : (
          <div className="home-post-list">
            {recentPosts.map((post) => (
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
            ))}
          </div>
        )}
      </section>
    </div>
  )
}

export default ChannelHomePage