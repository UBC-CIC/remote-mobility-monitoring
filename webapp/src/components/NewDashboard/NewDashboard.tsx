import React, { useState, useEffect } from "react";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import Graph from "./Graph";
import "./NewDashboard.css";
import "./sampleData";
import sampleData from "./sampleData";

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

function NewDashboard(){
    const  data =  sampleData();
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

    return (
        <Graph data={metricsData} patients={patientsList} />
    );


}




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
export default NewDashboard;  
