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
import ReactApexChart from "react-apexcharts";
import {encrypt} from "../../helpers/Crypto";
import MuliPatientGraph from "../NewDashboard/GraphForMultipatients";
import "react-datepicker/dist/react-datepicker.css";
import LineGraphbyMetrics, { transformDataAll} from "../NewDashboard/GraphForMultipatients";
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
    const [minHeight, setMinHeight] = useState("0");
    const [maxHeight, setMaxHeight] = useState("280");
    const [minWeight, setMinWeight] = useState("0");
    const [maxWeight, setMaxWeight] = useState("500");
    const [isPrimary, setIsPrimary] = useState(false);
    const [sex, setSex] = useState("A");
    const [data, setData] = useState<metric[]>(sampleData());
    const [nameMap, setNameMap]: any = useState({});
    const headers = ["Date", "Step Length (km)", "Double Support Time (%)", "Walking Speed (kpm)", "Walking Asymmetry (%)", "Distance Walked (km)", " Step Count (s)"];
    const [stepCountData, setStepCountData]: any = useState(null);
    const [walkingSpeedData, setWalkingSpeedData]: any = useState(null);
    const [stepLengthData, setStepLengthData]: any = useState(null);
    const [doubleSupportData, setDoubleSupportData]: any = useState(null);
    const [asymmetryData, setAssymetryData]: any = useState(null);
    const [distanceWalkedData, setDistanceWalkedData]: any = useState(null);
    const [walkingSteadinessData, setwalkingSteadinessData]: any = useState(null);
    const [colors, setColors]: any = useState("");
    const graphData = { data: data };
    
    // pass the `graphData` object as the props to `LineGraph`
    //
    const handleFromDateChange = (date: Date) => {
        if (date > selectedToDate) {
            return;
        }
        setSelectedFromDate(date);
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
        setIntervals("C");
    };

    const queryDateMetrics = () => {
        let minWeightNumber = minWeight.replace(/[^0-9.]+/g, "").replace(/^(\d*\.\d*)\..*/, "$1");
        let minHeightNumber = minHeight.replace(/[^0-9.]+/g, "").replace(/^(\d*\.\d*)\..*/, "$1");
        let maxWeightNumber =  maxWeight.replace(/[^0-9.]+/g, "").replace(/^(\d*\.\d*)\..*/, "$1");
        let maxHeightNumber =  maxHeight.replace(/[^0-9.]+/g, "").replace(/^(\d*\.\d*)\..*/, "$1");
        if (minHeightNumber === "") {
            minHeightNumber = "0";
        }
        if (minWeightNumber === "") {
            minWeightNumber = "0";
        }
        if (maxHeightNumber === "") {
            maxHeightNumber = "0";
        }
        if (maxWeightNumber === "") {
            maxWeightNumber = "0";
        }
        setMinWeight(minWeightNumber);
        setMinHeight(minHeightNumber);
        setMaxWeight(maxWeightNumber);
        setMaxHeight(maxHeightNumber);

        const selectedPatients: any = [];
        allPatients.forEach((pat: any) => selectedPatients.push(pat.patient_id));
        let end = new Date();
        let start = new Date();
        if (intervals === "all") {
            start.setFullYear(end.getFullYear()-300);
        }
        else {
            start = selectedFromDate;
            start.setDate(selectedFromDate.getDate() - 1);
            end = selectedToDate;
        }
        setStepCountData(null);
        setWalkingSpeedData(null);
        setStepLengthData(null);
        setDoubleSupportData(null);
        setAssymetryData(null);
        setDistanceWalkedData(null);
        setwalkingSteadinessData(null);
        ServiceHandler.queryMetrics(selectedPatients, start.toISOString(), end.toISOString(), Number(minHeightNumber), 
            Number(maxHeightNumber), Number(minWeightNumber), Number(maxWeightNumber), sex)
            .then((data: any) => {
                const allStepCount: any = [];
                const allWalkingSpeed: any = [];
                const allStepLength: any = [];
                const allDoubleSupport: any = [];
                const allAssymetry: any = [];
                const allDistanceWalked: any = [];
                const allWalkingSteadiness: any = [];
                data.metrics.forEach((m: any) => {
                    const currMetric = {
                        "patient_name": nameMap[m.patient_id],
                        "metric_name": m.metric_name,
                        "timestamp": m.timestamp,
                        "metric_value": m.metric_value, 
                        "patient_id": m.patient_id
                    };
                    if(m.metric_name === "step_count") {
                        allStepCount.push(currMetric);
                    }
                    else if (m.metric_name === "walking_speed") {
                        allWalkingSpeed.push(currMetric);

                    }
                    else if (m.metric_name === "step_length"){
                        allStepLength.push(currMetric);
                    }
                    else if (m.metric_name === "double_support_time") {
                        allDoubleSupport.push(currMetric);
                    }
                    else if (m.metric_name === "walking_asymmetry") {
                        allAssymetry.push(currMetric);
                    }
                    else if (m.metric_name === "distance_walked") {
                        allDistanceWalked.push(currMetric);
                    }
                    else if (m.metric_name === "walking_steadiness") {
                        allWalkingSteadiness.push(currMetric);
                    }
                    else {
                        console.log(m.metric_name);
                    }
                });
                setStepCountData(allStepCount);
                setWalkingSpeedData(allWalkingSpeed);
                setStepLengthData(allStepLength);
                setDoubleSupportData(allDoubleSupport);
                setAssymetryData(allAssymetry);
                setDistanceWalkedData(allDistanceWalked);
                setwalkingSteadinessData(allWalkingSteadiness);
            })
            .catch((err) => console.log());
        return;
    };


    const getAllPatients = () => {
        ServiceHandler.getAllPatients();
        let currIsPrimary = false;
        const currNameMap:any = {};
        ServiceHandler.getAllPatients()
            .then((data: any) => {
                const patientArray:any = [];
                const allColors: any = {};
                data.patients.forEach((pat: any) => {
                    if (pat.verified===null || pat.verified===true) {
                        const toAdd = {"patient_id": pat.patient_id, 
                            "name": `${pat.first_name} ${pat.last_name}`,
                            "is_primary": pat.is_primary};
                        if(pat.patient_id === patientId) {
                            if (pat.is_primary) {
                                currIsPrimary = true;
                            }
                        }
                        patientArray.push(toAdd);
                        currNameMap[pat.patient_id] = `${pat.first_name} ${pat.last_name}`;
                        if (!allColors[pat.patient_id]) {
                            allColors[pat.patient_id] = getRandomColor();
                        }
                    }
                });
                setAllPatients(patientArray);
                setIsPrimary(currIsPrimary);
                setNameMap(currNameMap);
                setColors(allColors);
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
        queryDateMetrics();
    }, [nameMap, intervals, selectedToDate, selectedFromDate]);

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

    const getRandomColor = () => {
        const hue = Math.floor(Math.random() * 360);
        const saturation = Math.floor(Math.random() * 100) + 1;
        const lightness = Math.floor(Math.random() * 60) + 20;
      
        return `hsl(${hue}, ${saturation}%, ${lightness}%)`;
    };

    const buildWalkingSpeedChart = (walkingSpeedData: any) => {
        const walkingSpeedSeries = transformDataAll(walkingSpeedData, "walking_speed", colors);
        console.log(walkingSpeedSeries);
        const walkingSpeedOptions = {
            chart: {
                id: "walkingSpeedChart",
                group: "social",
                type: "line",
                height: 300,
                toolbar: {
                    show: false
                }
            },
            title: {
                text: "",
                align: "left",
            },
            yaxis: {
                labels: {
                    minWidth: 40,
                    formatter: function (value: any) {
                        return Math.round((value + Number.EPSILON) * 100) / 100;
                    },
                },
            },
            xaxis: {
                type: "datetime",
                labels: {
                    rotate: -45,
                    rotateAlways: false,
                    format: "MM.dd.yy",
                    offsetX: 90,
                },
                title: {
                    text: "Date",
                },
            },
        };
        const options = {
            ...walkingSpeedOptions,
            colors: walkingSpeedSeries.map(() => getRandomColor()),
            series: walkingSpeedSeries,
        };

        return (
            <div key={"step-count"}>
                <div className="chart-title">Walking Speed</div>
                <ReactApexChart
                    options={options as any}
                    series={walkingSpeedSeries as any}
                    type="line"
                    height={300}
                />
                <div style={{ textAlign: "center", fontSize: "14px", fontWeight: "bold" }}>
                </div>
            </div>
        );

    };

    const buildStepCountChart = (stepCountData: any) => {
        const stepCountSeries = transformDataAll(stepCountData, "step_count", colors);
        const stepCountOptions = {
            chart: {
                id: "stepCountChart",
                group: "social",
                type: "line",
                height: 300,
                toolbar: {
                    show: false
                }
            },
            title: {
                text: "",
                align: "left",
            },
            yaxis: {
                labels: {
                    minWidth: 40,
                    formatter: function (value: any) {
                        return Math.round((value + Number.EPSILON) * 100) / 100;
                    },
                },
            },
            xaxis: {
                type: "datetime",
                labels: {
                    rotate: -45,
                    rotateAlways: false,
                    format: "MM.dd.yy",
                    offsetX: 90,
                },
                title: {
                    text: "Date",
                },
            },
        };
        const options = {
            ...stepCountOptions,
            colors: stepCountSeries.map(() => getRandomColor()),
            series: stepCountSeries,
        };

        return (
            <div key={"walking-speed"}>
                <div className="chart-title">Step Count</div>
                <ReactApexChart
                    options={options as any}
                    series={stepCountSeries as any}
                    type="line"
                    height={300}
                />
                <div style={{ textAlign: "center", fontSize: "14px", fontWeight: "bold" }}>
                </div>
            </div>
        );

    };

    const buildStepLengthChart = (stepLengthData: any) => {
        const stepLengthSeries = transformDataAll(stepLengthData, "step_length", colors);
        const walkingSpeedOptions = {
            chart: {
                id: "stepLengthChart",
                group: "social",
                type: "line",
                height: 300,
                toolbar: {
                    show: false
                }
            },
            title: {
                text: "",
                align: "left",
            },
            yaxis: {
                labels: {
                    minWidth: 40,
                    formatter: function (value: any) {
                        return Math.round((value + Number.EPSILON) * 100) / 100;
                    },
                },
            },
            xaxis: {
                type: "datetime",
                labels: {
                    rotate: -45,
                    rotateAlways: false,
                    format: "MM.dd.yy",
                    offsetX: 90,
                },
                title: {
                    text: "Date",
                },
            },
        };
        const options = {
            ...walkingSpeedOptions,
            colors: stepLengthSeries.map(() => getRandomColor()),
            series: stepLengthSeries,
        };

        return (
            <div key={"step-length"}>
                <div className="chart-title">Step Length</div>
                <ReactApexChart
                    options={options as any}
                    series={stepLengthSeries as any}
                    type="line"
                    height={300}
                />
                <div style={{ textAlign: "center", fontSize: "14px", fontWeight: "bold" }}>
                </div>
            </div>
        );

    };

    const buildDoubleSupportChart = (doubleSupportData: any) => {
        const doubleSupportSeries = transformDataAll(doubleSupportData, "double_support_time", colors);
        const doubleSupportOptions = {
            chart: {
                id: "doubleSupportChart",
                group: "social",
                type: "line",
                height: 300,
                toolbar: {
                    show: false
                }
            },
            title: {
                text: "",
                align: "left",
            },
            yaxis: {
                labels: {
                    minWidth: 40,
                    formatter: function (value: any) {
                        return Math.round((value + Number.EPSILON) * 100) / 100;
                    },
                },
            },
            xaxis: {
                type: "datetime",
                labels: {
                    rotate: -45,
                    rotateAlways: false,
                    format: "MM.dd.yy",
                    offsetX: 90,
                },
                title: {
                    text: "Date",
                },
            },
        };
        const options = {
            ...doubleSupportOptions,
            colors: doubleSupportSeries.map(() => getRandomColor()),
            series: doubleSupportSeries,
        };

        return (
            <div key={"double-support"}>
                <div className="chart-title">Double Support Time</div>
                <ReactApexChart
                    options={options as any}
                    series={doubleSupportSeries as any}
                    type="line"
                    height={300}
                />
                <div style={{ textAlign: "center", fontSize: "14px", fontWeight: "bold" }}>
                </div>
            </div>
        );

    };

    const buildAssymetryChart = (asymmetryData: any) => {
        const asymmetrySeries = transformDataAll(asymmetryData, "walking_asymmetry", colors);
        const walkingSpeedOptions = {
            chart: {
                id: "asymmetryChart",
                group: "social",
                type: "line",
                height: 300,
                toolbar: {
                    show: false
                }
            },
            title: {
                text: "",
                align: "left",
            },
            yaxis: {
                labels: {
                    minWidth: 40,
                    formatter: function (value: any) {
                        return Math.round((value + Number.EPSILON) * 100) / 100;
                    },
                },
            },
            xaxis: {
                type: "datetime",
                labels: {
                    rotate: -45,
                    rotateAlways: false,
                    format: "MM.dd.yy",
                    offsetX: 90,
                },
                title: {
                    text: "Date",
                },
            },
        };
        const options = {
            ...walkingSpeedOptions,
            colors: asymmetrySeries.map(() => getRandomColor()),
            series: asymmetrySeries,
        };

        return (
            <div key={"asymmetry"}>
                <div className="chart-title">Walking Assymetry</div>
                <ReactApexChart
                    options={options as any}
                    series={asymmetrySeries as any}
                    type="line"
                    height={300}
                />
                <div style={{ textAlign: "center", fontSize: "14px", fontWeight: "bold" }}>
                </div>
            </div>
        );

    };

    const buildDistanceWalkedChart = (distanceWalked: any) => {
        const distanceWalkedSeries = transformDataAll(distanceWalked, "distance_walked", colors);
        const distanceWalkedOptions = {
            chart: {
                id: "distanceWalkedChart",
                group: "social",
                type: "line",
                height: 300,
                toolbar: {
                    show: false
                }
            },
            title: {
                text: "",
                align: "left",
            },
            yaxis: {
                labels: {
                    minWidth: 40,
                    formatter: function (value: any) {
                        return Math.round((value + Number.EPSILON) * 100) / 100;
                    },
                },
            },
            xaxis: {
                type: "datetime",
                labels: {
                    rotate: -45,
                    rotateAlways: false,
                    format: "MM.dd.yy",
                    offsetX: 90,
                },
                title: {
                    text: "Date",
                },
            },
        };
        const options = {
            ...distanceWalkedOptions,
            colors: distanceWalkedSeries.map(() => getRandomColor()),
            series: distanceWalkedSeries,
        };

        return (
            <div key={"distance"}>
                <div className="chart-title">Distance Walked</div>
                <ReactApexChart
                    options={options as any}
                    series={distanceWalkedSeries as any}
                    type="line"
                    height={300}
                />
                <div style={{ textAlign: "center", fontSize: "14px", fontWeight: "bold" }}>
                </div>
            </div>
        );

    };

    const buildWalkingSteadinessChart = (walkingSteadinessData: any) => {
        const walkingSteadinessSeries = transformDataAll(walkingSteadinessData, "walking_steadiness", colors);
        const distanceWalkedOptions = {
            chart: {
                id: "walkingSteadinessChart",
                group: "social",
                type: "line",
                height: 300,
                toolbar: {
                    show: false
                }
            },
            title: {
                text: "",
                align: "left",
            },
            yaxis: {
                labels: {
                    minWidth: 40,
                    formatter: function (value: any) {
                        return Math.round((value + Number.EPSILON) * 100) / 100;
                    },
                },
            },
            xaxis: {
                type: "datetime",
                labels: {
                    rotate: -45,
                    rotateAlways: false,
                    format: "MM.dd.yy",
                    offsetX: 90,
                },
                title: {
                    text: "Date",
                },
            },
        };
        const options = {
            ...distanceWalkedOptions,
            colors: walkingSteadinessSeries.map(() => getRandomColor()),
            series: walkingSteadinessSeries,
        };

        return (
            <div key={"distance"}>
                <div className="chart-title">Walking Steadiness</div>
                <ReactApexChart
                    options={options as any}
                    series={walkingSteadinessSeries as any}
                    type="line"
                    height={300}
                />
                <div style={{ textAlign: "center", fontSize: "14px", fontWeight: "bold" }}>
                </div>
            </div>
        );

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
                                    onChange={(e) => setMinHeight(e.target.value)}
                                    style = {{width: 130}}
                                ></TextField>
                                <TextField className="filter-text"
                                    size="small" 
                                    label="Max Height (cm)"
                                    value={maxHeight}
                                    onChange={(e) => setMaxHeight(e.target.value)}
                                    style = {{width: 130}}
                                ></TextField>
                            </div>
                            <div className="padding"></div>
                            <div>
                                <TextField className="filter-text"
                                    size="small" 
                                    label="Min Weight (kg)"
                                    value={minWeight}
                                    onChange={(e) => setMinWeight(e.target.value)}
                                    style = {{width: 130}}
                                ></TextField>
                                <TextField className="filter-text"
                                    size="small" 
                                    label="Max Weight (kg)"
                                    value={maxWeight}
                                    onChange={(e) => setMaxWeight(e.target.value)}
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
                                    <MenuItem value={"A"}>All</MenuItem>
                                    <MenuItem value={"M"}>Male</MenuItem>
                                    <MenuItem value={"F"}>Female</MenuItem>
                                    <MenuItem value={"O"}>Other</MenuItem>
                                </Select>
                            </FormControl>
                            <div className="padding"></div>
                            <button className="share" onClick={() => queryDateMetrics()}>Filter Metrics</button>
                            
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
                    <div className="padding"></div>
                    <div id="step-count">
                        {stepCountData !== null? <>
                            {buildStepCountChart(stepCountData)}
                            <div className="padding"></div>
                            <Divider/>
                        </>:null}
                    </div>
                    <div id="walk-speed">
                        <div className="padding"></div>
                        {walkingSpeedData !== null? <>{buildWalkingSpeedChart(walkingSpeedData)}
                            <div className="padding"></div>
                            <Divider/>
                        </>:null}
                    </div>
                    <div id="step-length">
                        <div className="padding"></div>
                        {stepLengthData !== null? <>{buildStepLengthChart(stepLengthData)}
                            <div className="padding"></div>
                            <Divider/>
                        </>:null}
                    </div>
                    <div id="double">
                        <div className="padding"></div>
                        {doubleSupportData !== null? <>{buildDoubleSupportChart(doubleSupportData)}
                            <div className="padding"></div>
                            <Divider/>
                        </>:null}
                    </div>
                    <div className="padding"></div>
                    <div id="asy">
                        {asymmetryData !== null?<> {buildAssymetryChart(asymmetryData)}
                            <div className="padding"></div>
                            <Divider/>
                        </>:null}
                    </div>
                    <div className="padding"></div>
                    <div id="dist">
                        {distanceWalkedData !== null?<> {buildDistanceWalkedChart(distanceWalkedData)}
                            <div className="padding"></div>
                            <Divider/>
                        </>:null}
                    </div>
                    <div className="padding"></div>
                    <div id="dist">
                        {walkingSteadinessData !== null?<> {buildWalkingSteadinessChart(walkingSteadinessData)}
                            <div className="padding"></div>
                            <Divider/>
                        </>:null}
                    </div>
                </div>

            </div>
        </>
    );
}

export default AllPatients;
