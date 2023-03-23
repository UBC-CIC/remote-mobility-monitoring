import React, { useState } from 'react';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import Graph from './Graph';

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
  

function NewDashboard() {

    // Sidebar props
    const [selectedPatient, setSelectedPatient] = useState('All Patients');
    const [startDate, setStartDate] = useState<Date | null>(null);
    const [endDate, setEndDate] = useState<Date | null>(null);

    let patientsList: PatientsList = { patients: [] };
    let filteredPatients: PatientsList = { patients: [] };
    
    // call api to get patients list and filter it for query
    fetch('https://example.com/patients')
      .then(response => response.json())
      .then(data => {
        patientsList = data;
        filteredPatients = selectedPatient === 'All Patients'
          ? patientsList
          : { patients: patientsList.patients.filter(p => p.first_name + ' ' + p.last_name === selectedPatient) };
      })
      .catch(error => console.error(error));
    
    // reformat the start and end dates to pass them to api query
    const formattedStartDate = `${startDate.getFullYear()}_${(startDate.getMonth() + 1).toString().padStart(2, '0')}_${startDate.getDate().toString().padStart(2, '0')}T00:00:00`;
    const formattedEndDate = `${endDate.getFullYear()}_${(endDate.getMonth() + 1).toString().padStart(2, '0')}_${endDate.getDate().toString().padStart(2, '0')}T23:59:59`;

    // construct API query to fetch metrics
    const patientIds = filteredPatients.patients.map(p => p.patient_id).join('&patients=');
    const url = `https://example.com/metrics?patients=${patientIds}&start=${formattedStartDate}&end=${formattedEndDate}`;
    
    // create metricsdata
    let metricsData: MetricsData = {metrics: []}

    fetch(url)
      .then(response => response.json())
      .then(data => {
        metricsData = data as MetricsData;
      })
      .catch(error => console.error(error));

    // Render graph using the metricsData
    // let graph = Graph({patientsList, metricsData});

    return (

    <div className = "Dashboard">
      <h2>Dashboard</h2>
      <div className="sidebar">
        <select value={selectedPatient} onChange={(e: { target: { value: any; }; }) => setSelectedPatient(e.target.value)}>
          <option value="All Patients">All Patients</option>
          {patientsList.patients.map((patient: Patient) => (
            <option key = {patient.first_name + ' ' + patient.last_name} value = {patient.first_name + ' ' + patient.last_name}>
              {patient.first_name + ' ' + patient.last_name}
            </option>
          ))}
        </select>
        <div>
          <label htmlFor="startDatePicker">Start</label>
          <DatePicker
            id="startDatePicker"
            selected={startDate}
            onChange={(date: Date) => setStartDate(date as Date)}
            dateFormat="yyyy-MM-dd"
            maxDate={endDate}
          />
        </div>
        <div>
          <label htmlFor="endDatePicker">End</label>
          <DatePicker
            id="endDatePicker"
            selected={endDate}
            onChange={(date: Date) => setEndDate(date as Date)}
            dateFormat="yyyy-MM-dd"
            minDate={startDate}
          />
        </div>
      </div>

      <div className = "graphs">
        {graph}
      </div>

    </div>
    );
}

export default NewDashboard;
