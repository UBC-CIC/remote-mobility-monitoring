import React, {useState} from "react";
import "./ForceChangePassword.css";
import { useNavigate} from "react-router-dom";
import {FaArrowLeft} from "react-icons/fa";
import { useSelector } from "react-redux";
import {userTypes, strObjMap} from "../../helpers/types";
import {State} from "../../helpers/store";
import * as AmazonCognitoIdentity from "amazon-cognito-identity-js";
import jwt_decode from "jwt-decode";
import {TextField} from "@mui/material";

function ForceChangePassword() {
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [error, setError] = useState("");
    const nav = useNavigate();
    const cognitoUser = useSelector((state: State) => state.cognitoUser);
    const handleKey = (event:React.KeyboardEvent) => {
        if(event.key === "Enter") {
            handleSubmit();
        }
    };
    const handleSubmit = ()=> {
        // First check if password meets minimum requirements. 
        if(! (password.length >= 8)) return;
        if(! /\d/.test(password)) return;
        if(! /[a-z]/.test(password)) return;
        if(! /[A-Z]/.test(password)) return;
        if(! /[`!@#$%^&*()_+\-=\\[\]{};':"\\|,.<>\\/?~]/.test(password)) return;
        if (password !== confirmPassword) return;

        const callback = {
            onSuccess: function() {
                setError("");

                // Remove current username and sub from storage if exists
                
                if (localStorage.getItem("username")) {
                    localStorage.removeItem("username");
                }
                if (localStorage.getItem("sub")) {
                    localStorage.removeItem("sub");
                }
                nav("/login");
            },
            onFailure: function(err: any) {
                let errMsg = err.message || JSON.stringify(err);
                if (errMsg.includes("required parameter USERNAME")) {
                    errMsg = "Please enter your email";
                }
                else if (errMsg.includes("Incorrect username")) {
                    errMsg = "Incorrect email or password";
                }
            },
            newPasswordRequired: function() {
                nav("/login");
            }
        };
        cognitoUser.completeNewPasswordChallenge(password, null, callback);
    };
    return (
        <div className="force-pwd">
            <div className="icon" onClick={() => nav("/login")}><FaArrowLeft size="15px"/> Login Page</div>
            <div className="title"><h2>Update password</h2>
                <p className="desc">Since this is the first time you&#39;re logging in, you must change your password to continue</p>
            </div>
            <div></div>
            <div></div>
            <div className="wrapper">
                <div className='login-input'>
                    <div className="login-input-form">
                        <TextField color='secondary' fullWidth variant='outlined' type='password' label='New Password' onKeyUp={(e) => handleKey(e)} onChange={(e) => setPassword(e.target.value)}/>
                        <div className="pad"/>
                        <TextField color='secondary' fullWidth variant='outlined' type='password' label='Confirm Password' onKeyUp={(e) => handleKey(e)} onChange={(e) => setConfirmPassword(e.target.value)}/>
                        <div className="pad"/>
                    </div>
                    <button type='submit' onClick={(e) => handleSubmit()}>Change Password</button>
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
    );
}

export default ForceChangePassword;
