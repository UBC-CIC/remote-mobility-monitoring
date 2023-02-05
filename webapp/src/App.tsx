import React from "react";
import "./App.css";
import LoginPage from "./components/LoginPage/LoginPage";
import ForceChangePassword from "./components/LoginPage/ForceChangePassword";
import Home from "./Home";
import SystemAdmin from "./components/SystemAdmin/SystemAdmin";
import ProtectedRoute from "./ProtectedRoute";
import { BrowserRouter as Router, Routes, Route} from "react-router-dom";
import { Provider } from "react-redux";
import store from "./store";


function App() {
    return (
        <Provider store={store}>
            <Router>
                <Routes>
                    <Route path="/" element={
                        <ProtectedRoute><Home/></ProtectedRoute>
                    }/>
                    <Route path="/login" element={<LoginPage/>}/>
                    <Route path="/sysadmin" element={
                        <ProtectedRoute><SystemAdmin/></ProtectedRoute>
                    }/>
                    <Route path="/newuserpwd" element={
                        <ProtectedRoute><ForceChangePassword/></ProtectedRoute>
                    }/>
                </Routes>
            </Router>
        </Provider>
    );
}

export default App;
