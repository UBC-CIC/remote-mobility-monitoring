import React, {useState} from "react";
import "./Navbar.css";
import { useNavigate} from "react-router-dom";
import {logout} from "../../helpers/Cognito";

function CaregiverNavbar() {
    const nav = useNavigate();
    return (
        <div className="navbar">
            Mobility Monitor
            <span className="navbar-items" onClick={(e) => {
                nav("/dashboard");
            }}>Dashboard</span>
            <span className="navbar-items" onClick={(e) => {
                nav("/addpatient");
            }}>Add Patients</span>
            <span className="navbar-items" onClick={(e) => {
                nav("/changepwd");
            }}>Change Password</span>
            <span className="navbar-items" onClick={(e) => {
                logout();
                nav("/login");
            }}>Logout</span>
        </div>
    );
} 

export default CaregiverNavbar;
