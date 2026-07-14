import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { getChannels, getMySubscribedChannels } from '../../api/channelApi'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || ''

const getImageUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return `${API_BASE_URL}${url}`
}

function ChannelListPage() {
  const [channels, setChannels] = useState([])
  const [loading, setLoading] = useState(false)
  const [errorMessage, setErrorMessage] = useState('')

  const loadChannels = async () => {
    try {
      setLoading(true)
      setErrorMessage('')

      const result = await getChannels()

      if (!result.success) {
        setErrorMessage(result.message || '채널 목록을 불러오지 못했습니다.')
        return
      }

      let channelList = result.data

      const token = localStorage.getItem('accessToken')

      if (token) {
        try {
          const subscribedResult = await getMySubscribedChannels()

          if (subscribedResult.success) {
            const subscribedSlugs = new Set(
              subscribedResult.data.map((channel) => channel.slug),
            )

            channelList = channelList.map((channel) => ({
              ...channel,
              subscribed: subscribedSlugs.has(channel.slug),
            }))
          }
        } catch (error) {
          console.error(error)
        }
      }

      setChannels(channelList)
    } catch (error) {
      console.error(error)
      setErrorMessage('채널 목록을 불러오지 못했습니다.')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadChannels()
  }, [])

  if (loading) {
    return <p>불러오는 중...</p>
  }

  return (
    <div>
      <div className="page-title-row">
        <div>
          <h1>채널</h1>
          <p>FanFlow에서 운영 중인 팬 채널을 둘러보세요.</p>
        </div>
      </div>

      {errorMessage && <p className="error-message">{errorMessage}</p>}

      {channels.length === 0 ? (
        <div className="empty-box">등록된 채널이 없습니다.</div>
      ) : (
        <div className="channel-list-grid">
          {channels.map((channel) => (
            <Link
              key={channel.channelId}
              to={`/channels/${channel.slug}`}
              className="channel-list-card"
            >
              {channel.bannerImageUrl && (
                <div className="channel-list-banner">
                  <img src={getImageUrl(channel.bannerImageUrl)} alt="" />
                </div>
              )}

              <div className="channel-list-body">
                <div className="channel-list-profile">
                  {channel.profileImageUrl ? (
                    <img src={getImageUrl(channel.profileImageUrl)} alt={channel.name} />
                  ) : (
                    <span>{channel.name?.charAt(0) || '?'}</span>
                  )}
                </div>

                <div className="channel-list-info">
                  <span className="home-eyebrow">Fan Channel</span>
                  <strong>{channel.name}</strong>
                  <p>{channel.description || '채널 설명이 없습니다.'}</p>
                  <div className="channel-list-meta">
                    <span>구독자 {channel.subscriberCount || 0}</span>
                    {channel.subscribed && <span>구독 중</span>}
                  </div>
                </div>
              </div>
            </Link>
          ))}
        </div>
      )}
    </div>
  )
}

export default ChannelListPage