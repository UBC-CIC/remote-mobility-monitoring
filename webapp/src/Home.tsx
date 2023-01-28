import React from "react";
import { useNavigate} from "react-router-dom";

function App() {
    const nav = useNavigate();
    return (
        <button onClick={() => {
            nav("/login");
        }}>Login</button>
    );
}

export default App;
