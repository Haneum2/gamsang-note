import { useEffect, useState } from 'react'
import { api } from '../api.js'
import { TYPE_LABEL } from '../labels.js'
import Cover from '../components/Cover.jsx'
import Pager from '../components/Pager.jsx'
import Tabs from '../components/Tabs.jsx'
import WorkDetail from '../components/WorkDetail.jsx'

const TYPES = [['', '전체'], ...Object.entries(TYPE_LABEL)]

// 검색어가 있으면 외부 결과를 목록에 바로 보여 주는 종류: 영화 = TMDB, 책 = YES24, 공연 = KOPIS
const SOURCE = { MOVIE: 'tmdb', BOOK: 'yes24', PERFORMANCE: 'kopis' }
const SOURCE_TYPE = { tmdb: 'MOVIE', yes24: 'BOOK', kopis: 'PERFORMANCE' }
const SOURCE_NAME = { tmdb: 'TMDB', yes24: 'YES24', kopis: 'KOPIS' }
const CREDIT = {
  tmdb: '영화 정보 출처: TMDB (This product uses the TMDB API but is not endorsed or certified by TMDB.)',
  yes24: '도서 정보 출처: YES24 (작품 상세에서 YES24 상품 페이지로 이동할 수 있어요)',
  kopis: '공연 정보 출처: (재)예술경영지원센터 공연예술통합전산망(www.kopis.or.kr). 요즘(오늘 앞뒤 15일) 공연만 찾을 수 있어요.',
}

// 외부 검색 결과 한 건을 목록 카드 모양으로 바꾼다. id 는 이미 우리 DB에 있으면 작품 번호, 없으면 null.
// meta: TMDB [연도, 원제], YES24 [연도, 저자, 출판사], KOPIS [장르, 기간(2026-11-28 ~ ...), 공연장]
function fromExternal(source, it) {
  const kopis = source === 'kopis'
  const period = kopis ? it.meta.find((m) => /^\d{4}-/.test(m)) : undefined
  return {
    id: it.workId, source, externalId: it.externalId, type: SOURCE_TYPE[source],
    title: it.title, posterUrl: it.posterUrl,
    year: kopis ? period?.slice(0, 4) : it.meta[0],
    genres: kopis ? it.meta.filter((m) => m !== period) : it.meta.slice(1),
  }
}
const searchExternal = (source, q) => api.externalSearch(source, { q }).then((items) => items.map((it) => fromExternal(source, it)))
const keyOf = (w) => (w.source ? `${w.source}:${w.externalId}` : `db:${w.id}`)

