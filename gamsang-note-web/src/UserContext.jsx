import { createContext, useContext, useEffect, useState } from 'react'
import { api } from './api.js'

// 로그인한 회원 정보를 화면 전체에 나눠 준다.
// 로그인 상태는 서버 세션(쿠키)에 있으므로, 처음 열 때 /api/auth/me 로 "지금 누구로 로그인돼 있는지" 물어본다.
const UserContext = createContext(null)

export function UserProvider({ children }) {
  const [me, setMe] = useState(undefined) // undefined: 확인 중, null: 로그인 안 함, {id, nickname, email}: 로그인
  const [users, setUsers] = useState([]) // 친구 요청 상대 고르기용 회원 목록
  const [error, setError] = useState('')

  useEffect(() => {
    api
      .me()
      .then(setMe)
      .catch((e) => {
        if (e.status !== 401) setError(e.message)
        setMe(null)
      })
    // 다른 요청이 401(세션 만료)을 받으면 api.js 가 이 이벤트를 보낸다 → 로그인 화면으로
    const onExpired = () => setMe(null)
    window.addEventListener('gamsang:unauthorized', onExpired)
    return () => window.removeEventListener('gamsang:unauthorized', onExpired)
  }, [])

  useEffect(() => {
    if (!me) {
      setUsers([])
      return
    }
    api.users().then(setUsers).catch((e) => setError(e.message))
  }, [me])

  const logout = async () => {
    try {
      await api.logout()
    } finally {
      setMe(null)
    }
  }

  return (
    <UserContext.Provider value={{ me, userId: me?.id ?? null, users, setMe, logout, error }}>
      {children}
    </UserContext.Provider>
  )
}

export function useUser() {
  return useContext(UserContext)
}
