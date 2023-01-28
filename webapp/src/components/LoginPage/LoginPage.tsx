import React, {useState} from 'react';
import './LoginPage.css'

function LoginPage() {
    const [loginType, setLoginType] = useState('caregiver');
    const handleLogin = (event) => {
        event.preventDefault();
        console.log(loginType);
    }
    const toggleLoginType = () => {
        if (loginType === 'caregiver') setLoginType('admin')
        else setLoginType('caregiver')
    }

    return (
        <div className='wrapper'>
            <div className='login'>
                <h1>Sign in to</h1>
                <h2>Mobility Monitor</h2>
                <p>{loginType === 'caregiver' ? "Organization administrators can": "Caregivers can"}<br /> 
                <span className='alternate' onClick={toggleLoginType}>Login here</span></p>
            </div>
            <div className='login user'>
            <h2>Sign in</h2>
                <div className='login-input'>
                    <input placeholder='Username'></input>
                    <br />
                    <input type='password' placeholder='Password'></input>
                    <div className='forgot'>Forgot password?</div>
                    <button type='submit' onClick={handleLogin}>Login</button>
                </div>
            </div>
        </div>
    );
}

export default LoginPage;
