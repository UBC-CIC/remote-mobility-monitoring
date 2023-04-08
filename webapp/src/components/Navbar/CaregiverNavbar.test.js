import React from "react";
import { render, fireEvent } from "@testing-library/react";
import CaregiverNavbar from "./CaregiverNavbar";

describe("CaregiverNavbar component", () => {
  it("renders the navigation bar title", () => {
    const { getByText } = render(<CaregiverNavbar />);
    expect(getByText("Mobility Monitor")).toBeInTheDocument();
  });

  it("navigates to the dashboard page when the Dashboard item is clicked", () => {
    const { getByText } = render(<CaregiverNavbar />);
    fireEvent.click(getByText("Dashboard"));
    expect(window.location.pathname).toBe("/dashboard");
  });

  it("navigates to the change password page when the Change Password item is clicked", () => {
    const { getByText } = render(<CaregiverNavbar />);
    fireEvent.click(getByText("Change Password"));
    expect(window.location.pathname).toBe("/changepwd");
  });

  it("logs out the user and navigates to the login page when the Logout item is clicked", () => {
    const { getByText } = render(<CaregiverNavbar />);
    fireEvent.click(getByText("Logout"));
    expect(window.location.pathname).toBe("/login");
  });
  
  it("renders the Dashboard item", () => {
    const { getByText } = render(<CaregiverNavbar />);
    expect(getByText("Dashboard")).toBeInTheDocument();
  });

  it("renders the Change Password item", () => {
    const { getByText } = render(<CaregiverNavbar />);
    expect(getByText("Change Password")).toBeInTheDocument();
  });

  it("renders the Logout item", () => {
    const { getByText } = render(<CaregiverNavbar />);
    expect(getByText("Logout")).toBeInTheDocument();
  });

  it("renders the navigation bar with the correct class name", () => {
    const { container } = render(<CaregiverNavbar />);
    expect(container.firstChild).toHaveClass("navbarr");
  });
});
