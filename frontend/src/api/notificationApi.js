import axiosInstance from './axiosInstance'

export const getNotifications = async ({ page = 0, size = 10 }) => {
  const response = await axiosInstance.get('/api/notifications', {
    params: {
      page,
      size,
    },
  })

  return response.data
}

export const getUnreadNotificationCount = async () => {
  const response = await axiosInstance.get('/api/notifications/unread-count')

  return response.data
}

export const readNotification = async (notificationId) => {
  const response = await axiosInstance.patch(
    `/api/notifications/${notificationId}/read`,
  )

  return response.data
}

export const readAllNotifications = async () => {
  const response = await axiosInstance.patch('/api/notifications/read-all')

  return response.data
}

export const deleteNotification = async (notificationId) => {
  const response = await axiosInstance.delete(
    `/api/notifications/${notificationId}`,
  )

  return response.data
}