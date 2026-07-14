import axiosInstance from './axiosInstance'

export const getSubscriptionFeed = async ({
  page = 0,
  size = 10,
  channelSlug = '',
  sort = 'latest',
}) => {
  const response = await axiosInstance.get('/api/feed/subscriptions', {
    params: {
      page,
      size,
      channelSlug: channelSlug || undefined,
      sort,
    },
  })

  return response.data
}