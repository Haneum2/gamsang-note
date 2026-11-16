import { useCallback, useEffect, useState } from 'react'
import { api } from '../api.js'
import { useUser } from '../UserContext.jsx'

// 받은 요청 수락·거절, 보낸 요청 취소, 친구 목록, 친구 요청 보내기.
// 나(로그인한 회원)는 서버가 세션으로 알기 때문에 상대 번호만 보낸다.
export default function FriendsPage() {
  const { userId, users } = useUser()
  const [friends, setFriends] = useState(null)
  const [requests, setRequests] = useState(null)
  const [sent, setSent] = useState(null)
  const [target, setTarget] = useState('')
  const [message, setMessage] = useState(null) // { kind: 'ok' | 'error', text }
  const [error, setError] = useState('')

  const load = useCallback(() => {
    if (!userId) return
    Promise.all([api.friends(), api.friendRequests(), api.sentFriendRequests()])
      .then(([f, r, s]) => {
        setFriends(f)
        setRequests(r)
        setSent(s)
      })
      .catch((e) => setError(e.message))
  }, [userId])

  useEffect(load, [load])

  // 버튼 동작 공통: 서버 호출 → 결과 문구 → 목록 다시 읽기
  const act = async (fn, okText, conflictText) => {
    setMessage(null)
    try {
      await fn()
      setMessage({ kind: 'ok', text: okText })
      load()
      return true
    } catch (e) {
      setMessage({ kind: 'error', text: e.status === 409 && conflictText ? conflictText : e.message })
      return false
    }
  }

  const accept = (r) => act(() => api.acceptFriend(r.userId), `${r.nickname}님과 친구가 됐어요.`)
  const reject = (r) => act(() => api.rejectFriend(r.userId, userId), `${r.nickname}님의 요청을 거절했어요.`)
  const cancel = (s) => act(() => api.rejectFriend(userId, s.userId), `${s.nickname}님에게 보낸 요청을 취소했어요.`)

  const send = (e) => {
    e.preventDefault()
    const other = users.find((u) => u.id === Number(target))
    act(
      () => api.sendFriendRequest(other.id),
      `${other.nickname}님에게 친구 요청을 보냈어요. 상대가 수락하면 친구 목록에 나와요.`,
      `${other.nickname}님과는 이미 요청이 오가는 중이에요. 위 '받은 요청'이나 '보낸 요청'을 확인해 주세요.`,
    ).then((ok) => ok && setTarget(''))
  }

  // 요청 보낼 수 있는 사람: 나, 이미 친구, 요청을 주고받는 중인 사람을 뺀다.
  const others = [...(friends ?? []), ...(requests ?? []), ...(sent ?? [])]
  const taken = new Set([userId, ...others.map((x) => x.userId)])
  const candidates = users.filter((u) => !taken.has(u.id))

  return (
    <section>
      <h1 className="page-title">친구</h1>
      {error && <p className="notice error">친구 정보를 불러오지 못했어요. {error}</p>}
      {message && (
        <p className={`notice ${message.kind}`} role="status">
          {message.text}
        </p>
      )}

      <div className="section">
        <h2 className="section-title">받은 요청</h2>
        {requests?.length > 0 ? (
          <ul className="tickets">
            {requests.map((r) => (
              <li key={r.userId} className="ticket person">
                <strong>{r.nickname}</strong>
                <span className="person-actions">
                  <button className="btn primary" onClick={() => accept(r)}>
                    수락
                  </button>
                  <button className="btn" onClick={() => reject(r)}>
                    거절
                  </button>
                </span>
              </li>
            ))}
          </ul>
        ) : (
          requests && <p className="empty small">받은 친구 요청이 없어요.</p>
        )}
      </div>

      <div className="section">
        <h2 className="section-title">보낸 요청</h2>
        {sent?.length > 0 ? (
          <ul className="tickets">
            {sent.map((s) => (
              <li key={s.userId} className="ticket person">
                <strong>{s.nickname}</strong>
                <span className="person-actions">
                  <button className="btn" onClick={() => cancel(s)}>
                    요청 취소
                  </button>
                </span>
              </li>
            ))}
          </ul>
        ) : (
          sent && <p className="empty small">수락을 기다리는 요청이 없어요.</p>
        )}
      </div>

      <div className="section">
        <h2 className="section-title">내 친구 {friends ? `${friends.length}명` : ''}</h2>
        {friends?.length > 0 ? (
          <ul className="tickets">
            {friends.map((f) => (
              <li key={f.userId} className="ticket person">
                <strong>{f.nickname}</strong>
              </li>
            ))}
          </ul>
        ) : (
          friends && <p className="empty small">아직 친구가 없어요. 아래에서 친구 요청을 보내 보세요.</p>
        )}
      </div>

      <form className="section" onSubmit={send}>
        <h2 className="section-title">친구 요청 보내기</h2>
        {candidates.length > 0 ? (
          <div className="form-row">
            <label className="field">
              회원
              <select value={target} onChange={(e) => setTarget(e.target.value)} required>
                <option value="">고르세요</option>
                {candidates.map((u) => (
                  <option key={u.id} value={u.id}>
                    {u.nickname}
                  </option>
                ))}
              </select>
            </label>
            <button className="btn primary" disabled={!target}>
              요청 보내기
            </button>
          </div>
        ) : (
          <p className="empty small">요청을 보낼 수 있는 회원이 없어요.</p>
        )}
      </form>
    </section>
  )
}
