/* eslint-disable @typescript-eslint/no-non-null-assertion */
import React, {useState, useEffect} from "react";

function LineGraph(dates:string[], distances:number[]) {
    const [canvas, setCanvas] = useState<HTMLCanvasElement | null>(null);

    useEffect(() => {
        if (!canvas) return;
        const ctx = canvas.getContext("2d");
        if (!ctx) return;
  
        // Set the dimensions of the canvas
        canvas.width = 500;
        canvas.height = 500;

        // Find max and min distance
        const maxDistance = Math.max(...distances);
        const minDistance = Math.min(...distances);
  
        // Draw the x and y axis
        ctx.beginPath();
        ctx.moveTo(50, 50);
        ctx.lineTo(50, 450);
        ctx.lineTo(450, 450);
        ctx.lineWidth = 2;
        ctx.strokeStyle = "black";
        ctx.stroke();
  
        // Draw y axis labels
        const yAxisLabelCount = 6;
        const yAxisInterval = (maxDistance - minDistance) / (yAxisLabelCount - 1);
        for (let i = 0; i < yAxisLabelCount; i++) {
            const y = 450 - i * 400 / (yAxisLabelCount - 1);
            ctx.fillText((minDistance + i * yAxisInterval).toFixed(1), 15, y + 5);
        }

        // Draw x axis labels
        const xAxisLabelCount = Math.min(dates.length, 7);
        const xAxisInterval = (dates.length - 1) / (xAxisLabelCount - 1);
        for (let i = 0; i < xAxisLabelCount; i++) {
            const x = 50 + i * 400 / (xAxisLabelCount - 1);
            const dateIndex = Math.round(i * xAxisInterval);
            ctx.fillText(dates[dateIndex], x - 15, 470);
        }
  
        // Draw line graph
        ctx.beginPath();
        ctx.moveTo(50, 450 - (distances[0] - minDistance) * 400 / (maxDistance - minDistance));
        for (let i = 1; i < distances.length; i++) {
            const x = 50 + (i / (distances.length - 1)) * 400;
            const y = 450 - (distances[i] - minDistance) * 400 / (maxDistance - minDistance);
            ctx.lineTo(x, y);
        }
        ctx.strokeStyle = "blue";
        ctx.lineWidth = 2;
        ctx.stroke();
  
        // Add x-axis label
        ctx.font = "14px Arial";
        ctx.fillStyle = "black";
        ctx.textAlign = "center";
        ctx.textBaseline = "bottom";
        ctx.fillText("Date", 250, 490);

        // Add y-axis label
        ctx.save();
        ctx.translate(20, 250);
        ctx.rotate(-Math.PI / 2);
        ctx.fillText("Distance Walked (km)", 0, -5);
        ctx.restore();

    }, [canvas, dates, distances]);

    return (
        <div style={{ display: "flex", justifyContent: "center" }}>
            <canvas ref={(el) => setCanvas(el)} />
        </div>
    );
}

export default LineGraph;