import React from 'react';
import { render, fireEvent } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import AdminDashboard from './AdminDashboard';

const renderWithRouter = (ui, { route = '/addpatient' } = {}) => {
  window.history.pushState({}, 'Test page', route);
  return render(ui, { wrapper: MemoryRouter });
};

describe('AdminDashboard', () => {
  test('renders the search bar', () => {
    const { getByPlaceholderText } = renderWithRouter(<AdminDashboard />);
    expect(getByPlaceholderText('Search 0 caregivers')).toBeInTheDocument();
  });

  test('adds a new caregiver when add button is clicked', () => {
    const { getByText } = renderWithRouter(<AdminDashboard />);
    fireEvent.click(getByText('Add New Caregiver'));
    expect(window.location.pathname).toBe('/addcaregiver');
  });

  test('removes a caregiver when remove button is clicked', () => {
    const { getByText } = renderWithRouter(<AdminDashboard />);
    const removeButton = getByText('Remove');
    fireEvent.click(removeButton);
    expect(removeButton).not.toBeInTheDocument();
  });

  test('filters the caregiver list when search term is entered', () => {
    const { getByPlaceholderText, getByText } = renderWithRouter(<AdminDashboard />);
    const searchInput = getByPlaceholderText('Search 0 caregivers');
    fireEvent.change(searchInput, { target: { value: 'John' } });
    expect(getByText('John Doe')).toBeInTheDocument();
    expect(getByText('Remove')).toBeInTheDocument();
    expect(getByText('Jane Doe')).not.toBeInTheDocument();
  });
});
