import { render, fireEvent } from "@testing-library/react";
import { Router } from "react-router-dom";
import { createMemoryHistory } from "history";
import ForceChangePassword from "./ForceChangePassword";

test("renders update password page", () => {
  const history = createMemoryHistory();
  const { getByText } = render(
    <Router history={history}>
      <ForceChangePassword />
    </Router>
  );
  expect(getByText(/Update password/i)).toBeInTheDocument();
});

test("displays an error message with invalid inputs", () => {
  const history = createMemoryHistory();
  const { getByLabelText, getByText, getByTestId } = render(
    <Router history={history}>
      <ForceChangePassword />
    </Router>
  );

  const newPasswordInput = getByLabelText(/New Password/i);
  const confirmPasswordInput = getByLabelText(/Confirm Password/i);
  const submitButton = getByText(/Change Password/i);

  fireEvent.change(newPasswordInput, { target: { value: "invalid" } });
  fireEvent.change(confirmPasswordInput, { target: { value: "invalid" } });
  fireEvent.click(submitButton);

  expect(getByTestId("error-message")).toBeInTheDocument();
});

test("changes password with valid inputs", () => {
  const history = createMemoryHistory();
  const { getByLabelText, getByText, queryByTestId } = render(
    <Router history={history}>
      <ForceChangePassword />
    </Router>
  );

  const newPasswordInput = getByLabelText(/New Password/i);
  const confirmPasswordInput = getByLabelText(/Confirm Password/i);
  const submitButton = getByText(/Change Password/i);

  fireEvent.change(newPasswordInput, { target: { value: "ValidPass123!" } });
  fireEvent.change(confirmPasswordInput, { target: { value: "ValidPass123!" } });
  fireEvent.click(submitButton);

  expect(queryByTestId("error-message")).not.toBeInTheDocument();
});
