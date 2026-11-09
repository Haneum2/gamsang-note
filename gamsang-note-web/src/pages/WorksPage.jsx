import { useEffect, useState } from 'react'
import { api } from '../api.js'
import { TYPE_LABEL } from '../labels.js'
import Cover from '../components/Cover.jsx'
import Pager from '../components/Pager.jsx'
import Tabs from '../components/Tabs.jsx'
import WorkDetail from '../components/WorkDetail.jsx'

const TYPES = [['', '전체'], ...Object.entries(TYPE_LABEL)]

export default function WorksPage() {
  const [type, setType] = useState('')
  const [input, setInput] = useState('') // 입력 중인 검색어
  const [q, setQ] = useState('') // 검색 버튼을 눌러 확정된 검색어
  const [page, setPage] = useState(0)
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [selectedId, setSelectedId] = useState(null)

  // type, q, page 가 바뀔 때마다 서버에서 다시 가져온다.
  useEffect(() => {
    let cancelled = false // 오래된 응답이 최신 화면을 덮어쓰지 않게 한다
    setLoading(true)
    setError('')
    api
      .works({ type, q, page })
      .then((res) => !cancelled && setData(res))
      .catch((e) => !cancelled && setError(e.message))
      .finally(() => !cancelled && setLoading(false))
    return () => {
      cancelled = true
    }
  }, [type, q, page])

  const changeType = (v) => {
    setType(v)
    setPage(0)
  }
  const submit = (e) => {
    e.preventDefault()
    setQ(input.trim())
    setPage(0)
  }

  const works = data?.content ?? []

  return (
    <section>
      <h1 className="page-title">작품</h1>

      <form className="toolbar" onSubmit={submit}>
        <Tabs items={TYPES} value={type} onChange={changeType} label="작품 종류" />
        <div className="search">
          <input
            type="search"
            value={input}
            onChange={(e) => setInput(e.target.value)}
            placeholder="제목으로 찾기"
            aria-label="작품 제목"
          />
          <button type="submit" className="btn">
            찾기
          </button>
        </div>
      </form>

      {error && <p className="notice error">작품 목록을 불러오지 못했어요. {error}</p>}
      {data && !error && (
        <p className="count">{q ? `'${q}' 검색 결과 ${data.totalElements}편` : `${data.totalElements}편`}</p>
      )}

      <ul className={`wall ${loading ? 'loading' : ''}`}>
        {works.map((w) => (
          <li key={w.id}>
            <button className={`shelf-item ${w.type.toLowerCase()}`} onClick={() => setSelectedId(w.id)}>
              <Cover type={w.type} title={w.title} />
              <span className="shelf-title">{w.title}</span>
              <span className="shelf-meta">
                <span className="type-name">{TYPE_LABEL[w.type]}</span>
                <span>{w.releaseDate ? w.releaseDate.slice(0, 4) : '연도 미상'}</span>
              </span>
              {w.genres.length > 0 && <span className="shelf-meta">{w.genres.join(', ')}</span>}
            </button>
          </li>
        ))}
      </ul>

      {data && works.length === 0 && !loading && (
        <p className="empty">
          {q ? `'${q}'(이)가 들어간 제목이 없어요. 다른 낱말로 찾아보세요.` : '이 종류의 작품이 아직 없어요.'}
        </p>
      )}

      <Pager page={page} totalPages={data?.totalPages ?? 0} onChange={setPage} />

      {selectedId && <WorkDetail workId={selectedId} onClose={() => setSelectedId(null)} />}
    </section>
  )
}
