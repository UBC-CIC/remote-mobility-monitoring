import React from "react";
import { Bar } from "react-chartjs-2";
import "chart.js/auto";
import { MetricsData, PatientsList } from "./NewDashboard";

function Graph(props: { data: MetricsData, patients: PatientsList }) {
    const { data, patients } = props;
    const chartData: {[metricName: string]: {[patientName: string]: number[]}} = {};
  
    data.metrics.forEach((metric) => {
        const metricName = metric.metric_name;
        const patientName = patients.patients.find((patient) => patient.patient_id === metric.patient_id)?.first_name ?? metric.patient_id;
        const chartDataMetric = chartData[metricName] ?? {};
  
        chartDataMetric[patientName] = [...(chartDataMetric[patientName] ?? []), parseFloat(metric.metric_value)];
        chartData[metricName] = chartDataMetric;
    });
  
    const chartOptions = {
        scales: {
            y: {
                beginAtZero: true
            }
        },
        height: 20,
        width: 20
    };
    
  
    const chart = Object.entries(chartData).map(([metricName, metricValues]) => {
        const chartLabels = Object.keys(metricValues);
        const chartData = Object.values(metricValues);
  
        return (
            <div key={metricName}>
                <h2>{metricName}</h2>
                <Bar data={{
                    labels: chartLabels,
                    datasets: [{
                        label: metricName,
                        data: chartData,
                        backgroundColor: "rgba(75,192,192,1)"
                    }]
                }} options={chartOptions} />
            </div>
        );
    });
  
    return (
        <div>
            {chart}
        </div>
    );
}

export default Graph;