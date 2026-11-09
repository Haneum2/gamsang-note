import { useEffect, useState } from 'react'
import { api } from '../api.js'
import { useUser } from '../UserContext.jsx'
import { TYPE_LABEL, VISIBILITY_LABEL } from '../labels.js'
import Stars from './Stars.jsx'

// 후기 한 건 + 좋아요 + 댓글. review 는 /api/reviews 또는 /api/feed 의 항목(모양이 같다).
// 방명록처럼 왼쪽에 작성자·날짜, 오른쪽에 작품과 본문.
export default function ReviewCard({ review }) {
  const { userId } = useUser()
  const [social, setSocial] = useState(null) // { likeCount, commentCount, likedByMe }
  const [showSpoiler, setShowSpoiler] = useState(false)
  const [comments, setComments] = useState(null) // null = 댓글 창 닫힘
  const [text, setText] = useState('')
  const [error, setError] = useState('')

  // ponytail: 카드마다 social 을 따로 불러 N+1 호출 — 목록 API 에 수를 함께 내리면 제거 (CLAUDE.md 10-8)
  useEffect(() => {
    api.social(review.reviewId, userId).then(setSocial).catch((e) => setError(e.message))
  }, [review.reviewId, userId])

  const run = async (fn, what) => {
    setError('')
    try {
      await fn()
    } catch (e) {
      setError(`${what} ${e.message}`)
    }
  }

  const toggleLike = () =>
    run(async () => {
      const res = social.likedByMe
        ? await api.unlike(review.reviewId, userId)
        : await api.like(review.reviewId, userId)
      setSocial({ ...social, likeCount: res.likeCount, likedByMe: res.liked })
    }, '좋아요를 반영하지 못했어요.')

  const toggleComments = () =>
    run(async () => setComments(comments ? null : await api.comments(review.reviewId, userId)), '댓글을 불러오지 못했어요.')

  const addComment = (e) => {
    e.preventDefault()
    run(async () => {
      const c = await api.addComment(review.reviewId, userId, text)
      setComments([...comments, c])
      setSocial({ ...social, commentCount: social.commentCount + 1 })
      setText('')
    }, '댓글을 달지 못했어요.')
  }

  const hidden = review.spoiler && !showSpoiler

  return (
    <article className={`entry ${review.workType.toLowerCase()}`}>
      <div className="entry-author">
        <strong>{review.nickname}</strong>
        <time dateTime={review.createdAt}>{review.createdAt?.slice(0, 10)}</time>
        <span>{VISIBILITY_LABEL[review.visibility]}</span>
      </div>

      <div className="entry-body">
        <div className="entry-work">
          <h3>{review.title}</h3>
          <span className="type-name">{TYPE_LABEL[review.workType]}</span>
          <Stars value={review.rating} />
        </div>

        {hidden ? (
          <button className="spoiler" onClick={() => setShowSpoiler(true)} aria-label="스포일러가 있는 후기 보기">
            <span className="spoiler-text" aria-hidden="true">
              {review.content}
            </span>
            <span className="spoiler-label" aria-hidden="true">
              스포일러가 있어요. 눌러서 보기
            </span>
          </button>
        ) : (
          <p className="entry-text">{review.content}</p>
        )}

        <div className="entry-actions">
          <button className="btn text" aria-pressed={!!social?.likedByMe} disabled={!social} onClick={toggleLike}>
            좋아요 {social?.likeCount ?? ''}
          </button>
          <button className="btn text" aria-expanded={!!comments} disabled={!social} onClick={toggleComments}>
            {comments ? '댓글 닫기' : `댓글 ${social?.commentCount ?? ''}`}
          </button>
        </div>

        {error && <p className="notice error">{error}</p>}

        {comments && (
          <div className="comments">
            {comments.length === 0 && <p className="muted">첫 댓글을 남겨 보세요.</p>}
            {comments.map((c) => (
              <p key={c.id}>
                <strong>{c.nickname}</strong> {c.content}
              </p>
            ))}
            <form className="comment-form" onSubmit={addComment}>
              <input value={text} onChange={(e) => setText(e.target.value)} placeholder="댓글을 적어 주세요" aria-label="댓글" />
              <button className="btn" disabled={!text.trim()}>
                댓글 달기
              </button>
            </form>
          </div>
        )}
      </div>
    </article>
  )
}
