import { useEffect, useRef, useState } from 'react'

// Layout & UI Components
import ControlBar from './components/layout/ControlBar'
import AuthorPanel from './components/author/AuthorPanel'
import BlueprintCanvas from './components/canvas/BlueprintCanvas'
import Toast from './components/common/Toast'

// Services
import { fetchBlueprints, fetchPoints, saveBlueprintApi, deleteBlueprintApi } from './services/apiService'
import { createSocket } from './services/socketService'
import { createStompClient, subscribeBlueprint } from './services/stompService'

// Styles
import './styles/App.css'

const API_BASE = import.meta.env.VITE_API_BASE ?? window.location.origin
const IO_BASE  = import.meta.env.VITE_IO_BASE  ?? window.location.origin

export default function App() {
  const [tech, setTech] = useState('socketio')
  const [author, setAuthor] = useState('anonymous')
  const [name, setName] = useState('')
  const [apiUrl, setApiUrl] = useState(API_BASE)
  const [blueprints, setBlueprints] = useState([])
  const [points, setPoints] = useState([])
  const [loading, setLoading] = useState(false)
  const [status, setStatus] = useState('disconnected') 
  const [toasts, setToasts] = useState([])
  
  const canvasRef = useRef(null)
  const stompRef = useRef(null)
  const unsubRef = useRef(null)
  const socketRef = useRef(null)

  const showToast = (msg, duration = 3000) => {
    const id = Date.now()
    setToasts(prev => [...prev, { id, msg }])
    setTimeout(() => setToasts(prev => prev.filter(t => t.id !== id)), duration)
  }

  // -- CRUD Actions --

  async function getBlueprints() {
    if (!author) return showToast('Introduce un autor')
    setLoading(true)
    try {
      const data = await fetchBlueprints(apiUrl, author)
      setBlueprints(data)
      showToast(`Cargados ${data.length} planos`)
    } catch (e) {
      showToast('Error al listar planos')
    } finally {
      setLoading(false)
    }
  }

  async function getPointsData(authorName, bpName) {
    try {
      const data = await fetchPoints(apiUrl, authorName, bpName)
      setPoints(data.points || [])
    } catch (e) {
      console.error(e)
    }
  }

  async function saveBlueprint() {
    if (!name) return
    const payload = { author, name, points }
    try {
      const response = await saveBlueprintApi(apiUrl, payload)
      if (response.ok) {
        await getBlueprints()
        showToast('Guardado con éxito')
      } else {
        showToast('Error al guardar')
      }
    } catch (e) {
      showToast('Error de red al guardar')
    }
  }

  async function deleteBlueprint() {
    if (!name || !confirm('¿Eliminar plano?')) return
    try {
      await deleteBlueprintApi(apiUrl, author, name)
      setPoints([])
      setName('')
      getBlueprints()
      showToast('Borrado')
    } catch (e) {
      showToast('Error al eliminar')
    }
  }

  function createNew() {
    const newName = prompt('Nombre del nuevo plano:')
    if (newName) {
      setName(newName)
      setPoints([])
    }
  }

  // -- Canvas Logic --

  useEffect(() => {
    const ctx = canvasRef.current?.getContext('2d')
    if (!ctx) return
    ctx.clearRect(0, 0, 900, 600)
    ctx.strokeStyle = '#2563eb'
    ctx.lineWidth = 3
    ctx.lineJoin = 'round'
    ctx.lineCap = 'round'
    
    ctx.beginPath()
    points.forEach((p, i) => {
      if (i === 0) ctx.moveTo(p.x, p.y)
      else ctx.lineTo(p.x, p.y)
    })
    ctx.stroke()
  }, [points])

  // -- Real-Time sync --

  useEffect(() => {
    if (!author || !name || tech === 'none') {
      setStatus('disconnected')
      return
    }

    setStatus('connecting')
    unsubRef.current?.(); unsubRef.current = null
    stompRef.current?.deactivate?.(); stompRef.current = null
    socketRef.current?.disconnect?.(); socketRef.current = null

    if (tech === 'stomp') {
      const client = createStompClient(apiUrl)
      stompRef.current = client
      client.onConnect = () => {
        setStatus('connected')
        unsubRef.current = subscribeBlueprint(client, author, name, (upd) => {
          if (upd.points) setPoints(upd.points)
          else if (upd.point) setPoints(prev => [...prev, upd.point])
        })
      }
      client.onDisconnect = () => setStatus('disconnected')
      client.activate()
    } else {
      const s = createSocket(IO_BASE)
      socketRef.current = s
      const room = `blueprints.${author}.${name}`
      s.on('connect', () => {
        setStatus('connected')
        s.emit('join-room', room)
      })
      s.on('disconnect', () => setStatus('disconnected'))
      s.on('blueprint-update', (upd) => {
        if (upd.points) setPoints(upd.points)
        else if (upd.point) setPoints(prev => [...prev, upd.point])
      })
    }

    return () => {
      unsubRef.current?.()
      stompRef.current?.deactivate?.()
      socketRef.current?.disconnect?.()
    }
  }, [tech, author, name, apiUrl])

  function onCanvasClick(e) {
    if (!name) return
    const rect = canvasRef.current.getBoundingClientRect()
    const x = Math.round((e.clientX - rect.left) * (canvasRef.current.width / rect.width))
    const y = Math.round((e.clientY - rect.top) * (canvasRef.current.height / rect.height))
    const point = { x, y }

    setPoints(prev => [...prev, point])

    if (tech === 'stomp' && stompRef.current?.connected) {
      stompRef.current.publish({ 
        destination: '/app/draw', 
        body: JSON.stringify({ author, name, point }) 
      })
    } else if (tech === 'socketio' && socketRef.current?.connected) {
      const room = `blueprints.${author}.${name}`
      socketRef.current.emit('draw-event', { room, author, name, point })
    }
  }

  const totalAuthorPoints = blueprints.reduce((sum, bp) => sum + (bp.points?.length || 0), 0)

  return (
    <div className="app-container">
      <ControlBar 
        tech={tech} 
        setTech={setTech} 
        status={status} 
      />

      <div className="layout">
        <AuthorPanel 
          author={author}
          setAuthor={setAuthor}
          getBlueprints={getBlueprints}
          blueprints={blueprints}
          loading={loading}
          setName={setName}
          getPoints={getPointsData}
          totalAuthorPoints={totalAuthorPoints}
        />

        <BlueprintCanvas 
          canvasRef={canvasRef}
          name={name}
          createNew={createNew}
          saveBlueprint={saveBlueprint}
          deleteBlueprint={deleteBlueprint}
          onCanvasClick={onCanvasClick}
        />
      </div>

      <Toast toasts={toasts} />
    </div>
  )
}
