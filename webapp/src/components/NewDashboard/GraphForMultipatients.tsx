import React, { useEffect } from "react";
import ReactApexChart from "react-apexcharts";
import "./Graph.css";


export const transformDataAll = (data: any[], metric: string, colors: any) => {
    const series: any = {};
  
    data.forEach((item) => {
        const { patient_name, metric_name, metric_value, timestamp, patient_id }: any = item;
  
        if (metric_name !== metric) {
            return;
        }
  
        if (!series[patient_name]) {
            series[patient_name] = { name: patient_name, data: [], color: colors[patient_id]};
        }
  
        series[patient_name].data.push({
            x: new Date(timestamp).getTime(),
            y: Number(metric_value),
        });
    });
  
    return Object.values(series);
};

export const transformData = (data: any[], metric: string) => {
    const series: any = {};
  
    data.forEach((item) => {
        const { patient_name, metric_name, metric_value, timestamp } = item;
  
        if (metric_name !== metric) {
            return;
        }
  
        if (!series[patient_name]) {
            series[patient_name] = { name: patient_name, data: []};
        }
  
        series[patient_name].data.push({
            x: new Date(timestamp).getTime(),
            y: Number(metric_value),
        });
    });
  
    return Object.values(series);
};

export const initialChartsOptionsState2 = {
    chart: {
        id: "chart2",
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
            formatter: function (value: any) {
                return Math.round((value + Number.EPSILON) * 100) / 100;
            },
        },
    },
    xaxis: {
        type: "datetime",
        labels: {
            rotate: -45,
            rotateAlways: false,
            format: "MM.dd.yy",
            offsetX: 90,
        },
        title: {
            text: "Date",
        },
    },
};
  

export const initialChartsOptionsState1 = {
    chart: {
        id: "chart1",
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
            formatter: function (value: any) {
                return Math.round((value + Number.EPSILON) * 100) / 100;
            },
        },
    },
    xaxis: {
        type: "datetime",
        labels: {
            rotate: -45,
            rotateAlways: false,
            format: "MM.dd.yy",
            offsetX: 90,
        },
        title: {
            text: "Date",
        },
    },
};
export const initialChartsOptionsState = {
    chart: {
        id: "chart",
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
            formatter: function (value: any) {
                return Math.round((value + Number.EPSILON) * 100) / 100;
            },
        },
    },
    xaxis: {
        type: "datetime",
        labels: {
            rotate: -45,
            rotateAlways: false,
            format: "MM.dd.yy",
            offsetX: 90,
        },
        title: {
            text: "Date",
        },
    },
};

export const metrics = [
    "step_length",
    "double_support_time",
    "walking_speed",
    "walking_asymmetry",
    "distance_walked",
    "step_count",
    "walking_steadiness"
];

// eslint-disable-next-line react/prop-types
export default function LineGraphbyMetrics({ data }: any) {

    const getRandomColor = () => {
        const hue = Math.floor(Math.random() * 360);
        const saturation = Math.floor(Math.random() * 100) + 1;
        const lightness = Math.floor(Math.random() * 60) + 20;
      
        return `hsl(${hue}, ${saturation}%, ${lightness}%)`;
    };

    const charts = metrics.map((metric, index) => {
        const allSeries = transformData(data, metric).map((series:any, idx:any) => {
            return {
                ...series,
                color: getRandomColor(),
            };
        });

        const options = {
            ...initialChartsOptionsState,
            colors: allSeries.map(() => getRandomColor()),
            series: allSeries,
        };

        return (
            <div key={metric}>
                <ReactApexChart
                    options={options as any}
                    series={allSeries as any}
                    type="line"
                    height={300}
                />
                <div style={{ textAlign: "center", fontSize: "14px", fontWeight: "bold" }}>
                    {metric}
                </div>
            </div>
        );
    });

    return <div>{charts}</div>;
}
