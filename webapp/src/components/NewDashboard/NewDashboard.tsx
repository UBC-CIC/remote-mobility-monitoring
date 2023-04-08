import React, { useState } from "react";
//import "react-datepicker/dist/react-datepicker.css";
import "./NewDashboard.css";

import sampleData from "./sampleData";
//import Barchart from "./Barchart";
import LineGraphbyMetrics from "./GraphForMultipatients";

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
    // const [graph, setGraph] = useState<any>(LineGraph(graphData));


    // pass the `graphData` object as the props to `NewLineGraph`
    const [multiPatientsGraph, setmultiPatientsGraph] = useState<any>(LineGraphbyMetrics(graphData));
    

    return (
        <div className="Dashboard">
            <h2>Dashboard</h2>
            <h3>{data[0].patient_name}</h3>

            <div className="newlinegraphs"  style={{ width: "90%", height: "300%", marginBottom: "40px" }}>
                {multiPatientsGraph}
            </div>

            <div className="linegraphs"  style={{ width: "90%", height: "300%", marginBottom: "40px" }}>
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

