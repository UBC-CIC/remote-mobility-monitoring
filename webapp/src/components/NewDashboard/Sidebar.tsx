import React, { useState } from "react";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";

interface MobilityData  {
  date: string;
  stepLength: number;
  doubleSupportTime: number;
  walkingSpeed: number;
  walkingAsymmetry: number;
  distanceWalked: number;
}

interface Patient  {
  name: string;
  gender: string;
  age: number;
  mobilityData: MobilityData[];
}

interface Props  {
  patients: Patient[];
  onPatientChange: (patient: Patient) => void;
  onDateRangeChange: (startDate: Date, endDate: Date) => void;
}

export default function Sidebar({ patients, onPatientChange, onDateRangeChange }: Props) {
    const [selectedPatient, setSelectedPatient] = useState<Patient | undefined>();
    const [selectedStartDate, setSelectedStartDate] = useState<Date>(new Date());
    const [selectedEndDate, setSelectedEndDate] = useState<Date>(new Date());

    const handlePatientChange = (patient: Patient) => {
        setSelectedPatient(patient);
        onPatientChange(patient);
    };

    const handleDateRangeChange = (startDate: Date, endDate: Date) => {
        setSelectedStartDate(startDate);
        setSelectedEndDate(endDate);
        onDateRangeChange(startDate, endDate);
    };

    const handleShortcutClick = (days: number) => {
        const today = new Date();
        const startDate = new Date(today.getTime() - days * 24 * 60 * 60 * 1000);
        handleDateRangeChange(startDate, today);
    };

    const filteredPatients = selectedPatient ? [selectedPatient] : patients;

    return (
        <div className="sidebar">
            <div className="sidebar-item">
                <label htmlFor="patient-select">Select Patient</label>
                <select id="patient-select" value={selectedPatient?.name} onChange={(e) => handlePatientChange(patients.find(patient => patient.name === e.target.value) || undefined)}>
                    <option value="">All Patients</option>
                    {patients.map(patient => <option key={patient.name} value={patient.name}>{patient.name}</option>)}
                </select>
            </div>
            <div className="sidebar-item">
                <label htmlFor="date-picker">Select Date Range</label>
                <DatePicker
                    selected={selectedStartDate}
                    onChange={(date) => handleDateRangeChange(date as Date, selectedEndDate)}
                    selectsStart
                    startDate={selectedStartDate}
                    endDate={selectedEndDate}
                />
                <DatePicker
                    selected={selectedEndDate}
                    onChange={(date) => handleDateRangeChange(selectedStartDate, date as Date)}
                    selectsEnd
                    startDate={selectedStartDate}
                    endDate={selectedEndDate}
                    minDate={selectedStartDate}
                />
                <button onClick={() => handleShortcutClick(1)}>1d</button>
                <button onClick={() => handleShortcutClick(3)}>3d</button>
                <button onClick={() => handleShortcutClick(5)}>5d</button>
                <button onClick={() => handleShortcutClick(7)}>1w</button>
                <button onClick={() => handleShortcutClick(30)}>1m</button>
                <button onClick={() => handleShortcutClick(365)}>1y</button>
            </div>
        </div>
    );
}
