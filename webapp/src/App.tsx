import React from "react";
import "./App.css";
import LoginPage from "./components/LoginPage/LoginPage";
import ForceChangePassword from "./components/LoginPage/ForceChangePassword";
import ChangePassword from "./components/ChangePassword/ChangePassword";
import Home from "./Home";
import AddCaregiver from "./components/SystemAdmin/AddCaregiver";
import AddPatient from "./components/AddPatient/AddPatient";
import ProtectedRoute from "./helpers/ProtectedRoute";
import { BrowserRouter as Router, Routes, Route} from "react-router-dom";
import { Provider } from "react-redux";
import store from "./helpers/store";


function App() {
    return (
        <Provider store={store}>
            <Router>
                <Routes>
                    <Route path="/" element={
                        <ProtectedRoute type="caregiver"><Home/></ProtectedRoute>
                    }/>
                    <Route path="/login" element={<LoginPage/>}/>
                    <Route path="/addcaregiver" element={
                        <ProtectedRoute type="admin"><AddCaregiver/></ProtectedRoute>
                    }/>
                    <Route path="/newuserpwd" element={
                        <ProtectedRoute type="caregiver"><ForceChangePassword/></ProtectedRoute>
                    }/>
                    <Route path="/changepwd" element={
                        <ProtectedRoute type="caregiver"><ChangePassword/></ProtectedRoute>
                    }/>
                    <Route path="/addpatient" element={
                        <ProtectedRoute type="caregiver"><AddPatient/></ProtectedRoute>
                    }/>
                </Routes>
            </Router>
        </Provider>
    );
}

export default App;
