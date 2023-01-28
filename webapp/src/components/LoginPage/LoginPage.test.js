import React from 'react';
import { render, fireEvent, screen } from '@testing-library/react';
import Enzyme, {shallow} from 'enzyme';
import Adapter from 'enzyme-adapter-react-16';
import LoginPage from './LoginPage';

describe('LoginPage', () => {
    test('renders a header with the text "Sign in to"', () => {
        const { getByText } = render(<LoginPage />,);
        const headerElement = getByText('Sign in to');
        expect(headerElement).toBeInTheDocument();
    });

    test('renders a header with the text Mobility Monitor', () => {
        const { getByText } = render(<LoginPage />,);
        const headerElement = getByText('Mobility Monitor');
        expect(headerElement).toBeInTheDocument();
    });

    test('renders a form with a username and password input', () => {
        const { getByLabelText } = render(<LoginPage />);
        const usernameInput = screen.getByPlaceholderText('Username');
        const passwordInput = screen.getByPlaceholderText('Password');
        expect(usernameInput).toBeInTheDocument();
        expect(passwordInput).toBeInTheDocument();
    });

    test('initial login type should be caregiver', () => {
        const { getByText } = render(<LoginPage />,);
        const headerElement = getByText('Organization administrators can');
        expect(headerElement).toBeInTheDocument();
    })

    test('changes login type to admin when clicked', () => {
        const { getByText } = render(<LoginPage />);
        fireEvent.click(getByText("Login here"));
        const loginType = getByText('Caregivers can');
        expect(loginType).toBeInTheDocument();
    });

    test('changes login type back to caregiver when clicked twice', () => {
        const { getByText } = render(<LoginPage />);
        fireEvent.click(getByText('Login here'));
        let loginType = getByText('Caregivers can');
        expect(loginType).toBeInTheDocument();
        fireEvent.click(getByText('Login here'));
        loginType = getByText("Organization administrators can");
    });

})
