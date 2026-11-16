// 서버(Spring Boot) API 를 부르는 함수 모음. 화면 코드는 fetch 를 직접 쓰지 않고 여기만 거친다.

export class ApiError extends Error {
  constructor(status, message) {
    super(message)
    this.status = status
  }
}

const DEFAULT_MESSAGES = {
  400: '요청이 올바르지 않습니다.',
  401: '로그인이 필요합니다.',
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
    throw new ApiError(0, '서버에 연결할 수 없습니다. 잠시 후 다시 시도해 주세요.')
  }
  if (!res.ok) {
    // 세션이 끝났으면 화면 전체를 로그인 화면으로 돌린다(UserContext 가 듣는다). /me 확인 자체는 제외.
    if (res.status === 401 && path !== '/api/auth/me') window.dispatchEvent(new Event('gamsang:unauthorized'))
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

// 회원 번호(userId)는 보내지 않는다. 서버가 로그인 세션(쿠키)으로 누구인지 안다.
export const api = {
  me: () => request('/api/auth/me'),
  login: (body) => request('/api/auth/login', { method: 'POST', body }),
  signup: (body) => request('/api/auth/signup', { method: 'POST', body }),
  logout: () => request('/api/auth/logout', { method: 'POST' }),
  users: () => request('/api/users'),
  works: ({ type, q, page = 0, size = 12 }) => request(`/api/works${qs({ type, q, page, size })}`),
  work: (id) => request(`/api/works/${id}`),
  // 외부 작품 검색·가져오기. source: 'kopis'(공연) | 'tmdb'(영화)
  externalSearch: (source, { q, page = 0 }) => request(`/api/works/${source}${qs({ q, page })}`),
  importExternal: (source, externalId) => request(`/api/works/${source}/${externalId}`, { method: 'POST' }),
  createRecord: (body) => request('/api/records', { method: 'POST', body }),
  records: ({ status, page = 0, size = 20 }) => request(`/api/records${qs({ status, page, size })}`),
  updateRecord: (id, body) => request(`/api/records/${id}`, { method: 'PATCH', body }),
  createReview: (recordId, body) => request(`/api/records/${recordId}/review`, { method: 'POST', body }),
  reviews: ({ page = 0, size = 10 }) => request(`/api/reviews${qs({ page, size })}`),
  feed: ({ page = 0, size = 10 }) => request(`/api/feed${qs({ page, size })}`),
  social: (reviewId) => request(`/api/reviews/${reviewId}/social`),
  like: (reviewId) => request(`/api/reviews/${reviewId}/likes`, { method: 'POST' }),
  unlike: (reviewId) => request(`/api/reviews/${reviewId}/likes`, { method: 'DELETE' }),
  comments: (reviewId) => request(`/api/reviews/${reviewId}/comments`),
  addComment: (reviewId, content) => request(`/api/reviews/${reviewId}/comments`, { method: 'POST', body: { content } }),
  friends: () => request('/api/friends'),
  friendRequests: () => request('/api/friends/requests'),
  sentFriendRequests: () => request('/api/friends/requests/sent'),
  sendFriendRequest: (receiverId) => request('/api/friends/requests', { method: 'POST', body: { receiverId } }),
  acceptFriend: (requesterId) => request('/api/friends/requests/accept', { method: 'POST', body: { requesterId } }),
  rejectFriend: (requesterId, receiverId) =>
    request(`/api/friends/requests${qs({ requesterId, receiverId })}`, { method: 'DELETE' }),
  statsMonthly: (year) => request(`/api/stats/monthly${qs({ year })}`),
  statsGenres: () => request('/api/stats/genres'),
  statsYearEnd: (year) => request(`/api/stats/year-end${qs({ year })}`),
}
