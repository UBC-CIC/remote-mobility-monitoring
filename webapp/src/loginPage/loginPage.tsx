import React, {useState} from 'react';
import './loginPage.css'

function LoginPage() {
    return (
        <div className='wrapper'>
            <div className='login'>
                <h1>Sign in to</h1>
                <h2>Mobility Monitor</h2>
                <p>Organization administrators can <br /> Login here</p>
            </div>
            <div className='login'>
                <h2>Sign in</h2>
                <input placeholder='Username'></input>
                <br />
                <input type='password' placeholder='Password'></input>
            </div>
        </div>
    );
}

export default LoginPage;

