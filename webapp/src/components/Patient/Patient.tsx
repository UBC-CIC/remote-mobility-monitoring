import React, {useState, useEffect} from "react";
import { useNavigate, useParams} from "react-router-dom";
import {ServiceHandler} from "../../helpers/ServiceHandler";
import CaregiverNavbar from "../Navbar/CaregiverNavbar";
import {decrypt} from "../../helpers/Crypto";


function Patient() {
    const nav = useNavigate();
    const [patientDetails, setPatientDetails]: any = useState({});
    const {patientIdEncrypt} = useParams();
    const [deletionMessage, setDeletionMessage] = useState("");
    const patientId = decrypt(patientIdEncrypt);


    useEffect(() => {
        if (!patientId) return;
        ServiceHandler.getPatient(patientId)
            .then((data: any) => {
                setPatientDetails(data);
            })
            .catch((err) => console.log(err));
        ServiceHandler.queryMetrics([patientId], {"year": 2022, "month": 3, "day":20}, 
            {"year": 2023, "month": 11, "day":20})
            .then((data: any) => {
                console.log(data);
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
            {patientDetails.is_primary? "Hello": "Not primary"}<button onClick={(e) => nav("share")}>Share</button>
        </>
    );
}

export default Patient;
