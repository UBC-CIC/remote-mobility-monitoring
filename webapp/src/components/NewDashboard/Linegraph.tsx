import React, { useEffect, useState } from "react";
import ReactApexChart from "react-apexcharts";
import "bootstrap/dist/css/bootstrap.min.css";
import sampleData from "./sampleData";

const transformData = (data: any[]) => {
    const series: Record<string, { name: string, data: { x: number, y: number }[] }> = {};

    data.forEach(metric => {
        const { patient_name, metric_name, metric_value, timestamp } = metric;
    
        if (!series[metric_name]) {
            series[metric_name] = { name: metric_name, data: [] };
        }
    
        series[metric_name].data.push({
            x: new Date(timestamp).getTime(),
            y: Number(metric_value)
        });
    });
  
    return Object.values(series);
};

const initialChartsDataState = {
    seriesLine2: transformData(sampleData())
};

const initialChartsOptionsState = {
    optionsLine2: {
        chart: {
            id: "tw",
            group: "social",
            type: "line",
            height: 300
        },
        title: {
            text: "Middle chart",
            align: "left"
        },
        yaxis: {
            labels: {
                minWidth: 40
            }
        },
        xaxis: {
            type: "datetime",
            labels: {
                rotate: -45,
                rotateAlways: false,
                format: "dd.MM.yy HH:mm"
            },
            title: {
                text: "Date Time"
            }
        }
    }
};

export default function LineGraph() {
    const [chartsDataState, setChartsDataState] = useState(
        initialChartsDataState
    );
    const [updatedOptionsFlag, setUpdatedOptionsFlag] = useState(false);

    useEffect(() => {
        if (updatedOptionsFlag) {
            setUpdatedOptionsFlag(false);
        }
    }, [updatedOptionsFlag]);

    useEffect(() => {
        setChartsDataState({
            seriesLine2: transformData(sampleData())
        });
        setUpdatedOptionsFlag(true);
    }, []);

    const numSeries = chartsDataState.seriesLine2.length;
    const colors = ["#008FFB", "#00E396", "#FEB019", "#FF4560", "#775DD0", "#546E7A", "#26a69a", "#D10CE8"];

    const series = chartsDataState.seriesLine2.map((series, index) => {
        return {
            ...series,
            color: colors[index % numSeries]
        };
    });

    const options = {
        ...initialChartsOptionsState.optionsLine2,
        colors: series.map(series => series.color),
        series
    };

    return (
        <>
            <div id="wrapper">
                <div id="chart-line2">
                    {!updatedOptionsFlag && (
                        <ReactApexChart
                            options={options}
                            series={series}
                            type="line"
                            height={300}
                        />
                    )}
                </div>
            </div>
        </>
    );
}
