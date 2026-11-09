import { useCallback, useEffect, useState } from 'react'
import { api } from '../api.js'
import { useUser } from '../UserContext.jsx'
import { RATING_OPTIONS, STATUS_LABEL, TYPE_LABEL } from '../labels.js'
import Pager from '../components/Pager.jsx'
import ReviewForm from '../components/ReviewForm.jsx'
import Tabs from '../components/Tabs.jsx'

const FILTERS = [['', '전체'], ...Object.entries(STATUS_LABEL)]

export default function RecordsPage() {
  const { userId } = useUser()
  const [status, setStatus] = useState('')
  const [page, setPage] = useState(0)
  const [data, setData] = useState(null)
  const [error, setError] = useState('')

  // 수정·후기 작성 뒤에도 다시 부르므로 함수로 빼 둔다.
  const load = useCallback(() => {
    if (!userId) return
    setError('')
    api
      .records({ userId, status, page })
      .then(setData)
      .catch((e) => setError(e.message))
  }, [userId, status, page])

  useEffect(load, [load])

  const records = data?.content ?? []

  return (
    <section>
      <h1 className="page-title">내 기록</h1>
      <Tabs
        items={FILTERS}
        value={status}
        label="기록 상태"
        onChange={(v) => {
          setStatus(v)
          setPage(0)
        }}
      />

      {error && <p className="notice error">기록을 불러오지 못했어요. {error}</p>}
      {data && <p className="count">{data.totalElements}편</p>}

      {records.length > 0 && (
        <ul className="tickets">
          {records.map((r) => (
            <RecordRow key={r.id} record={r} userId={userId} onChanged={load} />
          ))}
        </ul>
      )}
      {data && records.length === 0 && (
        <p className="empty">
          {status
            ? `'${STATUS_LABEL[status]}' 기록이 없어요. '전체' 탭에서 다른 기록을 볼 수 있어요.`
            : "아직 기록이 없어요. 작품을 열어 '기록 추가'를 눌러 보세요."}
        </p>
      )}

      <Pager page={page} totalPages={data?.totalPages ?? 0} onChange={setPage} />
    </section>
  )
}

function RecordRow({ record, userId, onChanged }) {
  const [writing, setWriting] = useState(false)
  const [saved, setSaved] = useState(false)
  const [error, setError] = useState('')

  // 바꾼 항목 하나만 보낸다. 서버(PATCH)는 null 인 항목을 건드리지 않는다.
  const patch = async (change) => {
    setError('')
    try {
      await api.updateRecord(record.id, { userId, ...change })
      onChanged()
    } catch (e) {
      setError(`바꾸지 못했어요. ${e.message}`)
    }
  }

  return (
    <li className={`ticket ${record.workType.toLowerCase()}`}>
      <div className="ticket-main">
        <div className="ticket-info">
          <p className="ticket-title">{record.title}</p>
          <p className="ticket-meta">
            <span className="type-name">{TYPE_LABEL[record.workType]}</span>
            <span>{record.watchedDate ?? '감상일 없음'}</span>
          </p>
        </div>
        <div className="ticket-edit">
          {/* 상태는 도장 모양 select: 봤어요=채움, 보는 중=실선, 보고 싶어요=점선 */}
          <select
            className={`stamp ${record.status.toLowerCase()}`}
            value={record.status}
            onChange={(e) => patch({ status: e.target.value })}
            aria-label="상태"
          >
            {Object.entries(STATUS_LABEL).map(([v, l]) => (
              <option key={v} value={v}>
                {l}
              </option>
            ))}
          </select>
          {/* ponytail: 별점 지우기는 PATCH 가 null 을 무시해서 아직 불가 (CLAUDE.md 10-8) */}
          <select value={record.rating ?? ''} onChange={(e) => patch({ rating: Number(e.target.value) })} aria-label="별점">
            {record.rating == null && <option value="">별점 없음</option>}
            {RATING_OPTIONS.map((r) => (
              <option key={r} value={r}>
                {r.toFixed(1)}
              </option>
            ))}
          </select>
          <input
            type="date"
            value={record.watchedDate ?? ''}
            onChange={(e) => e.target.value && patch({ watchedDate: e.target.value })}
            aria-label="감상일"
          />
          <button
            className="btn"
            aria-expanded={writing}
            onClick={() => {
              setWriting(!writing)
              setSaved(false)
            }}
          >
            {writing ? '닫기' : '후기 쓰기'}
          </button>
        </div>
      </div>
      {error && <p className="notice error">{error}</p>}
      {saved && (
        <p className="notice ok" role="status">
          후기를 저장했어요. &apos;후기&apos; 화면에서 볼 수 있어요.
        </p>
      )}
      {writing && (
        <ReviewForm
          recordId={record.id}
          onSaved={() => {
            setWriting(false)
            setSaved(true)
            onChanged() // 트리거가 상태를 '봤어요'로 바꿨으니 다시 읽는다
          }}
        />
      )}
    </li>
  )
}
