import React from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend } from 'recharts';

interface Metric {
  patient_id: string;
  metric_name: string;
  metric_value: string;
  timestamp: string;
}

interface Patient {
  name: string;
  metrics: Metric[];
}

interface Props {
  patients: Patient[];
}

function NewDashboard({ patients }: Props) {
  const renderGraphs = (patient: Patient) => {
    const stepLengthData = patient.metrics.filter(metric => metric.metric_name === 'step_length');
    const doubleSupportTimeData = patient.metrics.filter(metric => metric.metric_name === 'double_support_time');
    const walkingSpeedData = patient.metrics.filter(metric => metric.metric_name === 'walking_speed');
    const walkingAsymmetryData = patient.metrics.filter(metric => metric.metric_name === 'walking_asymmetry');
    const distanceWalkedData = patient.metrics.filter(metric => metric.metric_name === 'distance_walked');
  
    return (
      <>
        <div>
          <h3>Step Length</h3>
          <LineChart
            data={stepLengthData.map(metric => ({ x: new Date(metric.timestamp), y: parseFloat(metric.metric_value) }))}
          />
        </div>
        <div>
          <h3>Double Support Time</h3>
          <LineChart
            data={doubleSupportTimeData.map(metric => ({ x: new Date(metric.timestamp), y: parseFloat(metric.metric_value) }))}
          />
        </div>
        <div>
          <h3>Walking Speed</h3>
          <LineChart
            data={walkingSpeedData.map(metric => ({ x: new Date(metric.timestamp), y: parseFloat(metric.metric_value) }))}
          />
        </div>
        <div>
          <h3>Walking Asymmetry</h3>
          <LineChart
            data={walkingAsymmetryData.map(metric => ({ x: new Date(metric.timestamp), y: parseFloat(metric.metric_value) }))}
          />
        </div>
        <div>
          <h3>Distance Walked</h3>
          <LineChart
            data={distanceWalkedData.map(metric => ({ x: new Date(metric.timestamp), y: parseFloat(metric.metric_value) }))}
          />
        </div>
      </>
    );
  };
  

  const renderTable = (patient: Patient) => {
    // Use the patient's metrics data to render a summary table
    return (
      <table>
        <thead>
          <tr>
            <th>Metric Name</th>
            <th>Metric Value</th>
            <th>Timestamp</th>
          </tr>
        </thead>
        <tbody>
          {patient.metrics.map(metric => (
            <tr key={metric.timestamp}>
              <td>{metric.metric_name}</td>
              <td>{metric.metric_value}</td>
              <td>{metric.timestamp}</td>
            </tr>
          ))}
        </tbody>
      </table>
    );
  };

  return (
    <div>
      {patients.map(patient => (
        <div key={patient.name}>
          <h2>{patient.name}</h2>
          {renderGraphs(patient)}
          {renderTable(patient)}
        </div>
      ))}
    </div>
  );
}

export default NewDashboard;
