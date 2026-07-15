import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import {
  clearRecentPosts,
  getRecentPosts,
  removeRecentPost,
} from '../../utils/recentPostStorage'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || ''

const getImageUrl = (url) => {
  if (!url) return ''

  if (
    url.startsWith('http://') ||
    url.startsWith('https://')
  ) {
    return url
  }

  return `${API_BASE_URL}${url}`
}

const formatViewedAt = (viewedAt) => {
  if (!viewedAt) {
    return ''
  }

  const date = new Date(viewedAt)

  if (Number.isNaN(date.getTime())) {
    return ''
  }

  return date.toLocaleString('ko-KR')
}

function RecentPostPage() {
  const [recentPosts, setRecentPosts] = useState([])

  const loadRecentPosts = () => {
    setRecentPosts(getRecentPosts())
  }

  useEffect(() => {
    loadRecentPosts()

    const handleRecentPostsChange = () => {
      loadRecentPosts()
    }

    window.addEventListener(
      'recent-posts-change',
      handleRecentPostsChange,
    )

    window.addEventListener(
      'storage',
      handleRecentPostsChange,
    )

    return () => {
      window.removeEventListener(
        'recent-posts-change',
        handleRecentPostsChange,
      )

      window.removeEventListener(
        'storage',
        handleRecentPostsChange,
      )
    }
  }, [])

  const handleRemove = (postId) => {
    setRecentPosts(removeRecentPost(postId))
  }

  const handleClear = () => {
    if (
      !window.confirm(
        '최근 본 게시글을 모두 삭제하시겠습니까?',
      )
    ) {
      return
    }

    clearRecentPosts()
    setRecentPosts([])
  }

  return (
    <div>
      <div className="page-title-row">
        <div>
          <h1>최근 본 게시글</h1>
          <p>
            이 브라우저에서 최근 확인한 게시글을 모아봅니다.
          </p>
        </div>

        <button
          type="button"
          className="danger-button"
          onClick={handleClear}
          disabled={recentPosts.length === 0}
        >
          전체 삭제
        </button>
      </div>

      {recentPosts.length === 0 ? (
        <div className="empty-box">
          최근 본 게시글이 없습니다.
        </div>
      ) : (
        <div className="recent-post-list">
          {recentPosts.map((post) => (
            <article
              key={post.postId}
              className="recent-post-item"
            >
              {post.thumbnailUrl && (
                <Link
                  to={`/posts/${post.postId}`}
                  className="recent-post-thumbnail"
                >
                  <img
                    src={getImageUrl(post.thumbnailUrl)}
                    alt=""
                  />
                </Link>
              )}

              <div className="recent-post-content">
                <div className="recent-post-path">
                  {post.channelSlug ? (
                    <Link
                      to={`/channels/${post.channelSlug}`}
                    >
                      {post.channelName || '채널'}
                    </Link>
                  ) : (
                    <span>{post.channelName || '채널'}</span>
                  )}

                  <span>&gt;</span>

                  {post.channelSlug && post.boardCode ? (
                    <Link
                      to={`/channels/${post.channelSlug}/posts?boardCode=${post.boardCode}`}
                    >
                      {post.boardName || '게시판'}
                    </Link>
                  ) : (
                    <span>{post.boardName || '게시판'}</span>
                  )}
                </div>

                <Link
                  to={`/posts/${post.postId}`}
                  className="recent-post-title"
                >
                  {post.title}
                </Link>

                <div className="post-meta">
                  <span>{post.writerNickname}</span>
                  <span>조회 {post.viewCount}</span>
                  <span>좋아요 {post.likeCount}</span>
                  <span>댓글 {post.commentCount}</span>

                  {post.viewedAt && (
                    <span>
                      최근 확인 {formatViewedAt(post.viewedAt)}
                    </span>
                  )}
                </div>
              </div>

              <button
                type="button"
                className="recent-post-remove-button"
                onClick={() => handleRemove(post.postId)}
              >
                삭제
              </button>
            </article>
          ))}
        </div>
      )}
    </div>
  )
}

export default RecentPostPage