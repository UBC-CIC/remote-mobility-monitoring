import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import CaregiverNavbar from './CaregiverNavbar';

const renderWithRouter = (ui, { route = '/' } = {}) => {
window.history.pushState({}, 'Test page', route);
return render(ui, { wrapper: MemoryRouter });
};

describe('CaregiverNavbar component', () => {
test('renders the navbar title', () => {
renderWithRouter(<CaregiverNavbar />);
const navbarTitle = screen.getByText(/Mobility Monitor/i);
expect(navbarTitle).toBeInTheDocument();
});

test('navigates to dashboard on clicking the Dashboard link', () => {
const { getByText } = renderWithRouter(<CaregiverNavbar />);
const dashboardLink = getByText(/Dashboard/i);
fireEvent.click(dashboardLink);
expect(window.location.pathname).toBe('/');
});

test('navigates to change password on clicking the Change Password link', () => {
const { getByText } = renderWithRouter(<CaregiverNavbar />);
const changePwdLink = getByText(/Change Password/i);
fireEvent.click(changePwdLink);
expect(window.location.pathname).toBe('/');
});

test('logs out and navigates to login page on clicking the Logout link', () => {
const { getByText } = renderWithRouter(<CaregiverNavbar />);
const logoutLink = getByText(/Logout/i);
fireEvent.click(logoutLink);
expect(window.location.pathname).toBe('/');
});
});