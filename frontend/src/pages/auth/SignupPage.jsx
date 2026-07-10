import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { signup } from '../../api/authApi'

function SignupPage() {
  const navigate = useNavigate()

  const [form, setForm] = useState({
    email: '',
    password: '',
    passwordConfirm: '',
    nickname: '',
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

    if (form.password.length < 8 || form.password.length > 20) {
      alert('비밀번호는 8자 이상 20자 이하로 입력해주세요.')
      return
    }

    if (form.password !== form.passwordConfirm) {
      alert('비밀번호 확인이 일치하지 않습니다.')
      return
    }

    if (!form.nickname.trim()) {
      alert('닉네임을 입력해주세요.')
      return
    }

    if (form.nickname.length < 2 || form.nickname.length > 20) {
      alert('닉네임은 2자 이상 20자 이하로 입력해주세요.')
      return
    }

    try {
      setLoading(true)
      setErrorMessage('')

      const result = await signup({
        email: form.email,
        password: form.password,
        nickname: form.nickname,
      })

      if (result.success) {
        alert('회원가입이 완료되었습니다. 로그인해주세요.')
        navigate('/login')
      } else {
        setErrorMessage(result.message || '회원가입에 실패했습니다.')
      }
    } catch (error) {
      console.error(error)

      const message =
        error.response?.data?.message || '회원가입에 실패했습니다.'

      setErrorMessage(message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth-page">
      <div className="auth-box">
        <h1>회원가입</h1>
        <p>FanFlow 계정을 생성하세요.</p>

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
              placeholder="8자 이상 20자 이하"
            />
          </div>

          <div className="form-group">
            <label htmlFor="passwordConfirm">비밀번호 확인</label>
            <input
              id="passwordConfirm"
              name="passwordConfirm"
              type="password"
              value={form.passwordConfirm}
              onChange={handleChange}
              placeholder="비밀번호를 다시 입력하세요"
            />
          </div>

          <div className="form-group">
            <label htmlFor="nickname">닉네임</label>
            <input
              id="nickname"
              name="nickname"
              type="text"
              value={form.nickname}
              onChange={handleChange}
              placeholder="2자 이상 20자 이하"
            />
          </div>

          <button
            type="submit"
            className="primary-button auth-submit"
            disabled={loading}
          >
            {loading ? '가입 중...' : '회원가입'}
          </button>
        </form>

        <div className="auth-link-row">
          이미 계정이 있나요? <Link to="/login">로그인</Link>
        </div>
      </div>
    </div>
  )
}

export default SignupPage