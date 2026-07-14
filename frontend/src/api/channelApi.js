import axiosInstance from './axiosInstance'

export const getChannels = async () => {
  const response = await axiosInstance.get('/api/channels')

  return response.data
}

export const getChannel = async (channelSlug) => {
  const response = await axiosInstance.get(`/api/channels/${channelSlug}`)

  return response.data
}

export const getChannelHome = async (channelSlug) => {
  const response = await axiosInstance.get(`/api/channels/${channelSlug}/home`)

  return response.data
}

export const subscribeChannel = async (channelSlug) => {
  const response = await axiosInstance.post(`/api/channels/${channelSlug}/subscribe`)

  return response.data
}

export const unsubscribeChannel = async (channelSlug) => {
  const response = await axiosInstance.delete(`/api/channels/${channelSlug}/subscribe`)

  return response.data
}

export const getChannelSubscription = async (channelSlug) => {
  const response = await axiosInstance.get(`/api/channels/${channelSlug}/subscription`)

  return response.data
}

export const getMySubscribedChannels = async () => {
  const response = await axiosInstance.get('/api/users/me/subscribed-channels')

  return response.data
}

export const updateChannelNotification = async (
  channelSlug,
  notificationEnabled,
) => {
  const response = await axiosInstance.patch(
    `/api/channels/${channelSlug}/subscription/notification`,
    {
      notificationEnabled,
    },
  )

  return response.data
}