import { render, fireEvent, waitFor } from '@testing-library/react';
import SharePatient from './SharePatient';

test('renders choose caregiver header', () => {
  const { getByText } = render(<SharePatient />);
  const headerElement = getByText(/Choose a caregiver to share the patient's metrics with:/i);
  expect(headerElement).toBeInTheDocument();
});

test('renders back button', () => {
  const { getByText } = render(<SharePatient />);
  const buttonElement = getByText(/back/i);
  expect(buttonElement).toBeInTheDocument();
});

test('search input updates filtered caregivers', async () => {
  const { getByPlaceholderText, getByText } = render(<SharePatient />);
  const searchInput = getByPlaceholderText(/search .* caregivers/i);
  fireEvent.change(searchInput, { target: { value: 'John' } });
  await waitFor(() => {
    const filteredCaregivers = getByText(/john.*smith/i);
    expect(filteredCaregivers).toBeInTheDocument();
  });
});

test('clicking back button navigates to patient dashboard', () => {
  const mockNavigate = jest.fn();
  jest.mock('react-router-dom', () => ({
    ...jest.requireActual('react-router-dom'),
    useNavigate: () => mockNavigate,
  }));
  const { getByText } = render(<SharePatient />);
  const buttonElement = getByText(/back/i);
  fireEvent.click(buttonElement);
  expect(mockNavigate).toHaveBeenCalled();
});

test('clicking on a caregiver shares patient metrics', async () => {
  const mockSharePatient = jest.fn();
  jest.mock('../../helpers/ServiceHandler', () => ({
    sharePatient: () => Promise.resolve({}),
  }));
  const { getByText } = render(<SharePatient />);
  const caregiver = getByText(/jane.*doe/i);
  fireEvent.click(caregiver);
  await waitFor(() => expect(mockSharePatient).toHaveBeenCalled());
});

test('does not show the same caregiver that is logged in', () => {
  const mockGetOrg = jest.fn(() => ({
    caregivers: [{ caregiver_id: '1', first_name: 'John', last_name: 'Doe' }],
  }));
  jest.mock('../../helpers/ServiceHandler', () => ({
    getOrg: mockGetOrg,
  }));
  localStorage.setItem('sub', '1');
  const { queryByText } = render(<SharePatient />);
  const caregiver = queryByText(/john.*doe/i);
  expect(caregiver).toBeNull();
});

test('shows an alert when patient metrics are shared', async () => {
  jest.spyOn(window, 'alert').mockImplementation(() => {});
  jest.mock('../../helpers/ServiceHandler', () => ({
    sharePatient: () => Promise.resolve({}),
  }));
  const { getByText } = render(<SharePatient />);
  const caregiver = getByText(/jane.*doe/i);
  fireEvent.click(caregiver);
  await waitFor(() => expect(window.alert).toHaveBeenCalled());
});

test('search is case insensitive and removes spaces', async () => {
  const { getByPlaceholderText, getByText, queryByText } = render(<SharePatient />);
  const searchInput = getByPlaceholderText(/search .* caregivers/i);
  fireEvent.change(searchInput, { target: { value: ' john  doe ' } });
  await waitFor(() => {
    expect(queryByText(/john.*doe/i)).toBeInTheDocument();
    expect(queryByText(/jane.*doe/i)).not.toBeInTheDocument();
  });
});
