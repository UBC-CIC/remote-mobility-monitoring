import React, {useState, useEffect} from "react";
import { useNavigate, useParams} from "react-router-dom";
import {ServiceHandler} from "../../helpers/ServiceHandler";
import CaregiverNavbar from "../Navbar/CaregiverNavbar";
import Divider from "@mui/material/Divider";
import {decrypt} from "../../helpers/Crypto";
import InputLabel from "@mui/material/InputLabel";
import MenuItem from "@mui/material/MenuItem";
import FormControl from "@mui/material/FormControl";
import Select, { SelectChangeEvent } from "@mui/material/Select";
import {ToggleButton, ToggleButtonGroup} from "@mui/material";
import "./Patient.css";


function Patient() {
    const nav = useNavigate();
    const [patientDetails, setPatientDetails]: any = useState({});
    const {patientIdEncrypt} = useParams();
    const [deletionMessage, setDeletionMessage] = useState("");
    const [intervals, setIntervals] = useState("1D");
    const [selectedPatient, setSelectedPatient] = useState("");
    const patientId = decrypt(patientIdEncrypt);


    useEffect(() => {
        if (!patientId) return;
        ServiceHandler.getPatient(patientId)
            .then((data: any) => {
                setPatientDetails(data);
                console.log(data);
            })
            .catch((err) => console.log(err));
        ServiceHandler.queryMetrics([patientId], {"year": 2022, "month": 3, "day":20}, 
            {"year": 2023, "month": 11, "day":20})
            .then((data: any) => {
                console.log(data);
            })
            .catch((err) => console.log(err));
    }, []);

    const handleIntervals = (e: any) => {
        setIntervals(e.target.value);
    };

    const handlePatientChange = (e: any) => {
        setSelectedPatient(e.target.value);
    };

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
            <div className="patient-container">
                <div className="filters">
                    <div className="padding"></div>
                    <FormControl fullWidth>
                        <InputLabel id="demo-simple-select-label">Patient</InputLabel>
                        <Select
                            labelId="demo-simple-select-label"
                            id="demo-simple-select"
                            value={selectedPatient}
                            label="Patient"
                            onChange={handlePatientChange}
                        >
                        </Select>
                    </FormControl>
                    <div className="padding"></div>
                    <ToggleButtonGroup
                        color="primary"
                        value={intervals}
                        exclusive
                        onChange={handleIntervals}
                        aria-label="Platform"
                    >
                        <ToggleButton value="1D">1D</ToggleButton>
                        <ToggleButton value="1W">1W</ToggleButton>
                        <ToggleButton value="1M">1M</ToggleButton>
                        <ToggleButton value="1Y">1Y</ToggleButton>
                    </ToggleButtonGroup>

                </div>
                <div className="data">b</div>

            </div>
        </>
    );
}

export default Patient;
