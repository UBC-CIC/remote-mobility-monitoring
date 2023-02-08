import React, {useState} from "react";
import "./AddCaregiver.css";
import * as AmazonCognitoIdentity from "amazon-cognito-identity-js";
import {FaArrowLeft} from "react-icons/fa";
import { useNavigate} from "react-router-dom";
import {ServiceHandler} from "../../helpers/ServiceHandler";

function AddCaregiver() {
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [email, setEmail] = useState("");
    const [contact, setContact] = useState("");
    const [error, setError] = useState("");
    const nav = useNavigate();

    const handleSubmit = (event : React.FormEvent) => {
        event.preventDefault();
        addCaregiver();
    };

    const handleKey = (event:React.KeyboardEvent) => {
        if(event.key === "Enter") {
            addCaregiver();
        }
    };

    const addCaregiver = () => {
        console.log(firstName);
        console.log(lastName);
        console.log(email);
        ServiceHandler.addCaregiver(firstName, lastName, email, contact);
    };

    return (
        <div className="sysadmin">
            <div className='wrapper'>
                <div className="icon" onClick={() => nav("/sysadmin")}><FaArrowLeft size="15px"/> Home</div>
                <div className="text-wrapper">
                    <h2>Add a caregiver</h2>
                    <p className="desc">
                    Enter the caregiver&#39;s information and click on the add caregiver button. An email will then be
                    sent to them with sign up instructions.
                    </p>
                </div>
                <div className='form-wrapper'>
                    <form onSubmit={handleSubmit} noValidate >
                        <div className='username'>
                            <input type='text' placeholder="First Name" onChange={
                                (e) => setFirstName(e.target.value)} onKeyDown={(e) => handleKey(e)}/>
                        </div>
                        <div className='username'>
                            <input type='text' placeholder="Last Name" onChange={
                                (e) => setLastName(e.target.value)} onKeyDown={(e) => handleKey(e)}/>
                        </div>
                        <div className='email'>
                            <input type='email' name='email' placeholder="Email" onChange={
                                (e) => setEmail(e.target.value)} onKeyDown={(e) => handleKey(e)}/>
                        </div>
                        <div className='contact_number'>
                            <input type='contact_number' name='contact number' placeholder="Contact number" onChange={
                                (e) => setContact(e.target.value)} onKeyDown={(e) => handleKey(e)}/>
                        </div>
                        <div className='submit'>
                            <button onClick={handleSubmit}>Add Caregiver</button>
                        </div>
                        {error === "" ? null: <div className="err">{error}</div>} 
                    </form>
                </div>
            </div>
        </div>
    );
}

export default AddCaregiver;
