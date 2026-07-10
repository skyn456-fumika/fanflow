import axiosInstance from './axiosInstance'

export const getMain = async () => {
  const response = await axiosInstance.get('/api/main')

  return response.data
}