import React from 'react';
import { render, screen } from '@testing-library/react';
import VerifyPatient from './VerifyPatient';

describe('VerifyPatient component', () => {
  test('renders the "Dashboard" button', () => {
    render(<VerifyPatient />);
    const dashboardButton = screen.getByText(/dashboard/i);
    expect(dashboardButton).toBeInTheDocument();
  });

  test('displays the patient email', () => {
    const email = 'test@example.com';
    const { getByText } = render(<VerifyPatient params={{ emailEncrypt: email }} />);
    const emailText = getByText(email);
    expect(emailText).toBeInTheDocument();
  });

  test('generates an authentication code', async () => {
    jest.spyOn(global, 'fetch').mockResolvedValueOnce({
      json: () => ({ auth_code: '1234' }),
    });

    const { findByText } = render(<VerifyPatient />);
    const qrCode = await findByText(/verify patient/i);
    expect(qrCode).toBeInTheDocument();
    expect(global.fetch).toHaveBeenCalledWith('/api/addPatient', expect.any(Object));
  });

  test('displays the QR code with the auth code and caregiver ID', async () => {
    const { findByTestId } = render(<VerifyPatient />);
    const qrCode = await findByTestId('qr-code');
    expect(qrCode).toBeInTheDocument();
    expect(qrCode.getAttribute('src')).toMatch(/auth_code/);
    expect(qrCode.getAttribute('src')).toMatch(/caregiver_id/);
  });

  test('resizes the QR code when the window is resized', () => {
    const { getByTestId } = render(<VerifyPatient />);
    const qrCode = getByTestId('qr-code');
    expect(qrCode.getAttribute('style')).toContain('width:');

    global.innerWidth = 800;
    global.innerHeight = 600;
    global.dispatchEvent(new Event('resize'));

    expect(qrCode.getAttribute('style')).toContain('width:');
  });

  test('navigates back to the dashboard when the "Dashboard" button is clicked', () => {
    const { getByText } = render(<VerifyPatient />);
    const dashboardButton = getByText(/dashboard/i);
    dashboardButton.click();
    expect(window.location.pathname).toBe('/dashboard');
  });

  test('displays an error message if there is an error generating the auth code', async () => {
    jest.spyOn(global, 'fetch').mockRejectedValueOnce(new Error('Failed to generate auth code'));

    const { findByText } = render(<VerifyPatient />);
    const errorMessage = await findByText(/failed to generate auth code/i);
    expect(errorMessage).toBeInTheDocument();
    expect(global.fetch).toHaveBeenCalledWith('/api/addPatient', expect.any(Object));
  });

  test('does not render anything if the email is not present', () => {
    const { container } = render(<VerifyPatient />);
    expect(container.firstChild).toBeNull();
  });
});
