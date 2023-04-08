import { render, fireEvent, screen, cleanup } from "@testing-library/react";
import AddPatient from "./AddPatient";
import React from "react";

afterEach(cleanup);
const location = { pathname: "/addpatient" };          

describe("AddPatient component", () => {
  test("renders without crashing", () => {
    render(<AddPatient />);
  });

  test("contains a CaregiverNavbar component", () => {
    render(<AddPatient />);
    expect(screen.getByRole("navigation")).toBeInTheDocument();
  });

  test("contains a form to add a patient", () => {
    render(<AddPatient />);
    expect(screen.getByRole("form")).toBeInTheDocument();
  });

  test("form contains an input field for the patient's email", () => {
    render(<AddPatient />);
    expect(screen.getByLabelText("Email")).toBeInTheDocument();
  });

  test("form contains a submit button to add the patient", () => {
    render(<AddPatient />);
    expect(screen.getByRole("button", { name: "Add Patient" })).toBeInTheDocument();
  });

  test("displays an error message if the email field is empty", () => {
    render(<AddPatient />);
    const submitButton = screen.getByRole("button", { name: "Add Patient" });
    fireEvent.click(submitButton);
    expect(screen.getByText("Email cannot be empty")).toBeInTheDocument();
  });

  test("error message is displayed when email is not provided", () => {
    render(<AddPatient />);
    const addButton = screen.getByText(/Add Patient/i);
    fireEvent.click(addButton);
    const errorMessage = screen.getByText(/Email cannot be empty/i);
    expect(errorMessage).toBeInTheDocument();
  });
  
  test("displays a QR code when the addPatient function is successfully executed", () => {
    render(<AddPatient />);
    const emailInput = screen.getByLabelText("Email");
    const submitButton = screen.getByRole("button", { name: "Add Patient" });
    fireEvent.change(emailInput, { target: { value: "test@test.com" } });
    fireEvent.click(submitButton);
    expect(screen.getByRole("img")).toBeInTheDocument();
  });
});

