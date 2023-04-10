import ServiceHandler from '../../helpers/ServiceHandler';
import { render, screen } from '@testing-library/react';
import SharePatient from './SharePatient';

describe("getOrg function", () => {
  it("should retrieve caregivers from service handler and exclude the current user", async () => {
    localStorage.setItem("sub", "currentUserId");
    const data = {
      caregivers: [
        {
          caregiver_id: "123",
          first_name: "John",
          last_name: "Doe"
        },
        {
          caregiver_id: "456",
          first_name: "Jane",
          last_name: "Doe"
        },
        {
          caregiver_id: "789",
          first_name: "Bob",
          last_name: "Smith"
        }
      ]
    };
    ServiceHandler.getOrg.mockResolvedValueOnce(data);

    const expectedCaregivers = [      {        caregiver_id: "123",        first_name: "John",        last_name: "Doe"      },      {        caregiver_id: "456",        first_name: "Jane",        last_name: "Doe"      }    ];

    await act(async () => {
      render(<SharePatient />);
    });

    expect(ServiceHandler.getOrg).toHaveBeenCalledTimes(1);
    expect(ServiceHandler.getOrg).toHaveBeenCalledWith();
    expect(setCaregivers).toHaveBeenCalledTimes(1);
    expect(setCaregivers).toHaveBeenCalledWith(expectedCaregivers);
  });
});

describe("handleShare function", () => {
  it("should share patient metrics with a caregiver and display success alert", async () => {
    ServiceHandler.sharePatient.mockResolvedValueOnce();

    await act(async () => {
      render(<SharePatient />);
    });

    const caregiverId = "123";
    const caregiverName = "John Doe";

    await act(async () => {
      fireEvent.click(screen.getByText(caregiverName));
    });

    expect(ServiceHandler.sharePatient).toHaveBeenCalledTimes(1);
    expect(ServiceHandler.sharePatient).toHaveBeenCalledWith(
      caregiverId,
      patientId
    );
    expect(window.alert).toHaveBeenCalledTimes(1);
    expect(window.alert).toHaveBeenCalledWith(
      `Patient metrics shared with ${caregiverName}`
    );
  });
});
