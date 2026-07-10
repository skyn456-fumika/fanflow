import { useState } from 'react'
import { Link, useNavigate, useLocation } from 'react-router-dom'
import { login } from '../../api/authApi'

function LoginPage() {
  const location = useLocation()
  const from = location.state?.from || '/'
  const navigate = useNavigate()

  const [form, setForm] = useState({
    email: '',
    password: '',
  })

  const [loading, setLoading] = useState(false)
  const [errorMessage, setErrorMessage] = useState('')

  const handleChange = (e) => {
    const { name, value } = e.target

    setForm((prev) => ({
      ...prev,
      [name]: value,
    }))
  }

  const handleSubmit = async (e) => {
    e.preventDefault()

    if (!form.email.trim()) {
      alert('이메일을 입력해주세요.')
      return
    }

    if (!form.password.trim()) {
      alert('비밀번호를 입력해주세요.')
      return
    }

    try {
      setLoading(true)
      setErrorMessage('')

      const result = await login(form)

      if (result.success) {
        const token =
          result.data.accessToken ||
          result.data.token ||
          result.data.access_token

        if (!token) {
          setErrorMessage('로그인 응답에서 토큰을 찾지 못했습니다.')
          return
        }

        localStorage.setItem('accessToken', token)

        //alert('로그인되었습니다.')
        navigate(from, { replace: true })
      } else {
        setErrorMessage(result.message || '로그인에 실패했습니다.')
      }
    } catch (error) {
      console.error(error)

      const message =
        error.response?.data?.message || '로그인에 실패했습니다.'

      setErrorMessage(message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth-page">
      <div className="auth-box">
        <h1>로그인</h1>
        <p>FanFlow 계정으로 로그인하세요.</p>

        {errorMessage && <p className="error-message">{errorMessage}</p>}

        <form onSubmit={handleSubmit} className="auth-form">
          <div className="form-group">
            <label htmlFor="email">이메일</label>
            <input
              id="email"
              name="email"
              type="email"
              value={form.email}
              onChange={handleChange}
              placeholder="이메일을 입력하세요"
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">비밀번호</label>
            <input
              id="password"
              name="password"
              type="password"
              value={form.password}
              onChange={handleChange}
              placeholder="비밀번호를 입력하세요"
            />
          </div>

          <button type="submit" className="primary-button auth-submit" disabled={loading}>
            {loading ? '로그인 중...' : '로그인'}
          </button>
        </form>

        <div className="auth-link-row">
          아직 계정이 없나요? <Link to="/signup">회원가입</Link>
        </div>
      </div>
    </div>
  )
}

export default LoginPage