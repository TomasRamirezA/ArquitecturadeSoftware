import { Client } from '@stomp/stompjs'

export function createStompClient(baseUrl) {
  const brokerURL = baseUrl.replace(/^http/, 'ws').replace(/\/$/, '') + '/ws-blueprints'
  
  return new Client({
    brokerURL,
    reconnectDelay: 5000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,
    onStompError: (frame) => {
      console.error('STOMP error: ' + frame.headers['message']);
    },
  })
}

export function subscribeBlueprint(client, author, name, onMsg) {
  const topic = `/topic/blueprints.${author}.${name}`
  return client.subscribe(topic, (m) => {
    onMsg(JSON.parse(m.body))
  })
}
