import axiosInstance from './axiosInstance'

export const getComments = async (postId) => {
  const response = await axiosInstance.get(`/api/posts/${postId}/comments`)

  return response.data
}

export const createComment = async (postId, { content }) => {
  const response = await axiosInstance.post(`/api/posts/${postId}/comments`, {
    content,
  })

  return response.data
}

export const deleteComment = async (commentId) => {
  const response = await axiosInstance.delete(`/api/comments/${commentId}`)

  return response.data
}

export const updateComment = async (commentId, { content }) => {
  const response = await axiosInstance.put(
    `/api/comments/${commentId}`,
    {
      content,
    },
  )

  return response.data
}

export const createReply = async (
  parentCommentId,
  { content },
) => {
  const response = await axiosInstance.post(
    `/api/comments/${parentCommentId}/replies`,
    {
      content,
    },
  )

  return response.data
}

export const likeComment = async (commentId) => {
  const response = await axiosInstance.post(
    `/api/comments/${commentId}/likes`,
  )

  return response.data
}

export const unlikeComment = async (commentId) => {
  const response = await axiosInstance.delete(
    `/api/comments/${commentId}/likes`,
  )

  return response.data
}

export const getMyCommentLikeStatus = async (commentId) => {
  const response = await axiosInstance.get(
    `/api/comments/${commentId}/likes/me`,
  )

  return response.data
}

export const blindChannelComment = async (
  commentId,
) => {
  const response = await axiosInstance.patch(
    `/api/channel-management/comments/${commentId}/blind`,
  )

  return response.data
}

export const unblindChannelComment = async (
  commentId,
) => {
  const response = await axiosInstance.patch(
    `/api/channel-management/comments/${commentId}/unblind`,
  )

  return response.data
}