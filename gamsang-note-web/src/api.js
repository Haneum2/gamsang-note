// 서버(Spring Boot) API 를 부르는 함수 모음. 화면 코드는 fetch 를 직접 쓰지 않고 여기만 거친다.

export class ApiError extends Error {
  constructor(status, message) {
    super(message)
    this.status = status
  }
}

const DEFAULT_MESSAGES = {
  400: '요청이 올바르지 않습니다.',
  403: '권한이 없습니다.',
  404: '찾을 수 없습니다.',
  409: '이미 처리된 요청입니다.',
  500: '서버에 문제가 생겼습니다. 잠시 후 다시 시도해 주세요.',
}

async function request(path, { method = 'GET', body } = {}) {
  let res
  try {
    res = await fetch(path, {
      method,
      headers: body ? { 'Content-Type': 'application/json' } : undefined,
      body: body ? JSON.stringify(body) : undefined,
    })
  } catch {
    throw new ApiError(0, '서버에 연결할 수 없습니다. 백엔드(8080)가 실행 중인지 확인해 주세요.')
  }
  if (!res.ok) {
    let message = ''
    try {
      message = (await res.json()).message || ''
    } catch {
      /* 응답이 JSON 이 아니면 기본 메시지를 쓴다 */
    }
    throw new ApiError(res.status, message || DEFAULT_MESSAGES[res.status] || `오류가 발생했습니다. (${res.status})`)
  }
  if (res.status === 204) return null
  return res.json()
}

function qs(params) {
  const sp = new URLSearchParams()
  Object.entries(params).forEach(([k, v]) => {
    if (v !== undefined && v !== null && v !== '') sp.set(k, v)
  })
  const s = sp.toString()
  return s ? `?${s}` : ''
}

export const api = {
  users: () => request('/api/users'),
  works: ({ type, q, page = 0, size = 12 }) => request(`/api/works${qs({ type, q, page, size })}`),
  work: (id) => request(`/api/works/${id}`),
  createRecord: (body) => request('/api/records', { method: 'POST', body }),
  records: ({ userId, status, page = 0, size = 20 }) => request(`/api/records${qs({ userId, status, page, size })}`),
  updateRecord: (id, body) => request(`/api/records/${id}`, { method: 'PATCH', body }),
  createReview: (recordId, body) => request(`/api/records/${recordId}/review`, { method: 'POST', body }),
  reviews: ({ viewerId, page = 0, size = 10 }) => request(`/api/reviews${qs({ viewerId, page, size })}`),
  feed: ({ viewerId, page = 0, size = 10 }) => request(`/api/feed${qs({ viewerId, page, size })}`),
  social: (reviewId, viewerId) => request(`/api/reviews/${reviewId}/social${qs({ viewerId })}`),
  like: (reviewId, userId) => request(`/api/reviews/${reviewId}/likes`, { method: 'POST', body: { userId } }),
  unlike: (reviewId, userId) => request(`/api/reviews/${reviewId}/likes${qs({ userId })}`, { method: 'DELETE' }),
  comments: (reviewId, viewerId) => request(`/api/reviews/${reviewId}/comments${qs({ viewerId })}`),
  addComment: (reviewId, userId, content) =>
    request(`/api/reviews/${reviewId}/comments`, { method: 'POST', body: { userId, content } }),
  friends: (userId) => request(`/api/friends${qs({ userId })}`),
  friendRequests: (userId) => request(`/api/friends/requests${qs({ userId })}`),
  sendFriendRequest: (requesterId, receiverId) =>
    request('/api/friends/requests', { method: 'POST', body: { requesterId, receiverId } }),
  acceptFriend: (requesterId, receiverId) =>
    request('/api/friends/requests/accept', { method: 'POST', body: { requesterId, receiverId } }),
  rejectFriend: (requesterId, receiverId) =>
    request(`/api/friends/requests${qs({ requesterId, receiverId })}`, { method: 'DELETE' }),
  statsMonthly: (userId, year) => request(`/api/stats/monthly${qs({ userId, year })}`),
  statsGenres: (userId) => request(`/api/stats/genres${qs({ userId })}`),
  statsYearEnd: (userId, year) => request(`/api/stats/year-end${qs({ userId, year })}`),
}
