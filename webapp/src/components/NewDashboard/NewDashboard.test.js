import React from "react";
import { render, screen } from "@testing-library/react";
import NewDashboard from "./NewDashboard";

describe("NewDashboard", () => {
  it("renders the dashboard title", () => {
    render(<NewDashboard />);
    expect(screen.getByText("Dashboard")).toBeInTheDocument();
  });

  it("renders the patient name", () => {
    render(<NewDashboard />);
    expect(screen.getByText("John Doe")).toBeInTheDocument();
  });

  it("renders the table headers", () => {
    render(<NewDashboard />);
    expect(screen.getByText("Date")).toBeInTheDocument();
    expect(screen.getByText("Step Length (km)")).toBeInTheDocument();
    expect(screen.getByText("Double Support Time (%)")).toBeInTheDocument();
    expect(screen.getByText("Walking Speed (kpm)")).toBeInTheDocument();
    expect(screen.getByText("Walking Asymmetry (%)")).toBeInTheDocument();
    expect(screen.getByText("Distance Walked (km)")).toBeInTheDocument();
    expect(screen.getByText("Step Count (s)")).toBeInTheDocument();
  });

  it("renders the table rows", () => {
    render(<NewDashboard />);
    expect(screen.getAllByRole("row")).toHaveLength(7);
  });

  it("displays metric values in table cells", () => {
    render(<NewDashboard />);
    expect(screen.getByText("0.7")).toBeInTheDocument();
    expect(screen.getByText("45")).toBeInTheDocument();
    expect(screen.getByText("1.2")).toBeInTheDocument();
    expect(screen.getByText("10")).toBeInTheDocument();
    expect(screen.getByText("2.5")).toBeInTheDocument();
    expect(screen.getByText("4321")).toBeInTheDocument();
  });

  it("formats timestamps as dates in the table", () => {
    render(<NewDashboard />);
    expect(screen.getByText("01/01/2021")).toBeInTheDocument();
    expect(screen.getByText("01/02/2021")).toBeInTheDocument();
    expect(screen.getByText("01/03/2021")).toBeInTheDocument();
  });

  it("renders the line graph section", () => {
    render(<NewDashboard />);
    expect(screen.getByRole("img", { name: "Line Graph" })).toBeInTheDocument();
  });

  it("renders the multi-patients line graph section", () => {
    render(<NewDashboard />);
    expect(screen.getByRole("img", { name: "Multi-Patients Line Graph" })).toBeInTheDocument();
  });
});
