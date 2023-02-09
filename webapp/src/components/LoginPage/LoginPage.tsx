import React, {useState} from "react";
import "./LoginPage.css";
import { useNavigate} from "react-router-dom";
import {createUser, login} from "../../Cognito";
import { useDispatch } from "react-redux";

function LoginPage() {
    const [loginType, setLoginType] = useState("caregiver");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const nav = useNavigate();
    const dispatch = useDispatch();

    const handleKey = (event:React.KeyboardEvent) => {
        if(event.key === "Enter") {
            handleLogin();
        }
    };

    const handleSubmit = (event: React.MouseEvent) => {
        event.preventDefault();
        handleLogin();
    };
    const handleLogin = () => {
        const cognitoUser = createUser(email);
        const callback = {
            onSuccess: function() {
                if (localStorage.getItem("username")) {
                    localStorage.removeItem("username");
                }
                localStorage.setItem("username", cognitoUser.getUsername());
                nav("/");
            },
            onFailure: function(err: any) {
                let errMsg = err.message || JSON.stringify(err);
                if (errMsg.includes("required parameter USERNAME")) {
                    errMsg = "Please enter your email";
                }
                else if (errMsg.includes("Incorrect username")) {
                    errMsg = "Incorrect email or password";
                }
                setError(errMsg);
            },
            newPasswordRequired: function() {
                if (localStorage.getItem("username")) {
                    localStorage.removeItem("username");
                }
                localStorage.setItem("username", cognitoUser.getUsername());
                dispatch({ type: "USER", payload: cognitoUser });
                nav("/newuserpwd");
            }
        };
        login(cognitoUser, email, password, callback);
    };

    const toggleLoginType = () => {
        if (loginType === "caregiver") setLoginType("admin");
        else setLoginType("caregiver");
    };

    return (
        <div className='login-page'>
            <div className='login'>
                <h1>Sign in to</h1>
                <h2>Mobility Monitor</h2>
                <p>{loginType === "caregiver" ? "Organization administrators can ": "Caregivers can\n"}
                    <span className='alternate' onClick={toggleLoginType}>Login here</span></p>
            </div>
            <div className='login user'>
                <h2>Sign in</h2>
                <div className='login-input'>
                    <input placeholder='Email' onKeyDown={(e) => handleKey(e)} onChange={(e) => setEmail(e.target.value)}></input>
                    <br />
                    <input type='password' placeholder='Password'onKeyDown={(e) => handleKey(e)} onChange={(e) => setPassword(e.target.value)}></input>
                    <div className='forgot'>Forgot password?</div>
                    {error === ("")? null: <p className="err">{error}</p>}
                    <button type='submit' onClick={(e) => { handleSubmit(e); }}>Login</button>
                </div>
            </div>
        </div>
    );
}

export default LoginPage;
