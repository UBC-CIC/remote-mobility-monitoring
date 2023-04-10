import React from "react";
import { fireEvent, render } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import VerifyPatient from "./VerifyPatient";

const renderWithRouter = (ui, { route = "/" } = {}) => {
  window.history.pushState({}, "Test page", route);
  return render(ui, { wrapper: MemoryRouter });
};

describe("VerifyPatient component", () => {

  it("should not render the QR code if not loaded", () => {
    const { queryByAltText } = renderWithRouter(<VerifyPatient />);
    expect(queryByAltText("QR Code")).toBeNull();
  });

  it('renders the component', () => {
    renderWithRouter(<VerifyPatient />);
    });
    
    ('should render "Verify Patient" text', () => {
      renderWithRouter(<VerifyPatient />);
      expect(screen.getByText('Verify')).toBeInTheDocument();
    });

  
});
