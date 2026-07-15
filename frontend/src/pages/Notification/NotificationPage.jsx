import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import {
  deleteNotification,
  deleteReadNotifications,
  getNotifications,
  readAllNotifications,
  readNotification,
} from '../../api/notificationApi'

function NotificationPage() {
  const navigate = useNavigate()

  const [notifications, setNotifications] = useState([])
  const [pageInfo, setPageInfo] = useState(null)
  const [page, setPage] = useState(0)

  const [loading, setLoading] = useState(false)
  const [readAllLoading, setReadAllLoading] = useState(false)
  const [deletingNotificationId, setDeletingNotificationId] = useState(null)
  const [deleteReadLoading, setDeleteReadLoading] = useState(false)
  const [errorMessage, setErrorMessage] = useState('')

  const hasUnreadNotification = notifications.some(
    (notification) => !notification.readStatus,
  )

  const hasReadNotification = notifications.some(
    (notification) => notification.readStatus,
  )

  const loadNotifications = async () => {
    try {
      setLoading(true)
      setErrorMessage('')

      const result = await getNotifications({
        page,
        size: 10,
      })

      if (result.success) {
        setNotifications(result.data.content)
        setPageInfo(result.data)
      } else {
        setErrorMessage(result.message || '알림 목록을 불러오지 못했습니다.')
      }
    } catch (error) {
      console.error(error)

      if (error.response?.status === 401) {
        localStorage.removeItem('accessToken')
        alert('로그인이 필요합니다.')
        navigate('/login', { replace: true })
        return
      }

      setErrorMessage('알림 목록을 불러오지 못했습니다.')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadNotifications()
  }, [page])

  const getTypeLabel = (type) => {
    if (type === 'COMMENT_ON_POST') {
      return '댓글'
    }

    if (type === 'REPLY_ON_COMMENT') {
      return '답글'
    }

    if (type === 'POST_BLINDED') {
      return '게시글 블라인드'
    }

    if (type === 'COMMENT_BLINDED') {
      return '댓글 블라인드'
    }

    if (type === 'SUBSCRIBED_CHANNEL_NEW_POST') {
      return '구독 채널 새 글'
    }

    return '알림'
  }

  const handleNotificationClick = async (notification) => {
    try {
      if (!notification.readStatus) {
        await readNotification(notification.notificationId)
        window.dispatchEvent(new Event('notification-change'))
      }

      if (notification.targetPostId) {
        const targetPath = notification.targetCommentId
          ? `/posts/${notification.targetPostId}#comment-${notification.targetCommentId}`
          : `/posts/${notification.targetPostId}`

        navigate(targetPath)
        return
      }

      await loadNotifications()
    } catch (error) {
      console.error(error)

      if (error.response?.status === 401) {
        localStorage.removeItem('accessToken')
        alert('로그인이 필요합니다.')
        navigate('/login', { replace: true })
        return
      }

      alert(error.response?.data?.message || '알림 처리에 실패했습니다.')
    }
  }

  const handleReadAll = async () => {
    try {
      setReadAllLoading(true)

      const result = await readAllNotifications()

      if (result.success) {
        setNotifications((prev) =>
          prev.map((notification) => ({
            ...notification,
            readStatus: true,
          })),
        )

        window.dispatchEvent(new Event('notification-change'))
      } else {
        alert(result.message || '전체 읽음 처리에 실패했습니다.')
      }
    } catch (error) {
      console.error(error)

      if (error.response?.status === 401) {
        localStorage.removeItem('accessToken')
        alert('로그인이 필요합니다.')
        navigate('/login', { replace: true })
        return
      }

      alert(
        error.response?.data?.message ||
          '전체 읽음 처리에 실패했습니다.',
      )
    } finally {
      setReadAllLoading(false)
    }
  }

  const handleDeleteNotification = async (e, notification) => {
    e.stopPropagation()

    if (!window.confirm('이 알림을 삭제하시겠습니까?')) {
      return
    }

    try {
      setDeletingNotificationId(notification.notificationId)

      const result = await deleteNotification(
        notification.notificationId,
      )

      if (result.success) {
        if (!notification.readStatus) {
          window.dispatchEvent(new Event('notification-change'))
        }

        await loadNotifications()
      } else {
        alert(result.message || '알림 삭제에 실패했습니다.')
      }
    } catch (error) {
      console.error(error)

      if (error.response?.status === 401) {
        localStorage.removeItem('accessToken')
        alert('로그인이 필요합니다.')
        navigate('/login', { replace: true })
        return
      }

      alert(
        error.response?.data?.message ||
          '알림 삭제에 실패했습니다.',
      )
    } finally {
      setDeletingNotificationId(null)
    }
  }

  const handleDeleteReadNotifications = async () => {
    if (!window.confirm('읽은 알림을 모두 삭제하시겠습니까?')) {
      return
    }

    try {
      setDeleteReadLoading(true)

      const result = await deleteReadNotifications()

      if (!result.success) {
        alert(result.message || '읽은 알림 삭제에 실패했습니다.')
        return
      }

      const remainingNotifications = notifications.filter(
        (notification) => !notification.readStatus,
      )

      if (remainingNotifications.length === 0 && page > 0) {
        setPage((prev) => prev - 1)
        return
      }

      await loadNotifications()
    } catch (error) {
      console.error(error)

      if (error.response?.status === 401) {
        localStorage.removeItem('accessToken')
        alert('로그인이 필요합니다.')
        navigate('/login', { replace: true })
        return
      }

      alert(
        error.response?.data?.message ||
          '읽은 알림 삭제에 실패했습니다.',
      )
    } finally {
      setDeleteReadLoading(false)
    }
  }

  return (
    <div>
      <div className="page-title-row">
        <div>
          <h1>알림</h1>
          <p>댓글, 운영 처리, 구독 채널의 새 글 알림을 확인합니다.</p>
        </div>

        <div className="notification-page-actions">
          <button
            type="button"
            className="secondary-button"
            onClick={handleReadAll}
            disabled={
              readAllLoading ||
              deleteReadLoading ||
              loading ||
              notifications.length === 0 ||
              !hasUnreadNotification
            }
          >
            {readAllLoading ? '처리 중...' : '전체 읽음'}
          </button>

          <button
            type="button"
            className="danger-button"
            onClick={handleDeleteReadNotifications}
            disabled={
              deleteReadLoading ||
              readAllLoading ||
              loading ||
              notifications.length === 0 ||
              !hasReadNotification
            }
          >
            {deleteReadLoading ? '삭제 중...' : '읽은 알림 삭제'}
          </button>
        </div>
      </div>

      <div className="notification-section">
        {errorMessage && <p className="error-message">{errorMessage}</p>}

        {loading ? (
          <p>불러오는 중...</p>
        ) : notifications.length === 0 ? (
          <div className="empty-box">알림이 없습니다.</div>
        ) : (
          <div className="notification-list">
            {notifications.map((notification) => (
              <div
                key={notification.notificationId}
                className={`notification-item ${
                  notification.readStatus ? 'read' : 'unread'
                }`}
                role="button"
                tabIndex={0}
                onClick={() => handleNotificationClick(notification)}
                onKeyDown={(e) => {
                  if (e.key === 'Enter' || e.key === ' ') {
                    handleNotificationClick(notification)
                  }
                }}
              >
                <div className="notification-item-header">
                  <span className="notification-type">
                    {getTypeLabel(notification.type)}
                  </span>

                  {!notification.readStatus && (
                    <span className="notification-unread-dot">NEW</span>
                  )}
                </div>

                {notification.channelName && (
                  <div className="notification-path">
                    <span>{notification.channelName}</span>
                    <span>&gt;</span>
                    <span>{notification.boardName || '게시판'}</span>
                  </div>
                )}

                {notification.targetPostTitle && (
                  <p className="notification-target-title">
                    {notification.targetPostTitle}
                  </p>
                )}

                <p>{notification.message}</p>

                <div className="notification-meta">
                  <div className="notification-meta-info">
                    <span>{notification.createdAt}</span>

                    {notification.targetPostId && (
                      <span>
                        {notification.targetCommentId
                          ? '댓글 위치로 이동'
                          : '게시글로 이동'}
                      </span>
                    )}
                  </div>

                  <button
                    type="button"
                    className="notification-delete-button"
                    disabled={
                      deletingNotificationId === notification.notificationId
                    }
                    onClick={(e) =>
                      handleDeleteNotification(e, notification)
                    }
                  >
                    {deletingNotificationId === notification.notificationId
                      ? '삭제 중...'
                      : '삭제'}
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}

        {pageInfo && !pageInfo.empty && (
          <div className="pagination">
            <button
              type="button"
              disabled={pageInfo.first}
              onClick={() => setPage((prev) => prev - 1)}
            >
              이전
            </button>

            <span>
              {pageInfo.page + 1} / {pageInfo.totalPages}
            </span>

            <button
              type="button"
              disabled={pageInfo.last}
              onClick={() => setPage((prev) => prev + 1)}
            >
              다음
            </button>
          </div>
        )}
      </div>
    </div>
  )
}

export default NotificationPage