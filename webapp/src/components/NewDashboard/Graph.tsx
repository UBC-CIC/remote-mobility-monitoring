import React from "react";
import { Bar } from "react-chartjs-2";
import "chart.js/auto";
import { MetricsData, PatientsList } from "./NewDashboard";
import "./NewDashboard.css"; // import the CSS file

function Graph(props: { data: MetricsData, patients: PatientsList }) {
    const { data, patients } = props;
    const chartData: {[metricName: string]: {[patientName: string]: number[]}} = {};
    const colors = ["rgba(75,192,192,1)", "rgba(192,75,192,1)", "rgba(192,192,75,1)", "rgba(75,75,192,1)", "rgba(192,75,75,1)", "rgba(75,192,75,1)"];
    let colorIndex = 0;

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
        }
    };

    const chart = Object.entries(chartData).map(([metricName, metricValues]) => {
        const chartLabels = Object.keys(metricValues);
        const chartData = Object.values(metricValues);
        const color = colors[colorIndex % colors.length];
        colorIndex++;

        return (
            <div key={metricName} className="chart-container" style={{ textAlign: "center",  marginBottom: "70px" }}>
                <h3>{metricName}</h3>
                <Bar data={{
                    labels: chartLabels,
                    datasets: [{
                        label: metricName,
                        data: chartData,
                        backgroundColor: color
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
