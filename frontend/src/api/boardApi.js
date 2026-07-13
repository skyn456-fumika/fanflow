import axiosInstance from './axiosInstance'

export const getBoards = async () => {
  const response = await axiosInstance.get('/api/boards')
  return response.data
}

export const getChannelBoards = async (channelSlug) => {
  const response = await axiosInstance.get(`/api/channels/${channelSlug}/boards`)

  return response.data
}