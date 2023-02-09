import React, {useState} from "react";
import "./SystemAdmin.css";
import * as AmazonCognitoIdentity from "amazon-cognito-identity-js";


function SystemAdmin() {
    const [name, setName] = useState("");
    const [email, setEmail] = useState("");
    const [contact, setContact] = useState("");
    const [password, setPassword] = useState("");
    const handleSubmit = (event : React.FormEvent) => {
        event.preventDefault();
        addPatient();
    };

    const addPatient = () => {
        const poolData = {
            UserPoolId: "us-west-2_KNaCjxQMe", // Your user pool id here
            ClientId: "4b9th6a530qb4dh44qphu07va7", // Your client id here
        };
        const userPool = new AmazonCognitoIdentity.CognitoUserPool(poolData);

        const attributeList = [];

        const dataEmail = {
            Name: "email",
            Value: email,
        };
        const attributeEmail = new AmazonCognitoIdentity.CognitoUserAttribute(dataEmail);
        attributeList.push(attributeEmail);

        userPool.signUp(email, password, attributeList, null!, function(
            err,
            result
        ) {
            if (err) {
                alert(err.message || JSON.stringify(err));
                return;
            }
            const cognitoUser = result?.user;
            console.log("user name is " + cognitoUser?.getUsername());
        });

    };

    return (
        <div className="sysadmin">
            <div className='wrapper'>
                <div className='form-wrapper'>
                    <h2>Sign Up</h2>
                    <form onSubmit={handleSubmit} noValidate >
                        <div className='username'>
                            <input type='text' name='username' placeholder="Name" onChange={
                                (e) => setName(e.target.value)}/>
                        </div>
                        <div className='email'>
                            <input type='email' name='email' placeholder="Email" onChange={
                                (e) => setEmail(e.target.value)}/>
                        </div>
                        <div className='contact_number'>
                            <input type='contact_number' name='contact number' placeholder="Contact number" onChange={
                                (e) => setContact(e.target.value)}/>
                        </div>
                        <div className='password'>
                            <input type='password' name='Password' placeholder="Password" onChange={
                                (e) => setPassword(e.target.value)}/>
                        </div>
                        <div className='submit'>
                            <button onClick={handleSubmit}>Register</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
}

export default SystemAdmin;
