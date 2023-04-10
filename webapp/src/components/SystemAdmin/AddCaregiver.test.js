import { render, fireEvent, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import AddCaregiver from "./AddCaregiver";

const renderWithRouter = (ui, { route = "/" } = {}) => {
  window.history.pushState({}, "Test page", route);
  return render(ui, { wrapper: MemoryRouter });
};

describe("AddCaregiver", () => {
  test("renders the AddCaregiver component", () => {
    renderWithRouter(<AddCaregiver />);
  });

  test("renders the correct header text", () => {
    const { getByText } = renderWithRouter(<AddCaregiver />);
    expect(getByText("Add a caregiver")).toBeInTheDocument();
  });
  
  test('renders the "First Name" input field', () => {
    renderWithRouter(<AddCaregiver />);
    const firstNameElement = screen.getByLabelText(/First Name/i);
    expect(firstNameElement).toBeInTheDocument();
  });

  test('renders the "Last Name" input field', () => {
    renderWithRouter(<AddCaregiver />);
    const lastNameElement = screen.getByLabelText(/Last Name/i);
    expect(lastNameElement).toBeInTheDocument();
  });

  test('renders the "Email" input field', () => {
    renderWithRouter(<AddCaregiver />);
    const emailElement = screen.getByLabelText(/Email/i);
    expect(emailElement).toBeInTheDocument();
  });

  test('renders the "Contact number" input field', () => {
    renderWithRouter(<AddCaregiver />);
    const contactNumberElement = screen.getByLabelText(/Contact number/i);
    expect(contactNumberElement).toBeInTheDocument();
  });

  test('renders the "Add Caregiver" button', () => {
    renderWithRouter(<AddCaregiver />);
    const addButtonElement = screen.getByRole('button', {name: /Add Caregiver/i});
    expect(addButtonElement).toBeInTheDocument();
  });
  
  test("renders the correct input fields", () => {
    const { getByLabelText } = renderWithRouter(<AddCaregiver />);
    expect(getByLabelText("First Name")).toBeInTheDocument();
    expect(getByLabelText("Last Name")).toBeInTheDocument();
    expect(getByLabelText("Email")).toBeInTheDocument();
    expect(getByLabelText("Contact number")).toBeInTheDocument();
  });
  

});
