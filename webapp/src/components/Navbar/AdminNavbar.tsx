import React, {useState} from "react";
import "./Navbar.css";
import { useNavigate} from "react-router-dom";
import {logout} from "../../helpers/Cognito";

function AdminNavbar() {
    const nav = useNavigate();
    return (
        <div className="navbarr">
            Mobility Monitor
            <span className="navbar-items" onClick={(e) => {
                nav("/admindashboard");
            }}>Dashboard</span>
            <span className="navbar-items" onClick={(e) => {
                nav("/addcaregiver");
            }}>Add Caregivers</span>
            <span className="navbar-items" onClick={(e) => {
                logout();
                nav("/login");
            }}>Logout</span>
        </div>
    );
} 

export default AdminNavbar;
