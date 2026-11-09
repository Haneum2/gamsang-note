import { NavLink, Navigate, Route, Routes } from 'react-router-dom'
import { useUser } from './UserContext.jsx'
import WorksPage from './pages/WorksPage.jsx'
import RecordsPage from './pages/RecordsPage.jsx'
import ReviewsPage from './pages/ReviewsPage.jsx'
import FriendsPage from './pages/FriendsPage.jsx'
import StatsPage from './pages/StatsPage.jsx'

export default function App() {
  const { users, userId, setUserId, error } = useUser()

  return (
    <div className="app">
      <header className="header">
        <div className="header-inner">
          <NavLink to="/" className="brand">
            감상노트
          </NavLink>
          <nav className="nav" aria-label="메뉴">
            <NavLink to="/works">작품</NavLink>
            <NavLink to="/records">내 기록</NavLink>
            <NavLink to="/reviews">후기</NavLink>
            <NavLink to="/friends">친구</NavLink>
            <NavLink to="/stats">통계</NavLink>
          </nav>
          <label className="me">
            나
            <select value={userId ?? ''} onChange={(e) => setUserId(Number(e.target.value))} disabled={!users.length}>
              {users.map((u) => (
                <option key={u.id} value={u.id}>
                  {u.nickname}
                </option>
              ))}
            </select>
          </label>
        </div>
      </header>

      <main className="main">
        {error && <p className="notice error">회원 목록을 불러오지 못했어요. {error}</p>}
        <Routes>
          <Route path="/" element={<Navigate to="/works" replace />} />
          <Route path="/works" element={<WorksPage />} />
          {/* key 가 바뀌면 React 가 화면을 새로 만든다 → '나'를 바꾸면 상태가 초기화된다 */}
          <Route path="/records" element={<RecordsPage key={userId} />} />
          <Route path="/reviews" element={<ReviewsPage key={userId} />} />
          <Route path="/friends" element={<FriendsPage key={userId} />} />
          <Route path="/stats" element={<StatsPage key={userId} />} />
          <Route path="*" element={<p className="empty">없는 페이지예요. 위 메뉴에서 화면을 골라 주세요.</p>} />
        </Routes>
      </main>
    </div>
  )
}
