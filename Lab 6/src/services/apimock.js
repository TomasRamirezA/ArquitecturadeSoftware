const blueprints = [
  {
    author: 'john',
    name: 'house',
    points: [
      { x: 10, y: 10 },
      { x: 10, y: 100 },
      { x: 100, y: 100 },
      { x: 100, y: 10 },
      { x: 10, y: 10 },
    ],
  },
  {
    author: 'john',
    name: 'gear',
    points: [
      { x: 200, y: 200 },
      { x: 250, y: 250 },
      { x: 300, y: 200 },
    ],
  },
  {
    author: 'mary',
    name: 'flower',
    points: [
      { x: 150, y: 150 },
      { x: 170, y: 130 },
      { x: 190, y: 150 },
      { x: 170, y: 170 },
      { x: 150, y: 150 },
    ],
  },
]

const apimock = {
  getAll: async () => {
    return [...blueprints]
  },

  getByAuthor: async (author) => {
    return blueprints.filter((bp) => bp.author === author)
  },

  getByAuthorAndName: async (author, name) => {
    return blueprints.find((bp) => bp.author === author && bp.name === name)
  },

  create: async (blueprint) => {
    blueprints.push(blueprint)
    return blueprint
  },
}


export default apimock
