import apimock from './apimock.js'
import apiclient from './apiclient.service.js'

const useMock = import.meta.env.VITE_USE_MOCK === 'true'

const blueprintsService = useMock ? apimock : apiclient

export default blueprintsService
