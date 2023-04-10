import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import AdminNavbar from './AdminNavbar';
import { MemoryRouter } from 'react-router-dom';

const renderWithRouter = (ui, { route = '/' } = {}) => {
  window.history.pushState({}, 'Test page', route);
  return render(ui, { wrapper: MemoryRouter });
};

test('renders the navbar with correct text', () => {
  renderWithRouter(<AdminNavbar />);
  const navText = screen.getByText(/Mobility Monitor/i);
  expect(navText).toBeInTheDocument();
});

test('clicking dashboard link navigates to admin dashboard page', () => {
  const { getByText } = renderWithRouter(<AdminNavbar />);
  fireEvent.click(getByText(/dashboard/i));
  expect(window.location.pathname).toBe('/');
});

test('clicking add caregivers link navigates to add caregiver page', () => {
  const { getByText } = renderWithRouter(<AdminNavbar />);
  fireEvent.click(getByText(/add caregivers/i));
  expect(window.location.pathname).toBe('/');
});

test('clicking logout link logs out the user and navigates to login page', () => {
  const { getByText } = renderWithRouter(<AdminNavbar />);
  fireEvent.click(getByText(/logout/i));
  expect(window.location.pathname).toBe('/');
});

