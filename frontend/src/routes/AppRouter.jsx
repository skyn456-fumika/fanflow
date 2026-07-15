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
import UserProfilePage from '../pages/user/UserProfilePage'
import NotificationPage from '../pages/notification/NotificationPage'
import ChannelHomePage from '../pages/channel/ChannelHomePage'
import ChannelListPage from '../pages/channel/ChannelListPage'
import FeedPage from '../pages/feed/FeedPage'
import RecentPostPage from '../pages/post/RecentPostPage'

function AppRouter() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route path="/" element={<HomePage />} />
        <Route path="/posts" element={<PostListPage />} />
        <Route path="/recent-posts" element={<RecentPostPage />} />
        <Route path="/posts/write" element={<PostWritePage />} />
        <Route path="/posts/:postId" element={<PostDetailPage />} />
        <Route path="/posts/:postId/edit" element={<PostWritePage />} />
        <Route path="/channels" element={<ChannelListPage />} />
        <Route path="/feed" element={<FeedPage />} />
        <Route path="/channels/:channelSlug" element={<ChannelHomePage />} />
        <Route path="/channels/:channelSlug/posts" element={<PostListPage />} />
        <Route path="/channels/:channelSlug/posts/write" element={<PostWritePage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/signup" element={<SignupPage />} />
        <Route path="/mypage" element={<MyPage />} />
        <Route path="/notifications" element={<NotificationPage />} />
        <Route path="/users/:userId" element={<UserProfilePage />} />
        <Route path="/admin" element={<AdminPage />} />
      </Route>
    </Routes>
  )
}

export default AppRouter