import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import AdminDashboard from "./AdminDashboard";
import * as ServiceHandler from "../../helpers/ServiceHandler";

jest.mock("../../helpers/ServiceHandler", () => ({
  getOrg: jest.fn(() =>
    Promise.resolve({
      caregivers: [
        { caregiver_id: 1, first_name: "John", last_name: "Doe" },
        { caregiver_id: 2, first_name: "Jane", last_name: "Doe" },
        { caregiver_id: 3, first_name: "Bob", last_name: "Smith" },
      ],
    })
  ),
  deleteCaregiver: jest.fn(() => Promise.resolve({})),
}));

describe("AdminDashboard component", () => {
  beforeEach(() => {
    render(
      <MemoryRouter>
        <AdminDashboard />
      </MemoryRouter>
    );
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it("renders the correct number of caregivers", async () => {
    await waitFor(() =>
      expect(screen.getByText("3 caregivers")).toBeInTheDocument()
    );
  });

  it("displays the caregivers in the correct format", async () => {
    await waitFor(() => {
      expect(screen.getByText("John")).toBeInTheDocument();
      expect(screen.getByText("Doe")).toBeInTheDocument();
      expect(screen.getByText("Jane")).toBeInTheDocument();
      expect(screen.getByText("Doe")).toBeInTheDocument();
      expect(screen.getByText("Bob")).toBeInTheDocument();
      expect(screen.getByText("Smith")).toBeInTheDocument();
    });
  });

  it("filters the caregivers correctly when searching", async () => {
    const searchInput = screen.getByPlaceholderText("Search 3 caregivers");
    fireEvent.change(searchInput, { target: { value: "John" } });
    await waitFor(() => {
      expect(screen.getByText("John")).toBeInTheDocument();
      expect(screen.queryByText("Jane")).toBeNull();
      expect(screen.queryByText("Bob")).toBeNull();
    });
  });

  it("removes a caregiver when the remove button is clicked", async () => {
    const removeButton = await screen.findByText("Remove");
    fireEvent.click(removeButton);
    await waitFor(() => {
      expect(ServiceHandler.deleteCaregiver).toHaveBeenCalledTimes(1);
      expect(screen.getByText("Caregiver removed")).toBeInTheDocument();
      expect(ServiceHandler.getOrg).toHaveBeenCalledTimes(2);
    });
  });

  it("navigates to the AddCaregiver page when the Add New Caregiver button is clicked", async () => {
    const addButton = await screen.findByText("Add New Caregiver");
    fireEvent.click(addButton);
    await waitFor(() => {
      expect(screen.getByText("Add Caregiver")).toBeInTheDocument();
    });
  });
});
