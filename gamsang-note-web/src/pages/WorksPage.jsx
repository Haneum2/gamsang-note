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
  const [reload, setReload] = useState(0) // KOPIS 에서 가져오면 목록을 다시 읽는다

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
  }, [type, q, page, reload])

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

      {type === 'PERFORMANCE' && (
        <KopisSearch
          key={q}
          q={q}
          onOpen={(workId) => {
            setSelectedId(workId)
            setReload((n) => n + 1)
          }}
        />
      )}

      {selectedId && <WorkDetail workId={selectedId} onClose={() => setSelectedId(null)} />}
    </section>
  )
}

// 공연 탭에서만 보이는 KOPIS(공연예술통합전산망) 검색. 가져오면 우리 DB에 저장하고 상세를 연다.
function KopisSearch({ q, onOpen }) {
  const [items, setItems] = useState(null)
  const [error, setError] = useState('')
  const [busyId, setBusyId] = useState(null)

  useEffect(() => {
    if (!q) return
    let cancelled = false
    api
      .kopis({ q })
      .then((res) => !cancelled && setItems(res))
      .catch((e) => !cancelled && setError(e.message))
    return () => {
      cancelled = true
    }
  }, [q])

  const open = async (item) => {
    if (item.workId) return onOpen(item.workId)
    setBusyId(item.externalId)
    setError('')
    try {
      const work = await api.importKopis(item.externalId)
      setItems((list) => list.map((x) => (x.externalId === item.externalId ? { ...x, workId: work.id } : x)))
      onOpen(work.id)
    } catch (e) {
      setError(`'${item.title}'을(를) 가져오지 못했어요. ${e.message}`)
    } finally {
      setBusyId(null)
    }
  }

  return (
    <div className="section">
      <h2 className="section-title">KOPIS에서 찾기</h2>
      {!q && <p className="empty small">제목으로 찾으면 요즘(오늘 앞뒤 15일) 공연 중인 작품을 KOPIS에서 함께 찾아요.</p>}
      {error && <p className="notice error">{error}</p>}
      {q && items && items.length === 0 && (
        <p className="empty small">KOPIS에도 '{q}' 공연이 없어요. 요즘 공연 중인 작품만 찾을 수 있어요.</p>
      )}
      {items && items.length > 0 && (
        <ul className="tickets">
          {items.map((it) => (
            <li key={it.externalId} className="ticket performance">
              <div className="ticket-main">
                <div className="ticket-info">
                  <p className="ticket-title">{it.title}</p>
                  <p className="ticket-meta">
                    <span className="type-name">공연</span>
                    {it.genre && <span>{it.genre}</span>}
                    {it.period && <span>{it.period}</span>}
                    {it.venue && <span>{it.venue}</span>}
                  </p>
                </div>
                <button className={`btn ${it.workId ? '' : 'primary'}`} disabled={busyId === it.externalId} onClick={() => open(it)}>
                  {it.workId ? '열기' : busyId === it.externalId ? '가져오는 중' : '가져오기'}
                </button>
              </div>
            </li>
          ))}
        </ul>
      )}
      <p className="note small-text">공연 정보 출처: (재)예술경영지원센터 공연예술통합전산망(www.kopis.or.kr)</p>
    </div>
  )
}
