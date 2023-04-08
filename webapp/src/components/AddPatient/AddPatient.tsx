import React, {useState, useEffect} from "react";
import "./AddPatient.css";
import * as AmazonCognitoIdentity from "amazon-cognito-identity-js";
import {FaArrowLeft} from "react-icons/fa";
import { useNavigate} from "react-router-dom";
import {ServiceHandler} from "../../helpers/ServiceHandler";
import {getCaregiverId} from "../../helpers/types";
import { QRCode } from "react-qr-svg";
import CaregiverNavbar from "../Navbar/CaregiverNavbar";
import {TextField} from "@mui/material";

type response = {
    patient_id: string,
    auth_code: string,
    device_id: string
}

function AddPatient() {
    const [email, setEmail] = useState("");
    const [error, setError] = useState("");
    const [submitted, setSubmitted] = useState(false);
    const [windowDimensions, setWindowDimensions] = useState(getWindowDimensions());
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
        if (!email) {
            setError("Email cannot be empty");
            return;
        }
        setError("");
        ServiceHandler.addPatient(email)
            .then((code: any) => {
                setAuthCode(code.auth_code);
                setSubmitted(true);
            })
            .catch(err => setError("A patient with this email does not exist"));
    };

    return (
        <>
            <CaregiverNavbar/>
            <div className="add-patient">
                <div className='wrapper'>
                    <div className="icon" onClick={() => nav("/dashboard")}><FaArrowLeft size="15px"/> Dashboard</div>
                    {submitted === true? 
                        <>
                            <div className="text-wrapper">
                                <h2>&nbsp;Verify Patient</h2>
                                <p className="desc">
                                    <b>Patient email: {email}</b><br/>
                    Scan the qr code below using your patient&#39;s Mobility Monitor app to link them to your dashboard.
                    Alternatively, we have also sent them an email with instructions if they wish to link their account remotely.
                                </p>
                            </div>
                            <div className='qr-wrapper'>
                                <QRCode
                                    level="Q"
                                    style={{ width: windowDimensions.width/6 }}
                                    value={JSON.stringify({
                                        "auth_code": authCode,
                                        "caregiver_id": caregiverId
                                    })}
                                />
                                <p className="or">Made a mistake?</p>
                                <button id="or-button" onClick={(e) => {
                                    setSubmitted(false);} }>Enter new email</button>
                            </div>
                        </>
                        : 
                        <>
                            <div className="text-wrapper">
                                <h2>&nbsp;Add a patient</h2>
                                <p className="desc">
                    Enter the patient&#39;s information and click on the add patient button. Next, 
                    a QR code will be generated which you can scan from your patient&#39;s 
                    Mobility Monitor app. Additionally, 
                    we will send the patient an email with instructions if they choose to link remotely.
                    This will link your patient to your dashboard.</p>
                            </div>
                            <div className='form-wrapper'>
                                <form onSubmit={handleSubmit} noValidate >
                                    <div className='username'>
                                        <TextField variant="outlined" color="secondary" type='text' label="Email" onChange={
                                            (e) => setEmail(e.target.value)} onKeyDown={(e) => handleKey(e)}/>
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

export default AddPatient;
