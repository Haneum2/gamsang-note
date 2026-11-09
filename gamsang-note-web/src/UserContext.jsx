import { createContext, useContext, useEffect, useState } from 'react'
import { api } from './api.js'

// 로그인 기능을 붙이기 전까지, 화면 위쪽에서 '나'를 고르는 임시 방식.
// 선택한 회원 번호는 브라우저(localStorage)에 저장해 새로고침해도 유지한다.
const UserContext = createContext(null)
const STORAGE_KEY = 'gamsang.userId'

function readSaved() {
  try {
    const v = Number(localStorage.getItem(STORAGE_KEY))
    return Number.isFinite(v) && v > 0 ? v : null
  } catch {
    return null
  }
}

export function UserProvider({ children }) {
  const [users, setUsers] = useState([])
  const [userId, setUserIdState] = useState(readSaved())
  const [error, setError] = useState('')

  useEffect(() => {
    api
      .users()
      .then((list) => {
        setUsers(list)
        setUserIdState((cur) => (list.some((u) => u.id === cur) ? cur : list[0]?.id ?? null))
      })
      .catch((e) => setError(e.message))
  }, [])

  const setUserId = (id) => {
    setUserIdState(id)
    try {
      localStorage.setItem(STORAGE_KEY, String(id))
    } catch {
      /* 저장이 막혀 있어도 동작에는 문제없다 */
    }
  }

  const me = users.find((u) => u.id === userId) || null
  return <UserContext.Provider value={{ users, me, userId, setUserId, error }}>{children}</UserContext.Provider>
}

export function useUser() {
  return useContext(UserContext)
}
