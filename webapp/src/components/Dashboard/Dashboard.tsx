import React, { useState } from 'react';
import { Form, Input, Button,  Card, Table, Typography, Space } from 'antd';
import { Line } from 'react-chartjs-2';
import moment from 'moment';
import DatePicker from "react-datepicker";

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

// dummy data
const sampleData = [
    {
      date: "2022-01-01",
      stepLength: 60,
      doubleSupportTime: 10,
      walkingSpeed: 1.5,
      walkingAsymmetry: 5,
      distanceWalked: 1000,
    },
    {
      date: "2022-01-02",
      stepLength: 55,
      doubleSupportTime: 11,
      walkingSpeed: 1.6,
      walkingAsymmetry: 6,
      distanceWalked: 900,
    },
    {
      date: "2022-02-01",
      stepLength: 65,
      doubleSupportTime: 11,
      walkingSpeed: 1.7,
      walkingAsymmetry: 10,
      distanceWalked: 1300,
      },
  ];

function Dashboard() {

    const [startDate, setStartDate] = useState<moment.Moment | null>(null);
    const [endDate, setEndDate] = useState<moment.Moment | null>(null);
   
    const handleStartDateChange = (date: Date | null, event: any) => {
      setStartDate(moment(date));
    };
    const handleEndDateChange = (date: Date | null, event: any) => {
      setEndDate(moment(date));
    };
   
    //TODO:   make api call and convert the data into prop
    const dummyProp = {
        patientName: "John Doe",
        data: sampleData,
      } as Props;

    const filteredData = dummyProp.data.filter(
      (d) => (!startDate || moment(d.date).isSameOrAfter(startDate)) && (!endDate || moment(d.date).isSameOrBefore(endDate))
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
    };
   
    const handleUpdateDevice = () => {
      // implement logic to update the device used to collect data
    };
   
    const handleShareData = () => {
      // implement logic to share data with another caregiver
    };
   
    const handleRemoveRecord = () => {
      // implement logic to remove this patient's record
    };
   
    const graphData = {
      labels: dateData,
      datasets: [
        {
          label: 'Distance Walked',
          data: distanceWalkedData,
          backgroundColor: 'white',
          borderColor: 'blue',
          fill: false,
        },
      ],
    };

    // table colmns and data
    const columns = [
      {
        title: 'Date',
        dataIndex: 'date',
        key: 'date',
      },
      {
        title: 'Step Length',
        dataIndex: 'stepLength',
        key: 'stepLength',
      },
      {
        title: 'Double Support Time',
        dataIndex: 'doubleSupportTime',
        key: 'doubleSupportTime',
      },
      {
        title: 'Walking Speed',
        dataIndex: 'walkingSpeed',
        key: 'walkingSpeed',
      },
      {
        title: 'Walking Asymmetry',
        dataIndex: 'walkingAsymmetry',
        key: 'walkingAsymmetry',
      },
      {
        title: 'Distance Walked',
        dataIndex: 'distanceWalked',
        key: 'distanceWalked',
      },
    ];
   
    const tableData = filteredData.map((d, i) => ({
      key: i,
      date: d.date,
      stepLength: d.stepLength,
      doubleSupportTime: d.doubleSupportTime,
      walkingSpeed: d.walkingSpeed,
      walkingAsymmetry: d.walkingAsymmetry,
      distanceWalked: d.distanceWalked,
    }));
   
    return (
        <div className="dashboard">
         <h1>{dummyProp.patientName}</h1>
          <div className="date-picker">
            <span>Start Date </span>
            <DatePicker onChange={handleStartDateChange} />
            <span>End Date </span>
            <DatePicker onChange={handleEndDateChange} />
          </div>
          <div className="buttons" style={{ display: 'flex', justifyContent: 'space-between' }}>
            <Button className="export-button" onClick={handleExportData}>Export Data</Button>
            <Button className="update-button" onClick={handleUpdateDevice}>Update Device</Button>
            <Button className="share-button" onClick={handleShareData}>Share</Button>
            <Button className="remove-button" onClick={handleRemoveRecord}>Remove</Button>
          </div>
          <div className="cards" style={{ display: 'flex', justifyContent: 'space-between', marginTop: 16 }}>
           <Card title="Step Length" style={{ width: 300, display: 'inline-block' }}> {averageStepLength}</Card>
           <Card title="Double Support Time" style={{ width: 300, display: 'inline-block' }}> {averageDoubleSupportTime}</Card>
           <Card title="Walking Speed" style={{ width: 300, display: 'inline-block' }}> {averageWalkingSpeed}</Card>
           <Card title="Walking Asymmetry" style={{ width: 300, display: 'inline-block' }}> {averageWalkingAsymmetry}</Card>
          </div>
          <div className='graph' style={{ marginTop: 16 }}>
            <Line data={graphData} />
          </div>
          <div className = 'table' style={{ marginTop: 16 }}>
            <Table columns={columns} dataSource={tableData} />
          </div>
      </div>
    );
   };

export default Dashboard;
