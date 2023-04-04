import React, {useState, useEffect} from "react";
import moment from "moment";
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
import DatePicker, { ReactDatePickerProps } from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import "./Patient.css";


function Patient() {
    const nav = useNavigate();
    const [patientDetails, setPatientDetails]: any = useState({});
    const {patientIdEncrypt} = useParams();
    const [deletionMessage, setDeletionMessage] = useState("");
    const [intervals, setIntervals] = useState("1D");
    const patientId = decrypt(patientIdEncrypt);
    const [selectedPatient, setSelectedPatient]: any = useState("");
    const [allPatients, setAllPatients] = useState([]);
    const [endDate, setEndDate] = useState(moment());
    const [selectedFromDate, setSelectedFromDate] = useState<Date>(new Date());
    const [selectedToDate, setSelectedToDate] = useState<Date>(new Date());

    const handleFromDateChange = (date: Date) => {
        if (date > selectedToDate) {
            return;
        }
        setSelectedFromDate(date);
        queryDateMetrics(date, selectedToDate);
        setIntervals("C");
    };

    const handleToDateChange = (date: Date) => {
        if (date < selectedFromDate) {
            return;
        }
        setSelectedToDate(date);
        queryDateMetrics(selectedFromDate, date);
        setIntervals("C");
    };

    const queryDateMetrics = (startDate: Date, endDate: Date) => {
        ServiceHandler.queryMetrics([selectedPatient], selectedFromDate.toISOString(), selectedToDate.toISOString())
            .then((data) => console.log(data))
            .catch((err) => console.log());
        return;
    };


    const getAllPatients = () => {
        ServiceHandler.getAllPatients()
            .then((data: any) => {
                const patientArray:any = [];
                data.patients.forEach((pat: any) => {
                    if (pat.verified===null || pat.verified===true) {
                        const toAdd = {"patient_id": pat.patient_id, "name": `${pat.first_name} ${pat.last_name}`};
                        patientArray.push(toAdd);
                    }
                });
                setAllPatients(patientArray);
                setSelectedPatient(patientId);
            })
            .catch((err) => console.log(err));
    };

    useEffect(() => {
        const date = new Date();
        date.setDate(date.getDate() - 1);
        setSelectedFromDate(date);
        getAllPatients();
        if (!patientId) return;
        ServiceHandler.getPatient(patientId)
            .then((data: any) => {
                setPatientDetails(data);
            })
            .catch((err) => console.log(err));
    }, []);

    useEffect(() => {
        queryDateMetrics(selectedFromDate, selectedToDate);
    }, [selectedPatient]);

    const handleIntervals = (e: any) => {
        const interval = e.target.value;
        setIntervals(interval);
        if (interval === "C") {
            return;
        }
        const toDate = new Date();
        setSelectedToDate(toDate);
        const date = new Date();
        if (interval === "1D") {
            date.setDate(date.getDate() - 1);
        }
        else if (interval === "1W") {
            date.setDate(date.getDate() - 7);
        }
        else if (interval === "1M") {
            date.setMonth(date.getMonth() - 1);
        }
        else if (interval === "1Y") {
            date.setFullYear(date.getFullYear() - 1);
        }
        setSelectedFromDate(date);
        queryDateMetrics(date, toDate);
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
                            {allPatients.map((pat: any) => <MenuItem key={pat.patient_id} 
                                value={pat.patient_id}>{pat.name}</MenuItem>)}
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
                        <ToggleButton value="C">C</ToggleButton>
                    </ToggleButtonGroup>
                    <div className="padding"></div>
                    <label className="date-label">From:</label>
                    <DatePicker
                        className="date"
                        selected={selectedFromDate}
                        onChange={handleFromDateChange}
                        dateFormat="dd/MM/yyyy"
                    />
                    <div className="padding"></div>
                    <label className="date-label">To:</label>
                    <DatePicker
                        className="date"
                        selected={selectedToDate}
                        onChange={handleToDateChange}
                        dateFormat="dd/MM/yyyy"
                    />
                </div>
                <div className="data">{selectedPatient}</div>

            </div>
        </>
    );
}

export default Patient;
