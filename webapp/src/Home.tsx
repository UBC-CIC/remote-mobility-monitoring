import React from 'react'
import { useNavigate } from 'react-router-dom'

function App(): JSX.Element {
  const nav = useNavigate()
  return (<button onClick={(e) => { nav('/login') }}>Login</button>)
}

export default App
