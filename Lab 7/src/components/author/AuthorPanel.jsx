import React from 'react'

export default function AuthorPanel({ author, setAuthor, getBlueprints, blueprints, loading, setName, getPoints, totalAuthorPoints }) {
  return (
    <div className="card">
      <h3 style={{ marginBottom: 16 }}>Panel del Autor</h3>
      <div className="input-group" style={{ marginBottom: '16px' }}>
        <input 
          value={author} 
          onChange={e => setAuthor(e.target.value)} 
          placeholder="Nombre del autor..."
          style={{ width: '100%' }}
        />
        <button className="btn-primary" onClick={getBlueprints} disabled={loading} style={{ width: '100%', marginTop: '8px' }}>
          {loading ? '...' : 'LISTAR PLANOS'}
        </button>
      </div>

      <div className="table-container">
        <table>
          <thead>
            <tr>
              <th>Nombre</th>
              <th>Puntos</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {blueprints.length === 0 && (
              <tr><td colSpan="3" style={{ textAlign: 'center', color: 'var(--text-muted)', padding: '20px' }}>No hay planos</td></tr>
            )}
            {blueprints.map(bp => (
              <tr key={bp.name}>
                <td>{bp.name}</td>
                <td>{bp.points?.length || 0}</td>
                <td style={{ textAlign: 'right' }}>
                  <button 
                    className="btn-secondary" 
                    style={{ padding: '6px 12px', fontSize: '11px' }}
                    onClick={() => { setName(bp.name); getPoints(author, bp.name); }}
                  >
                    ABRIR
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className="stats-bar">
        <div className="stat-item">
          <span className="label">Planos</span>
          <span className="value">{blueprints.length}</span>
        </div>
        <div className="stat-item" style={{ textAlign: 'right' }}>
          <span className="label">Puntos Totales</span>
          <span className="value">{totalAuthorPoints}</span>
        </div>
      </div>
    </div>
  )
}
