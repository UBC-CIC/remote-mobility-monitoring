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
export interface metric {
    patient_name: string;
    metric_name: string;
    metric_value: string;
    timestamp: string;
  }


function NewDashboard(){

    const [data, setData] = useState<metric[]>(sampleData());

    const headers = ["Date", "Step Length (km)", "Double Support Time (%)", "Walking Speed (kpm)", "Walking Asymmetry (%)", "Distance Walked (km)", " Step Count (s)"];

    const getMetricValue = (metricName: string, timestamp: string): string => {
        const metricData = data.find(
            (metric) => metric.metric_name === metricName && metric.timestamp === timestamp
        );
        return metricData ? metricData.metric_value : "-";
    };

    const formatDate = (timestamp: string): string => {
        const date = new Date(timestamp);
        return date.toLocaleDateString();
    };

    const uniqueTimestamps: string[] = [];

    // create an object with a `data` property
    const graphData = { data: data };
    
    // pass the `graphData` object as the props to `LineGraph`
    const [graph, setGraph] = useState<any>(LineGraph(graphData));
    

    return (
        <div className="Dashboard">
            <h2>Dashboard</h2>
            <h3>{data[0].patient_name}</h3>

            <div className="linegraphs"  style={{ width: "90%", height: "150%", marginBottom: "40px" }}>
                {graph}
            </div>

            <table style={{ margin: "0 auto", width: "80%" }}>
                <thead>
                    <tr>
                        {headers.map((header) => (
                            <th key={header}>{header}</th>
                        ))}
                    </tr>
                </thead>
                <tbody>
                    {data.map((metric) => {
                        const timestamp = metric.timestamp;
                        if (uniqueTimestamps.includes(timestamp)) {
                            return null; // skip this row
                        } else {
                            uniqueTimestamps.push(timestamp);
                            return (
                                <React.Fragment key={timestamp}>
                                    <tr>
                                        <td>{formatDate(timestamp)}</td>
                                        <td>{getMetricValue("stepLength", timestamp)}</td>
                                        <td>{getMetricValue("doubleSupportTime", timestamp)}</td>
                                        <td>{getMetricValue("walkingSpeed", timestamp)}</td>
                                        <td>{getMetricValue("walkingAsymmetry", timestamp)}</td>
                                        <td>{getMetricValue("distanceWalked", timestamp)}</td>
                                        <td>{getMetricValue("stepCount", timestamp)}</td>
                                    </tr>
                                    <tr style={{ backgroundColor: "#eee" }}>
                                        <td colSpan={7} style={{ height: "1px" }}></td>
                                    </tr>
                                </React.Fragment>
                            );
                        }
                    })}
                </tbody>
            </table>

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


            <button 
                style={{marginLeft: "800px", color: "white", backgroundColor: "black", fontSize: "20px"}} onClick={() => handleExport(data)}>
                    Export to CSV
            </button>
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