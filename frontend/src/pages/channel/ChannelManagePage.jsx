import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import {
  activateManagedChannelBoard,
  assignChannelManager,
  createManagedChannelBoard,
  deactivateManagedChannelBoard,
  getManagedChannel,
  getManagedChannelBoards,
  getManagedChannelMembers,
  removeChannelManager,
  searchChannelManagerCandidates,
  updateManagedChannel,
  updateManagedChannelBoard,
  uploadManagedChannelBannerImage,
  uploadManagedChannelProfileImage,
} from '../../api/channelManagementApi'

const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || ''

const getImageUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return `${API_BASE_URL}${url}`
}

function ChannelManagePage() {
  const { channelSlug } = useParams()
  const navigate = useNavigate()

  const [channel, setChannel] = useState(null)
  const [members, setMembers] = useState([])

  const [keyword, setKeyword] = useState('')
  const [candidates, setCandidates] = useState([])
  const [selectedUser, setSelectedUser] = useState(null)

  const [loading, setLoading] = useState(true)
  const [searchLoading, setSearchLoading] = useState(false)
  const [errorMessage, setErrorMessage] = useState('')

  const [channelForm, setChannelForm] = useState({
    name: '',
    description: '',
  })

  const [boards, setBoards] = useState([])
  const [boardLoading, setBoardLoading] =
    useState(false)
  const [editingBoardId, setEditingBoardId] =
    useState(null)

  const [boardForm, setBoardForm] = useState({
    code: '',
    name: '',
    description: '',
    sortOrder: 0,
  })

  const loadMembers = async () => {
    const result =
      await getManagedChannelMembers(channelSlug)

    if (result.success) {
      setMembers(result.data || [])
    }
  }

  const loadBoards = async () => {
    try {
      setBoardLoading(true)

      const result =
        await getManagedChannelBoards(channelSlug)

      if (result.success) {
        setBoards(result.data || [])
      }
    } catch (error) {
      console.error(error)

      alert(
        error.response?.data?.message ||
          '게시판 목록을 불러오지 못했습니다.',
      )
    } finally {
      setBoardLoading(false)
    }
  }

  const initialize = async () => {
    const token = localStorage.getItem('accessToken')

    if (!token) {
      alert('로그인이 필요합니다.')
      navigate('/login', {
        replace: true,
        state: {
          from: `/channels/${channelSlug}/manage`,
        },
      })
      return
    }

    try {
      setLoading(true)
      setErrorMessage('')

      const channelResult =
        await getManagedChannel(channelSlug)

      if (!channelResult.success) {
        setErrorMessage(
          channelResult.message ||
            '채널 정보를 불러오지 못했습니다.',
        )
        return
      }

      const loadedChannel = channelResult.data

      setChannel(loadedChannel)

      setChannelForm({
        name: loadedChannel.name || '',
        description: loadedChannel.description || '',
      })

      await Promise.all([
        loadMembers(),
        loadBoards(),
      ])
    } catch (error) {
      console.error(error)

      if (error.response?.status === 401) {
        localStorage.removeItem('accessToken')
        alert('로그인이 필요합니다.')

        navigate('/login', {
          replace: true,
          state: {
            from: `/channels/${channelSlug}/manage`,
          },
        })

        return
      }

      if (error.response?.status === 403) {
        alert('채널 소유자만 접근할 수 있습니다.')
        navigate(`/channels/${channelSlug}`, {
          replace: true,
        })
        return
      }

      setErrorMessage(
        error.response?.data?.message ||
          '채널 관리 정보를 불러오지 못했습니다.',
      )
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    initialize()
  }, [channelSlug])

  const handleSearch = async (e) => {
    e.preventDefault()

    const normalizedKeyword = keyword.trim()

    if (!normalizedKeyword) {
      alert('닉네임 또는 이메일을 입력해주세요.')
      return
    }

    try {
      setSearchLoading(true)
      setSelectedUser(null)

      const result =
        await searchChannelManagerCandidates(
          channelSlug,
          normalizedKeyword,
        )

      if (result.success) {
        setCandidates(result.data || [])
      } else {
        setCandidates([])
        alert(result.message || '회원 검색에 실패했습니다.')
      }
    } catch (error) {
      console.error(error)
      setCandidates([])

      alert(
        error.response?.data?.message ||
          '회원 검색에 실패했습니다.',
      )
    } finally {
      setSearchLoading(false)
    }
  }

  const handleAssign = async () => {
    if (!selectedUser) {
      alert('매니저로 지정할 회원을 선택해주세요.')
      return
    }

    if (
      !window.confirm(
        `${selectedUser.nickname}님을 채널 매니저로 지정하시겠습니까?`,
      )
    ) {
      return
    }

    try {
      const result = await assignChannelManager(
        channelSlug,
        selectedUser.userId,
      )

      if (result.success) {
        alert('채널 매니저가 지정되었습니다.')

        setKeyword('')
        setCandidates([])
        setSelectedUser(null)

        await loadMembers()
      } else {
        alert(
          result.message ||
            '채널 매니저 지정에 실패했습니다.',
        )
      }
    } catch (error) {
      console.error(error)

      alert(
        error.response?.data?.message ||
          '채널 매니저 지정에 실패했습니다.',
      )
    }
  }

  const handleRemove = async (manager) => {
    if (
      !window.confirm(
        `${manager.nickname}님의 매니저 권한을 해제하시겠습니까?`,
      )
    ) {
      return
    }

    try {
      const result = await removeChannelManager(
        channelSlug,
        manager.userId,
      )

      if (result.success) {
        alert('채널 매니저가 해제되었습니다.')
        await loadMembers()
      } else {
        alert(
          result.message ||
            '채널 매니저 해제에 실패했습니다.',
        )
      }
    } catch (error) {
      console.error(error)

      alert(
        error.response?.data?.message ||
          '채널 매니저 해제에 실패했습니다.',
      )
    }
  }

  const handleChannelFormChange = (e) => {
    const { name, value } = e.target

    setChannelForm((prev) => ({
      ...prev,
      [name]: value,
    }))
  }

  const handleUpdateChannel = async (e) => {
    e.preventDefault()

    if (!channelForm.name.trim()) {
      alert('채널명을 입력해주세요.')
      return
    }

    try {
      const result = await updateManagedChannel(
        channelSlug,
        {
          name: channelForm.name.trim(),
          description:
            channelForm.description.trim(),
        },
      )

      if (result.success) {
        setChannel(result.data)
        alert('채널 정보가 수정되었습니다.')
      } else {
        alert(
          result.message ||
            '채널 정보 수정에 실패했습니다.',
        )
      }
    } catch (error) {
      console.error(error)

      alert(
        error.response?.data?.message ||
          '채널 정보 수정에 실패했습니다.',
      )
    }
  }

  const resetBoardForm = () => {
    setEditingBoardId(null)

    setBoardForm({
      code: '',
      name: '',
      description: '',
      sortOrder: 0,
    })
  }

  const handleBoardFormChange = (e) => {
    const { name, value } = e.target

    setBoardForm((prev) => ({
      ...prev,
      [name]:
        name === 'sortOrder'
          ? Number(value)
          : value,
    }))
  }

  const handleEditBoard = (board) => {
    setEditingBoardId(board.boardId)

    setBoardForm({
      code: board.code || '',
      name: board.name || '',
      description: board.description || '',
      sortOrder: board.sortOrder ?? 0,
    })
  }

  const handleSubmitBoard = async (e) => {
    e.preventDefault()

    const code = boardForm.code
      .trim()
      .toUpperCase()

    if (!code) {
      alert('게시판 코드를 입력해주세요.')
      return
    }

    if (!/^[A-Z0-9_]+$/.test(code)) {
      alert(
        '게시판 코드는 영문 대문자, 숫자, 언더스코어만 사용할 수 있습니다.',
      )
      return
    }

    if (!boardForm.name.trim()) {
      alert('게시판명을 입력해주세요.')
      return
    }

    const payload = {
      code,
      name: boardForm.name.trim(),
      description:
        boardForm.description.trim(),
      sortOrder: Number(boardForm.sortOrder),
    }

    try {
      const result = editingBoardId
        ? await updateManagedChannelBoard(
            channelSlug,
            editingBoardId,
            payload,
          )
        : await createManagedChannelBoard(
            channelSlug,
            payload,
          )

      if (result.success) {
        alert(
          editingBoardId
            ? '게시판이 수정되었습니다.'
            : '게시판이 생성되었습니다.',
        )

        resetBoardForm()
        await loadBoards()
      }
    } catch (error) {
      console.error(error)

      alert(
        error.response?.data?.message ||
          '게시판 저장에 실패했습니다.',
      )
    }
  }

  const handleToggleBoard = async (board) => {
    const actionText = board.active
      ? '비활성화'
      : '활성화'

    if (
      !window.confirm(
        `${board.name} 게시판을 ${actionText}하시겠습니까?`,
      )
    ) {
      return
    }

    try {
      const result = board.active
        ? await deactivateManagedChannelBoard(
            channelSlug,
            board.boardId,
          )
        : await activateManagedChannelBoard(
            channelSlug,
            board.boardId,
          )

      if (result.success) {
        alert(`게시판이 ${actionText}되었습니다.`)
        await loadBoards()
      }
    } catch (error) {
      console.error(error)

      alert(
        error.response?.data?.message ||
          `게시판 ${actionText}에 실패했습니다.`,
      )
    }
  }

  const handleProfileImageUpload = async (file) => {
    if (!file) {
      return
    }

    try {
      const result =
        await uploadManagedChannelProfileImage(
          channelSlug,
          file,
        )

      if (result.success) {
        setChannel(result.data)
        alert('프로필 이미지가 변경되었습니다.')
      } else {
        alert(
          result.message ||
            '프로필 이미지 변경에 실패했습니다.',
        )
      }
    } catch (error) {
      console.error(error)

      alert(
        error.response?.data?.message ||
          '프로필 이미지 변경에 실패했습니다.',
      )
    }
  }

  const handleBannerImageUpload = async (file) => {
    if (!file) {
      return
    }

    try {
      const result =
        await uploadManagedChannelBannerImage(
          channelSlug,
          file,
        )

      if (result.success) {
        setChannel(result.data)
        alert('배너 이미지가 변경되었습니다.')
      } else {
        alert(
          result.message ||
            '배너 이미지 변경에 실패했습니다.',
        )
      }
    } catch (error) {
      console.error(error)

      alert(
        error.response?.data?.message ||
          '배너 이미지 변경에 실패했습니다.',
      )
    }
  }

  if (loading) {
    return <p>불러오는 중...</p>
  }

  if (errorMessage) {
    return (
      <p className="error-message">
        {errorMessage}
      </p>
    )
  }

  if (!channel) {
    return null
  }

  const managers = members.filter(
    (member) => member.role === 'MANAGER',
  )

  return (
    <div>
      <div className="page-title-row">
        <div>
          <h1>{channel.name} 관리</h1>
          <p>
            채널 운영을 도와줄 매니저를 관리합니다.
          </p>
        </div>

        <Link
          to={`/channels/${channel.slug}`}
          className="secondary-button"
        >
          채널로 돌아가기
        </Link>
      </div>

      <section className="admin-section channel-manage-info-section">
        <div className="home-section-title">
          <div>
            <h2>채널 정보</h2>
            <p>
              채널명, 설명과 대표 이미지를 관리합니다.
            </p>
          </div>
        </div>

        <div className="channel-manage-image-grid">
          <div className="channel-manage-image-card">
            <span>프로필 이미지</span>

            <div className="channel-manage-profile-preview">
              {channel.profileImageUrl ? (
                <img
                  src={getImageUrl(
                    channel.profileImageUrl,
                  )}
                  alt={channel.name}
                />
              ) : (
                <strong>
                  {channel.name?.charAt(0) || '?'}
                </strong>
              )}
            </div>

            <label className="admin-file-button">
              프로필 변경
              <input
                type="file"
                accept="image/*"
                onChange={(e) => {
                  handleProfileImageUpload(
                    e.target.files?.[0],
                  )
                  e.target.value = ''
                }}
              />
            </label>
          </div>

          <div className="channel-manage-image-card">
            <span>배너 이미지</span>

            <div className="channel-manage-banner-preview">
              {channel.bannerImageUrl ? (
                <img
                  src={getImageUrl(
                    channel.bannerImageUrl,
                  )}
                  alt=""
                />
              ) : (
                <strong>배너 없음</strong>
              )}
            </div>

            <label className="admin-file-button">
              배너 변경
              <input
                type="file"
                accept="image/*"
                onChange={(e) => {
                  handleBannerImageUpload(
                    e.target.files?.[0],
                  )
                  e.target.value = ''
                }}
              />
            </label>
          </div>
        </div>

        <form
          className="channel-manage-info-form"
          onSubmit={handleUpdateChannel}
        >
          <div className="form-group">
            <label htmlFor="managedChannelName">
              채널명
            </label>

            <input
              id="managedChannelName"
              name="name"
              type="text"
              value={channelForm.name}
              onChange={handleChannelFormChange}
              maxLength={100}
            />
          </div>

          <div className="form-group">
            <label htmlFor="managedChannelSlug">
              채널 주소
            </label>

            <input
              id="managedChannelSlug"
              type="text"
              value={channel.slug}
              disabled
            />

            <small>
              채널 주소는 관리자만 변경할 수 있습니다.
            </small>
          </div>

          <div className="form-group">
            <label htmlFor="managedChannelDescription">
              채널 설명
            </label>

            <textarea
              id="managedChannelDescription"
              name="description"
              value={channelForm.description}
              onChange={handleChannelFormChange}
              maxLength={500}
              placeholder="채널 설명"
            />
          </div>

          <button
            type="submit"
            className="primary-button"
          >
            채널 정보 저장
          </button>
        </form>
      </section>

      <section className="admin-section">
        <h2>현재 매니저</h2>

        {managers.length === 0 ? (
          <div className="empty-box">
            지정된 채널 매니저가 없습니다.
          </div>
        ) : (
          <div className="channel-manager-list">
            {managers.map((manager) => (
              <div
                key={manager.channelMemberId}
                className="channel-manager-item"
              >
                <div className="admin-owner-search-avatar">
                  {manager.profileImageUrl ? (
                    <img
                      src={getImageUrl(
                        manager.profileImageUrl,
                      )}
                      alt={manager.nickname}
                    />
                  ) : (
                    <span>
                      {manager.nickname?.charAt(0) ||
                        '?'}
                    </span>
                  )}
                </div>

                <div>
                  <strong>{manager.nickname}</strong>
                  <span>{manager.email}</span>
                </div>

                <button
                  type="button"
                  className="admin-danger-button"
                  onClick={() =>
                    handleRemove(manager)
                  }
                >
                  매니저 해제
                </button>
              </div>
            ))}
          </div>
        )}
      </section>

      <section className="admin-section channel-manager-search-section">
        <h2>매니저 추가</h2>

        <form
          className="admin-channel-owner-form"
          onSubmit={handleSearch}
        >
          <div className="form-group">
            <label htmlFor="managerKeyword">
              회원 검색
            </label>

            <input
              id="managerKeyword"
              type="text"
              value={keyword}
              onChange={(e) =>
                setKeyword(e.target.value)
              }
              placeholder="닉네임 또는 이메일"
            />
          </div>

          <button
            type="submit"
            className="secondary-button"
            disabled={searchLoading}
          >
            {searchLoading ? '검색 중...' : '검색'}
          </button>
        </form>

        {candidates.length > 0 && (
          <div className="admin-owner-search-results">
            {candidates.map((candidate) => {
              const selected =
                selectedUser?.userId ===
                candidate.userId

              return (
                <button
                  key={candidate.userId}
                  type="button"
                  className={
                    selected
                      ? 'admin-owner-search-item selected'
                      : 'admin-owner-search-item'
                  }
                  onClick={() =>
                    setSelectedUser(candidate)
                  }
                >
                  <div className="admin-owner-search-avatar">
                    {candidate.profileImageUrl ? (
                      <img
                        src={getImageUrl(
                          candidate.profileImageUrl,
                        )}
                        alt={candidate.nickname}
                      />
                    ) : (
                      <span>
                        {candidate.nickname?.charAt(0) ||
                          '?'}
                      </span>
                    )}
                  </div>

                  <div>
                    <strong>
                      {candidate.nickname}
                    </strong>
                    <span>{candidate.email}</span>
                  </div>
                </button>
              )
            })}
          </div>
        )}

        {selectedUser && (
          <div className="admin-selected-owner">
            <div>
              <span>선택한 회원</span>
              <strong>
                {selectedUser.nickname}
              </strong>
              <p>{selectedUser.email}</p>
            </div>

            <button
              type="button"
              className="primary-button"
              onClick={handleAssign}
            >
              매니저 지정
            </button>
          </div>
        )}
      </section>

      <section className="admin-section channel-manage-board-section">
        <div className="home-section-title">
          <div>
            <h2>게시판 관리</h2>
            <p>
              채널에서 사용할 게시판을 생성하고 관리합니다.
            </p>
          </div>
        </div>

        <form
          className="admin-channel-form"
          onSubmit={handleSubmitBoard}
        >
          <div className="admin-board-form-grid">
            <div className="form-group">
              <label htmlFor="managedBoardCode">
                게시판 코드
              </label>

              <input
                id="managedBoardCode"
                name="code"
                type="text"
                value={boardForm.code}
                onChange={handleBoardFormChange}
                maxLength={30}
                placeholder="예: CLIP"
              />
            </div>

            <div className="form-group">
              <label htmlFor="managedBoardName">
                게시판명
              </label>

              <input
                id="managedBoardName"
                name="name"
                type="text"
                value={boardForm.name}
                onChange={handleBoardFormChange}
                maxLength={50}
              />
            </div>

            <div className="form-group">
              <label htmlFor="managedBoardSortOrder">
                정렬 순서
              </label>

              <input
                id="managedBoardSortOrder"
                name="sortOrder"
                type="number"
                min={0}
                value={boardForm.sortOrder}
                onChange={handleBoardFormChange}
              />
            </div>

            <div className="form-group admin-channel-form-wide">
              <label htmlFor="managedBoardDescription">
                설명
              </label>

              <input
                id="managedBoardDescription"
                name="description"
                type="text"
                value={boardForm.description}
                onChange={handleBoardFormChange}
                maxLength={255}
              />
            </div>
          </div>

          <div className="write-action-row">
            <button
              type="submit"
              className="primary-button"
            >
              {editingBoardId
                ? '게시판 수정'
                : '게시판 생성'}
            </button>

            {editingBoardId && (
              <button
                type="button"
                className="secondary-button"
                onClick={resetBoardForm}
              >
                취소
              </button>
            )}
          </div>
        </form>

        {boardLoading ? (
          <p>불러오는 중...</p>
        ) : (
          <div className="admin-table-wrap">
            <table className="admin-table">
              <thead>
                <tr>
                  <th>코드</th>
                  <th>게시판명</th>
                  <th>설명</th>
                  <th>정렬</th>
                  <th>상태</th>
                  <th>관리</th>
                </tr>
              </thead>

              <tbody>
                {boards.length === 0 ? (
                  <tr>
                    <td colSpan="6">
                      게시판이 없습니다.
                    </td>
                  </tr>
                ) : (
                  boards.map((board) => (
                    <tr key={board.boardId}>
                      <td>{board.code}</td>
                      <td>{board.name}</td>
                      <td>
                        {board.description || '-'}
                      </td>
                      <td>{board.sortOrder}</td>
                      <td>
                        {board.active
                          ? '활성'
                          : '비활성'}
                      </td>
                      <td>
                        <div className="admin-action-row">
                          <button
                            type="button"
                            className="admin-normal-button"
                            onClick={() =>
                              handleEditBoard(board)
                            }
                          >
                            수정
                          </button>

                          <button
                            type="button"
                            className={
                              board.active
                                ? 'admin-danger-button'
                                : 'admin-normal-button'
                            }
                            onClick={() =>
                              handleToggleBoard(board)
                            }
                          >
                            {board.active
                              ? '비활성화'
                              : '활성화'}
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        )}
      </section>
    </div>
  )
}

export default ChannelManagePage