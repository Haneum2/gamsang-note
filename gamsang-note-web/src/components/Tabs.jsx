// 글자 탭. items = [[값, 이름], ...]. 세 화면(작품 종류, 기록 상태, 후기 탭)이 같이 쓴다.
export default function Tabs({ items, value, onChange, label }) {
  return (
    <div className="tabs" role="group" aria-label={label}>
      {items.map(([v, l]) => (
        <button key={v} type="button" className="tab" aria-pressed={value === v} onClick={() => onChange(v)}>
          {l}
        </button>
      ))}
    </div>
  )
}
