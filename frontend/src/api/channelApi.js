import axiosInstance from './axiosInstance'

export const getChannels = async () => {
  const response = await axiosInstance.get('/api/channels')

  return response.data
}

export const getChannel = async (channelSlug) => {
  const response = await axiosInstance.get(`/api/channels/${channelSlug}`)

  return response.data
}