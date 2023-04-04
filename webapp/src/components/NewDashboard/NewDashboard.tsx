import React, { useState, useEffect } from "react";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import "./NewDashboard.css";

import sampleData from "./sampleData";
//import Barchart from "./Barchart";
import LineGraph from "./Linegraph";


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

// This interface is only used for data filtering
interface metric {
    patient_name: string;
    metric_name: string;
    metric_value: string;
    timestamp: string;
  }

function NewDashboard(){

    const [startDate, setStartDate] = useState<Date | null>(null);
    const [endDate, setEndDate] = useState<Date | null>(null);
    const [data, setData] = useState<metric[]>(sampleData());
    

    // Create a new instance of PatientsList
    const patientsList: PatientsList = {
        patients: data.map(({ patient_name }) => {
            const [first_name, last_name] = patient_name.split("-");
            return {
                patient_id: patient_name,
                first_name,
                last_name
            };
        })
    };
  
    // Create a new instance of MetricsData
    const metricsData: MetricsData = {
        metrics: data.map(({ patient_name, metric_name, metric_value, timestamp }) => {
            return {
                patient_id: patient_name,
                metric_name,
                metric_value,
                timestamp
            };
        })
    };
    
    // objects for constructing tables
    const patientNames = Array.from(new Set(data.map((d) => d.patient_name)));
    const [expandedTables, setExpandedTables] = useState(
        Object.fromEntries(patientNames.map((patientName) => [patientName, false]))
    );
    
    const toggleTable = (patientName: string) => {
        setExpandedTables((prevExpandedTables) => ({
            ...prevExpandedTables,
            [patientName]: !prevExpandedTables[patientName],
        }));
    };

    return (
        <div className="Dashboard">
            <h2>Dashboard</h2>

            <button 
                style={{marginLeft: "800px", color: "white", backgroundColor: "black", fontSize: "20px"}} onClick={() => handleExport(data)}>
                    Export to CSV
            </button>

            <div className="datepicker" style={{ marginTop: "20px", marginLeft : "100px", marginBottom : "40px", }} >
                <div>
                    <label htmlFor="startDatePicker" style={{textAlign: "center", marginLeft : "60px"}} >Start</label>
                    <DatePicker
                        id="startDatePicker"
                        selected={startDate}
                        onChange={(date: Date | null) => setStartDate(date)}
                        dateFormat="yyyy-MM-dd"
                        maxDate={endDate}
                    />
                </div>
                <div style={{marginTop: "20px"}}>
                    <label htmlFor="endDatePicker" style={{textAlign: "center", marginLeft : "60px"}}>End</label>
                    <DatePicker
                        id="endDatePicker"
                        selected={endDate}
                        onChange={(date: Date | null) => setEndDate(date)}
                        dateFormat="yyyy-MM-dd"
                        minDate={startDate}
                    />
                </div>
            </div>

            <div className="linegraphs"  style={{ width: "100%", height: "70%" }}>
                <LineGraph />
            </div>

            <div className="Instructions" style={{textAlign: "center", fontSize: "30px", marginTop: "50px", marginBottom: "30px" }}>
                Click pateint name to view full metrics
            </div>
            <div className="table">
                <div style={{ textAlign: "center", maxWidth: "1000px", margin: "0 auto" }}>
                    {patientNames.map((patientName) => (
                        <div
                            key={patientName}
                            style={{
                                marginBottom: "120px",
                                margin: "0 auto",
                                maxWidth: "1000px",
                            }}
                        >
                            <h3 style={{ cursor: "pointer" }} onClick={() => toggleTable(patientName)}>
                                {patientName}
                            </h3>
                            {expandedTables[patientName] && (
                                <table
                                    style={{
                                        width: "100%",
                                        borderCollapse: "collapse",
                                        marginLeft: "-20px",
                                        marginBottom: "70px",
                                        fontSize: "0.7em",
                                    }}
                                >
                                    <thead style={{ borderBottom: "1px solid black" }}>
                                        <tr>
                                            <th style={{ padding: "10px", minWidth: "200px" }}>Timestamp</th>
                                            <th style={{ padding: "10px", minWidth: "200px" }}>Metric Name</th>
                                            <th style={{ padding: "10px", minWidth: "200px" }}>Metric Value</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {data
                                            .filter((d) => d.patient_name === patientName)
                                            .map((d, index) => (
                                                <tr
                                                    key={index}
                                                    style={{ borderBottom: "1px solid lightgray" }}
                                                >
                                                    <td style={{ padding: "10px" }}>
                                                        {new Date(d.timestamp).toLocaleDateString()}
                                                    </td>
                                                    <td style={{ padding: "10px" }}>{d.metric_name}</td>
                                                    <td style={{ padding: "10px" }}>{d.metric_value}</td>
                                                </tr>
                                            ))}
                                    </tbody>
                                </table>
                            )}
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}

export default NewDashboard;

// handler of exporting data to csv
function handleExport(data: any[]) {
    // Create the CSV string
    let csv = "data:text/csv;charset=utf-8,";
  
    // Add the header row
    csv += "Patient Name,Metric Name,Metric Value,Timestamp\n";
  
    // Add each data row
    data.forEach((row) => {
        csv += `${row.patient_name},${row.metric_name},${row.metric_value},${row.timestamp}\n`;
    });
  
    // Create a temporary link to download the CSV file
    const link = document.createElement("a");
    link.setAttribute("href", encodeURI(csv));
    link.setAttribute("download", "patient_metrics.csv");
    link.click();
}

/*
            <div className="barcharts"  style={{ width: "80%", height: "50%" }}>
                <Barchart data={metricsData} patients={patientsList} />
            </div>
*/ 

/*
function NewDashboard() {
    // Sidebar props
    const [selectedPatient, setSelectedPatient] = useState("All Patients");
    const [startDate, setStartDate] = useState<Date | null>(null);
    const [endDate, setEndDate] = useState<Date | null>(null);

    const [patientsList, setPatientsList] = useState<PatientsList>({ patients: [] });
    const [filteredPatients, setFilteredPatients] = useState<PatientsList>({ patients: [] });
    const [metricsData, setMetricsData] = useState<MetricsData>({ metrics: [] });

    useEffect(() => {
    // call api to get patients list and filter it for query
        fetch("https://example.com/patients")
            .then((response) => response.json())
            .then((data) => {
                setPatientsList(data);
                setFilteredPatients(
                    selectedPatient === "All Patients"
                        ? data
                        : { patients: data.patients.filter((p:Patient) => p.first_name + " " + p.last_name === selectedPatient) }
                );
            })
            .catch((error) => console.error(error));
    }, [selectedPatient]);

    useEffect(() => {
    // reformat the start and end dates to pass them to api query
        const formattedStartDate =
      startDate &&
      `${startDate.getFullYear()}_${(startDate.getMonth() + 1).toString().padStart(2, "0")}_${startDate
          .getDate()
          .toString()
          .padStart(2, "0")}T00:00:00`;
        const formattedEndDate =
      endDate &&
      `${endDate.getFullYear()}_${(endDate.getMonth() + 1).toString().padStart(2, "0")}_${endDate.getDate().toString().padStart(2, "0")}T23:59:59`;

        // construct API query to fetch metrics
        const patientIds = filteredPatients.patients.map((p: Patient) => p.patient_id).join("&patients=");
        const url = `https://example.com/metrics?patients=${patientIds}&start=${formattedStartDate}&end=${formattedEndDate}`;

        fetch(url)
            .then((response) => response.json())
            .then((data) => {
                setMetricsData(data as MetricsData);
            })
            .catch((error) => console.error(error));
    }, [filteredPatients, startDate, endDate]);

    // Render graph using the metricsData
    const graph = Graph({patientsList, metricsData});

    return (
        <div className="Dashboard">
            <h2>Dashboard</h2>
            <div className="sidebar">
                <select value={selectedPatient} onChange={(e) => setSelectedPatient(e.target.value)}>
                    <option value="All Patients">All Patients</option>
                    {patientsList.patients.map((patient: Patient) => (
                        <option key={patient.patient_id} value={patient.first_name + " " + patient.last_name}>
                            {patient.first_name + " " + patient.last_name}
                        </option>
                    ))}
                </select>
                <div>
                    <label htmlFor="startDatePicker">Start</label>
                    <DatePicker
                        id="startDatePicker"
                        selected={startDate}
                        onChange={(date: Date | null) => setStartDate(date)}
                        dateFormat="yyyy-MM-dd"
                        maxDate={endDate}
                    />
                </div>
                <div>
                    <label htmlFor="endDatePicker">End</label>
                    <DatePicker
                        id="endDatePicker"
                        selected={endDate}
                        onChange={(date: Date | null) => setEndDate(date)}
                        dateFormat="yyyy-MM-dd"
                        minDate={startDate}
                    />
                </div>
            </div>
  
            <div className="graphs">
                {graph}
            </div>
        </div>
    );
  
}
*/