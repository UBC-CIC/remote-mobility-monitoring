import React, { useEffect, useRef } from "react";
import Chart from "chart.js/auto";

// Caution: This function needs to be debugged!

interface ChartData {
  x: number;
  y: number;
}

interface ChartDataSets {
  label: string;
  data: ChartData[];
  borderColor: string;
  fill: boolean;
  tension: number;
}

function Makegraph(data: any[]) {
    const chartRef = useRef<HTMLCanvasElement>(null);
    const chartInstanceRef = useRef<Chart | null>(null);

    useEffect(() => {
        const chartData: { [key: string]: { [key: string]: ChartData[] } } = {};

        data.forEach(({ patient_name, metric_name, metric_value, timestamp }) => {
            if (!chartData[patient_name]) chartData[patient_name] = {};
            if (!chartData[patient_name][metric_name])
                chartData[patient_name][metric_name] = [];

            chartData[patient_name][metric_name].push({
                x: new Date(timestamp).getTime(),
                y: parseFloat(metric_value),
            });
        });

        if (chartInstanceRef.current) {
            chartInstanceRef.current.destroy();
        }

        chartInstanceRef.current = new Chart(chartRef.current as HTMLCanvasElement, {
            type: "line",
            data: {
                datasets: Object.keys(chartData).reduce(
                    (acc: ChartDataSets[], patient: string) => {
                        Object.keys(chartData[patient]).forEach((metric: string) => {
                            acc.push({
                                label: `${patient} - ${metric}`,
                                data: chartData[patient][metric],
                                borderColor: `rgb(${Math.floor(Math.random() * 256)}, ${Math.floor(
                                    Math.random() * 256
                                )}, ${Math.floor(Math.random() * 256)})`,
                                fill: false,
                                tension: 0.1,
                            });
                        });
                        return acc;
                    },
                    []
                ),
            },
            options: {
                scales: {
                    x: {
                        type: "time",
                        time: {
                            unit: "day",
                        },
                    },
                },
            },
        });
    }, [data]);

    return <canvas ref={chartRef} />;
}

export default Makegraph;