export default function WorksPage() {
  const [type, setType] = useState('')
  const [input, setInput] = useState('') // 입력 중인 검색어
  const [q, setQ] = useState('') // 검색 버튼을 눌러 확정된 검색어
  const [page, setPage] = useState(0)
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [selectedId, setSelectedId] = useState(null)
  const [opening, setOpening] = useState(null) // 저장하며 여는 중인 카드의 keyOf

  // 검색 방식
  //  - 영화·책·공연 탭 + 검색어: 외부(TMDB·YES24·KOPIS) 결과만 바로 보여 준다(누르면 그때 저장하고 연다)
  //  - 전체 탭 + 검색어: 우리 DB 결과 + TMDB + YES24 + KOPIS 를 합친다(이미 DB에 있는 작품은 한 번만)
  //  - 그 밖: 우리 DB만(페이지 나눔)
  // ponytail: 외부 결과는 첫 20건씩만, 합친 목록은 DB 100건까지만 보여 준다. 더 필요하면 백엔드에서 페이지 정보를 넘기게 한다.
  const source = q ? SOURCE[type] : undefined
  const mixed = q !== '' && type === ''

  // type, q, page 가 바뀔 때마다 서버에서 다시 가져온다.
  useEffect(() => {
    let cancelled = false // 오래된 응답이 최신 화면을 덮어쓰지 않게 한다
    setLoading(true)
    setError('')
    const asList = (content) => ({ content, totalElements: content.length, totalPages: 0 })
    let load
    if (source) {
      load = searchExternal(source, q).then(asList)
    } else if (mixed) {
      // 외부 검색 하나가 실패해도(키 없음 등) 나머지 결과는 보여 준다.
      load = Promise.all([
        api.works({ q, page: 0, size: 100 }),
        searchExternal('tmdb', q).catch(() => []),
        searchExternal('yes24', q).catch(() => []),
        searchExternal('kopis', q).catch(() => []),
      ]).then(([db, movies, books, shows]) => {
        const inDb = new Set(db.content.map((w) => w.id))
        return asList([...db.content, ...[...movies, ...books, ...shows].filter((w) => !inDb.has(w.id))])
      })
    } else {
      load = api.works({ type, q, page })
    }
    load
      .then((res) => !cancelled && setData(res))
      .catch((e) => !cancelled && setError(e.message))
      .finally(() => !cancelled && setLoading(false))
    return () => {
      cancelled = true
    }
  }, [type, q, page, source, mixed])

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
  const busy = (w) => opening === keyOf(w)

  // 카드를 누르면: 우리 DB에 있으면 바로 열고, 외부 결과면 먼저 저장(POST)한 뒤 연다.
  const open = async (w) => {
    if (w.id) return setSelectedId(w.id)
    setOpening(keyOf(w))
    setError('')
    try {
      const saved = await api.importExternal(w.source, w.externalId)
      setData((d) => ({ ...d, content: d.content.map((x) => (keyOf(x) === keyOf(w) ? { ...x, id: saved.id } : x)) }))
      setSelectedId(saved.id)
    } catch (e) {
      setError(`'${w.title}'을(를) 열지 못했어요. ${e.message}`)
    } finally {
      setOpening(null)
    }
  }

  const credits = mixed ? ['tmdb', 'yes24', 'kopis'] : SOURCE[type] ? [SOURCE[type]] : []

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
        <p className="count">
          {source
            ? `'${q}' ${SOURCE_NAME[source]} 검색 결과 ${data.totalElements}건`
            : q
              ? `'${q}' 검색 결과 ${data.totalElements}건`
              : `${data.totalElements}편`}
        </p>
      )}

      <ul className={`wall ${loading ? 'loading' : ''}`}>
        {works.map((w) => (
          <li key={keyOf(w)}>
            <button className={`shelf-item ${w.type.toLowerCase()}`} onClick={() => open(w)} disabled={busy(w)}>
              <Cover type={w.type} title={w.title} src={w.posterUrl} />
              <span className="shelf-title">{w.title}</span>
              <span className="shelf-meta">
                <span className="type-name">{TYPE_LABEL[w.type]}</span>
                <span>{busy(w) ? '여는 중' : (w.releaseDate?.slice(0, 4) ?? w.year ?? '연도 미상')}</span>
              </span>
              {w.genres.length > 0 && <span className="shelf-meta">{w.genres.join(', ')}</span>}
            </button>
          </li>
        ))}
      </ul>

      {data && works.length === 0 && !loading && (
        <p className="empty">
          {source
            ? source === 'kopis'
              ? `KOPIS에서 '${q}' 공연을 찾지 못했어요. 요즘(오늘 앞뒤 15일) 공연 중인 작품만 찾을 수 있어요.`
              : `${SOURCE_NAME[source]}에도 '${q}'(이)가 없어요. 다른 제목으로 찾아보세요.`
            : q
              ? `'${q}'(이)가 들어간 제목이 없어요. 다른 낱말로 찾아보세요.`
              : '이 종류의 작품이 아직 없어요.'}
        </p>
      )}
      {credits.map((c) => (
        <p key={c} className="note small-text">
          {CREDIT[c]}
        </p>
      ))}

      <Pager page={page} totalPages={data?.totalPages ?? 0} onChange={setPage} />

      {selectedId && <WorkDetail workId={selectedId} onClose={() => setSelectedId(null)} />}
    </section>
  )
}
