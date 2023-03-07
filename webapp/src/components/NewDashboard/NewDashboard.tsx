import React, { useState } from "react";
//import Sidebar, { Patient } from "./Sidebar";
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import sampleData from "./sampleData";

/*
 if all patients or no patient is selected,  the dashboard should display one graph per mobility metric per patient. In the end there is a summary table that contains all that patient's mobility data.
I want to display the data in the following format:
1 one chart/graph for each mobility metric that describes the change of metric with respect to time.
2 each patient name will be followed by 5 charts/graphs plus a table that summarize all his data
3 distinguish different patients sections
 */
interface Patient {
    name: string;
    gender: string;
    age: number;
    mobilityData: {
      date: string;
      stepLength: number;
      doubleSupportTime: number;
      walkingSpeed: number;
      walkingAsymmetry: number;
      distanceWalked: number;
    }[];
  }
  
interface SidebarProps {
    patients: Patient[];
  }

interface PatientListProps {
    patients: Patient[];
    filteredMobilityData: {
      date: string;
      stepLength: number;
      doubleSupportTime: number;
      walkingSpeed: number;
      walkingAsymmetry: number;
      distanceWalked: number;
    }[];
  }

function NewDashboard({ patients }: SidebarProps) {
    const [selectedPatient, setSelectedPatient] = useState('All Patients');
    const [startDate, setStartDate] = useState<Date | null>(null);
    const [endDate, setEndDate] = useState<Date | null>(null);
  
    const filteredPatients = selectedPatient === 'All Patients' ? patients : [patients.find(p => p.name === selectedPatient)].filter(Boolean);
  
    const filteredMobilityData = filteredPatients.flatMap(p => (p?.mobilityData ?? []).filter(d => {
        const date = new Date(d.date);
        const previousDate = startDate ? new Date(startDate) : new Date(0);
        previousDate.setDate(previousDate.getDate() - 1);
        return (!startDate || date >= previousDate) && (!endDate || date <= endDate);
      }));
    
      return (
        <div className="dashboard">
          <div className="sidebar">
            <h3>Patients</h3>
            <select value={selectedPatient} onChange={e => setSelectedPatient(e.target.value)}>
              <option value="All Patients">All Patients</option>
              {patients.map(patient => (
                <option key={patient.name} value={patient.name}>
                  {patient.name}
                </option>
              ))}
            </select>
            <div>
              <label htmlFor="startDatePicker">Start</label>
              <DatePicker
                id="startDatePicker"
                selected={startDate}
                onChange={date => setStartDate(date as Date)}
                dateFormat="yyyy-MM-dd"
                maxDate={endDate}
              />
            </div>
            <div>
              <label htmlFor="endDatePicker">End</label>
              <DatePicker
                id="endDatePicker"
                selected={endDate}
                onChange={date => setEndDate(date as Date)}
                dateFormat="yyyy-MM-dd"
                minDate={startDate}
              />
            </div>
          </div>
            <div className="patient-list">
              <h3>Patient List</h3>
              <ul>
                {filteredMobilityData.map(data => {
                  const patient = patients.find(p => p.mobilityData.includes(data));
                  return (
                    <li key={data.date}>
                      <h4>{patient?.name ?? 'Unknown Patient'}</h4>
                      <p>Date: {data.date}</p>
                      <p>Step Length: {data.stepLength}</p>
                      <p>Double Support Time: {data.doubleSupportTime}</p>
                      <p>Walking Speed: {data.walkingSpeed}</p>
                      <p>Walking Asymmetry: {data.walkingAsymmetry}</p>
                      <p>Distance Walked: {data.distanceWalked}</p>
                    </li>
                  );
                })}
              </ul>
            </div>
        </div>
      );
      
}

export default NewDashboard;
