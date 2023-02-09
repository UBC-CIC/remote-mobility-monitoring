import React, {useState} from "react";
import "./LoginPage.css";
import { useNavigate} from "react-router-dom";
import {createUser, login} from "../../helpers/Cognito";
import { useDispatch } from "react-redux";
import {userTypes, strObjMap}  from "../../helpers/types";
import * as AmazonCognitoIdentity from "amazon-cognito-identity-js";
import jwt_decode from "jwt-decode";


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
        const cognitoUser = createUser(email, false);
        if (!cognitoUser) {
            setError("An unexpected error occured. Please try again later");
            return;
        }
        const callback = {
            onSuccess: function(result: AmazonCognitoIdentity.CognitoUserSession) {
                /*
                 * on success, we store the username of the user. This is stored as
                 * the sub in the access token. Therefore, we first decode the access
                 * token and then access the sub field. 
                 * */

                setError("");

                // Remove current username from storage if exists
                
                if (localStorage.getItem("username")) {
                    localStorage.removeItem("username");
                }
                if (localStorage.getItem("sub")) {
                    localStorage.removeItem("sub");
                }
                const decodedToken: strObjMap = jwt_decode(result.getAccessToken().getJwtToken());
                let sub = decodedToken["sub"];
                const userType = userTypes[loginType];
                console.log(sub);

                sub = userType.concat(sub);
                const username = userType.concat(cognitoUser.getUsername());
                localStorage.setItem("sub", sub);
                localStorage.setItem("username", username);
                if (loginType === "caregiver") {
                    nav("/dashboard");
                }
                else {
                    nav("/admindashboard");
                }
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
                if (localStorage.getItem("sub")) {
                    localStorage.removeItem("sub");
                }
                localStorage.setItem("username", userTypes["caregiver"]);
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
                <h2>Mobility Monitor {loginType === "caregiver" ? "": "as admin"}</h2>
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
                    <button type='submit' onClick={(e) => { handleSubmit(e); }}>Login</button>
                    {error === ("")? null: <p className="err">{error}</p>}
                </div>
            </div>
        </div>
    );
}

export default LoginPage;
