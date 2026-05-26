import React from 'react'

export default function Toast({ toasts }) {
  if (toasts.length === 0) return null

  return (
    <div className="toast-area">
      {toasts.map(t => (
        <div key={t.id} className="toast">{t.msg}</div>
      ))}
    </div>
  )
}
