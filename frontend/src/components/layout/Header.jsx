import { useEffect, useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { getMyInfo } from '../../api/authApi'

function Header() {
  const navigate = useNavigate()
  const location = useLocation()

  const [me, setMe] = useState(null)
  const [isLoggedIn, setIsLoggedIn] = useState(false)

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

  useEffect(() => {
    loadMyInfo()
  }, [location.pathname])

  const handleLogout = () => {
    localStorage.removeItem('accessToken')
    setIsLoggedIn(false)
    setMe(null)
    // alert('로그아웃되었습니다.')

    const currentPath = location.pathname

    const shouldMoveHome =
      currentPath === '/mypage' ||
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
              <Link to="/mypage">마이페이지</Link>

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