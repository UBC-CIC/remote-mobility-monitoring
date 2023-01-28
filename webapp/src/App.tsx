import React from "react";
import "./App.css";
import LoginPage from "./components/LoginPage/LoginPage";
import Home from "./Home";
import SystemAdmin from "./components/SystemAdmin/SystemAdmin";
import { BrowserRouter as Router, Routes, Route} from "react-router-dom";

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<Home/>}/>
                <Route path="/login" element={<LoginPage/>}/>
                <Route path="/sysadmin" element={<SystemAdmin/>}/>
            </Routes>
        </Router>
    );
}

export default App;
