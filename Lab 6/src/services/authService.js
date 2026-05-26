import api from './apiClient.js'

const useMock = import.meta.env.VITE_USE_MOCK === 'true'

const authService = {
  login: async (username, password) => {
    if (useMock) {
      // Simula una respuesta exitosa con un token falso
      return new Promise((resolve, reject) => {
        setTimeout(() => {
          if (username && password) {
            resolve({ data: { token: 'mock-jwt-token-' + Date.now() } })
          } else {
            reject({ response: { status: 401 } })
          }
        }, 500)
      })
    } else {
      // Llama al API real
      return api.post('/auth/login', { username, password })
    }
  },
}

export default authService
