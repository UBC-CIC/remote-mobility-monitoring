
import React, {useState, useEffect} from "react";

function LineGraph(dates:string[], distances:number[]) {
    const [canvas, setCanvas] = useState<HTMLCanvasElement | null>(null);
    const [hoverIndex, setHoverIndex] = useState(-1);

    // Find max and min distance
    const maxDistance = Math.max(...distances);
    const minDistance = Math.min(...distances);

    const handleMouseMove = (event: React.MouseEvent<HTMLCanvasElement>) => {
        if (!canvas) return;

        // Calculate the mouse position relative to the canvas
        const rect = canvas.getBoundingClientRect();
        const x = event.clientX - rect.left;
        const y = event.clientY - rect.top;

        // Check if the mouse is within a certain range of a datapoint
        for (let i = 0; i < distances.length; i++) {
            const pointX = 50 + (i / (distances.length - 1)) * 400;
            const pointY = 450 - (distances[i] - minDistance) * 400 / (maxDistance - minDistance);
            if (Math.abs(x - pointX) <= 10 && Math.abs(y - pointY) <= 10) {
                setHoverIndex(i);
                return;
            }
        }

        setHoverIndex(-1);
    };

    useEffect(() => {
        if (!canvas) return;
        const ctx = canvas.getContext("2d");
        if (!ctx) return;
  
        // Set the dimensions of the canvas
        canvas.width = 500;
        canvas.height = 500;

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

            // Draw gray horizontal lines for the y-axis labels
            ctx.beginPath();
            ctx.moveTo(50, y);
            ctx.lineTo(450, y);
            ctx.lineWidth = 1;
            ctx.strokeStyle = "lightgray";
            ctx.stroke();
        }

        // Draw x axis labels
        const xAxisLabelCount = Math.min(dates.length, 7);
        const xAxisInterval = (dates.length - 1) / (xAxisLabelCount - 1);
        for (let i = 0; i < xAxisLabelCount; i++) {
            const x = 50 + i * 400 / (xAxisLabelCount - 1);
            const dateIndex = Math.round(i * xAxisInterval);
            ctx.fillText(dates[dateIndex], x - 15, 470);
        }

        // Draw data points
        ctx.fillStyle = "blue";
        
        for (let i = 0; i < distances.length; i++) {
            const x = 50 + (i / (distances.length - 1)) * 400;
            const y = 450 - (distances[i] - minDistance) * 400 / (maxDistance - minDistance);
            if (hoverIndex === i) {
                ctx.fillStyle = "red";
                ctx.fillText(`${dates[i]} - ${distances[i].toFixed(1)} km`, x + 10, y - 10);
            } else {
                ctx.fillStyle = "blue";
            }
            ctx.beginPath();
            ctx.arc(x, y, 3, 0, 2 * Math.PI);
            ctx.fill();
            ctx.stroke();
        }

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
            <canvas ref={(el) => setCanvas(el)} onMouseMove={handleMouseMove}/>
        </div>
    );
}




export default LineGraph;