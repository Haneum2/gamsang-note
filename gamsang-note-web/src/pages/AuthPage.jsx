import { useState } from 'react'
import { api } from '../api.js'
import { useUser } from '../UserContext.jsx'
import Tabs from '../components/Tabs.jsx'

const MODES = [
  ['login', '로그인'],
  ['signup', '가입'],
]

// 로그인하지 않았을 때 보이는 화면. 로그인·가입에 성공하면 서버가 세션 쿠키를 주고, me 를 채워 앱으로 들어간다.
export default function AuthPage() {
  const { setMe } = useUser()
  const [mode, setMode] = useState('login')
  const [email, setEmail] = useState('')
  const [nickname, setNickname] = useState('')
  const [password, setPassword] = useState('')
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')

  const submit = async (e) => {
    e.preventDefault()
    setBusy(true)
    setError('')
    try {
      setMe(mode === 'login' ? await api.login({ email, password }) : await api.signup({ email, nickname, password }))
    } catch (err) {
      setError(`${mode === 'login' ? '로그인하지' : '가입하지'} 못했어요. ${err.message}`)
    } finally {
      setBusy(false)
    }
  }

  return (
    <section className="auth">
      <h1 className="page-title">감상노트</h1>
      <Tabs
        items={MODES}
        value={mode}
        onChange={(m) => {
          setMode(m)
          setError('')
        }}
        label="로그인 또는 가입"
      />
      <form className="form box" onSubmit={submit}>
        <label className="field">
          이메일
          <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} autoComplete="email" required />
        </label>
        {mode === 'signup' && (
          <label className="field">
            닉네임
            <input value={nickname} onChange={(e) => setNickname(e.target.value)} maxLength={30} required />
          </label>
        )}
        <label className="field">
          비밀번호{mode === 'signup' && ' (8자 이상)'}
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            autoComplete={mode === 'login' ? 'current-password' : 'new-password'}
            minLength={mode === 'signup' ? 8 : undefined}
            maxLength={64}
            required
          />
        </label>
        <button className="btn primary" disabled={busy}>
          {busy ? '확인하는 중' : mode === 'login' ? '로그인' : '가입하고 시작하기'}
        </button>
        {error && (
          <p className="notice error" role="alert">
            {error}
          </p>
        )}
      </form>
      <p className="note small-text">
        시연용 샘플 계정: user01@example.com ~ user12@example.com, 비밀번호 gamsang1234
      </p>
    </section>
  )
}
