import React, {useState} from "react";
import "./SystemAdmin.css";

function SystemAdmin() {
    const [name, setName] = useState("");
    const [email, setEmail] = useState("");
    const [contact, setContact] = useState("");
    const handleSubmit = (event : React.FormEvent) => {
        event.preventDefault();
        console.log(name);
        console.log(email);
        console.log(contact);
    };

    return (
        <div className="sysadmin">
            <div className='wrapper'>
                <div className='form-wrapper'>
                    <h2>Sign Up</h2>
                    <form onSubmit={handleSubmit} noValidate >
                        <div className='email'>
                            <input type='text' name='name' placeholder="Name" onChange={
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
