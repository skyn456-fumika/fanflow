import { useEffect, useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { getMyInfo } from '../../api/authApi'
import { getUnreadNotificationCount } from '../../api/notificationApi'

function Header() {
  const navigate = useNavigate()
  const location = useLocation()

  const [me, setMe] = useState(null)
  const [isLoggedIn, setIsLoggedIn] = useState(false)

  const [unreadCount, setUnreadCount] = useState(0)

  const getImageUrl = (imageUrl) => {
    if (!imageUrl) {
      return null
    }

    if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
      return imageUrl
    }

    return `${import.meta.env.VITE_API_BASE_URL}${imageUrl}`
  }

  const loadMyInfo = async () => {
    const token = localStorage.getItem('accessToken')

    if (!token) {
      setIsLoggedIn(false)
      setMe(null)
      return
    }

    try {
      const result = await getMyInfo()

      if (result.success) {
        setIsLoggedIn(true)
        setMe(result.data)
      } else {
        setIsLoggedIn(false)
        setMe(null)
      }
    } catch (error) {
      console.error(error)
      localStorage.removeItem('accessToken')
      setIsLoggedIn(false)
      setMe(null)
    }
  }

  const loadUnreadCount = async () => {
    const token = localStorage.getItem('accessToken')

    if (!token) {
      setUnreadCount(0)
      return
    }

    try {
      const result = await getUnreadNotificationCount()

      if (result.success) {
        setUnreadCount(result.data.unreadCount)
      } else {
        setUnreadCount(0)
      }
    } catch (error) {
      console.error(error)
      setUnreadCount(0)
    }
  }

  useEffect(() => {
    loadMyInfo()
    loadUnreadCount()
  }, [location.pathname])

  const handleLogout = () => {
    localStorage.removeItem('accessToken')
    setIsLoggedIn(false)
    setMe(null)
    setUnreadCount(0)
    // alert('로그아웃되었습니다.')

    const currentPath = location.pathname

    const shouldMoveHome =
      currentPath === '/mypage' ||
      currentPath === '/notifications' ||
      currentPath === '/admin' ||
      currentPath === '/posts/write' ||
      currentPath.endsWith('/edit')

    if (shouldMoveHome) {
      navigate('/', { replace: true })
    }
  }

  return (
    <header className="header">
      <div className="header-inner">
        <Link to="/" className="logo">
          FanFlow
        </Link>

        <nav className="nav">
          <Link to="/posts">게시글</Link>

          {isLoggedIn ? (
            <>
              <Link to="/mypage" className="nav-profile-link">
                <span className="profile-avatar nav-profile-avatar">
                  {me?.profileImageUrl ? (
                    <img src={getImageUrl(me.profileImageUrl)} alt="내 프로필" />
                  ) : (
                    <span>{me?.nickname?.charAt(0) || '?'}</span>
                  )}
                </span>

                <span className="nav-profile-name">{me?.nickname}</span>
              </Link>

              <Link to="/notifications" className="nav-notification-link">
                알림

                {unreadCount > 0 && (
                  <span className="nav-notification-badge">
                    {unreadCount > 99 ? '99+' : unreadCount}
                  </span>
                )}
              </Link>

              {me?.role === 'ADMIN' && <Link to="/admin">관리자</Link>}

              <button type="button" className="nav-button" onClick={handleLogout}>
                로그아웃
              </button>
            </>
          ) : (
            <>
              <Link
                to="/login"
                state={{ from: location.pathname }}
              >
                로그인
              </Link>
              <Link to="/signup">회원가입</Link>
            </>
          )}
        </nav>
      </div>
    </header>
  )
}

export default Header