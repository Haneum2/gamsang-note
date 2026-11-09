// 표지: 2:3 단색 사각형 + 제목. 색은 작품 종류, 농도는 제목 해시로 3단계 변주한다.
// 제목 글자 크기는 표지 너비 기준(cqi)이라 표지 크기가 달라도 비율이 유지된다.
function shade(title) {
  let h = 0
  for (const ch of title) h = (h * 31 + ch.codePointAt(0)) >>> 0
  return h % 3
}

// 전체 길이와 가장 긴 낱말 길이를 함께 본다. 낱말 중간에서 줄이 바뀌지 않게 하려는 것.
function sizeClass(title) {
  const n = [...title].length
  const w = Math.max(...title.split(/\s+/).map((x) => [...x].length))
  if (n <= 3) return 'xl'
  if (n <= 6 && w <= 4) return 'lg'
  if (n <= 12 && w <= 6) return 'md'
  return 'sm'
}

export default function Cover({ type, title }) {
  return (
    <div className={`cover ${type.toLowerCase()} shade-${shade(title)} t-${sizeClass(title)}`} aria-hidden="true">
      <span>{title}</span>
    </div>
  )
}
