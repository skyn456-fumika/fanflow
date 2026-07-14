import axiosInstance from './axiosInstance'

export const getAdminUsers = async ({
  status,
  keyword,
  page = 0,
  size = 10,
}) => {
  const response = await axiosInstance.get('/api/admin/users', {
    params: {
      status: status || undefined,
      keyword: keyword || undefined,
      page,
      size,
    },
  })

  return response.data
}

export const blockUser = async (userId) => {
  const response = await axiosInstance.patch(`/api/admin/users/${userId}/block`)

  return response.data
}

export const activateUser = async (userId) => {
  const response = await axiosInstance.patch(
    `/api/admin/users/${userId}/activate`,
  )

  return response.data
}

export const getAdminPosts = async ({
  boardCode,
  keyword,
  page = 0,
  size = 10,
}) => {
  const response = await axiosInstance.get('/api/admin/posts', {
    params: {
      boardCode: boardCode || undefined,
      keyword: keyword || undefined,
      page,
      size,
    },
  })

  return response.data
}

export const blindPost = async (postId) => {
  const response = await axiosInstance.patch(`/api/admin/posts/${postId}/blind`)

  return response.data
}

export const unblindPost = async (postId) => {
  const response = await axiosInstance.patch(
    `/api/admin/posts/${postId}/unblind`,
  )

  return response.data
}

export const getAdminComments = async ({
  keyword,
  page = 0,
  size = 10,
}) => {
  const response = await axiosInstance.get('/api/admin/comments', {
    params: {
      keyword: keyword || undefined,
      page,
      size,
    },
  })

  return response.data
}

export const blindComment = async (commentId) => {
  const response = await axiosInstance.patch(
    `/api/admin/comments/${commentId}/blind`,
  )

  return response.data
}

export const unblindComment = async (commentId) => {
  const response = await axiosInstance.patch(
    `/api/admin/comments/${commentId}/unblind`,
  )

  return response.data
}

export const getAdminReports = async ({
  channelSlug,
  status,
  targetType,
  page = 0,
  size = 10,
}) => {
  const response = await axiosInstance.get('/api/admin/reports', {
    params: {
      channelSlug: channelSlug || undefined,
      status: status || undefined,
      targetType: targetType || undefined,
      page,
      size,
    },
  })

  return response.data
}

export const resolveReport = async (reportId) => {
  const response = await axiosInstance.patch(
    `/api/admin/reports/${reportId}/resolve`,
  )

  return response.data
}

export const getAdminDashboard = async () => {
  const response = await axiosInstance.get('/api/admin/dashboard')

  return response.data
}

export const getAdminChannels = async () => {
  const response = await axiosInstance.get('/api/admin/channels')

  return response.data
}

export const createAdminChannel = async ({
  name,
  slug,
  description,
  profileImageUrl,
  bannerImageUrl,
}) => {
  const response = await axiosInstance.post('/api/admin/channels', {
    name,
    slug,
    description,
    profileImageUrl,
    bannerImageUrl,
  })

  return response.data
}

export const updateAdminChannel = async (
  channelId,
  {
    name,
    slug,
    description,
    profileImageUrl,
    bannerImageUrl,
  },
) => {
  const response = await axiosInstance.put(`/api/admin/channels/${channelId}`, {
    name,
    slug,
    description,
    profileImageUrl,
    bannerImageUrl,
  })

  return response.data
}

export const activateAdminChannel = async (channelId) => {
  const response = await axiosInstance.patch(
    `/api/admin/channels/${channelId}/activate`,
  )

  return response.data
}

export const deactivateAdminChannel = async (channelId) => {
  const response = await axiosInstance.patch(
    `/api/admin/channels/${channelId}/deactivate`,
  )

  return response.data
}

export const getAdminChannelBoards = async (channelId) => {
  const response = await axiosInstance.get(`/api/admin/channels/${channelId}/boards`)

  return response.data
}

export const createAdminChannelBoard = async (
  channelId,
  {
    code,
    name,
    description,
    sortOrder,
  },
) => {
  const response = await axiosInstance.post(`/api/admin/channels/${channelId}/boards`, {
    code,
    name,
    description,
    sortOrder,
  })

  return response.data
}

export const updateAdminChannelBoard = async (
  channelId,
  boardId,
  {
    code,
    name,
    description,
    sortOrder,
  },
) => {
  const response = await axiosInstance.put(
    `/api/admin/channels/${channelId}/boards/${boardId}`,
    {
      code,
      name,
      description,
      sortOrder,
    },
  )

  return response.data
}

export const activateAdminChannelBoard = async (channelId, boardId) => {
  const response = await axiosInstance.patch(
    `/api/admin/channels/${channelId}/boards/${boardId}/activate`,
  )

  return response.data
}

export const deactivateAdminChannelBoard = async (channelId, boardId) => {
  const response = await axiosInstance.patch(
    `/api/admin/channels/${channelId}/boards/${boardId}/deactivate`,
  )

  return response.data
}