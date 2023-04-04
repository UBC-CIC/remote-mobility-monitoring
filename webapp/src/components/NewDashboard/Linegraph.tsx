import React, { useEffect, useState } from "react";
import ReactApexChart from "react-apexcharts";
import "bootstrap/dist/css/bootstrap.min.css";
import sampleData from "./sampleData";

const transformData = (data: any[]) => {
    const series: Record<string, { name: string; data: { x: number; y: number }[] }> = {};

    data.forEach((metric) => {
        const { patient_name, metric_name, metric_value, timestamp } = metric;

        if (!series[metric_name]) {
            series[metric_name] = { name: metric_name, data: [] };
        }

        series[metric_name].data.push({
            x: new Date(timestamp).getTime(),
            y: Number(metric_value),
        });
    });

    return Object.values(series);
};

const initialChartsDataState = {
    seriesLine2: transformData(sampleData()),
};

const initialChartsOptionsState = {
    optionsLine2: {
        chart: {
            id: "tw",
            group: "social",
            type: "line",
            height: 300,
        },
        title: {
            text: "Metrics Chart",
            align: "left",
        },
        yaxis: {
            labels: {
                minWidth: 40,
            },
        },
        xaxis: {
            type: "datetime",
            labels: {
                rotate: -45,
                rotateAlways: false,
                format: "dd.MM.yy",
            },
            title: {
                text: "Date Time",
            },
        },
    },
};

export default function LineGraph() {
    const [chartsDataState, setChartsDataState] = useState(initialChartsDataState);
    const [updatedOptionsFlag, setUpdatedOptionsFlag] = useState(false);
    const [selectedMetric, setSelectedMetric] = useState("all");

    useEffect(() => {
        if (updatedOptionsFlag) {
            setUpdatedOptionsFlag(false);
        }
    }, [updatedOptionsFlag]);

    useEffect(() => {
        setChartsDataState({
            seriesLine2: transformData(sampleData()),
        });
        setUpdatedOptionsFlag(true);
    }, []);

    const numSeries = chartsDataState.seriesLine2.length;
    const colors = [
        "#008FFB",
        "#00E396",
        "#FEB019",
        "#FF4560",
        "#775DD0",
        "#546E7A",
        "#26a69a",
        "#D10CE8",
    ];

    const allSeries = chartsDataState.seriesLine2.map((series, index) => {
        return {
            ...series,
            color: colors[index % numSeries],
        };
    });

    const filteredSeries =
    selectedMetric === "all"
        ? allSeries
        : allSeries.filter((series) => series.name === selectedMetric);

    const options = {
        ...initialChartsOptionsState.optionsLine2,
        colors: filteredSeries.map((series) => series.color),
        series: filteredSeries,
    };

    const handleMetricSelect = (event: React.ChangeEvent<HTMLSelectElement>) => {
        setSelectedMetric(event.target.value);
    };

    return (
        <div>
            <div style={{ display: "flex", alignItems: "center", justifyContent: "center" }}>
                <select value={selectedMetric} onChange={handleMetricSelect} style={{ fontSize: "14px" }}>
                    <option value="all">All Metrics</option>
                    {allSeries.map((series) => (
                        <option key={series.name} value={series.name}>
                            {series.name}
                        </option>
                    ))}
                </select>
            </div>
            <ReactApexChart
                options={options}
                series={filteredSeries}
                type="line"
                height={300}
            />
        </div>
    );    

}

