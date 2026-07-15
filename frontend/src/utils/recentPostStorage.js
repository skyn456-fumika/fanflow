const RECENT_POSTS_KEY = 'fanflowRecentPosts'
const MAX_RECENT_POSTS = 20

const parseRecentPosts = (value) => {
  try {
    const parsed = JSON.parse(value)

    return Array.isArray(parsed) ? parsed : []
  } catch (error) {
    console.error('최근 본 게시글 정보를 읽지 못했습니다.', error)
    return []
  }
}

export const getRecentPosts = () => {
  const savedValue = localStorage.getItem(RECENT_POSTS_KEY)

  if (!savedValue) {
    return []
  }

  return parseRecentPosts(savedValue)
}

export const saveRecentPost = (post) => {
  if (!post?.postId) {
    return
  }

  const currentPosts = getRecentPosts()

  const recentPost = {
    postId: post.postId,

    channelName: post.channelName || '',
    channelSlug: post.channelSlug || '',

    boardCode: post.boardCode || '',
    boardName: post.boardName || '',

    writerNickname: post.writerNickname || '',

    title: post.title || '',

    viewCount: post.viewCount || 0,
    likeCount: post.likeCount || 0,
    commentCount: post.commentCount || 0,

    thumbnailUrl: post.thumbnailUrl || '',
    createdAt: post.createdAt || '',

    viewedAt: new Date().toISOString(),
  }

  const filteredPosts = currentPosts.filter(
    (item) => String(item.postId) !== String(post.postId),
  )

  const nextPosts = [
    recentPost,
    ...filteredPosts,
  ].slice(0, MAX_RECENT_POSTS)

  localStorage.setItem(
    RECENT_POSTS_KEY,
    JSON.stringify(nextPosts),
  )

  window.dispatchEvent(new Event('recent-posts-change'))
}

export const removeRecentPost = (postId) => {
  const nextPosts = getRecentPosts().filter(
    (item) => String(item.postId) !== String(postId),
  )

  localStorage.setItem(
    RECENT_POSTS_KEY,
    JSON.stringify(nextPosts),
  )

  window.dispatchEvent(new Event('recent-posts-change'))

  return nextPosts
}

export const clearRecentPosts = () => {
  localStorage.removeItem(RECENT_POSTS_KEY)
  window.dispatchEvent(new Event('recent-posts-change'))
}