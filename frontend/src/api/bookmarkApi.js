import axiosInstance from './axiosInstance'

export const getBookmarkStatus = async (postId) => {
  const response = await axiosInstance.get(
    `/api/posts/${postId}/bookmark`,
  )

  return response.data
}

export const addBookmark = async (postId) => {
  const response = await axiosInstance.post(
    `/api/posts/${postId}/bookmark`,
  )

  return response.data
}

export const removeBookmark = async (postId) => {
  const response = await axiosInstance.delete(
    `/api/posts/${postId}/bookmark`,
  )

  return response.data
}

export const getMyBookmarks = async ({
  page = 0,
  size = 5,
}) => {
  const response = await axiosInstance.get('/api/users/me/bookmarks', {
    params: {
      page,
      size,
    },
  })

  return response.data
}