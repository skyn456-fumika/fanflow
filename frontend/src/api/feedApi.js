import axiosInstance from './axiosInstance'

export const getSubscriptionFeed = async ({ page = 0, size = 10 }) => {
  const response = await axiosInstance.get('/api/feed/subscriptions', {
    params: {
      page,
      size,
    },
  })

  return response.data
}