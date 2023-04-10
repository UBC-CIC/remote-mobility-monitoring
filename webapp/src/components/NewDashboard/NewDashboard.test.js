import { render } from "@testing-library/react";
import NewDashboard from "./NewDashboard";


describe("NewDashboard", () => {

  test('renders the dashboard without errors', () => {
    render(<NewDashboard />);
  });

  test('renders the patient name', () => {
    const { getByText } = render(<NewDashboard />);
    const patientName = getByText(/Patient/i);
    expect(patientName).toBeInTheDocument();
  });

});
