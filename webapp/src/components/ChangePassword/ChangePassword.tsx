import React, {useState, useEffect} from "react";
import "./ChangePassword.css";
import { useNavigate} from "react-router-dom";
import {createUser, createUserAndGetSession} from "../../helpers/Cognito";
import {FaArrowLeft} from "react-icons/fa";
import * as AmazonCognitoIdentity from "amazon-cognito-identity-js";
import CaregiverNavbar from "../Navbar/CaregiverNavbar";
import {TextField} from "@mui/material";

function ChangePassword() {
    const [password, setPassword] = useState("");
    const [oldPassword, setOldPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const nav = useNavigate();
    let cognitoUser: AmazonCognitoIdentity.CognitoUser | null;

    useEffect(() => {
        const username = localStorage.getItem("username");
        cognitoUser = createUserAndGetSession(username, true,() => {
            nav("/login");
        }, () => {
            setLoading(false);
        });
    });

    const handleKey = (event:React.KeyboardEvent) => {
        if(event.key === "Enter") {
            handleSubmit();
        }
    };
    const handleSubmit = ()=> {
        if (loading) return;
        if(! (password.length >= 8)) return;
        if(! /\d/.test(password)) return;
        if(! /[a-z]/.test(password)) return;
        if(! /[A-Z]/.test(password)) return;
        if(! /[`!@#$%^&*()_+\-=\\[\]{};':"\\|,.<>\\/?~]/.test(password)) return;
        if (password !== confirmPassword) return;
        cognitoUser?.changePassword(oldPassword, password, (err) => {
            if (err) {
                alert(err.message || JSON.stringify(err));
            }
            else {
                nav("/");
            }
        });
    };
    return (
        <>
            <CaregiverNavbar/>
            <div className="force-pwd">
                <div className="icon" onClick={() => nav("/")}><FaArrowLeft size="15px"/> Home Page</div>
                <div className="title"><h2>Change password</h2>
                    <p className="desc">Please enter your old password for verification along with your new desired password</p>
                </div>
                <div></div>
                <div></div>
                <div className="wrapper">
                    <div className='login-input'>
                        <div className="login-input-form">
                            <TextField color='secondary' fullWidth variant='outlined' type='password' label='Old Password' onKeyUp={(e) => handleKey(e)} onChange={(e) => setOldPassword(e.target.value)} />
                            <div className="pad"/>
                            <TextField color='secondary' fullWidth variant='outlined' type='password' label='New Password' onKeyUp={(e) => handleKey(e)} onChange={(e) => setPassword(e.target.value)}/>
                            <div className="pad"/>
                            <TextField color='secondary' fullWidth variant='outlined' type='password' label='Confirm Password' onKeyUp={(e) => handleKey(e)} onChange={(e) => setConfirmPassword(e.target.value)}/>
                            <div className="pad"/>
                        </div>
                        {loading === true? 
                            <button type='submit' onClick={(e) => {return;}}>Loading</button>:
                            <button type='submit' onClick={(e) => handleSubmit()}>Change Password</button>}
                        {error === ("")? null: <p className="err">{error}</p>}
                    </div>
                </div>
                <div className="compare-wrapper">
                    <div className="compare">
                        <p className={password.length >= 8 ? "green": "red"}> Minimum length should be 8 characters</p><br/>
                        <p className={/\d/.test(password) ? "green": "red"}> Must contain at least one number</p><br/>
                        <p className={/[a-z]/.test(password) ? "green": "red"}> Must contain at least one lowercase letter</p><br/>
                        <p className={/[A-Z]/.test(password) ? "green": "red"}> Must contain at least one uppercase letter</p><br/>
                        <p className={/[`!@#$%^&*()_+\-=\\[\]{};':"\\|,.<>\\/?~]/.test(password)? "green": "red"}> Must contain at least one special character</p><br/>
                        <p className={password === confirmPassword && password.length > 0 ? "green":"red"}> Passwords must match</p><br/>
                    </div>
                </div>
            </div>

        </>
    );
}

export default ChangePassword;
