import React, {useState, useEffect} from "react";
import {FaArrowRight, FaSearch} from "react-icons/fa";
import { useNavigate} from "react-router-dom";
import {ServiceHandler} from "../../helpers/ServiceHandler";
import "./Dashboard.css";
import CaregiverNavbar from "../Navbar/CaregiverNavbar";
import {encrypt} from "../../helpers/Crypto";
import {Switch} from "@mui/material";

type patient = {
    patient_id: string,
    first_name: string,
    last_name: string,
    verified: boolean,
    email: string
}

function CaregiverDashboard() {
    const nav = useNavigate();
    const [patients, setPatients]:React.SetStateAction<any> = useState([]);
    const [showVerify, setShowVerify] = React.useState(false);
    const [search, setSearch] = React.useState("");

    const getPatients = () => {
        ServiceHandler.getAllPatients()
            .then((data: any) => {
                setPatients(data.patients);
                setFilteredPatients(data.patients);
            })
            .catch((err) => {
                setTimeout(getPatients, 1000);
            });

    };
    useEffect(() => {
        getPatients();
    }, []);


    const [filteredPatients, setFilteredPatients] = useState(patients);
    const handleSearch = (search: string, showVerify: boolean) => {
        let searchString = search.toLowerCase();
        searchString = searchString.replace(/\s/g, "");
        const searchPatients: Array<patient> = [];

        patients.forEach((patient: patient) => {
            if (showVerify === true && patient.verified === false) {
                return;
            }
            let name = patient.first_name.concat(patient.last_name);
            name = name.toLowerCase();
            if (name.includes(searchString)) {
                searchPatients.push(patient);
            }
        });
        setFilteredPatients(searchPatients);
    };


    return (
        <>
            <CaregiverNavbar/>
            <div className="dashboard">
                <svg xmlns="search" width="1.3em" height="1.3em" viewBox="0 -4 24 24"><path fill="currentColor" 
                    d="M15.5 14h-.79l-.28-.27A6.471 6.471 0 0 0 16 9.5A6.5 6.5 0 1 0 9.5 16c1.61 0 3.09-.59 
            4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5S14 
            7.01 14 9.5S11.99 14 9.5 14z"/></svg>
                <input value={search} onChange={(e) => {
                    e.preventDefault();
                    setSearch(e.target.value);
                    handleSearch(e.target.value, showVerify);
                }} placeholder={"Search ".concat(filteredPatients.length.toString())
                    .concat(" patients")}></input>
                <div className="show-verified">Show only verified patients 
                    <Switch checked={showVerify} onChange={(e) => {
                        setShowVerify(e.target.checked);
                        handleSearch(search, e.target.checked);
                    }}/> </div>
                <div className={filteredPatients.length === 1? "entry-view1":
                    filteredPatients.length === 2? "entry-view2": 
                        filteredPatients.length === 3? "entry-view3" : "entry-view"}>
                    {filteredPatients.map((patient: patient) => {
                        return <div key={patient.patient_id} className="entry" onClick={() => {
                            if (patient.verified===null || patient.verified) {
                                const patientIdEncrypt = encrypt(patient.patient_id);
                                const navUrl = "patient/".concat(patientIdEncrypt);
                                nav(navUrl);
                                window.location.reload();
                            }
                            else {
                                const emailEncrypt = encrypt(patient.email);
                                const navUrl = "verifyPatient/".concat(emailEncrypt);
                                nav(navUrl);
                            }
                        }}>
                            <span className={patient.verified === null || patient.verified? "": "patient-name-verify"}>{patient.first_name} <br/> {patient.last_name} 
                                <br/>
                                {patient.verified === null || patient.verified? "": "(Unverified)"}
                            </span>

                            <div className={patient.verified === null || patient.verified? "view": "verify"}>
                                {patient.verified === null || patient.verified? "View": "Verify"} <FaArrowRight size="5%"/>
                            </div>
                        </div>;
                    })} 
                </div>
                <br/>
                <div className={filteredPatients.length < 4? "button-grid2": "button-grid"}>
                    <div>
                        <button className={"add"}
                            onClick={(e) => nav("/addpatient")}>Add a New Patient</button>
                    </div>
                    <div>
                        <button className={"addl"}
                            onClick={(e) => {
                                nav("allPatients");
                            }}>View All Patient Metrics</button>
                    </div>
                </div>
            </div>
        </>
    );
}

export default CaregiverDashboard;
