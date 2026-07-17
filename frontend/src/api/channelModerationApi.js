import axiosInstance from './axiosInstance'

export const getBlindChannelPosts = async (
  channelSlug,
) => {
  const response = await axiosInstance.get(
    `/api/channel-management/channels/${channelSlug}/blind-posts`,
  )

  return response.data
}

export const getBlindChannelComments = async (
  channelSlug,
) => {
  const response = await axiosInstance.get(
    `/api/channel-management/channels/${channelSlug}/blind-comments`,
  )

  return response.data
}

export const unblindModeratedPost = async (
  postId,
) => {
  const response = await axiosInstance.patch(
    `/api/channel-management/posts/${postId}/unblind`,
  )

  return response.data
}

export const unblindModeratedComment = async (
  commentId,
) => {
  const response = await axiosInstance.patch(
    `/api/channel-management/comments/${commentId}/unblind`,
  )

  return response.data
}