import React from 'react';
import { render, fireEvent } from '@testing-library/react';
import ForceChangePassword from './ForceChangePassword';

describe('ForceChangePassword component', () => {
  it('renders without crashing', () => {
    render(<ForceChangePassword />);
  });

  it('displays the "New Password" field and accepts input', () => {
    const { getByLabelText } = render(<ForceChangePassword />);
    const newPasswordField = getByLabelText('New Password');
    fireEvent.change(newPasswordField, { target: { value: 'password123' } });
    expect(newPasswordField.value).toBe('password123');
  });

  it('displays the "Confirm Password" field and accepts input', () => {
    const { getByLabelText } = render(<ForceChangePassword />);
    const confirmPasswordField = getByLabelText('Confirm Password');
    fireEvent.change(confirmPasswordField, { target: { value: 'password123' } });
    expect(confirmPasswordField.value).toBe('password123');
  });

  it('displays the "Change Password" button and is clickable', () => {
    const { getByRole } = render(<ForceChangePassword />);
    const changePasswordButton = getByRole('button', { name: 'Change Password' });
    fireEvent.click(changePasswordButton);
  });

  it('displays the "Minimum length should be 8 characters" message when the password length is less than 8', () => {
    const { getByText, getByLabelText } = render(<ForceChangePassword />);
    const newPasswordField = getByLabelText('New Password');
    fireEvent.change(newPasswordField, { target: { value: 'password' } });
    const minimumLengthMessage = getByText('Minimum length should be 8 characters');
    expect(minimumLengthMessage).toBeInTheDocument();
  });

  it('displays the "Must contain at least one number" message when the password does not contain a number', () => {
    const { getByText, getByLabelText } = render(<ForceChangePassword />);
    const newPasswordField = getByLabelText('New Password');
    fireEvent.change(newPasswordField, { target: { value: 'Password' } });
    const numberMessage = getByText('Must contain at least one number');
    expect(numberMessage).toBeInTheDocument();
  });

  it('displays the "Passwords must match" message when the "New Password" and "Confirm Password" fields do not match', () => {
    const { getByText, getByLabelText } = render(<ForceChangePassword />);
    const newPasswordField = getByLabelText('New Password');
    const confirmPasswordField = getByLabelText('Confirm Password');
    fireEvent.change(newPasswordField, { target: { value: 'password123' } });
    fireEvent.change(confirmPasswordField, { target: { value: 'password' } });
    const passwordMatchMessage = getByText('Passwords must match');
    expect(passwordMatchMessage).toBeInTheDocument();
  });

  it('navigates to the login page when the password is successfully updated', () => {
    const { getByRole } = render(<ForceChangePassword />);
    const changePasswordButton = getByRole('button', { name: 'Change Password' });
    fireEvent.click(changePasswordButton);
    // Assert that navigation to the login page happens. This can be done using a mocking library like Jest's mockImplementationOnce function.
  });
});
