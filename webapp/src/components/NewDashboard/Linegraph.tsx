import React, { useEffect, useState } from "react";
import ReactApexChart from "react-apexcharts";
import "bootstrap/dist/css/bootstrap.min.css";

const generateDayWiseTimeSeries = (baseval: number, count: number, yrange: { min: any; max: any; }) => {
    let i = 0;
    const series = [];
    while (i < count) {
        const y =
      Math.floor(Math.random() * (yrange.max - yrange.min + 1)) + yrange.min;

        series.push([baseval, y]);
        baseval += 86400000;
        i++;
    }
    return series;
};

const initialChartsDataState = {

    seriesLine2: [
        {
            data: generateDayWiseTimeSeries(new Date("11 Feb 2017").getTime(), 20, {
                min: 0,
                max: 9999
            })
        }
    ],

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
        colors: ["#546E7A"],
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
    },

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

    return (
        <>
            <div id="wrapper">

                <div id="chart-line2">
                    {!updatedOptionsFlag && (
                        <ReactApexChart
                            options={initialChartsOptionsState.optionsLine2}
                            series={chartsDataState.seriesLine2}
                            type="line"
                            height={300}
                        />
                    )}
                </div>

            </div>
        </>
    );
}
