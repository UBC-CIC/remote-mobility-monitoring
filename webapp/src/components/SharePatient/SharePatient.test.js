import { MemoryRouter } from 'react-router-dom';
import { render, screen } from '@testing-library/react';
import SharePatient from './SharePatient';

const renderWithRouter = (ui, { route = '/addpatient' } = {}) => {
  window.history.pushState({}, 'Test page', route);
  return render(ui, { wrapper: MemoryRouter });
};

describe('SharePatient component', () => {
  it('renders the back button', () => {
    renderWithRouter(<SharePatient />, { route: '/dashboard/share-patient/encryptedPatientId' });
    expect(screen.getByText(/Back/i)).toBeInTheDocument();
  });

  it('renders the search icon', () => {
    renderWithRouter(<SharePatient />, { route: '/dashboard/share-patient/encryptedPatientId' });
    expect(screen.getByLabelText(/search/i)).toBeInTheDocument();
  });

  it('renders the caregiver search input', () => {
    renderWithRouter(<SharePatient />, { route: '/dashboard/share-patient/encryptedPatientId' });
    expect(screen.getByPlaceholderText(/Search/i)).toBeInTheDocument();
  });

  it('renders the caregiver entries', () => {
    renderWithRouter(<SharePatient />, { route: '/dashboard/share-patient/encryptedPatientId' });
    expect(screen.getAllByRole('button', {name: /entry/i})).toHaveLength(0);
  });
});