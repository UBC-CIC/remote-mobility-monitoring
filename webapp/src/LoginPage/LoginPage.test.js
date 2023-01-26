import React from 'react';
import { render, fireEvent } from '@testing-library/react';
import LoginPage from './LoginPage';

describe('LoginPage', () => {
    it('renders a header with the text "Login Page"', () => {
    const { getByText } = render(<LoginPage />,);
    const headerElement = getByText('Sign in to');
    expect(headerElement).toBeInTheDocument();
  });

})
