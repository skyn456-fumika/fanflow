import axiosInstance from './axiosInstance'

export const getManagedChannelMembers = async (
  channelSlug,
) => {
  const response = await axiosInstance.get(
    `/api/channel-management/channels/${channelSlug}/members`,
  )

  return response.data
}

export const searchChannelManagerCandidates = async (
  channelSlug,
  keyword,
) => {
  const response = await axiosInstance.get(
    `/api/channel-management/channels/${channelSlug}/manager-candidates`,
    {
      params: {
        keyword,
      },
    },
  )

  return response.data
}

export const assignChannelManager = async (
  channelSlug,
  userId,
) => {
  const response = await axiosInstance.post(
    `/api/channel-management/channels/${channelSlug}/managers`,
    {
      userId,
    },
  )

  return response.data
}

export const removeChannelManager = async (
  channelSlug,
  userId,
) => {
  const response = await axiosInstance.delete(
    `/api/channel-management/channels/${channelSlug}/managers/${userId}`,
  )

  return response.data
}

export const getManagedChannel = async (
  channelSlug,
) => {
  const response = await axiosInstance.get(
    `/api/channel-management/channels/${channelSlug}`,
  )

  return response.data
}

export const updateManagedChannel = async (
  channelSlug,
  {
    name,
    description,
  },
) => {
  const response = await axiosInstance.put(
    `/api/channel-management/channels/${channelSlug}`,
    {
      name,
      description,
    },
  )

  return response.data
}

export const uploadManagedChannelProfileImage = async (
  channelSlug,
  file,
) => {
  const formData = new FormData()
  formData.append('file', file)

  const response = await axiosInstance.post(
    `/api/channel-management/channels/${channelSlug}/profile-image`,
    formData,
  )

  return response.data
}

export const uploadManagedChannelBannerImage = async (
  channelSlug,
  file,
) => {
  const formData = new FormData()
  formData.append('file', file)

  const response = await axiosInstance.post(
    `/api/channel-management/channels/${channelSlug}/banner-image`,
    formData,
  )

  return response.data
}

export const getManagedChannelBoards = async (
  channelSlug,
) => {
  const response = await axiosInstance.get(
    `/api/channel-management/channels/${channelSlug}/boards`,
  )

  return response.data
}

export const createManagedChannelBoard = async (
  channelSlug,
  payload,
) => {
  const response = await axiosInstance.post(
    `/api/channel-management/channels/${channelSlug}/boards`,
    payload,
  )

  return response.data
}

export const updateManagedChannelBoard = async (
  channelSlug,
  boardId,
  payload,
) => {
  const response = await axiosInstance.put(
    `/api/channel-management/channels/${channelSlug}/boards/${boardId}`,
    payload,
  )

  return response.data
}

export const activateManagedChannelBoard = async (
  channelSlug,
  boardId,
) => {
  const response = await axiosInstance.patch(
    `/api/channel-management/channels/${channelSlug}/boards/${boardId}/activate`,
  )

  return response.data
}

export const deactivateManagedChannelBoard = async (
  channelSlug,
  boardId,
) => {
  const response = await axiosInstance.patch(
    `/api/channel-management/channels/${channelSlug}/boards/${boardId}/deactivate`,
  )

  return response.data
}