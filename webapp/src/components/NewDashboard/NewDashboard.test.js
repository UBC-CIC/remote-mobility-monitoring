import { render, screen } from '@testing-library/react';
import NewDashboard from './NewDashboard';

const renderWithRouter = (ui, { route = '/' } = {}) => {
  window.history.pushState({}, 'Test page', route);
  return render(ui, { wrapper: MemoryRouter });
};

test('renders the dashboard title', () => {
  renderWithRouter(<NewDashboard />);
  const dashboardTitle = screen.getByRole('heading', { level: 2 });
  expect(dashboardTitle).toBeInTheDocument();
});

test('renders the patient name', () => {
  renderWithRouter(<NewDashboard />);
  const patientName = screen.getByRole('heading', { level: 3 });
  expect(patientName).toBeInTheDocument();
});

test('renders the graph', () => {
  renderWithRouter(<NewDashboard />);
  const graph = screen.getByRole('img');
  expect(graph).toBeInTheDocument();
});

test('renders the table', () => {
  renderWithRouter(<NewDashboard />);
  const table = screen.getByRole('table');
  expect(table).toBeInTheDocument();
});

test('renders the table headers', () => {
  renderWithRouter(<NewDashboard />);
  const tableHeaders = screen.getAllByRole('columnheader');
  expect(tableHeaders).toHaveLength(7);
});

test('renders the table rows', () => {
  renderWithRouter(<NewDashboard />);
  const tableRows = screen.getAllByRole('row');
  expect(tableRows).not.toHaveLength(0);
});
