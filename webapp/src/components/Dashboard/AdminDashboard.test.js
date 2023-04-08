import React from "react";
import { render, fireEvent, screen, waitFor } from "@testing-library/react";
import AdminDashboard from "./AdminDashboard";
import { ServiceHandler } from "../../helpers/ServiceHandler";

// Mock ServiceHandler module
jest.mock("../../helpers/ServiceHandler", () => ({
  getOrg: jest.fn(() =>
    Promise.resolve({
      caregivers: [
        { caregiver_id: "1", first_name: "John", last_name: "Doe" },
        { caregiver_id: "2", first_name: "Jane", last_name: "Smith" },
      ],
    })
  ),
  deleteCaregiver: jest.fn(() => Promise.resolve()),
}));

describe("AdminDashboard component", () => {
  afterEach(() => {
    jest.clearAllMocks();
  });

  it("renders without throwing any errors", () => {
    render(<AdminDashboard />);
  });

  it("correctly filters the list of caregivers based on input", async () => {
    render(<AdminDashboard />);

    const searchInput = screen.getByPlaceholderText("Search 2 caregivers");
    fireEvent.change(searchInput, { target: { value: "John" } });

    await waitFor(() => {
      expect(screen.getByText("John")).toBeInTheDocument();
      expect(screen.queryByText("Jane")).not.toBeInTheDocument();
    });
  });

  it("navigates to the 'Add New Caregiver' page when button is clicked", () => {
    const mockNavigate = jest.fn();
    jest.mock("react-router-dom", () => ({
      useNavigate: () => mockNavigate,
    }));

    render(<AdminDashboard />);

    const addButton = screen.getByText("Add New Caregiver");
    fireEvent.click(addButton);

    expect(mockNavigate).toHaveBeenCalledWith("/addcaregiver");
  });

  it("removes a caregiver from the list when 'Remove' button is clicked", async () => {
    render(<AdminDashboard />);

    const removeButton = await screen.findByText("Remove");
    fireEvent.click(removeButton);

    expect(ServiceHandler.deleteCaregiver).toHaveBeenCalledWith("1");
  });

  it("fetches data from server and populates caregiver list on mount", async () => {
    render(<AdminDashboard />);

    expect(ServiceHandler.getOrg).toHaveBeenCalledTimes(1);
    expect(await screen.findByText("John")).toBeInTheDocument();
    expect(await screen.findByText("Jane")).toBeInTheDocument();
  });

  it("re-fetches data from server and updates caregiver list when a caregiver is deleted", async () => {
    render(<AdminDashboard />);

    const removeButton = await screen.findByText("Remove");
    fireEvent.click(removeButton);

    await waitFor(() => {
      expect(ServiceHandler.getOrg).toHaveBeenCalledTimes(2);
      expect(screen.queryByText("John")).not.toBeInTheDocument();
    });
  });

  it("handles errors when fetching data from server", async () => {
    ServiceHandler.getOrg.mockImplementationOnce(() =>
      Promise.reject(new Error("Server error"))
    );

    render(<AdminDashboard />);

    await waitFor(() => {
      expect(screen.getByText("Server error")).toBeInTheDocument();
    });
  });

  it("handles errors when deleting a caregiver from server", async () => {
    ServiceHandler.deleteCaregiver.mockImplementationOnce(() =>
      Promise.reject(new Error("Server error"))
    );

    render(<AdminDashboard />);

    const removeButton = await screen.findByText("Remove");
    fireEvent.click(removeButton);

    await waitFor(() => {
      expect(screen.getByText("Server error")).toBeInTheDocument();
    });
  });
});
