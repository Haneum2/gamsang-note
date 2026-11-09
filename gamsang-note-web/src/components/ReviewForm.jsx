import { useState } from 'react'
import { api } from '../api.js'
import { useUser } from '../UserContext.jsx'
import { VISIBILITY_LABEL } from '../labels.js'

export default function ReviewForm({ recordId, onSaved }) {
  const { userId } = useUser()
  const [content, setContent] = useState('')
  const [visibility, setVisibility] = useState('ALL')
  const [spoiler, setSpoiler] = useState(false)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')

  const save = async (e) => {
    e.preventDefault()
    setSaving(true)
    setError('')
    try {
      await api.createReview(recordId, { userId, content, spoiler, visibility })
      onSaved()
    } catch (err) {
      setError(`후기를 저장하지 못했어요. ${err.message}`)
    } finally {
      setSaving(false)
    }
  }

  return (
    <form className="form review-form" onSubmit={save}>
      <label className="field">
        후기
        <textarea
          value={content}
          onChange={(e) => setContent(e.target.value)}
          rows={4}
          placeholder="어땠는지 적어 주세요. 저장하면 상태가 '봤어요'로 바뀌어요."
          required
        />
      </label>
      <div className="form-row">
        <label className="field">
          공개 범위
          <select value={visibility} onChange={(e) => setVisibility(e.target.value)}>
            {Object.entries(VISIBILITY_LABEL).map(([v, l]) => (
              <option key={v} value={v}>
                {l}
              </option>
            ))}
          </select>
        </label>
        <label className="check">
          <input type="checkbox" checked={spoiler} onChange={(e) => setSpoiler(e.target.checked)} />
          스포일러가 있어요
        </label>
        <button className="btn primary" disabled={saving || !content.trim()}>
          {saving ? '저장하는 중' : '후기 저장'}
        </button>
      </div>
      {error && <p className="notice error">{error}</p>}
    </form>
  )
}
