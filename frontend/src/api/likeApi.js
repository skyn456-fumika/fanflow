import axiosInstance from './axiosInstance'

export const getMyLikeStatus = async (postId) => {
  const response = await axiosInstance.get(`/api/posts/${postId}/likes/me`)

  return response.data
}

export const likePost = async (postId) => {
  const response = await axiosInstance.post(`/api/posts/${postId}/likes`)

  return response.data
}

export const unlikePost = async (postId) => {
  const response = await axiosInstance.delete(`/api/posts/${postId}/likes`)

  return response.data
}