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