import logo from './logo.svg';
import { BrowserRouter as Router, Routes, Route, useNavigate} from "react-router-dom";

function App() {
    const nav = useNavigate();
    return (
        <button onClick={(e) => {
            nav('/login')
        }}>Login</button>
    );
}

export default App;
