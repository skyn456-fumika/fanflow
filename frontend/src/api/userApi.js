import axiosInstance from './axiosInstance'

export const getUserProfile = async (userId) => {
  const response = await axiosInstance.get(`/api/users/${userId}/profile`)

  return response.data
}

export const getUserPosts = async ({ userId, page = 0, size = 5 }) => {
  const response = await axiosInstance.get(`/api/users/${userId}/posts`, {
    params: {
      page,
      size,
    },
  })

  return response.data
}

export const getUserComments = async ({ userId, page = 0, size = 5 }) => {
  const response = await axiosInstance.get(`/api/users/${userId}/comments`, {
    params: {
      page,
      size,
    },
  })

  return response.data
}