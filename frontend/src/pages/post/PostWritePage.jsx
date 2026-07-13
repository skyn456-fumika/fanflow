import { useEffect, useRef, useState } from 'react'
import { Link, useLocation, useNavigate, useParams } from 'react-router-dom'
import { CKEditor } from '@ckeditor/ckeditor5-react'
import ClassicEditor from '@ckeditor/ckeditor5-build-classic'
import { getBoards, getChannelBoards } from '../../api/boardApi'
import {
  createChannelPost,
  createPost,
  getPostDetail,
  updatePost,
} from '../../api/postApi'
import { getMyInfo } from '../../api/authApi'
import { CustomUploadAdapterPlugin } from '../../utils/ckeditorUploadAdapter'

function PostWritePage() {
  const location = useLocation()
  const navigate = useNavigate()
  const { channelSlug, postId } = useParams()

  const [me, setMe] = useState(null)

  const isEditMode = !!postId

  const DEFAULT_CHANNEL_SLUG = 'fumika'
  const currentChannelSlug = channelSlug || DEFAULT_CHANNEL_SLUG

  const postListPath = channelSlug
    ? `/channels/${currentChannelSlug}/posts`
    : '/posts'

  const alertShownRef = useRef(false)
  const contentRef = useRef('')
  const editorRef = useRef(null)

  const [editorReady, setEditorReady] = useState(!isEditMode)

  const [boards, setBoards] = useState([])
  const [form, setForm] = useState({
    boardCode: '',
    title: '',
    content: '',
  })

  const [loading, setLoading] = useState(false)
  const [errorMessage, setErrorMessage] = useState('')

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

  const loadBoards = async () => {
    try {
      const result = channelSlug
        ? await getChannelBoards(currentChannelSlug)
        : await getBoards()

      if (result.success) {
        setBoards(result.data)

        if (!isEditMode && result.data.length > 0) {
          setForm((prev) => ({
            ...prev,
            boardCode: result.data[0].code,
          }))
        }
      }
    } catch (error) {
      console.error(error)
      setErrorMessage('게시판 목록을 불러오지 못했습니다.')
    }
  }

  const loadPost = async () => {
    try {
      const postResult = await getPostDetail(postId)

      if (!postResult.success) {
        setErrorMessage(postResult.message || '게시글을 불러오지 못했습니다.')
        return
      }

      const myInfoResult = await getMyInfo()

      if (!myInfoResult.success) {
        requireLogin()
        return
      }

      const postData = postResult.data
      const meData = myInfoResult.data

      setMe(meData)

      if (postData.writerId !== meData.userId) {
        alert('게시글 작성자만 수정할 수 있습니다.')
        navigate(`/posts/${postId}`, { replace: true })
        return
      }

      contentRef.current = postData.content || ''

      setForm({
        boardCode: postData.boardCode,
        title: postData.title,
        content: postData.content || '',
      })

      setEditorReady(true)
    } catch (error) {
      console.error(error)

      if (error.response?.status === 401) {
        localStorage.removeItem('accessToken')
        requireLogin()
        return
      }

      if (error.response?.status === 403) {
        alert('게시글 작성자만 수정할 수 있습니다.')
        navigate(`/posts/${postId}`, { replace: true })
        return
      }

      setErrorMessage('게시글을 불러오지 못했습니다.')
    }
  }

  useEffect(() => {
    const token = localStorage.getItem('accessToken')

    if (!token) {
      requireLogin()
      return
    }

    loadBoards()

    if (isEditMode) {
      loadPost()
    } else {
      contentRef.current = ''
      setEditorReady(true)
    }
  }, [postId])

  const handleChange = (e) => {
    const { name, value } = e.target

    setForm((prev) => ({
      ...prev,
      [name]: value,
    }))
  }

  const isEmptyContent = (html) => {
    if (!html || html.trim().length === 0) {
      return true
    }

    const hasImage = /<img\b[^>]*src=["'][^"']+["'][^>]*>/i.test(html)

    if (hasImage) {
      return false
    }

    const text = html
      .replace(/<[^>]*>/g, '')
      .replace(/&nbsp;/g, '')
      .trim()

    return text.length === 0
  }

  const handleSubmit = async (e) => {
    e.preventDefault()

    if (!isEditMode && !form.boardCode) {
      alert('게시판을 선택해주세요.')
      return
    }

    if (!form.title.trim()) {
      alert('제목을 입력해주세요.')
      return
    }

    if (form.title.length > 200) {
      alert('제목은 200자 이하로 입력해주세요.')
      return
    }

    const content = editorRef.current
      ? editorRef.current.getData()
      : contentRef.current

    if (isEmptyContent(content)) {
      alert('내용을 입력해주세요.')
      return
    }

    try {
      setLoading(true)
      setErrorMessage('')

      const result = isEditMode
        ? await updatePost(postId, {
            title: form.title.trim(),
            content,
          })
        : channelSlug
          ? await createChannelPost({
              channelSlug: currentChannelSlug,
              boardCode: form.boardCode,
              title: form.title.trim(),
              content,
            })
          : await createPost({
              boardCode: form.boardCode,
              title: form.title.trim(),
              content,
            })

      if (result.success) {
        //alert(isEditMode ? '게시글이 수정되었습니다.' : '게시글이 작성되었습니다.')
        navigate(`/posts/${result.data.postId}`)
      } else {
        setErrorMessage(
          result.message ||
            (isEditMode
              ? '게시글 수정에 실패했습니다.'
              : '게시글 작성에 실패했습니다.'),
        )
      }
    } catch (error) {
      console.error(error)

      if (error.response?.status === 401) {
        localStorage.removeItem('accessToken')
        requireLogin()
        return
      }

      const message =
        error.response?.data?.message ||
        (isEditMode
          ? '게시글 수정에 실패했습니다.'
          : '게시글 작성에 실패했습니다.')

      setErrorMessage(message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div>
      <div className="page-title-row">
        <div>
          <h1>{isEditMode ? '게시글 수정' : '게시글 작성'}</h1>
          <p>
            {isEditMode
              ? 'FanFlow 커뮤니티 게시글을 수정합니다.'
              : 'FanFlow 커뮤니티에 새 게시글을 작성합니다.'}
          </p>
        </div>

        <Link
          to={isEditMode ? `/posts/${postId}` : postListPath}
          className="secondary-button"
        >
          {isEditMode ? '상세' : '목록'}
        </Link>
      </div>

      {errorMessage && <p className="error-message">{errorMessage}</p>}

      <div className="write-box">
        <form onSubmit={handleSubmit} className="write-form">
          <div className="form-group">
            <label htmlFor="boardCode">게시판</label>
            <select
              id="boardCode"
              name="boardCode"
              value={form.boardCode}
              onChange={handleChange}
              disabled={isEditMode}
            >
              {boards.length === 0 ? (
                <option value="">게시판 없음</option>
              ) : (
                boards.map((board) => (
                  <option key={board.boardId} value={board.code}>
                    {board.name}
                  </option>
                ))
              )}
            </select>
          </div>

          <div className="form-group">
            <label htmlFor="title">제목</label>
            <input
              id="title"
              name="title"
              type="text"
              value={form.title}
              onChange={handleChange}
              placeholder="제목을 입력하세요"
              maxLength={200}
            />
          </div>

          <div className="form-group">
            <label htmlFor="content">내용</label>
            <div className="ckeditor-wrap">
              {editorReady && (
                <CKEditor
                  editor={ClassicEditor}
                  data={form.content}
                  config={{
                    licenseKey: 'GPL',
                    extraPlugins: [CustomUploadAdapterPlugin],
                    toolbar: [
                      'heading',
                      '|',
                      'bold',
                      'italic',
                      'link',
                      'bulletedList',
                      'numberedList',
                      '|',
                      'imageUpload',
                      'blockQuote',
                      'undo',
                      'redo',
                    ],
                    image: {
                      toolbar: [
                        'imageTextAlternative',
                        'toggleImageCaption',
                        'imageStyle:inline',
                        'imageStyle:block',
                        'imageStyle:side',
                      ],
                    },
                  }}
                  onReady={(editor) => {
                    editorRef.current = editor
                    contentRef.current = editor.getData()

                    const editableElement = editor.ui.view.editable.element

                    if (editableElement) {
                      editableElement.setAttribute('lang', 'ko')
                      editableElement.setAttribute('spellcheck', 'false')
                      editableElement.setAttribute('autocapitalize', 'off')
                      editableElement.setAttribute('autocomplete', 'off')
                    }
                  }}
                />
              )}
            </div>
          </div>

          <div className="write-action-row">
            <button type="submit" className="primary-button" disabled={loading}>
              {loading
                ? isEditMode
                  ? '수정 중...'
                  : '작성 중...'
                : isEditMode
                  ? '수정하기'
                  : '작성하기'}
            </button>

            <Link
              to={isEditMode ? `/posts/${postId}` : postListPath}
              className="secondary-button"
            >
              취소
            </Link>
          </div>
        </form>
      </div>
    </div>
  )
}

export default PostWritePage