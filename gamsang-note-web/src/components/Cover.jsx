import { useState } from 'react'

// 표지: 포스터 주소(src)가 있으면 이미지, 없거나 불러오지 못하면 2:3 단색 사각형 + 제목.
// 글자 표지: 색은 작품 종류, 농도는 제목 해시로 3단계 변주한다.
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

export default function Cover({ type, title, src }) {
  const [broken, setBroken] = useState(false)
  if (src && !broken) {
    return (
      <div className={`cover poster ${type.toLowerCase()}`} aria-hidden="true">
        {/* no-referrer: 외부 서버가 다른 사이트에서 온 요청을 막는 경우를 피한다. lazy: 화면에 보일 때 불러온다 */}
        <img src={src} alt="" loading="lazy" referrerPolicy="no-referrer" onError={() => setBroken(true)} />
      </div>
    )
  }
  return (
    <div className={`cover ${type.toLowerCase()} shade-${shade(title)} t-${sizeClass(title)}`} aria-hidden="true">
      <span>{title}</span>
    </div>
  )
}
