/** 
 * API Service for Blueprint CRUD operations
 */
export async function fetchBlueprints(apiUrl, author) {
  const r = await fetch(`${apiUrl}/api/blueprints?author=${author}`)
  if (!r.ok) throw new Error('Error fetching blueprints')
  return await r.json()
}

export async function fetchPoints(apiUrl, author, name) {
  const r = await fetch(`${apiUrl}/api/blueprints/${author}/${name}`)
  if (!r.ok) throw new Error('Error fetching points')
  return await r.json()
}

export async function saveBlueprintApi(apiUrl, payload) {
  let response = await fetch(`${apiUrl}/api/blueprints`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  })

  if (!response.ok) {
    const { author, name } = payload
    response = await fetch(`${apiUrl}/api/blueprints/${author}/${name}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    })
  }
  return response
}

export async function deleteBlueprintApi(apiUrl, author, name) {
  return await fetch(`${apiUrl}/api/blueprints/${author}/${name}`, { method: 'DELETE' })
}
