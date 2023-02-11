import React, {useState, useEffect} from "react";
import "./AddPatient.css";
import * as AmazonCognitoIdentity from "amazon-cognito-identity-js";
import {FaArrowLeft} from "react-icons/fa";
import { useNavigate} from "react-router-dom";
import {ServiceHandler} from "../../helpers/ServiceHandler";
import {getCaregiverId} from "../../helpers/types";
import { QRCode } from "react-qr-svg";
import CaregiverNavbar from "../Navbar/CaregiverNavbar";

type response = {
    patient_id: string,
    auth_code: string
}

function AddCaregiver() {
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [contact, setContact] = useState("");
    const [error, setError] = useState("");
    const [submitted, setSubmitted] = useState(false);
    const [windowDimensions, setWindowDimensions] = useState(getWindowDimensions());
    const [patientId, setPatientID] = useState("");
    const [authCode, setAuthCode] = useState("");
    const caregiverId = getCaregiverId();
    const nav = useNavigate();

    function getWindowDimensions() {
        const { innerWidth: width, innerHeight: height } = window;
        return {
            width,
            height
        };
    }

    useEffect(() => {
        function handleResize() {
            setWindowDimensions(getWindowDimensions());
        }

        window.addEventListener("resize", handleResize);
        return () => window.removeEventListener("resize", handleResize);
    }, []);

    const handleSubmit = (event : React.FormEvent) => {
        event.preventDefault();
        addPatient();
    };

    const handleKey = (event:React.KeyboardEvent) => {
        if(event.key === "Enter") {
            addPatient();
        }
    };

    const addPatient = () => {
        if (!firstName) {
            setError("First name cannot be empty");
            return;
        }
        if (!lastName) {
            setError("Last name cannot be empty");
            return;
        }
        if (!contact) {
            setError("Contact Number cannot be empty");
            return;
        }
        setError("");
        ServiceHandler.addPatient(firstName, lastName, contact)
            .then((res: any) => {
                setPatientID(res.patient_id);
                setAuthCode(res.auth_code);
                console.log(res);
                console.log(caregiverId);
                setSubmitted(true);
            })
            .catch((err: Error) => setError(err.message));
    };

    return (
        <>
            <CaregiverNavbar/>
            <div className="add-patient">
                <div className='wrapper'>
                    <div className="icon" onClick={() => nav("/dashboard")}><FaArrowLeft size="15px"/> Home</div>
                    {submitted === true? 
                        <>
                            <div className="text-wrapper">
                                <h2>&nbsp;Add a patient</h2>
                                <p className="desc">
                            Please scan this QR code with your patient&#39;s Mobility Monitor app to 
                            link them to your dashboard.
                                </p>
                            </div>
                            <div className='qr-wrapper'>
                                <QRCode
                                    level="Q"
                                    style={{ width: windowDimensions.width/6 }}
                                    value={JSON.stringify({
                                        "caregiver_id": caregiverId,
                                        "auth_code": authCode,
                                        "patient_id": patientId
                                    })}
                                />
                                <p className="or">or</p>
                                <button id="or-button" onClick={(e) => {
                                    setSubmitted(false);} }>Re-fill patient information</button>
                            </div>
                        </>
                        : 
                        <>
                            <div className="text-wrapper">
                                <h2>&nbsp;Add a patient</h2>
                                <p className="desc">
                    Enter the patient&#39;s information and click on the add patient button. Next, 
                    a QR code will be generated which you can scan from your patient&#39;s Mobility Monitor app.
                    This will link your patient to your dashboard.</p>
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
                                    <div className='contact_number'>
                                        <input type='contact_number' name='contact number' placeholder="Contact number" onChange={
                                            (e) => setContact(e.target.value)} onKeyDown={(e) => handleKey(e)}/>
                                    </div>
                                    <div className='submit'>
                                        <button className="add-button" onClick={handleSubmit}>Add Patient</button>
                                    </div>
                                    {error === "" ? null: <div className="err">{error}</div>} 
                                </form>
                            </div>
                        </>
                    };
                </div>
            </div>
        </>
    );
}

export default AddCaregiver;
