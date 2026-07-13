import api from './axiosInstance'

export const createReport = async ({ targetType, targetId, reason }) => {
  const response = await api.post('/api/reports', {
    targetType,
    targetId,
    reason,
  })

  return response.data
}

export const getAdminReports = async ({ status, targetType, page = 0, size = 10 }) => {
  const response = await api.get('/api/admin/reports', {
    params: {
      status,
      targetType,
      page,
      size,
    },
  })

  return response.data
}

export const resolveReport = async (reportId) => {
  const response = await api.patch(`/api/admin/reports/${reportId}/resolve`)

  return response.data
}