import React, { useState } from 'react';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';

export interface Patient {
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

function Sidebar({ patients }: SidebarProps) {
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
    );
  }
  

export default Sidebar;
