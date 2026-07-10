import { Route, Routes } from 'react-router-dom'
import Layout from '../components/layout/Layout'
import PostListPage from '../pages/post/PostListPage'
import PostDetailPage from '../pages/post/PostDetailPage'
import PostWritePage from '../pages/post/PostWritePage'
import LoginPage from '../pages/auth/LoginPage'
import SignupPage from '../pages/auth/SignupPage'
import MyPage from '../pages/user/MyPage'
import AdminPage from '../pages/admin/AdminPage'
import HomePage from '../pages/home/HomePage'

function AppRouter() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route path="/" element={<HomePage />} />
        <Route path="/posts" element={<PostListPage />} />
        <Route path="/posts/write" element={<PostWritePage />} />
        <Route path="/posts/:postId" element={<PostDetailPage />} />
        <Route path="/posts/:postId/edit" element={<PostWritePage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/signup" element={<SignupPage />} />
        <Route path="/mypage" element={<MyPage />} />
        <Route path="/admin" element={<AdminPage />} />
      </Route>
    </Routes>
  )
}

export default AppRouter