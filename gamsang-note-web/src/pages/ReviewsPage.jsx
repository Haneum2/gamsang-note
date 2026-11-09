import { useEffect, useState } from 'react'
import { api } from '../api.js'
import { useUser } from '../UserContext.jsx'
import Pager from '../components/Pager.jsx'
import ReviewCard from '../components/ReviewCard.jsx'
import Tabs from '../components/Tabs.jsx'

// 두 탭은 같은 모양의 목록을 돌려준다. 공개 범위 판단은 DB 뷰(v_visible_review, v_friend_feed)가 한다.
const TABS = {
  all: { label: '볼 수 있는 후기', load: api.reviews, empty: "볼 수 있는 후기가 아직 없어요. '내 기록'에서 첫 후기를 써 보세요." },
  feed: { label: '친구 피드', load: api.feed, empty: '친구들이 쓴 후기가 아직 없어요.' },
}

export default function ReviewsPage() {
  const { userId } = useUser()
  const [tab, setTab] = useState('all')
  const [page, setPage] = useState(0)
  const [data, setData] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    if (!userId) return
    let cancelled = false
    setError('')
    TABS[tab]
      .load({ viewerId: userId, page })
      .then((res) => !cancelled && setData(res))
      .catch((e) => !cancelled && setError(e.message))
    return () => {
      cancelled = true
    }
  }, [userId, tab, page])

  const reviews = data?.content ?? []

  return (
    <section>
      <h1 className="page-title">후기</h1>
      <Tabs
        items={Object.entries(TABS).map(([k, t]) => [k, t.label])}
        value={tab}
        label="후기 범위"
        onChange={(k) => {
          setTab(k)
          setPage(0)
          setData(null)
        }}
      />

      {error && <p className="notice error">후기를 불러오지 못했어요. {error}</p>}
      {data && <p className="count">{data.totalElements}건</p>}

      {reviews.length > 0 && (
        <div className="entries">
          {reviews.map((r) => (
            <ReviewCard key={r.reviewId} review={r} />
          ))}
        </div>
      )}
      {data && reviews.length === 0 && <p className="empty">{TABS[tab].empty}</p>}

      <Pager page={page} totalPages={data?.totalPages ?? 0} onChange={setPage} />
    </section>
  )
}
