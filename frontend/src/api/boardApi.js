import axiosInstance from './axiosInstance'

export const getBoards = async () => {
  const response = await axiosInstance.get('/api/boards')
  return response.data
}