import React from "react";
import { useNavigate} from "react-router-dom";
import {createUser} from "./helpers/Cognito";
import CaregiverNavbar from "./components/Navbar/CaregiverNavbar";


function Home() {
    const nav = useNavigate();
    return (
        <div>
            <CaregiverNavbar/>
            <button onClick={() => {
                nav("/login");
            }}>Login</button>
            <button onClick={() => {
                nav("/sysadmin");
            }}>Sysadmin</button>
        </div>
    );
}

export default Home;
