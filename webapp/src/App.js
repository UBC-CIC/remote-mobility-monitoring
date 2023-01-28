import logo from './logo.svg';
import './App.css';
import LoginPage from './components/LoginPage/LoginPage.tsx'
import Home from './Home.js'
import { BrowserRouter as Router, Routes, Route, useNavigate} from "react-router-dom";

function App() {
    return (
        <Router>
            <Routes>
                <Route exact path="/" element={<Home/>}/>
                <Route exact path="/login" element={<LoginPage/>}/>

            </Routes>
        </Router>
    );
}

export default App;
