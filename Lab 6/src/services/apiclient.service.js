import api from './apiClient.js'

const apiclient = {
  getAll: async () => {
    const { data } = await api.get('/blueprints')
    return data
  },

  getByAuthor: async (author) => {
    const { data } = await api.get(`/blueprints/${encodeURIComponent(author)}`)
    return data
  },

  getByAuthorAndName: async (author, name) => {
    const { data } = await api.get(
      `/blueprints/${encodeURIComponent(author)}/${encodeURIComponent(name)}`,
    )
    return data
  },

  create: async (blueprint) => {
    const { data } = await api.post('/blueprints', blueprint)
    return data
  },
}

export default apiclient
