import { useEffect, useState } from 'react'
import { api } from '../api.js'
import { useUser } from '../UserContext.jsx'
import { TYPE_LABEL } from '../labels.js'
import Cover from '../components/Cover.jsx'
import Stars from '../components/Stars.jsx'
import Tabs from '../components/Tabs.jsx'

// 연말 결산·월별 감상 수는 '봤어요' 기록의 감상일 기준(연도별), 장르 통계는 전체 기간.
const THIS_YEAR = new Date().getFullYear()
const YEARS = [THIS_YEAR, THIS_YEAR - 1, THIS_YEAR - 2].map((y) => [String(y), `${y}년`])

export default function StatsPage() {
  const { userId } = useUser()
  const [year, setYear] = useState(String(THIS_YEAR))
  const [yearly, setYearly] = useState(null) // { monthly, yearEnd }
  const [genres, setGenres] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    if (!userId) return
    let cancelled = false
    setYearly(null)
    Promise.all([api.statsMonthly(userId, year), api.statsYearEnd(userId, year)])
      .then(([monthly, yearEnd]) => !cancelled && setYearly({ monthly, yearEnd }))
      .catch((e) => !cancelled && setError(e.message))
    return () => {
      cancelled = true
    }
  }, [userId, year])

  useEffect(() => {
    if (!userId) return
    api.statsGenres(userId).then(setGenres).catch((e) => setError(e.message))
  }, [userId])

  return (
    <section>
      <h1 className="page-title">통계</h1>
      <Tabs items={YEARS} value={year} onChange={setYear} label="연도" />
      {error && <p className="notice error">통계를 불러오지 못했어요. {error}</p>}

      {yearly && (
        <>
          <YearEnd data={yearly.yearEnd} />
          <MonthlyChart year={year} months={yearly.monthly} />
        </>
      )}
      {genres && <GenreTable genres={genres} />}
    </section>
  )
}

function YearEnd({ data }) {
  if (data.totalDone === 0) {
    return (
      <div className="section">
        <h2 className="section-title">{data.year}년 결산</h2>
        <p className="empty small">
          {data.year}년에 &apos;봤어요&apos;로 기록한 작품이 없어요. &apos;내 기록&apos;에서 상태와 감상일을 정해 보세요.
        </p>
      </div>
    )
  }
  // 세 종류를 항상 같은 순서로 보여 준다(없는 종류는 0편).
  const counts = Object.keys(TYPE_LABEL).map((type) => ({
    type,
    count: data.byType.find((t) => t.type === type)?.count ?? 0,
  }))
  const max = Math.max(...counts.map((c) => c.count))

  return (
    <div className="section">
      <h2 className="section-title">{data.year}년 결산</h2>
      <div className="box year-end">
        <div>
          <p className="hero">
            <strong>{data.totalDone}</strong>편
          </p>
          <p className="muted small-text">&apos;봤어요&apos;로 기록한 작품</p>
          <ul className="type-bars">
            {counts.map((c) => (
              <li key={c.type} className={c.type.toLowerCase()}>
                <span>{TYPE_LABEL[c.type]}</span>
                <span className="type-bar">
                  <i style={{ width: max ? `${(c.count / max) * 100}%` : 0 }} />
                </span>
                <span className="num">{c.count}편</span>
              </li>
            ))}
          </ul>
        </div>
        <div>
          <h3 className="box-title">많이 본 장르</h3>
          <ol className="rank">
            {data.topGenres.map((g) => (
              <li key={g.genre}>
                <span>{g.genre}</span>
                <span className="num">{g.count}편</span>
              </li>
            ))}
          </ol>
        </div>
        <div className="year-end-top">
          <h3 className="box-title">별점 높은 작품</h3>
          <ol className="mini-wall">
            {data.topRated.map((w) => (
              <li key={w.workId}>
                <Cover type={w.type} title={w.title} />
                <span className="shelf-title">{w.title}</span>
                <Stars value={w.rating} />
              </li>
            ))}
          </ol>
        </div>
      </div>
    </div>
  )
}

// 월별 감상 수: 막대 하나 = 한 달. 0이 아닌 달만 숫자를 붙인다.
function MonthlyChart({ year, months }) {
  const max = Math.max(...months.map((m) => m.count))
  return (
    <div className="section">
      <h2 className="section-title">{year}년 월별 감상</h2>
      {max === 0 ? (
        <p className="empty small">이 해에 감상일이 있는 &apos;봤어요&apos; 기록이 없어요.</p>
      ) : (
        <ol className="box month-chart">
          {months.map((m) => (
            <li key={m.month} title={`${m.month}월 ${m.count}편`} aria-label={`${m.month}월 ${m.count}편`}>
              <span className="month-track" aria-hidden="true">
                {m.count > 0 && <span className="month-value">{m.count}</span>}
                <span className="month-bar" style={{ height: `calc((100% - 22px) * ${m.count / max})` }} />
              </span>
              <span className="month-label" aria-hidden="true">
                {m.month}
              </span>
            </li>
          ))}
        </ol>
      )}
    </div>
  )
}

function GenreTable({ genres }) {
  const max = Math.max(0, ...genres.map((g) => g.percent))
  return (
    <div className="section">
      <h2 className="section-title">장르별 감상 (전체 기간)</h2>
      {genres.length === 0 ? (
        <p className="empty small">&apos;봤어요&apos; 기록이 생기면 장르별로 모아 보여 드려요.</p>
      ) : (
        <div className="box table-wrap">
          <table className="table">
            <thead>
              <tr>
                <th scope="col">장르</th>
                <th scope="col" className="num">
                  작품
                </th>
                <th scope="col">비율</th>
                <th scope="col">평균 별점</th>
              </tr>
            </thead>
            <tbody>
              {genres.map((g) => (
                <tr key={g.genre}>
                  <th scope="row">{g.genre}</th>
                  <td className="num">{g.count}편</td>
                  <td>
                    <span className="share">
                      <span className="share-bar" aria-hidden="true">
                        <i style={{ width: `${(g.percent / max) * 100}%` }} />
                      </span>
                      {g.percent.toFixed(1)}%
                    </span>
                  </td>
                  <td>{g.avgRating != null ? <Stars value={g.avgRating} /> : <span className="muted">없음</span>}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
      <p className="muted small-text note">
        한 작품에 장르가 여러 개면 장르마다 1편으로 세요. 그래서 장르별 작품 수를 더하면 실제 작품 수보다 클 수 있어요.
      </p>
    </div>
  )
}
