import React from "react";
import "./App.css";
import LoginPage from "./components/LoginPage/LoginPage";
import Home from "./Home";
import SystemAdmin from "./components/SystemAdmin/SystemAdmin";
import { BrowserRouter as Router, Routes, Route} from "react-router-dom";
import Dashboard from "./components/Dashboard/Dashboard";
import NewDashboard from "./components/NewDashboard/NewDashboard";
import sampleData from "./components/NewDashboard/sampleData";

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<Home/>}/>
                <Route path="/login" element={<LoginPage/>}/>
                <Route path="/sysadmin" element={<SystemAdmin/>}/>
                <Route path="/Dashboard" element={<Dashboard/>}/>
                <Route path="/NewDashboard" element={<NewDashboard/>}/>
            </Routes>
        </Router>
    );
}

export default App;
