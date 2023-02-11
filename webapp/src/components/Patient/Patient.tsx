import React, {useState, useEffect} from "react";
import { useNavigate, useParams} from "react-router-dom";
import {ServiceHandler} from "../../helpers/ServiceHandler";
import CaregiverNavbar from "../Navbar/CaregiverNavbar";


function Patient() {
    const nav = useNavigate();
    const [patientDetails, setPatientDetails] = useState({});
    const {patientId} = useParams();
    const [deletionMessage, setDeletionMessage] = useState("");

    useEffect(() => {
        if (!patientId) return;
        ServiceHandler.getPatient(patientId)
            .then((data: any) => {
                setPatientDetails(data);
            })
            .catch((err) => console.log(err));
    }, []);

    const handleDeletePatient = () => {
        ServiceHandler.deletePatient(patientId!)
            .then((data: any) => {
                setDeletionMessage(data.message);
            })
            .catch((err: any) => console.log(err));
    };

    return (
        <>
            <CaregiverNavbar/>
            <div><button onClick={(e) => nav("/dashboard")}>
            Dashboard
            </button>
            <br/>
            <br/>
            <div>{JSON.stringify(patientDetails)}</div>
            </div>
            <button onClick={handleDeletePatient}>Delete Patient</button>
            <br />
            <br />
            {deletionMessage && (
                <div>{deletionMessage}</div>
            )}
        </>
    );
}

export default Patient;
