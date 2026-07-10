import { uploadPostImage } from '../api/postApi'

class CkeditorUploadAdapter {
  constructor(loader) {
    this.loader = loader
  }

  async upload() {
    try {
      const file = await this.loader.file
      const result = await uploadPostImage(file)

      if (!result.success) {
        throw new Error(result.message || '이미지 업로드에 실패했습니다.')
      }

      const API_BASE_URL = import.meta.env.VITE_API_BASE_URL

      return {
        default: `${API_BASE_URL}${result.data.imageUrl}`,
      }
    } catch (error) {
      const message =
        error.response?.data?.message ||
        error.message ||
        '이미지 업로드에 실패했습니다.'

      return Promise.reject(message)
    }
  }

  abort() {}
}

export function CustomUploadAdapterPlugin(editor) {
  editor.plugins.get('FileRepository').createUploadAdapter = (loader) => {
    return new CkeditorUploadAdapter(loader)
  }
}