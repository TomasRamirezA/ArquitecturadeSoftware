import React from 'react'

export default function ControlBar({ tech, setTech, status }) {
  return (
    <header className="header">
      <div>
        <h1 style={{ color: 'var(--primary)' }}>BluePrints RT</h1>
        <p style={{ color: 'var(--text-muted)', fontSize: '0.8rem' }}>Colaboración en tiempo real</p>
      </div>
      
      <div className="input-group" style={{ marginBottom: 0, alignItems: 'center', gap: '15px' }}>
        <div className="status-indicator">
          <span className={`dot dot-${status === 'connected' ? 'online' : status === 'connecting' ? 'waiting' : 'offline'}`}></span>
          <span style={{ textTransform: 'uppercase' }}>{status}</span>
        </div>
        
        <select value={tech} onChange={e => setTech(e.target.value)} style={{ padding: '6px 10px', fontSize: '13px' }}>
          <option value="socketio">Socket.IO (Node)</option>
          <option value="stomp">STOMP (Spring)</option>
          <option value="none">Local (None)</option>
        </select>
        <span className={`badge ${tech === 'stomp' ? 'badge-blue' : tech === 'socketio' ? 'badge-pink' : 'badge-secondary'}`}>
          {tech === 'stomp' ? 'Spring' : tech === 'socketio' ? 'Node' : 'Offline'}
        </span>
      </div>
    </header>
  )
}
