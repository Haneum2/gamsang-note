// 별점: 숫자 + 5칸 막대. 칸 하나가 1점이고 0.5점은 반 칸을 채운다.
export default function Stars({ value }) {
  if (value == null) return null
  const v = Number(value)
  return (
    <span className="rating" role="img" aria-label={`별점 ${v.toFixed(1)}점, 5점 만점`}>
      <span className="rating-num">{v.toFixed(1)}</span>
      <span className="rating-bar">
        {[0, 1, 2, 3, 4].map((i) => (
          <i key={i} style={{ '--fill': `${Math.min(Math.max(v - i, 0), 1) * 100}%` }} />
        ))}
      </span>
    </span>
  )
}
