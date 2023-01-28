import React from "react";
import "./App.css";
import LoginPage from "./components/LoginPage/LoginPage";
import Home from "./Home";
import SignUp from "./components/SystemAdmin/SystemAdmin";
import { BrowserRouter as Router, Routes, Route} from "react-router-dom";

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<Home/>}/>
                <Route path="/login" element={<LoginPage/>}/>
                <Route path="/sysadmin" element={<SignUp/>}/>
            </Routes>
        </Router>
    );
}

export default App;
