import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import AddPatient from './AddPatient';

import { MemoryRouter } from 'react-router-dom';

const renderWithRouter = (ui, { route = '/addpatient' } = {}) => {
  window.history.pushState({}, 'Test page', route);
  return render(ui, { wrapper: MemoryRouter });
};

describe('AddPatient component', () => {
  test('renders the initial Add Patient form', () => {
    renderWithRouter(<AddPatient />);
    const header = screen.getByText(/Add a patient/i);
    expect(header).toBeInTheDocument();
  });

  test("renders Add Patient form", async () => {
    renderWithRouter(<AddPatient />);
    const formHeader = screen.getByRole("heading", { name: "Add a patient" });
    const emailInput = screen.getByLabelText("Email");
    const submitButton = screen.getByRole("button", { name: "Add Patient" });
    expect(formHeader).toBeInTheDocument();
    expect(emailInput).toBeInTheDocument();
    expect(submitButton).toBeInTheDocument();
  });
  
  test("displays error message for empty email input", async () => {
    renderWithRouter(<AddPatient />);
    const submitButton = screen.getByRole("button", { name: "Add Patient" });
    fireEvent.click(submitButton);
    await waitFor(() => {
      expect(screen.getByText("Email cannot be empty")).toBeInTheDocument();
    });
  });
  
  test("renders Add Patient component with form input and submit button", () => {
    renderWithRouter(<AddPatient />);
    const emailInput = screen.getByLabelText(/email/i);
    expect(emailInput).toBeInTheDocument();
    const addButton = screen.getByRole("button", { name: /add patient/i });
    expect(addButton).toBeInTheDocument();
  });
  
  test("displays error message when submitting form with empty email field", () => {
    renderWithRouter(<AddPatient />);
    const addButton = screen.getByRole("button", { name: /add patient/i });
    fireEvent.click(addButton);
    const errorMessage = screen.getByText(/email cannot be empty/i);
    expect(errorMessage).toBeInTheDocument();
  });
  
  test("renders the Add Patient page with the form visible", () => {
    const { getByLabelText, getByText } = renderWithRouter(<AddPatient />);
  
    expect(getByText("Add a patient")).toBeInTheDocument();
    expect(getByLabelText("Email")).toBeInTheDocument();
    expect(getByText("Add Patient")).toBeInTheDocument();
  });
  
  test("displays an error message when email field is empty", () => {
    const { getByLabelText, getByText } = renderWithRouter(<AddPatient />);
  
    const emailInput = getByLabelText("Email");
    fireEvent.change(emailInput, { target: { value: "" } });
    fireEvent.click(getByText("Add Patient"));
  
    expect(getByText("Email cannot be empty")).toBeInTheDocument();
  });
   

  test("renders the email input field", () => {
    const { getByLabelText } = renderWithRouter(<AddPatient />);
    const emailInput = getByLabelText(/email/i);
    expect(emailInput).toBeInTheDocument();
  });

});
