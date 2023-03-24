import React from "react";
import { Line } from "react-chartjs-2";
import { Patient, PatientsList, Metric, MetricsData } from "./NewDashboard";

const Graph = ({ patientsList, metricsData }: { patientsList: PatientsList, metricsData: MetricsData }) => {
    const patientIds = patientsList.patients.map((patient: { patient_id: any; }) => patient.patient_id);

    const graphData = patientIds.map((patientId: any) => {
        const metricsForPatient = metricsData.metrics.filter((metric: { patient_id: any; }) => metric.patient_id === patientId);
        const labels = metricsForPatient.map((metric: { timestamp: any; }) => metric.timestamp);
        const stepLengthData = metricsForPatient.filter((metric: { metric_name: string; }) => metric.metric_name === "step_length").map((metric: { metric_value: any; }) => metric.metric_value);
        const walkingSpeedData = metricsForPatient.filter((metric: { metric_name: string; }) => metric.metric_name === "walking_speed").map((metric: { metric_value: any; }) => metric.metric_value);
        const doubleSupportTimeData = metricsForPatient.filter((metric: { metric_name: string; }) => metric.metric_name === "double_support_time").map((metric: { metric_value: any; }) => metric.metric_value);
        const walkingAsymmetryData = metricsForPatient.filter((metric: { metric_name: string; }) => metric.metric_name === "walking_asymmetry").map((metric: { metric_value: any; }) => metric.metric_value);
        const distanceWalkedData = metricsForPatient.filter((metric: { metric_name: string; }) => metric.metric_name === "distance_walked").map((metric: { metric_value: any; }) => metric.metric_value);

        const data = {
            labels,
            datasets: [
                {
                    label: "Step Length",
                    data: stepLengthData,
                    fill: false,
                    borderColor: "red",
                },
                {
                    label: "Walking Speed",
                    data: walkingSpeedData,
                    fill: false,
                    borderColor: "blue",
                },
                {
                    label: "Double Support Time",
                    data: doubleSupportTimeData,
                    fill: false,
                    borderColor: "green",
                },
                {
                    label: "Walking Asymmetry",
                    data: walkingAsymmetryData,
                    fill: false,
                    borderColor: "orange",
                },
                {
                    label: "Distance Walked",
                    data: distanceWalkedData,
                    fill: false,
                    borderColor: "purple",
                },
            ],
        };

        return { patientId, data };
    });

    return (
        <div>
            {graphData.map(({ patientId, data }) => (
                <div key={patientId}>
                    <h3>
                        {patientsList.patients.find(
                            (patient: { patient_id: string }) => patient.patient_id === patientId
                        )?.first_name}{" "}
                        {patientsList.patients.find(
                            (patient: { patient_id: string }) => patient.patient_id === patientId
                        )?.last_name}
                    </h3>

                </div>
            ))}
        </div>
    );
};

export default Graph;
