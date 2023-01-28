import React from 'react'
import './App.css'
import LoginPage from './components/LoginPage/LoginPage'
import Home from './Home'
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'

function App(): JSX.Element {
  return (
        <Router>
            <Routes>
                <Route path="/" element={<Home/>}/>
                <Route path="/login" element={<LoginPage/>}/>
            </Routes>
        </Router>
  )
}

export default App
