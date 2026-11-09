import { useEffect, useState } from 'react'
import { api } from '../api.js'
import { useUser } from '../UserContext.jsx'
import { RATING_OPTIONS, STATUS_LABEL, TYPE_LABEL } from '../labels.js'
import Cover from './Cover.jsx'

// 작품 상세 + 감상 기록 등록을 한 패널에서 한다.
export default function WorkDetail({ workId, onClose }) {
  const { userId } = useUser()
  const [work, setWork] = useState(null)
  const [error, setError] = useState('')

  const [status, setStatus] = useState('WANT')
  const [rating, setRating] = useState('')
  const [watchedDate, setWatchedDate] = useState('')
  const [saving, setSaving] = useState(false)
  const [message, setMessage] = useState(null) // { kind: 'ok' | 'error', text }

  useEffect(() => {
    let cancelled = false
    setWork(null)
    api
      .work(workId)
      .then((w) => !cancelled && setWork(w))
      .catch((e) => !cancelled && setError(e.message))
    return () => {
      cancelled = true
    }
  }, [workId])

  // Esc 키로 닫기
  useEffect(() => {
    const onKey = (e) => e.key === 'Escape' && onClose()
    window.addEventListener('keydown', onKey)
    return () => window.removeEventListener('keydown', onKey)
  }, [onClose])

  const save = async (e) => {
    e.preventDefault()
    setSaving(true)
    setMessage(null)
    try {
      await api.createRecord({
        userId,
        workId,
        status,
        rating: rating === '' ? null : Number(rating),
        watchedDate: watchedDate || null,
      })
      setMessage({ kind: 'ok', text: "기록을 추가했어요. '내 기록'에서 고칠 수 있어요." })
    } catch (err) {
      const text = err.status === 409 ? "이미 기록한 작품이에요. '내 기록'에서 상태와 별점을 고칠 수 있어요." : err.message
      setMessage({ kind: 'error', text })
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="overlay" onMouseDown={(e) => e.target === e.currentTarget && onClose()}>
      <aside className="panel" role="dialog" aria-modal="true" aria-label="작품 상세">
        <button className="btn text close" onClick={onClose}>
          닫기
        </button>

        {error && <p className="notice error">작품 정보를 불러오지 못했어요. {error}</p>}
        {!work && !error && <p className="empty">불러오는 중이에요.</p>}

        {work && (
          <div className={work.type.toLowerCase()}>
            <div className="panel-head">
              <Cover type={work.type} title={work.title} />
              <div>
                <h2>{work.title}</h2>
                <p className="panel-meta">
                  <span className="type-name">{TYPE_LABEL[work.type]}</span>
                  <span>{work.releaseDate ? work.releaseDate.slice(0, 4) : '연도 미상'}</span>
                </p>
                {work.genres.length > 0 && <p className="panel-meta">{work.genres.join(', ')}</p>}
              </div>
            </div>

            <dl className="facts">
              {work.runtime != null && (
                <>
                  <dt>상영 시간</dt>
                  <dd>{work.runtime}분</dd>
                </>
              )}
              {work.pages != null && (
                <>
                  <dt>쪽수</dt>
                  <dd>{work.pages}쪽</dd>
                </>
              )}
              {work.isbn && (
                <>
                  <dt>ISBN</dt>
                  <dd>{work.isbn}</dd>
                </>
              )}
              {work.venue && (
                <>
                  <dt>공연장</dt>
                  <dd>{work.venue}</dd>
                </>
              )}
              {work.period && (
                <>
                  <dt>공연 기간</dt>
                  <dd>{work.period}</dd>
                </>
              )}
            </dl>
            {work.summary && <p className="summary">{work.summary}</p>}

            <form className="form" onSubmit={save}>
              <h3>기록 추가</h3>
              <label className="field">
                상태
                <select value={status} onChange={(e) => setStatus(e.target.value)}>
                  {Object.entries(STATUS_LABEL).map(([v, l]) => (
                    <option key={v} value={v}>
                      {l}
                    </option>
                  ))}
                </select>
              </label>
              <label className="field">
                별점
                <select value={rating} onChange={(e) => setRating(e.target.value)}>
                  <option value="">없음</option>
                  {RATING_OPTIONS.map((r) => (
                    <option key={r} value={r}>
                      {r.toFixed(1)}
                    </option>
                  ))}
                </select>
              </label>
              <label className="field">
                감상일
                <input type="date" value={watchedDate} onChange={(e) => setWatchedDate(e.target.value)} />
              </label>
              <button className="btn primary" disabled={saving || !userId}>
                {saving ? '추가하는 중' : '기록 추가'}
              </button>
              {message && (
                <p className={`notice ${message.kind}`} role="status">
                  {message.text}
                </p>
              )}
            </form>
          </div>
        )}
      </aside>
    </div>
  )
}
