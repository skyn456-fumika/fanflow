import axiosInstance from './axiosInstance'

export const blockUser = async (userId) => {
  const response = await axiosInstance.post(
    `/api/users/${userId}/blocks`,
  )

  return response.data
}

export const unblockUser = async (userId) => {
  const response = await axiosInstance.delete(
    `/api/users/${userId}/blocks`,
  )

  return response.data
}

export const getUserBlockStatus = async (userId) => {
  const response = await axiosInstance.get(
    `/api/users/${userId}/blocks/me`,
  )

  return response.data
}

export const getMyBlockedUsers = async ({
  page = 0,
  size = 10,
}) => {
  const response = await axiosInstance.get(
    '/api/users/me/blocks',
    {
      params: {
        page,
        size,
      },
    },
  )

  return response.data
}