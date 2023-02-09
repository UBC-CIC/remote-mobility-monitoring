import React, { useState } from "react";
import DatePicker from "react-datepicker";
import "./Dashboard.css";
import "react-datepicker/dist/react-datepicker.css";
import LineGraph from "./LineGraph";
import sampleData1 from "./sampleData";

interface Props {
  patientName: string;
  data: Array<{
    date: string;
    stepLength: number;
    doubleSupportTime: number;
    walkingSpeed: number;
    walkingAsymmetry: number;
    distanceWalked: number;
  }>;
}

//dummy data
const sampleData = sampleData1;

function Dashboard() {
    const [startDate, setStartDate] = useState<Date | null>();
    const [endDate, setEndDate] = useState<Date | null>();
   
    //TODO:   make api call and convert the data into prop
    const dummyProp = {
        patientName: "John Doe",
        data: sampleData,
    } as Props;
    
    const stringToDate = (s: string) => {
        const parts = s.split("-");
        return new Date(parseInt(parts[0]), parseInt(parts[1]) - 1, parseInt(parts[2]));
    };
    
    const filteredData = dummyProp.data.filter(
        (d) => (!startDate || stringToDate(d.date).getTime()>=startDate.getTime()) && (!endDate || stringToDate(d.date).getTime()<=endDate.getTime())
    );
   
    const stepLengthData = filteredData.map((d) => d.stepLength);
    const doubleSupportTimeData = filteredData.map((d) => d.doubleSupportTime);
    const walkingSpeedData = filteredData.map((d) => d.walkingSpeed);
    const walkingAsymmetryData = filteredData.map((d) => d.walkingAsymmetry);
    const distanceWalkedData = filteredData.map((d) => d.distanceWalked);
    const dateData = filteredData.map((d) => d.date);
    
    const averageStepLength = stepLengthData.reduce( ( p, c ) => p + c, 0 ) / stepLengthData.length;
    const averageDoubleSupportTime = doubleSupportTimeData.reduce( ( p, c ) => p + c, 0 ) / doubleSupportTimeData.length;
    const averageWalkingSpeed = walkingSpeedData.reduce( ( p, c ) => p + c, 0 ) / walkingSpeedData.length;
    const averageWalkingAsymmetry = walkingAsymmetryData.reduce( ( p, c ) => p + c, 0 ) / walkingAsymmetryData.length;

    const handleExportData = () => {
        // implement logic to export data as a csv file
        //entire data to csv
    };
   
    const handleUpdateDevice = () => {
        // implement logic to update the device used to collect data
        //call device update api
    };
   
    const handleShareData = () => {
        // implement logic to share data with another caregiver
        //call share api
    };
   
    const handleRemoveRecord = () => {
        // implement logic to remove this patient's record
        //call delete api
    };

    const graph = LineGraph(dateData, distanceWalkedData);
 
    return (
        <div className="Dashboard">
            <h1>{dummyProp.patientName}</h1>
            <div className="date-picker">
                <div className="start-date-picker">
                    <span>Start Date</span>
                    <DatePicker
                        selected={startDate}
                        name="start-date-picker"
                        selectsStart
                        startDate={startDate}
                        endDate={endDate}
                        onChange={date => setStartDate(date)}
                        className="fancy-datepicker"
                    />
                </div>
                <div className="end-date-picker">
                    <span>End Date</span>
                    <DatePicker
                        selected={endDate}
                        name="end-date-picker"
                        selectsEnd
                        startDate={startDate}
                        endDate={endDate}
                        minDate={startDate}
                        onChange={date => setEndDate(date)}
                        className="fancy-datepicker"
                    />
                </div>
            </div>
            <div className="buttons">
                <button type="button" name="export-button" onClick={handleExportData}>Export Data</button>
                <button type="button" name="update-button" onClick={handleUpdateDevice}>Update Device</button>
                <button type="button" name="share-button"  onClick={handleShareData}>Share</button>
                <button type="button" name="remove-button" onClick={handleRemoveRecord}>Remove</button>
            </div>
            <div className="cards">
                <div className="card">
                    <h3>Step Length</h3>
                    <p>{Math.round(averageStepLength)} cm</p>
                </div>
                <div className="card">
                    <h3>Double Supoort Time</h3>
                    <p>{Math.round(averageDoubleSupportTime)}%</p>
                </div>
                <div className="card">
                    <h3>Walking Speed</h3>
                    <p>{Math.round(averageWalkingSpeed)} kpm</p>
                </div>
                <div className="card">
                    <h3>Walking Asymmetry</h3>
                    <p>{Math.round(averageWalkingAsymmetry)}%</p>
                </div>
            </div>
            <div className="graph">
                {graph}
            </div>
            <table>
                <thead>
                    <tr>
                        <th>Date</th>
                        <th>Step Length</th>
                        <th>Double Support Time</th>
                        <th>Walking Speed</th>
                        <th>Walking Asymmetry</th>
                        <th>Distance Walked</th>
                        
                    </tr>
                </thead>
                <tbody>
                    {filteredData.map((data, index) => (
                        <tr key={index}>
                            <td>{dateData[index]}</td>
                            <td>{stepLengthData[index]}</td>
                            <td>{doubleSupportTimeData[index]}</td>
                            <td>{walkingSpeedData[index]}</td>
                            <td>{walkingAsymmetryData[index]}</td>
                            <td>{distanceWalkedData[index]}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}

export default Dashboard;

