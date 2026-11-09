// 이전/다음 페이지 버튼. page 는 0부터 센다(Spring 의 Pageable 과 같음).
export default function Pager({ page, totalPages, onChange }) {
  if (totalPages <= 1) return null
  return (
    <nav className="pager" aria-label="페이지">
      <button className="btn" disabled={page === 0} onClick={() => onChange(page - 1)}>
        이전
      </button>
      <span>
        {page + 1} / {totalPages}쪽
      </span>
      <button className="btn" disabled={page + 1 >= totalPages} onClick={() => onChange(page + 1)}>
        다음
      </button>
    </nav>
  )
}
