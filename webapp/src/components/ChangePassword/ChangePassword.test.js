import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import ChangePassword from "./ChangePassword";

import { MemoryRouter } from 'react-router-dom';

const renderWithRouter = (ui, { route = '/changepwd' } = {}) => {
  window.history.pushState({}, 'Test page1', route);
  return render(ui, { wrapper: MemoryRouter });
};

describe("ChangePassword", () => {
  it("renders the component with form inputs", () => {
    const { getByLabelText, getByText } = renderWithRouter(<ChangePassword />);
    expect(getByLabelText("Old Password")).toBeInTheDocument();
    expect(getByLabelText("New Password")).toBeInTheDocument();
    expect(getByLabelText("Confirm Password")).toBeInTheDocument();
    expect(getByText("Change Password")).toBeInTheDocument();
  });

  it("shows error message when new password doesn't meet criteria", async () => {
    const { getByLabelText, getByText } = renderWithRouter(<ChangePassword />);
    const newPasswordInput = getByLabelText("New Password");
    fireEvent.change(newPasswordInput, { target: { value: "1234" } });
    fireEvent.click(getByText("Change Password"));
    await waitFor(() => {
      expect(getByText("Minimum length should be 8 characters")).toBeInTheDocument();
      expect(getByText("Must contain at least one number")).toBeInTheDocument();
      expect(getByText("Must contain at least one lowercase letter")).toBeInTheDocument();
      expect(getByText("Must contain at least one uppercase letter")).toBeInTheDocument();
      expect(getByText("Must contain at least one special character")).toBeInTheDocument();
      expect(getByText("Passwords must match")).toBeInTheDocument();
    });
  });

  it("shows error message when new passwords don't match", async () => {
    const { getByLabelText, getByText } = renderWithRouter(<ChangePassword />);
    const newPasswordInput = getByLabelText("New Password");
    const confirmPasswordInput = getByLabelText("Confirm Password");
    fireEvent.change(newPasswordInput, { target: { value: "Test1234!" } });
    fireEvent.change(confirmPasswordInput, { target: { value: "Mismatch1234!" } });
    fireEvent.click(getByText("Change Password"));
    await waitFor(() => {
      expect(getByText("Passwords must match")).toBeInTheDocument();
    });
  });

  // You can add more tests here for other scenarios such as successful password change, etc.
});