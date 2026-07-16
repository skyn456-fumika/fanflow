import axiosInstance from './axiosInstance'

export const getPosts = async ({ boardCode, keyword, page = 0, size = 10 }) => {
  const response = await axiosInstance.get('/api/posts', {
    params: {
      boardCode: boardCode || undefined,
      keyword: keyword || undefined,
      page,
      size,
    },
  })

  return response.data
}

export const getPostDetail = async (postId) => {
  const response = await axiosInstance.get(`/api/posts/${postId}`)

  return response.data
}

export const createPost = async ({ boardCode, title, content }) => {
  const response = await axiosInstance.post('/api/posts', {
    boardCode,
    title,
    content,
  })

  return response.data
}

export const updatePost = async (postId, { title, content }) => {
  const response = await axiosInstance.put(`/api/posts/${postId}`, {
    title,
    content,
  })

  return response.data
}

export const deletePost = async (postId) => {
  const response = await axiosInstance.delete(`/api/posts/${postId}`)

  return response.data
}

export const uploadPostImage = async (file) => {
  const formData = new FormData()
  formData.append('file', file)

  const response = await axiosInstance.post('/api/posts/images', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })

  return response.data
}

export const getChannelPosts = async ({
  channelSlug,
  boardCode,
  keyword,
  page = 0,
  size = 10,
}) => {
  const response = await axiosInstance.get(`/api/channels/${channelSlug}/posts`, {
    params: {
      boardCode: boardCode || undefined,
      keyword: keyword || undefined,
      page,
      size,
    },
  })

  return response.data
}

export const createChannelPost = async ({
  channelSlug,
  boardCode,
  title,
  content,
}) => {
  const response = await axiosInstance.post(`/api/channels/${channelSlug}/posts`, {
    boardCode,
    title,
    content,
  })

  return response.data
}

export const blindChannelPost = async (postId) => {
  const response = await axiosInstance.patch(
    `/api/channel-management/posts/${postId}/blind`,
  )

  return response.data
}

export const unblindChannelPost = async (postId) => {
  const response = await axiosInstance.patch(
    `/api/channel-management/posts/${postId}/unblind`,
  )

  return response.data
}