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
import {ToggleButton, ToggleButtonGroup, TextField} from "@mui/material";
import DatePicker, { ReactDatePickerProps } from "react-datepicker";
import sampleData from "../NewDashboard/sampleData";
import {encrypt} from "../../helpers/Crypto";
import MuliPatientGraph from "../NewDashboard/GraphForMultipatients";
import "react-datepicker/dist/react-datepicker.css";
import "./Patient.css";

export interface Patient {
  patient_id: string;
  first_name: string;
  last_name: string;
}

export interface PatientsList {
  patients: Patient[];
}

export interface Metric {
  patient_id: string;
  metric_name: string;
  metric_value: string;
  timestamp: string;
}

export interface MetricsData {
  metrics: Metric[];
}

export interface metric {
    patient_name: string;
    metric_name: string;
    metric_value: string;
    timestamp: string;
  }



function AllPatients() {
    const nav = useNavigate();
    const [patientDetails, setPatientDetails]: any = useState({});
    const {patientIdEncrypt} = useParams();
    const [deletionMessage, setDeletionMessage] = useState("");
    const [intervals, setIntervals] = useState("all");
    const patientId = decrypt(patientIdEncrypt);
    const [selectedPatient, setSelectedPatient]: any = useState("all");
    const [allPatients, setAllPatients] = useState([]);
    const [endDate, setEndDate] = useState(moment());
    const [selectedFromDate, setSelectedFromDate] = useState<Date>(new Date());
    const [selectedToDate, setSelectedToDate] = useState<Date>(new Date());
    const [minHeight, setMinHeight] = useState(0);
    const [maxHeight, setMaxHeight] = useState(280);
    const [minWeight, setMinWeight] = useState(0);
    const [maxWeight, setMaxWeight] = useState(500);
    const [isPrimary, setIsPrimary] = useState(false);
    const [sex, setSex] = useState("a");
    const [data, setData] = useState<metric[]>(sampleData());
    const headers = ["Date", "Step Length (km)", "Double Support Time (%)", "Walking Speed (kpm)", "Walking Asymmetry (%)", "Distance Walked (km)", " Step Count (s)"];
    const graphData = { data: data };
    
    // pass the `graphData` object as the props to `LineGraph`
    

    const handleFromDateChange = (date: Date) => {
        if (date > selectedToDate) {
            return;
        }
        setSelectedFromDate(date);
        queryDateMetrics("C", date, selectedToDate);
        setIntervals("C");
    };

    const handleSexUpdate =  (e: any) => {
        const updatedSex = e.target.value;
        setSex(updatedSex);
    };

    const handleToDateChange = (date: Date) => {
        if (date < selectedFromDate) {
            return;
        }
        setSelectedToDate(date);
        queryDateMetrics("C", selectedFromDate, date);
        setIntervals("C");
    };

    const handleDelete = () => {
        ServiceHandler.deletePatient(patientId)
            .then((data) => {
                alert("Patient unlinked");
                nav("/dashboard");
            });
    };

    const queryDateMetrics = (interval:string, startDate: Date, endDate: Date) => {
        const selectedPatients: any = [];
        if (selectedPatient === "all") {
            allPatients.forEach((pat: any) => selectedPatients.push(pat.patient_id));
        }
        else {
            selectedPatients.push(selectedPatient);
        }
        if (interval === "all") {
            console.log("hello");
            return;
        }
        ServiceHandler.queryMetrics(selectedPatients, startDate.toISOString(), endDate.toISOString())
            .then((data) => console.log(data))
            .catch((err) => console.log());
        return;
    };


    const getAllPatients = () => {
        ServiceHandler.getAllPatients();
        let currPatientId = "all";
        let currIsPrimary = false;
        ServiceHandler.getAllPatients()
            .then((data: any) => {
                const patientArray:any = [];
                data.patients.forEach((pat: any) => {
                    if (pat.verified===null || pat.verified===true) {
                        const toAdd = {"patient_id": pat.patient_id, 
                            "name": `${pat.first_name} ${pat.last_name}`,
                            "is_primary": pat.is_primary};
                        if(pat.patient_id === patientId) {
                            currPatientId = patientId;
                            if (pat.is_primary) {
                                currIsPrimary = true;
                            }
                        }
                        patientArray.push(toAdd);
                    }
                });
                setAllPatients(patientArray);
                setIsPrimary(currIsPrimary);
            })
            .catch((err: any) => console.log(err));
    };

    useEffect(() => {
        const date = new Date();
        date.setDate(date.getDate() - 7);
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
        queryDateMetrics(intervals, selectedFromDate, selectedToDate);
    }, [selectedPatient]);

    const handleIntervals = (e: any) => {
        const interval = e.target.value;
        setIntervals(interval);
        const toDate = new Date();
        setSelectedToDate(toDate);
        const date = new Date();
        if (interval === "1W") {
            date.setDate(date.getDate() - 7);
        }
        else if (interval === "1M") {
            date.setMonth(date.getMonth() - 1);
        }
        else if (interval === "1Y") {
            date.setFullYear(date.getFullYear() - 1);
        }
        setSelectedFromDate(date);
        queryDateMetrics(interval, date, toDate);
    };

    const handlePatientChange = (e: any) => {
        if (e.target.value === "all") {
            const url = "/dashboard/allPatients";
            nav(url);
        }
        else {
            const url = "/dashboard/patient/".concat(encrypt(e.target.value));
            nav(url);
            window.location.reload();

        }
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
                            <MenuItem value={"all"}>All Patients</MenuItem>
                            {allPatients.map((pat: any) => <MenuItem key={pat.patient_id} 
                                value={pat.patient_id}>{pat.name}</MenuItem>)}
                        </Select>
                    </FormControl>
                    {selectedPatient === "all"?
                        <>
                            <div className="padding"></div>
                            <div>
                                <TextField className="filter-text"
                                    size="small" 
                                    label="Min Height (cm)"
                                    value={minHeight}
                                    style = {{width: 130}}
                                ></TextField>
                                <TextField className="filter-text"
                                    size="small" 
                                    label="Max Height (cm)"
                                    value={maxHeight}
                                    style = {{width: 130}}
                                ></TextField>
                            </div>
                            <div className="padding"></div>
                            <div>
                                <TextField className="filter-text"
                                    size="small" 
                                    label="Min Weight (kg)"
                                    value={minWeight}
                                    style = {{width: 130}}
                                ></TextField>
                                <TextField className="filter-text"
                                    size="small" 
                                    label="Max Weight (kg)"
                                    value={maxWeight}
                                    style = {{width: 130}}
                                ></TextField>
                            </div>
                            <div className="padding"></div>
                            <FormControl>
                                <InputLabel id="sex-label">Sex</InputLabel>
                                <Select className="sex"
                                    labelId="sex-label"
                                    id="sex-select"
                                    value={sex}
                                    label="Patient"
                                    onChange={handleSexUpdate}
                                >
                                    <MenuItem value={"a"}>All</MenuItem>
                                    <MenuItem value={"m"}>Male</MenuItem>
                                    <MenuItem value={"f"}>Female</MenuItem>
                                    <MenuItem value={"o"}>Other</MenuItem>
                                </Select>
                            </FormControl>
                        </>:null}
                    <div className="padding"></div>
                    <Divider/>
                    <div className="padding"></div>
                    <label className="time-label">Timespan For Metrics:</label>
                    <div className="padding2"></div>
                    <ToggleButtonGroup
                        color="primary"
                        value={intervals}
                        exclusive
                        onChange={handleIntervals}
                        aria-label="Platform"
                    >
                        <ToggleButton value="all">All Time</ToggleButton>
                        <ToggleButton value="1W">1W</ToggleButton>
                        <ToggleButton value="1M">1M</ToggleButton>
                        <ToggleButton value="1Y">1Y</ToggleButton>
                        <ToggleButton value="C">Custom</ToggleButton>
                    </ToggleButtonGroup>
                    <div className="padding"></div>
                    {intervals !== "all"?
                        <>
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
                        </>:null}
                    <div className="padding"></div>
                    <Divider/>
                    <div className="padding"></div>
                </div>
                <div className="data">
                    {selectedPatient !== "all"? <>
                        <button className="unlink" type='submit' onClick={handleDelete}>Unlink Patient</button>
                        <button className="share" onClick={(e) => nav("share")} type='submit'>Share Patient</button>
                    </>:null}
                    <div className="padding"></div>
                    {MuliPatientGraph(graphData)}
                </div>

            </div>
        </>
    );
}

export default AllPatients;
