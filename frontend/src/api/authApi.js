import axiosInstance from './axiosInstance'

export const login = async ({ email, password }) => {
  const response = await axiosInstance.post('/api/auth/login', {
    email,
    password,
  })

  return response.data
}

export const signup = async ({ email, password, nickname }) => {
  const response = await axiosInstance.post('/api/users/signup', {
    email,
    password,
    nickname,
  })

  return response.data
}

export const getMyInfo = async () => {
  const response = await axiosInstance.get('/api/users/me')

  return response.data
}

export const updateNickname = async ({ nickname }) => {
  const response = await axiosInstance.patch('/api/users/me/nickname', {
    nickname,
  })

  return response.data
}

export const updatePassword = async ({ currentPassword, newPassword }) => {
  const response = await axiosInstance.patch('/api/users/me/password', {
    currentPassword,
    newPassword,
  })

  return response.data
}

export const deleteMe = async ({ password }) => {
  const response = await axiosInstance.delete('/api/users/me', {
    data: {
      password,
    },
  })

  return response.data
}

export const getMyPosts = async ({ page = 0, size = 5 }) => {
  const response = await axiosInstance.get('/api/users/me/posts', {
    params: {
      page,
      size,
    },
  })

  return response.data
}

export const getMyComments = async ({ page = 0, size = 5 }) => {
  const response = await axiosInstance.get('/api/users/me/comments', {
    params: {
      page,
      size,
    },
  })

  return response.data
}

export const getMyLikedPosts = async ({ page = 0, size = 5 }) => {
  const response = await axiosInstance.get('/api/users/me/likes', {
    params: {
      page,
      size,
    },
  })

  return response.data
}