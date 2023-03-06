import React from "react";
import { useNavigate} from "react-router-dom";

function App() {
    const nav = useNavigate();
    return (
        <div>
            <button onClick={() => {
                nav("/login");
            }}>Login</button>
            <button onClick={() => {
                nav("/sysadmin");
            }}>Sysadmin</button>
            <button onClick={() => {
                nav("/Dashboard");
            }}>Dashboard</button>
            <button onClick={() => {
                nav("/NewDashboard");
            }}>NewDashboard</button>
        </div>
    );
}

export default App;
