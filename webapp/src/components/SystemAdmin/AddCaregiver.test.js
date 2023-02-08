import React from "react";
import { render, fireEvent } from "@testing-library/react";
import SystemAdmin from "./SystemAdmin";

describe("SystemAdmin", () => {
    test("renders a header with the text Sign Up", () => {
        const { getByText } = render(<SystemAdmin />,);
        const headerElement = getByText("Sign Up");
        expect(headerElement).toBeInTheDocument();
    });

    test("renders signup form with a username, email and contact number", () => {
        const {getByPlaceholderText } = render(<SystemAdmin />);
        const usernameInput = getByPlaceholderText("Name");
        const emailInput = getByPlaceholderText("Email");
        const contact_number_Input = getByPlaceholderText("Contact number");
        expect(usernameInput).toBeInTheDocument();
        expect(emailInput).toBeInTheDocument();
        expect(contact_number_Input).toBeInTheDocument();
    });

});
