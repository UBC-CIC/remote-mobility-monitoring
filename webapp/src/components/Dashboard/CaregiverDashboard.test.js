import { render, fireEvent, screen } from "@testing-library/react";
import CaregiverDashboard from "./CaregiverDashboard";

describe("CaregiverDashboard", () => {
  test("renders search input correctly", () => {
    render(<CaregiverDashboard />);
    const searchInput = screen.getByPlaceholderText("Search 0 patients");
    expect(searchInput).toBeInTheDocument();
  });

  test("renders switch to show verified patients", () => {
    render(<CaregiverDashboard />);
    const switchElement = screen.getByRole("switch");
    expect(switchElement).toBeInTheDocument();
  });

  test("renders add patient button", () => {
    render(<CaregiverDashboard />);
    const addPatientButton = screen.getByText("Add a New Patient");
    expect(addPatientButton).toBeInTheDocument();
  });

  test("renders view all patient metrics button", () => {
    render(<CaregiverDashboard />);
    const viewMetricsButton = screen.getByText("View All Patient Metrics");
    expect(viewMetricsButton).toBeInTheDocument();
  });

  test("searches for patients correctly", () => {
    render(<CaregiverDashboard />);
    const searchInput = screen.getByPlaceholderText("Search 0 patients");
    fireEvent.change(searchInput, { target: { value: "john" } });
    const searchButton = screen.getByRole("button", { name: "Search" });
    fireEvent.click(searchButton);
    const filteredPatients = screen.getAllByRole("entry");
    expect(filteredPatients.length).toBeGreaterThan(0);
  });

  test("filters unverified patients correctly", () => {
    render(<CaregiverDashboard />);
    const switchElement = screen.getByRole("switch");
    fireEvent.click(switchElement);
    const filteredPatients = screen.getAllByRole("entry");
    expect(filteredPatients.length).toBe(0);
  });

  test("navigates to add patient page when add patient button is clicked", () => {
    const mockNav = jest.fn();
    jest.mock("react-router-dom", () => ({
      useNavigate: () => mockNav,
    }));
    render(<CaregiverDashboard />);
    const addPatientButton = screen.getByText("Add a New Patient");
    fireEvent.click(addPatientButton);
    expect(mockNav).toHaveBeenCalledWith("/addpatient");
  });

  test("navigates to view all patient metrics page when view all patient metrics button is clicked", () => {
    const mockNav = jest.fn();
    jest.mock("react-router-dom", () => ({
      useNavigate: () => mockNav,
    }));
    render(<CaregiverDashboard />);
    const viewMetricsButton = screen.getByText("View All Patient Metrics");
    fireEvent.click(viewMetricsButton);
    expect(mockNav).toHaveBeenCalledWith("/allPatients");
  });
});

