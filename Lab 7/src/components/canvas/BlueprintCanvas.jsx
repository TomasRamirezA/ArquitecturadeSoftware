import React from 'react'

export default function BlueprintCanvas({ canvasRef, name, createNew, saveBlueprint, deleteBlueprint, onCanvasClick }) {
  return (
    <div className="canvas-wrapper">
      <div className="card" style={{ padding: '16px 24px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <span style={{ color: 'var(--text-muted)', fontSize: '0.7rem', textTransform: 'uppercase', fontWeight: 700, letterSpacing: '0.05em' }}>
            Lienzo Actual
          </span>
          <h2 style={{ margin: 0, fontSize: '1.5rem', fontWeight: 700 }}>{name || '—'}</h2>
        </div>
        <div className="input-group" style={{ marginBottom: 0 }}>
          <button className="btn-secondary" onClick={createNew}>NUEVO</button>
          <button className="btn-primary" onClick={saveBlueprint} disabled={!name}>GUARDAR</button>
          <button className="btn-danger" onClick={deleteBlueprint} disabled={!name}>BORRAR</button>
        </div>
      </div>
      
      <div className="canvas-container">
        <canvas
          ref={canvasRef}
          width={900}
          height={600}
          onClick={onCanvasClick}
        />
      </div>
      <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem', textAlign: 'center' }}>
        ✨ Haz clic en el lienzo para dibujar. Los cambios se sincronizan en tiempo real.
      </p>
    </div>
  )
}
