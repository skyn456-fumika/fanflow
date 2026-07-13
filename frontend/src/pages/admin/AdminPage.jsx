import { useEffect, useRef, useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import {
  activateAdminChannel,
  activateAdminChannelBoard,
  createAdminChannelBoard,
  deactivateAdminChannelBoard,
  getAdminChannelBoards,
  updateAdminChannelBoard,
  activateUser,
  blindComment,
  blindPost,
  blockUser,
  createAdminChannel,
  deactivateAdminChannel,
  getAdminChannels,
  getAdminComments,
  getAdminDashboard,
  getAdminPosts,
  getAdminReports,
  getAdminUsers,
  resolveReport,
  unblindComment,
  unblindPost,
  updateAdminChannel,
} from '../../api/adminApi'
import { getBoards } from '../../api/boardApi'
import { getMyInfo } from '../../api/authApi'

function AdminPage() {
  const location = useLocation()
  const navigate = useNavigate()
  const alertShownRef = useRef(false)

  const [me, setMe] = useState(null)

  const [users, setUsers] = useState([])
  const [pageInfo, setPageInfo] = useState(null)

  const [status, setStatus] = useState('')
  const [keyword, setKeyword] = useState('')
  const [page, setPage] = useState(0)

  const [loading, setLoading] = useState(false)
  const [errorMessage, setErrorMessage] = useState('')

  const size = 10

  const [activeAdminTab, setActiveAdminTab] = useState('dashboard')

  const [boards, setBoards] = useState([])

  const [posts, setPosts] = useState([])
  const [postPageInfo, setPostPageInfo] = useState(null)
  const [postBoardCode, setPostBoardCode] = useState('')
  const [postKeyword, setPostKeyword] = useState('')
  const [postPage, setPostPage] = useState(0)
  const [postLoading, setPostLoading] = useState(false)
  const [postChannelSlug, setPostChannelSlug] = useState('')
  const [postFilterBoards, setPostFilterBoards] = useState([])

  const [comments, setComments] = useState([])
  const [commentPageInfo, setCommentPageInfo] = useState(null)
  const [commentKeyword, setCommentKeyword] = useState('')
  const [commentPage, setCommentPage] = useState(0)
  const [commentLoading, setCommentLoading] = useState(false)

  const [reports, setReports] = useState([])
  const [reportPageInfo, setReportPageInfo] = useState(null)
  const [reportStatus, setReportStatus] = useState('')
  const [reportTargetType, setReportTargetType] = useState('')
  const [reportPage, setReportPage] = useState(0)
  const [reportLoading, setReportLoading] = useState(false)

  const [dashboard, setDashboard] = useState(null)
  const [dashboardLoading, setDashboardLoading] = useState(false)

  const [channels, setChannels] = useState([])
  const [channelLoading, setChannelLoading] = useState(false)
  const [editingChannelId, setEditingChannelId] = useState(null)

  const [channelForm, setChannelForm] = useState({
    name: '',
    slug: '',
    description: '',
    profileImageUrl: '',
    bannerImageUrl: '',
  })

  const [selectedChannelForBoards, setSelectedChannelForBoards] = useState(null)
  const [channelBoards, setChannelBoards] = useState([])
  const [channelBoardLoading, setChannelBoardLoading] = useState(false)
  const [editingChannelBoardId, setEditingChannelBoardId] = useState(null)

  const [channelBoardForm, setChannelBoardForm] = useState({
    code: '',
    name: '',
    description: '',
    sortOrder: 0,
  })

  const requireLogin = () => {
    if (!alertShownRef.current) {
      alertShownRef.current = true
      alert('로그인이 필요합니다.')
    }

    navigate('/login', {
      replace: true,
      state: {
        from: location.pathname,
      },
    })
  }

  const requireAdmin = () => {
    if (!alertShownRef.current) {
      alertShownRef.current = true
      alert('관리자만 접근할 수 있습니다.')
    }

    navigate('/posts', { replace: true })
  }

  const loadMe = async () => {
    const token = localStorage.getItem('accessToken')

    if (!token) {
      requireLogin()
      return false
    }

    try {
      const result = await getMyInfo()

      if (result.success) {
        setMe(result.data)

        if (result.data.role !== 'ADMIN') {
          requireAdmin()
          return false
        }

        return true
      }

      requireLogin()
      return false
    } catch (error) {
      console.error(error)

      if (error.response?.status === 401) {
        localStorage.removeItem('accessToken')
        requireLogin()
        return false
      }

      if (error.response?.status === 403) {
        requireAdmin()
        return false
      }

      setErrorMessage('관리자 정보를 확인하지 못했습니다.')
      return false
    }
  }

  const loadUsers = async () => {
    try {
      setLoading(true)
      setErrorMessage('')

      const result = await getAdminUsers({
        status,
        keyword,
        page,
        size,
      })

      if (result.success) {
        setUsers(result.data.content)
        setPageInfo(result.data)
      } else {
        setErrorMessage(result.message || '회원 목록을 불러오지 못했습니다.')
      }
    } catch (error) {
      console.error(error)

      if (error.response?.status === 401) {
        localStorage.removeItem('accessToken')
        requireLogin()
        return
      }

      if (error.response?.status === 403) {
        requireAdmin()
        return
      }

      setErrorMessage('회원 목록을 불러오지 못했습니다.')
    } finally {
      setLoading(false)
    }
  }

  const loadBoards = async () => {
    try {
      const result = await getBoards()

      if (result.success) {
        setBoards(result.data)
      }
    } catch (error) {
      console.error(error)
    }
  }

  const loadPosts = async () => {
    try {
      setPostLoading(true)
      setErrorMessage('')

      const result = await getAdminPosts({
        channelSlug: postChannelSlug,
        boardCode: postBoardCode,
        keyword: postKeyword,
        page: postPage,
        size,
      })

      if (result.success) {
        setPosts(result.data.content)
        setPostPageInfo(result.data)
      } else {
        setErrorMessage(result.message || '게시글 목록을 불러오지 못했습니다.')
      }
    } catch (error) {
      console.error(error)

      if (error.response?.status === 401) {
        localStorage.removeItem('accessToken')
        requireLogin()
        return
      }

      if (error.response?.status === 403) {
        requireAdmin()
        return
      }

      setErrorMessage('게시글 목록을 불러오지 못했습니다.')
    } finally {
      setPostLoading(false)
    }
  }

  const loadComments = async () => {
    try {
      setCommentLoading(true)
      setErrorMessage('')

      const result = await getAdminComments({
        keyword: commentKeyword,
        page: commentPage,
        size,
      })

      if (result.success) {
        setComments(result.data.content)
        setCommentPageInfo(result.data)
      } else {
        setErrorMessage(result.message || '댓글 목록을 불러오지 못했습니다.')
      }
    } catch (error) {
      console.error(error)

      if (error.response?.status === 401) {
        localStorage.removeItem('accessToken')
        requireLogin()
        return
      }

      if (error.response?.status === 403) {
        requireAdmin()
        return
      }

      setErrorMessage('댓글 목록을 불러오지 못했습니다.')
    } finally {
      setCommentLoading(false)
    }
  }

  const loadReports = async () => {
    try {
      setReportLoading(true)
      setErrorMessage('')

      const result = await getAdminReports({
        status: reportStatus,
        targetType: reportTargetType,
        page: reportPage,
        size,
      })

      if (result.success) {
        setReports(result.data.content)
        setReportPageInfo(result.data)
      } else {
        setErrorMessage(result.message || '신고 목록을 불러오지 못했습니다.')
      }
    } catch (error) {
      console.error(error)

      if (error.response?.status === 401) {
        localStorage.removeItem('accessToken')
        requireLogin()
        return
      }

      if (error.response?.status === 403) {
        requireAdmin()
        return
      }

      setErrorMessage('신고 목록을 불러오지 못했습니다.')
    } finally {
      setReportLoading(false)
    }
  }

  const loadDashboard = async () => {
    try {
      setDashboardLoading(true)
      setErrorMessage('')

      const result = await getAdminDashboard()

      if (result.success) {
        setDashboard(result.data)
      } else {
        setErrorMessage(result.message || '대시보드를 불러오지 못했습니다.')
      }
    } catch (error) {
      console.error(error)

      if (error.response?.status === 401) {
        localStorage.removeItem('accessToken')
        requireLogin()
        return
      }

      if (error.response?.status === 403) {
        requireAdmin()
        return
      }

      setErrorMessage('대시보드를 불러오지 못했습니다.')
    } finally {
      setDashboardLoading(false)
    }
  }

  const loadChannels = async () => {
    try {
      setChannelLoading(true)
      setErrorMessage('')

      const result = await getAdminChannels()

      if (result.success) {
        setChannels(result.data)
      } else {
        setErrorMessage(result.message || '채널 목록을 불러오지 못했습니다.')
      }
    } catch (error) {
      console.error(error)

      if (error.response?.status === 401) {
        localStorage.removeItem('accessToken')
        requireLogin()
        return
      }

      if (error.response?.status === 403) {
        requireAdmin()
        return
      }

      setErrorMessage('채널 목록을 불러오지 못했습니다.')
    } finally {
      setChannelLoading(false)
    }
  }

  const loadChannelBoards = async (channelId) => {
    if (!channelId) {
      return
    }

    try {
      setChannelBoardLoading(true)
      setErrorMessage('')

      const result = await getAdminChannelBoards(channelId)

      if (result.success) {
        setChannelBoards(result.data)
      } else {
        setErrorMessage(result.message || '채널 게시판 목록을 불러오지 못했습니다.')
      }
    } catch (error) {
      console.error(error)

      if (error.response?.status === 401) {
        localStorage.removeItem('accessToken')
        requireLogin()
        return
      }

      if (error.response?.status === 403) {
        requireAdmin()
        return
      }

      setErrorMessage('채널 게시판 목록을 불러오지 못했습니다.')
    } finally {
      setChannelBoardLoading(false)
    }
  }

  useEffect(() => {
    const init = async () => {
      const ok = await loadMe()

      if (ok) {
        await loadBoards()
        await loadChannels()
        await loadDashboard()
      }
    }

    init()
  }, [])

  useEffect(() => {
    if (!me || me.role !== 'ADMIN') {
      return
    }

    if (activeAdminTab === 'dashboard') {
      loadDashboard()
    }

    if (activeAdminTab === 'users') {
      loadUsers()
    }

    if (activeAdminTab === 'posts') {
      if (channels.length === 0) {
        loadChannels()
      }

      loadPosts()
    }

    if (activeAdminTab === 'comments') {
      loadComments()
    }

    if (activeAdminTab === 'reports') {
      loadReports()
    }

    if (activeAdminTab === 'channels') {
      loadChannels()
    }
  }, [
    activeAdminTab,
    page,
    status,
    postPage,
    postChannelSlug,
    postBoardCode,
    commentPage,
    reportPage,
    reportStatus,
    reportTargetType,
  ])

  const handleSearch = (e) => {
    e.preventDefault()
    setPage(0)
    loadUsers()
  }

  const handleStatusChange = (e) => {
    setStatus(e.target.value)
    setPage(0)
  }

  const handleBlockUser = async (userId) => {
    if (!window.confirm('해당 회원을 정지하시겠습니까?')) {
      return
    }

    try {
      const result = await blockUser(userId)

      if (result.success) {
        alert('회원이 정지되었습니다.')
        await loadUsers()
      } else {
        alert(result.message || '회원 정지에 실패했습니다.')
      }
    } catch (error) {
      console.error(error)
      alert(error.response?.data?.message || '회원 정지에 실패했습니다.')
    }
  }

  const handleActivateUser = async (userId) => {
    if (!window.confirm('해당 회원의 정지를 해제하시겠습니까?')) {
      return
    }

    try {
      const result = await activateUser(userId)

      if (result.success) {
        alert('회원 정지가 해제되었습니다.')
        await loadUsers()
      } else {
        alert(result.message || '회원 정지 해제에 실패했습니다.')
      }
    } catch (error) {
      console.error(error)
      alert(error.response?.data?.message || '회원 정지 해제에 실패했습니다.')
    }
  }

  const handlePostSearch = (e) => {
    e.preventDefault()
    setPostPage(0)

    if (postPage === 0) {
      loadPosts()
    }
  }

  const handlePostBoardChange = (e) => {
    setPostBoardCode(e.target.value)
    setPostPage(0)
  }

  const handlePostChannelChange = async (e) => {
    const nextChannelSlug = e.target.value

    setPostChannelSlug(nextChannelSlug)
    setPostBoardCode('')
    setPostPage(0)

    if (!nextChannelSlug) {
      setPostFilterBoards([])
      return
    }

    const selectedChannel = channels.find((channel) => channel.slug === nextChannelSlug)

    if (!selectedChannel) {
      setPostFilterBoards([])
      return
    }

    try {
      const result = await getAdminChannelBoards(selectedChannel.channelId)

      if (result.success) {
        setPostFilterBoards(result.data.filter((board) => board.active))
      } else {
        setPostFilterBoards([])
      }
    } catch (error) {
      console.error(error)
      setPostFilterBoards([])
    }
  }

  const handleBlindPost = async (postId) => {
    if (!window.confirm('해당 게시글을 블라인드 처리하시겠습니까?')) {
      return
    }

    try {
      const result = await blindPost(postId)

      if (result.success) {
        alert('게시글이 블라인드 처리되었습니다.')
        await loadPosts()
      } else {
        alert(result.message || '게시글 블라인드 처리에 실패했습니다.')
      }
    } catch (error) {
      console.error(error)
      alert(error.response?.data?.message || '게시글 블라인드 처리에 실패했습니다.')
    }
  }

  const handleUnblindPost = async (postId) => {
    if (!window.confirm('해당 게시글의 블라인드를 해제하시겠습니까?')) {
      return
    }

    try {
      const result = await unblindPost(postId)

      if (result.success) {
        alert('게시글 블라인드가 해제되었습니다.')
        await loadPosts()
      } else {
        alert(result.message || '게시글 블라인드 해제에 실패했습니다.')
      }
    } catch (error) {
      console.error(error)
      alert(error.response?.data?.message || '게시글 블라인드 해제에 실패했습니다.')
    }
  }

  const handleCommentSearch = (e) => {
    e.preventDefault()
    setCommentPage(0)
    loadComments()
  }

  const handleBlindComment = async (commentId) => {
    if (!window.confirm('해당 댓글을 블라인드 처리하시겠습니까?')) {
      return
    }

    try {
      const result = await blindComment(commentId)

      if (result.success) {
        alert('댓글이 블라인드 처리되었습니다.')
        await loadComments()
      } else {
        alert(result.message || '댓글 블라인드 처리에 실패했습니다.')
      }
    } catch (error) {
      console.error(error)
      alert(error.response?.data?.message || '댓글 블라인드 처리에 실패했습니다.')
    }
  }

  const handleUnblindComment = async (commentId) => {
    if (!window.confirm('해당 댓글의 블라인드를 해제하시겠습니까?')) {
      return
    }

    try {
      const result = await unblindComment(commentId)

      if (result.success) {
        alert('댓글 블라인드가 해제되었습니다.')
        await loadComments()
      } else {
        alert(result.message || '댓글 블라인드 해제에 실패했습니다.')
      }
    } catch (error) {
      console.error(error)
      alert(error.response?.data?.message || '댓글 블라인드 해제에 실패했습니다.')
    }
  }

  const handleReportStatusChange = (e) => {
    setReportStatus(e.target.value)
    setReportPage(0)
  }

  const handleReportTargetTypeChange = (e) => {
    setReportTargetType(e.target.value)
    setReportPage(0)
  }

  const handleResolveReport = async (reportId) => {
    if (!window.confirm('해당 신고를 처리 완료하시겠습니까?')) {
      return
    }

    try {
      const result = await resolveReport(reportId)

      if (result.success) {
        alert('신고가 처리 완료되었습니다.')
        await loadReports()
      } else {
        alert(result.message || '신고 처리에 실패했습니다.')
      }
    } catch (error) {
      console.error(error)
      alert(error.response?.data?.message || '신고 처리에 실패했습니다.')
    }
  }

  const handleChannelFormChange = (e) => {
    const { name, value } = e.target

    setChannelForm((prev) => ({
      ...prev,
      [name]: value,
    }))
  }

  const handleSubmitChannel = async (e) => {
    e.preventDefault()

    if (!channelForm.name.trim()) {
      alert('채널명을 입력해주세요.')
      return
    }

    if (!channelForm.slug.trim()) {
      alert('채널 주소를 입력해주세요.')
      return
    }

    if (!/^[a-z0-9-]+$/.test(channelForm.slug.trim())) {
      alert('채널 주소는 영문 소문자, 숫자, 하이픈만 사용할 수 있습니다.')
      return
    }

    const payload = {
      name: channelForm.name.trim(),
      slug: channelForm.slug.trim().toLowerCase(),
      description: channelForm.description.trim(),
      profileImageUrl: channelForm.profileImageUrl.trim(),
      bannerImageUrl: channelForm.bannerImageUrl.trim(),
    }

    try {
      const result = editingChannelId
        ? await updateAdminChannel(editingChannelId, payload)
        : await createAdminChannel(payload)

      if (result.success) {
        alert(editingChannelId ? '채널이 수정되었습니다.' : '채널이 생성되었습니다.')
        resetChannelForm()
        await loadChannels()
      } else {
        alert(result.message || '채널 저장에 실패했습니다.')
      }
    } catch (error) {
      console.error(error)
      alert(error.response?.data?.message || '채널 저장에 실패했습니다.')
    }
  }

  const handleEditChannel = (channel) => {
    setEditingChannelId(channel.channelId)
    setChannelForm({
      name: channel.name || '',
      slug: channel.slug || '',
      description: channel.description || '',
      profileImageUrl: channel.profileImageUrl || '',
      bannerImageUrl: channel.bannerImageUrl || '',
    })
  }

  const handleActivateChannel = async (channelId) => {
    if (!window.confirm('해당 채널을 활성화하시겠습니까?')) {
      return
    }

    try {
      const result = await activateAdminChannel(channelId)

      if (result.success) {
        alert('채널이 활성화되었습니다.')
        await loadChannels()
      } else {
        alert(result.message || '채널 활성화에 실패했습니다.')
      }
    } catch (error) {
      console.error(error)
      alert(error.response?.data?.message || '채널 활성화에 실패했습니다.')
    }
  }

  const handleDeactivateChannel = async (channelId) => {
    if (!window.confirm('해당 채널을 비활성화하시겠습니까?')) {
      return
    }

    try {
      const result = await deactivateAdminChannel(channelId)

      if (result.success) {
        alert('채널이 비활성화되었습니다.')
        await loadChannels()
      } else {
        alert(result.message || '채널 비활성화에 실패했습니다.')
      }
    } catch (error) {
      console.error(error)
      alert(error.response?.data?.message || '채널 비활성화에 실패했습니다.')
    }
  }

  const handleManageChannelBoards = async (channel) => {
    setSelectedChannelForBoards(channel)
    resetChannelBoardForm()
    await loadChannelBoards(channel.channelId)
  }

  const handleChannelBoardFormChange = (e) => {
    const { name, value } = e.target

    setChannelBoardForm((prev) => ({
      ...prev,
      [name]: name === 'sortOrder' ? Number(value) : value,
    }))
  }

  const handleSubmitChannelBoard = async (e) => {
    e.preventDefault()

    if (!selectedChannelForBoards) {
      alert('게시판을 관리할 채널을 선택해주세요.')
      return
    }

    if (!channelBoardForm.code.trim()) {
      alert('게시판 코드를 입력해주세요.')
      return
    }

    if (!/^[A-Z0-9_]+$/.test(channelBoardForm.code.trim().toUpperCase())) {
      alert('게시판 코드는 영문 대문자, 숫자, 언더스코어만 사용할 수 있습니다.')
      return
    }

    if (!channelBoardForm.name.trim()) {
      alert('게시판명을 입력해주세요.')
      return
    }

    const payload = {
      code: channelBoardForm.code.trim().toUpperCase(),
      name: channelBoardForm.name.trim(),
      description: channelBoardForm.description.trim(),
      sortOrder: Number(channelBoardForm.sortOrder),
    }

    try {
      const result = editingChannelBoardId
        ? await updateAdminChannelBoard(
            selectedChannelForBoards.channelId,
            editingChannelBoardId,
            payload,
          )
        : await createAdminChannelBoard(
            selectedChannelForBoards.channelId,
            payload,
          )

      if (result.success) {
        alert(editingChannelBoardId ? '게시판이 수정되었습니다.' : '게시판이 생성되었습니다.')
        resetChannelBoardForm()
        await loadChannelBoards(selectedChannelForBoards.channelId)
      } else {
        alert(result.message || '게시판 저장에 실패했습니다.')
      }
    } catch (error) {
      console.error(error)
      alert(error.response?.data?.message || '게시판 저장에 실패했습니다.')
    }
  }

  const handleEditChannelBoard = (board) => {
    setEditingChannelBoardId(board.boardId)
    setChannelBoardForm({
      code: board.code || '',
      name: board.name || '',
      description: board.description || '',
      sortOrder: board.sortOrder ?? 0,
    })
  }

  const handleActivateChannelBoard = async (boardId) => {
    if (!selectedChannelForBoards) {
      return
    }

    if (!window.confirm('해당 게시판을 활성화하시겠습니까?')) {
      return
    }

    try {
      const result = await activateAdminChannelBoard(
        selectedChannelForBoards.channelId,
        boardId,
      )

      if (result.success) {
        alert('게시판이 활성화되었습니다.')
        await loadChannelBoards(selectedChannelForBoards.channelId)
      } else {
        alert(result.message || '게시판 활성화에 실패했습니다.')
      }
    } catch (error) {
      console.error(error)
      alert(error.response?.data?.message || '게시판 활성화에 실패했습니다.')
    }
  }

  const handleDeactivateChannelBoard = async (boardId) => {
    if (!selectedChannelForBoards) {
      return
    }

    if (!window.confirm('해당 게시판을 비활성화하시겠습니까?')) {
      return
    }

    try {
      const result = await deactivateAdminChannelBoard(
        selectedChannelForBoards.channelId,
        boardId,
      )

      if (result.success) {
        alert('게시판이 비활성화되었습니다.')
        await loadChannelBoards(selectedChannelForBoards.channelId)
      } else {
        alert(result.message || '게시판 비활성화에 실패했습니다.')
      }
    } catch (error) {
      console.error(error)
      alert(error.response?.data?.message || '게시판 비활성화에 실패했습니다.')
    }
  }

  const resetChannelForm = () => {
    setEditingChannelId(null)
    setChannelForm({
      name: '',
      slug: '',
      description: '',
      profileImageUrl: '',
      bannerImageUrl: '',
    })
  }

  const resetChannelBoardForm = () => {
    setEditingChannelBoardId(null)
    setChannelBoardForm({
      code: '',
      name: '',
      description: '',
      sortOrder: 0,
    })
  }

  if (!me) {
    return null
  }

  return (
    <div>
      <div className="page-title-row">
        <div>
          <h1>관리자 페이지</h1>
          <p>회원, 게시글, 댓글을 관리합니다.</p>
        </div>
      </div>

      <div className="admin-tab-row">
        <button
          type="button"
          className={activeAdminTab === 'dashboard' ? 'active' : ''}
          onClick={() => setActiveAdminTab('dashboard')}
        >
          대시보드
        </button>

        <button
          type="button"
          className={activeAdminTab === 'users' ? 'active' : ''}
          onClick={() => setActiveAdminTab('users')}
        >
          회원 관리
        </button>

        <button
          type="button"
          className={activeAdminTab === 'posts' ? 'active' : ''}
          onClick={() => setActiveAdminTab('posts')}
        >
          게시글 관리
        </button>

        <button
          type="button"
          className={activeAdminTab === 'comments' ? 'active' : ''}
          onClick={() => setActiveAdminTab('comments')}
        >
          댓글 관리
        </button>

        <button
          type="button"
          className={activeAdminTab === 'reports' ? 'active' : ''}
          onClick={() => setActiveAdminTab('reports')}
        >
          신고 관리
        </button>

        <button
          type="button"
          className={activeAdminTab === 'channels' ? 'active' : ''}
          onClick={() => setActiveAdminTab('channels')}
        >
          채널 관리
        </button>
      </div>

      {activeAdminTab === 'dashboard' && (
        <div className="admin-section">
          <h2>운영 대시보드</h2>

          {errorMessage && <p className="error-message">{errorMessage}</p>}

          {dashboardLoading ? (
            <p>불러오는 중...</p>
          ) : !dashboard ? (
            <div className="empty-box">대시보드 데이터가 없습니다.</div>
          ) : (
            <div className="admin-dashboard-grid">
              <div className="admin-dashboard-card">
                <span>전체 회원</span>
                <strong>{dashboard.totalUserCount}</strong>
                <p>오늘 가입 {dashboard.todayUserCount}</p>
              </div>

              <div className="admin-dashboard-card">
                <span>전체 게시글</span>
                <strong>{dashboard.totalPostCount}</strong>
                <p>오늘 작성 {dashboard.todayPostCount}</p>
              </div>

              <div className="admin-dashboard-card">
                <span>전체 댓글</span>
                <strong>{dashboard.totalCommentCount}</strong>
                <p>오늘 작성 {dashboard.todayCommentCount}</p>
              </div>

              <div className="admin-dashboard-card danger">
                <span>대기 중 신고</span>
                <strong>{dashboard.pendingReportCount}</strong>
                <p>처리가 필요한 신고</p>
              </div>

              <div className="admin-dashboard-card">
                <span>블라인드 게시글</span>
                <strong>{dashboard.blindPostCount}</strong>
                <p>관리자에 의해 숨김 처리</p>
              </div>

              <div className="admin-dashboard-card">
                <span>블라인드 댓글</span>
                <strong>{dashboard.blindCommentCount}</strong>
                <p>관리자에 의해 숨김 처리</p>
              </div>
            </div>
          )}
        </div>
      )}
      {activeAdminTab === 'users' && (
        <div className="admin-section">
          <h2>회원 관리</h2>

          <div className="filter-box">
            <select value={status} onChange={handleStatusChange}>
              <option value="">전체 상태</option>
              <option value="ACTIVE">정상</option>
              <option value="BLOCKED">정지</option>
              <option value="DELETED">탈퇴</option>
            </select>

            <form onSubmit={handleSearch} className="search-form">
              <input
                type="text"
                value={keyword}
                placeholder="이메일 또는 닉네임 검색"
                onChange={(e) => setKeyword(e.target.value)}
              />

              <button type="submit">검색</button>
            </form>
          </div>

          {errorMessage && <p className="error-message">{errorMessage}</p>}

          {loading ? (
            <p>불러오는 중...</p>
          ) : (
            <div className="admin-table-wrap">
              <table className="admin-table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>이메일</th>
                    <th>닉네임</th>
                    <th>권한</th>
                    <th>상태</th>
                    <th>가입일</th>
                    <th>관리</th>
                  </tr>
                </thead>

                <tbody>
                  {users.length === 0 ? (
                    <tr>
                      <td colSpan="7">회원이 없습니다.</td>
                    </tr>
                  ) : (
                    users.map((user) => (
                      <tr key={user.userId}>
                        <td>{user.userId}</td>
                        <td>{user.email}</td>
                        <td>{user.nickname}</td>
                        <td>{user.role}</td>
                        <td>{user.status}</td>
                        <td>{user.createdAt}</td>
                        <td>
                          {user.status === 'ACTIVE' && user.userId !== me.userId && (
                            <button
                              type="button"
                              className="admin-danger-button"
                              onClick={() => handleBlockUser(user.userId)}
                            >
                              정지
                            </button>
                          )}

                          {user.status === 'BLOCKED' && (
                            <button
                              type="button"
                              className="admin-normal-button"
                              onClick={() => handleActivateUser(user.userId)}
                            >
                              해제
                            </button>
                          )}
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
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
      )}
      {activeAdminTab === 'posts' && (
        <div className="admin-section">
          <h2>게시글 관리</h2>

          <div className="filter-box">
            <select value={postChannelSlug} onChange={handlePostChannelChange}>
              <option value="">전체 채널</option>
              {channels.map((channel) => (
                <option key={channel.channelId} value={channel.slug}>
                  {channel.name}
                </option>
              ))}
            </select>

            <select
              value={postBoardCode}
              onChange={handlePostBoardChange}
              disabled={!postChannelSlug}
            >
              <option value="">
                {postChannelSlug ? '전체 게시판' : '채널을 먼저 선택하세요'}
              </option>
              {postFilterBoards.map((board) => (
                <option key={board.boardId} value={board.code}>
                  {board.name}
                </option>
              ))}
            </select>

            <form onSubmit={handlePostSearch} className="search-form">
              <input
                type="text"
                value={postKeyword}
                placeholder="제목 또는 내용 검색"
                onChange={(e) => setPostKeyword(e.target.value)}
              />

              <button type="submit">검색</button>
            </form>
          </div>

          {errorMessage && <p className="error-message">{errorMessage}</p>}

          {postLoading ? (
            <p>불러오는 중...</p>
          ) : (
            <div className="admin-table-wrap">
              <table className="admin-table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>채널</th>
                    <th>게시판</th>
                    <th>제목</th>
                    <th>작성자</th>
                    <th>조회</th>
                    <th>좋아요</th>
                    <th>댓글</th>
                    <th>상태</th>
                    <th>작성일</th>
                    <th>관리</th>
                  </tr>
                </thead>

                <tbody>
                  {posts.length === 0 ? (
                    <tr>
                      <td colSpan="11">게시글이 없습니다.</td>
                    </tr>
                  ) : (
                    posts.map((post) => {
                      const isBlind = post.blind === true
                      const isDeleted = post.deleted === true

                      return (
                        <tr key={post.postId}>
                          <td>{post.postId}</td>
                          <td>{post.channelName || '-'}</td>
                          <td>{post.boardName}</td>
                          <td>{post.title}</td>
                          <td>{post.writerNickname}</td>
                          <td>{post.viewCount}</td>
                          <td>{post.likeCount}</td>
                          <td>{post.commentCount}</td>
                          <td>
                            {isDeleted ? '삭제' : isBlind ? '블라인드' : '정상'}
                          </td>
                          <td>{post.createdAt}</td>
                          <td>
                            {isDeleted ? (
                              '-'
                            ) : isBlind ? (
                              <button
                                type="button"
                                className="admin-normal-button"
                                onClick={() => handleUnblindPost(post.postId)}
                              >
                                해제
                              </button>
                            ) : (
                              <button
                                type="button"
                                className="admin-danger-button"
                                onClick={() => handleBlindPost(post.postId)}
                              >
                                블라인드
                              </button>
                            )}
                          </td>
                        </tr>
                      )
                    })
                  )}
                </tbody>
              </table>
            </div>
          )}

          {postPageInfo && !postPageInfo.empty && (
            <div className="pagination">
              <button
                type="button"
                disabled={postPageInfo.first}
                onClick={() => setPostPage((prev) => prev - 1)}
              >
                이전
              </button>

              <span>
                {postPageInfo.page + 1} / {postPageInfo.totalPages}
              </span>

              <button
                type="button"
                disabled={postPageInfo.last}
                onClick={() => setPostPage((prev) => prev + 1)}
              >
                다음
              </button>
            </div>
          )}
        </div>
      )}
      {activeAdminTab === 'comments' && (
        <div className="admin-section">
          <h2>댓글 관리</h2>

          <div className="filter-box">
            <form onSubmit={handleCommentSearch} className="search-form">
              <input
                type="text"
                value={commentKeyword}
                placeholder="댓글 내용, 닉네임, 이메일 검색"
                onChange={(e) => setCommentKeyword(e.target.value)}
              />

              <button type="submit">검색</button>
            </form>
          </div>

          {errorMessage && <p className="error-message">{errorMessage}</p>}

          {commentLoading ? (
            <p>불러오는 중...</p>
          ) : (
            <div className="admin-table-wrap">
              <table className="admin-table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>게시글 ID</th>
                    <th>작성자</th>
                    <th>내용</th>
                    <th>상태</th>
                    <th>작성일</th>
                    <th>관리</th>
                  </tr>
                </thead>

                <tbody>
                  {comments.length === 0 ? (
                    <tr>
                      <td colSpan="7">댓글이 없습니다.</td>
                    </tr>
                  ) : (
                    comments.map((comment) => {
                      const isBlind = comment.blind === true
                      const isDeleted = comment.deleted === true

                      return (
                        <tr key={comment.commentId}>
                          <td>{comment.commentId}</td>
                          <td>{comment.postId}</td>
                          <td>{comment.writerNickname}</td>
                          <td>{comment.content}</td>
                          <td>{isDeleted ? '삭제' : isBlind ? '블라인드' : '정상'}</td>
                          <td>{comment.createdAt}</td>
                          <td>
                            {isDeleted ? (
                              '-'
                            ) : isBlind ? (
                              <button
                                type="button"
                                className="admin-normal-button"
                                onClick={() => handleUnblindComment(comment.commentId)}
                              >
                                해제
                              </button>
                            ) : (
                              <button
                                type="button"
                                className="admin-danger-button"
                                onClick={() => handleBlindComment(comment.commentId)}
                              >
                                블라인드
                              </button>
                            )}
                          </td>
                        </tr>
                      )
                    })
                  )}
                </tbody>
              </table>
            </div>
          )}

          {commentPageInfo && !commentPageInfo.empty && (
            <div className="pagination">
              <button
                type="button"
                disabled={commentPageInfo.first}
                onClick={() => setCommentPage((prev) => prev - 1)}
              >
                이전
              </button>

              <span>
                {commentPageInfo.page + 1} / {commentPageInfo.totalPages}
              </span>

              <button
                type="button"
                disabled={commentPageInfo.last}
                onClick={() => setCommentPage((prev) => prev + 1)}
              >
                다음
              </button>
            </div>
          )}
        </div>
      )}
      {activeAdminTab === 'reports' && (
        <div className="admin-section">
          <h2>신고 관리</h2>

          <div className="filter-box">
            <select value={reportStatus} onChange={handleReportStatusChange}>
              <option value="">전체 상태</option>
              <option value="PENDING">대기</option>
              <option value="RESOLVED">처리완료</option>
            </select>

            <select value={reportTargetType} onChange={handleReportTargetTypeChange}>
              <option value="">전체 대상</option>
              <option value="POST">게시글</option>
              <option value="COMMENT">댓글</option>
            </select>
          </div>

          {errorMessage && <p className="error-message">{errorMessage}</p>}

          {reportLoading ? (
            <p>불러오는 중...</p>
          ) : (
            <div className="admin-table-wrap">
              <table className="admin-table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>대상</th>
                    <th>대상 정보</th>
                    <th>신고자</th>
                    <th>신고 사유</th>
                    <th>상태</th>
                    <th>신고일</th>
                    <th>관리</th>
                  </tr>
                </thead>

                <tbody>
                  {reports.length === 0 ? (
                    <tr>
                      <td colSpan="8">신고 내역이 없습니다.</td>
                    </tr>
                  ) : (
                    reports.map((report) => {
                      const isPending = report.status === 'PENDING'

                      return (
                        <tr key={report.reportId}>
                          <td>{report.reportId}</td>
                          <td>{report.targetType === 'POST' ? '게시글' : '댓글'}</td>

                          <td>
                            <div className="admin-report-target">
                              <strong>
                                {report.targetTitle || `대상 ID ${report.targetId}`}
                              </strong>

                              {report.targetPreview && (
                                <p>{report.targetPreview}</p>
                              )}

                              {report.targetPostId ? (
                                <Link
                                  to={`/posts/${report.targetPostId}`}
                                  className="admin-inline-link"
                                >
                                  대상 보기
                                </Link>
                              ) : (
                                <span className="admin-muted-text">대상 없음</span>
                              )}
                            </div>
                          </td>

                          <td>{report.reporterNickname}</td>
                          <td>{report.reason}</td>
                          <td>{isPending ? '대기' : '처리완료'}</td>
                          <td>{report.createdAt}</td>
                          <td>
                            {isPending ? (
                              <button
                                type="button"
                                className="admin-normal-button"
                                onClick={() => handleResolveReport(report.reportId)}
                              >
                                처리완료
                              </button>
                            ) : (
                              '-'
                            )}
                          </td>
                        </tr>
                      )
                    })
                  )}
                </tbody>
              </table>
            </div>
          )}

          {reportPageInfo && !reportPageInfo.empty && (
            <div className="pagination">
              <button
                type="button"
                disabled={reportPageInfo.first}
                onClick={() => setReportPage((prev) => prev - 1)}
              >
                이전
              </button>

              <span>
                {reportPageInfo.page + 1} / {reportPageInfo.totalPages}
              </span>

              <button
                type="button"
                disabled={reportPageInfo.last}
                onClick={() => setReportPage((prev) => prev + 1)}
              >
                다음
              </button>
            </div>
          )}
        </div>
      )}
      {activeAdminTab === 'channels' && (
        <div className="admin-section">
          <h2>채널 관리</h2>

          {errorMessage && <p className="error-message">{errorMessage}</p>}

          <form onSubmit={handleSubmitChannel} className="admin-channel-form">
            <div className="admin-channel-form-grid">
              <div className="form-group">
                <label htmlFor="channelName">채널명</label>
                <input
                  id="channelName"
                  name="name"
                  type="text"
                  value={channelForm.name}
                  onChange={handleChannelFormChange}
                  placeholder="예: 후미카"
                  maxLength={100}
                />
              </div>

              <div className="form-group">
                <label htmlFor="channelSlug">채널 주소</label>
                <input
                  id="channelSlug"
                  name="slug"
                  type="text"
                  value={channelForm.slug}
                  onChange={handleChannelFormChange}
                  placeholder="예: fumika"
                  maxLength={100}
                />
              </div>

              <div className="form-group admin-channel-form-wide">
                <label htmlFor="channelDescription">설명</label>
                <input
                  id="channelDescription"
                  name="description"
                  type="text"
                  value={channelForm.description}
                  onChange={handleChannelFormChange}
                  placeholder="채널 설명"
                  maxLength={500}
                />
              </div>

              <div className="form-group admin-channel-form-wide">
                <label htmlFor="channelProfileImageUrl">프로필 이미지 URL</label>
                <input
                  id="channelProfileImageUrl"
                  name="profileImageUrl"
                  type="text"
                  value={channelForm.profileImageUrl}
                  onChange={handleChannelFormChange}
                  placeholder="/uploads/... 또는 https://..."
                  maxLength={500}
                />
              </div>

              <div className="form-group admin-channel-form-wide">
                <label htmlFor="channelBannerImageUrl">배너 이미지 URL</label>
                <input
                  id="channelBannerImageUrl"
                  name="bannerImageUrl"
                  type="text"
                  value={channelForm.bannerImageUrl}
                  onChange={handleChannelFormChange}
                  placeholder="/uploads/... 또는 https://..."
                  maxLength={500}
                />
              </div>
            </div>

            <div className="write-action-row">
              <button type="submit" className="primary-button">
                {editingChannelId ? '채널 수정' : '채널 생성'}
              </button>

              {editingChannelId && (
                <button
                  type="button"
                  className="secondary-button"
                  onClick={resetChannelForm}
                >
                  취소
                </button>
              )}
            </div>
          </form>

          {channelLoading ? (
            <p>불러오는 중...</p>
          ) : (
            <>
            <div className="admin-table-wrap">
              <table className="admin-table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>채널명</th>
                    <th>주소</th>
                    <th>설명</th>
                    <th>상태</th>
                    <th>생성일</th>
                    <th>관리</th>
                  </tr>
                </thead>

                <tbody>
                  {channels.length === 0 ? (
                    <tr>
                      <td colSpan="7">채널이 없습니다.</td>
                    </tr>
                  ) : (
                    channels.map((channel) => (
                      <tr key={channel.channelId}>
                        <td>{channel.channelId}</td>
                        <td>{channel.name}</td>
                        <td>{channel.slug}</td>
                        <td>{channel.description || '-'}</td>
                        <td>{channel.active ? '활성' : '비활성'}</td>
                        <td>{channel.createdAt}</td>
                        <td>
                          <div className="admin-action-row">
                            <button
                              type="button"
                              className="admin-normal-button"
                              onClick={() => handleEditChannel(channel)}
                            >
                              수정
                            </button>

                            <button
                              type="button"
                              className="admin-normal-button"
                              onClick={() => handleManageChannelBoards(channel)}
                            >
                              게시판 관리
                            </button>

                            {channel.active ? (
                              <button
                                type="button"
                                className="admin-danger-button"
                                onClick={() => handleDeactivateChannel(channel.channelId)}
                              >
                                비활성화
                              </button>
                            ) : (
                              <button
                                type="button"
                                className="admin-normal-button"
                                onClick={() => handleActivateChannel(channel.channelId)}
                              >
                                활성화
                              </button>
                            )}

                            <Link
                              to={`/channels/${channel.slug}`}
                              className="admin-inline-link"
                            >
                              보기
                            </Link>
                          </div>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
            {selectedChannelForBoards && (
              <div className="admin-board-manager">
                <div className="home-section-title">
                  <div>
                    <h2>{selectedChannelForBoards.name} 게시판 관리</h2>
                    <p>이 채널에서 사용할 게시판을 생성하고 관리합니다.</p>
                  </div>

                  <button
                    type="button"
                    className="secondary-button"
                    onClick={() => {
                      setSelectedChannelForBoards(null)
                      setChannelBoards([])
                      resetChannelBoardForm()
                    }}
                  >
                    닫기
                  </button>
                </div>

                <form onSubmit={handleSubmitChannelBoard} className="admin-channel-form">
                  <div className="admin-board-form-grid">
                    <div className="form-group">
                      <label htmlFor="boardCode">게시판 코드</label>
                      <input
                        id="boardCode"
                        name="code"
                        type="text"
                        value={channelBoardForm.code}
                        onChange={handleChannelBoardFormChange}
                        placeholder="예: CLIP"
                        maxLength={30}
                      />
                    </div>

                    <div className="form-group">
                      <label htmlFor="boardName">게시판명</label>
                      <input
                        id="boardName"
                        name="name"
                        type="text"
                        value={channelBoardForm.name}
                        onChange={handleChannelBoardFormChange}
                        placeholder="예: 클립"
                        maxLength={50}
                      />
                    </div>

                    <div className="form-group">
                      <label htmlFor="boardSortOrder">정렬 순서</label>
                      <input
                        id="boardSortOrder"
                        name="sortOrder"
                        type="number"
                        value={channelBoardForm.sortOrder}
                        onChange={handleChannelBoardFormChange}
                        min={0}
                      />
                    </div>

                    <div className="form-group admin-channel-form-wide">
                      <label htmlFor="boardDescription">설명</label>
                      <input
                        id="boardDescription"
                        name="description"
                        type="text"
                        value={channelBoardForm.description}
                        onChange={handleChannelBoardFormChange}
                        placeholder="게시판 설명"
                        maxLength={255}
                      />
                    </div>
                  </div>

                  <div className="write-action-row">
                    <button type="submit" className="primary-button">
                      {editingChannelBoardId ? '게시판 수정' : '게시판 생성'}
                    </button>

                    {editingChannelBoardId && (
                      <button
                        type="button"
                        className="secondary-button"
                        onClick={resetChannelBoardForm}
                      >
                        취소
                      </button>
                    )}
                  </div>
                </form>

                {channelBoardLoading ? (
                  <p>불러오는 중...</p>
                ) : (
                  <div className="admin-table-wrap">
                    <table className="admin-table">
                      <thead>
                        <tr>
                          <th>ID</th>
                          <th>코드</th>
                          <th>게시판명</th>
                          <th>설명</th>
                          <th>정렬</th>
                          <th>상태</th>
                          <th>관리</th>
                        </tr>
                      </thead>

                      <tbody>
                        {channelBoards.length === 0 ? (
                          <tr>
                            <td colSpan="7">게시판이 없습니다.</td>
                          </tr>
                        ) : (
                          channelBoards.map((board) => (
                            <tr key={board.boardId}>
                              <td>{board.boardId}</td>
                              <td>{board.code}</td>
                              <td>{board.name}</td>
                              <td>{board.description || '-'}</td>
                              <td>{board.sortOrder}</td>
                              <td>{board.active ? '활성' : '비활성'}</td>
                              <td>
                                <div className="admin-action-row">
                                  <button
                                    type="button"
                                    className="admin-normal-button"
                                    onClick={() => handleEditChannelBoard(board)}
                                  >
                                    수정
                                  </button>

                                  {board.active ? (
                                    <button
                                      type="button"
                                      className="admin-danger-button"
                                      onClick={() => handleDeactivateChannelBoard(board.boardId)}
                                    >
                                      비활성화
                                    </button>
                                  ) : (
                                    <button
                                      type="button"
                                      className="admin-normal-button"
                                      onClick={() => handleActivateChannelBoard(board.boardId)}
                                    >
                                      활성화
                                    </button>
                                  )}

                                  <Link
                                    to={`/channels/${selectedChannelForBoards.slug}/posts?boardCode=${board.code}`}
                                    className="admin-inline-link"
                                  >
                                    보기
                                  </Link>
                                </div>
                              </td>
                            </tr>
                          ))
                        )}
                      </tbody>
                    </table>
                  </div>
                )}
              </div>
            )}
          </>  
          )}
        </div>
      )}
    </div>
  )
}

export default AdminPage