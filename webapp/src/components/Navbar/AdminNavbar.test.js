import React from "react";
import { render, fireEvent } from "@testing-library/react";
import AdminNavbar from "./AdminNavbar";

describe("AdminNavbar component", () => {
  it("renders the navigation bar title", () => {
    const { getByText } = render(<AdminNavbar />);
    expect(getByText("Mobility Monitor")).toBeInTheDocument();
  });

  it("navigates to the dashboard page when the Dashboard item is clicked", () => {
    const { getByText } = render(<AdminNavbar />);
    fireEvent.click(getByText("Dashboard"));
    expect(window.location.pathname).toBe("/admindashboard");
  });

  it("navigates to the add caregiver page when the Add Caregivers item is clicked", () => {
    const { getByText } = render(<AdminNavbar />);
    fireEvent.click(getByText("Add Caregivers"));
    expect(window.location.pathname).toBe("/addcaregiver");
  });

  it("logs out the user and navigates to the login page when the Logout item is clicked", () => {
    const { getByText } = render(<AdminNavbar />);
    fireEvent.click(getByText("Logout"));
    expect(window.location.pathname).toBe("/login");
  });
  
  it("renders the Dashboard item", () => {
    const { getByText } = render(<AdminNavbar />);
    expect(getByText("Dashboard")).toBeInTheDocument();
  });

  it("renders the Add Caregivers item", () => {
    const { getByText } = render(<AdminNavbar />);
    expect(getByText("Add Caregivers")).toBeInTheDocument();
  });

  it("renders the Logout item", () => {
    const { getByText } = render(<AdminNavbar />);
    expect(getByText("Logout")).toBeInTheDocument();
  });

  it("renders the navigation bar with the correct class name", () => {
    const { container } = render(<AdminNavbar />);
    expect(container.firstChild).toHaveClass("navbarr");
  });
});
