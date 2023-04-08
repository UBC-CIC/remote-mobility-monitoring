import { render, screen, fireEvent } from '@testing-library/react';
import AddCaregiver from './AddCaregiver';

describe('AddCaregiver', () => {
  it('renders the component without crashing', () => {
    render(<AddCaregiver />);
    expect(screen.getByText('Add a caregiver')).toBeInTheDocument();
  });

  it('displays an error message if first name is not entered', () => {
    render(<AddCaregiver />);
    fireEvent.click(screen.getByText('Add Caregiver'));
    expect(screen.getByText('First name cannot be empty')).toBeInTheDocument();
  });

  it('displays an error message if last name is not entered', () => {
    render(<AddCaregiver />);
    fireEvent.change(screen.getByLabelText('First Name'), { target: { value: 'John' } });
    fireEvent.click(screen.getByText('Add Caregiver'));
    expect(screen.getByText('Last name cannot be empty')).toBeInTheDocument();
  });

  it('displays an error message if email is not entered', () => {
    render(<AddCaregiver />);
    fireEvent.change(screen.getByLabelText('First Name'), { target: { value: 'John' } });
    fireEvent.change(screen.getByLabelText('Last Name'), { target: { value: 'Doe' } });
    fireEvent.click(screen.getByText('Add Caregiver'));
    expect(screen.getByText('Email cannot be empty')).toBeInTheDocument();
  });

  it('displays an error message if contact number is not entered', () => {
    render(<AddCaregiver />);
    fireEvent.change(screen.getByLabelText('First Name'), { target: { value: 'John' } });
    fireEvent.change(screen.getByLabelText('Last Name'), { target: { value: 'Doe' } });
    fireEvent.change(screen.getByLabelText('Email'), { target: { value: 'johndoe@example.com' } });
    fireEvent.click(screen.getByText('Add Caregiver'));
    expect(screen.getByText('Contact Number cannot be empty')).toBeInTheDocument();
  });

  it('submits the form when Add Caregiver button is clicked', () => {
    render(<AddCaregiver />);
    fireEvent.change(screen.getByLabelText('First Name'), { target: { value: 'John' } });
    fireEvent.change(screen.getByLabelText('Last Name'), { target: { value: 'Doe' } });
    fireEvent.change(screen.getByLabelText('Email'), { target: { value: 'johndoe@example.com' } });
    fireEvent.change(screen.getByLabelText('Contact number'), { target: { value: '1234567890' } });
    fireEvent.click(screen.getByText('Add Caregiver'));
    expect(screen.getByText('Caregiver sucessfully added. They have received an email with further instructions.')).toBeInTheDocument();
  });

  it('navigates to the admin dashboard when Back button is clicked', () => {
    const { container } = render(<AddCaregiver />);
    fireEvent.click(container.querySelector('.icon'));
    expect(screen.getByText('Admin Dashboard')).toBeInTheDocument();
  });

  it('navigates to the admin dashboard when Admin Dashboard is clicked', () => {
    render(<AddCaregiver />);
    fireEvent.click(screen.getByText('Dashboard'));
    expect(screen.getByText('Admin Dashboard')).toBeInTheDocument();
  });
});
