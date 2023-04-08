import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import ChangePassword from "./ChangePassword";

describe("ChangePassword", () => {
  test("renders the component without crashing", () => {
    render(<ChangePassword />);
  });

  test("contains a CaregiverNavbar component", () => {
    render(<ChangePassword />);
    const navbar = screen.getByRole("navigation");
    expect(navbar).toBeInTheDocument();
  });

  test("contains three TextField components", () => {
    render(<ChangePassword />);
    const textFields = screen.getAllByRole("textbox");
    expect(textFields.length).toBe(3);
  });

  test("contains a button with text 'Change Password'", () => {
    render(<ChangePassword />);
    const button = screen.getByRole("button", { name: "Change Password" });
    expect(button).toBeInTheDocument();
  });

  test("contains a button with text 'Loading' when the loading state is true", () => {
    render(<ChangePassword />);
    const loadingButton = screen.getByRole("button", { name: "Loading" });
    expect(loadingButton).toBeInTheDocument();
  });

  test("handleKey function is called when a key is pressed", () => {
    const handleKey = jest.fn();
    render(<ChangePassword handleKey={handleKey} />);
    fireEvent.keyUp(screen.getByRole("textbox"), { key: "Enter", code: "Enter" });
    expect(handleKey).toHaveBeenCalled();
  });

  test("handleSubmit function is called when the 'Change Password' button is clicked", () => {
    const handleSubmit = jest.fn();
    render(<ChangePassword handleSubmit={handleSubmit} />);
    fireEvent.click(screen.getByRole("button", { name: "Change Password" }));
    expect(handleSubmit).toHaveBeenCalled();
  });

  test("cognitoUser.changePassword function is called with the correct arguments when the handleSubmit function is called", () => {
    const changePassword = jest.fn();
    const cognitoUser = { changePassword };
    localStorage.setItem("username", "testUser");
    render(<ChangePassword />);
    const oldPasswordField = screen.getByLabelText("Old Password");
    const newPasswordField = screen.getByLabelText("New Password");
    const confirmPasswordField = screen.getByLabelText("Confirm Password");
    fireEvent.change(oldPasswordField, { target: { value: "oldPassword" } });
    fireEvent.change(newPasswordField, { target: { value: "newPassword123!" } });
    fireEvent.change(confirmPasswordField, { target: { value: "newPassword123!" } });
    fireEvent.click(screen.getByRole("button", { name: "Change Password" }));
    expect(changePassword).toHaveBeenCalledWith(
      "oldPassword",
      "newPassword123!",
      expect.any(Function)
    );
  });
});
