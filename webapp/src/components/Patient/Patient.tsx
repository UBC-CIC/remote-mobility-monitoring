import React, {useState, useEffect} from "react";
import { useNavigate, useParams} from "react-router-dom";
import {ServiceHandler} from "../../helpers/ServiceHandler";
import CaregiverNavbar from "../Navbar/CaregiverNavbar";


function Patient() {
    const nav = useNavigate();
    const [patientDetails, setPatientDetails] = useState({});
    const {patientId} = useParams();
    useEffect(() => {
        if (!patientId) return;
        ServiceHandler.getPatient(patientId)
            .then((data: any) => {
                setPatientDetails(data);
            })
            .catch((err) => console.log(err));
    }, []);
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

        </>
    );

}

export default Patient;
