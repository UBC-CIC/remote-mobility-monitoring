import React, {useState} from "react";
import "./AddCaregiver.css";
import * as AmazonCognitoIdentity from "amazon-cognito-identity-js";
import {FaArrowLeft} from "react-icons/fa";
import { useNavigate} from "react-router-dom";
import AdminNavbar from "../Navbar/AdminNavbar";
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
        if (!firstName) {
            setError("First name cannot be empty");
            return;
        }
        if (!lastName) {
            setError("Last name cannot be empty");
            return;
        }
        if (!email) {
            setError("Email cannot be empty");
            return;
        }
        if (!contact) {
            setError("Contact Number cannot be empty");
            return;
        }
        setError("");
        ServiceHandler.addCaregiver(firstName, lastName, email, contact)
            .then((data: any) => {
                console.log(data);
                alert("Caregiver sucessfully added. They have received an email with further instructions.");
                nav("/admindashboard");
            })
            .catch((err: Error) => setError(err.message.split("(")[0]));
    };

    return (
        <>
            <AdminNavbar/>
            <div className="sysadmin">
                <div className='wrapper'>
                    <div className="icon" onClick={() => nav("/admindashboard")}><FaArrowLeft size="15px"/> Home</div>
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

        </>
    );
}

export default AddCaregiver;
