import React from "react";
import { render, fireEvent, cleanup } from "@testing-library/react";
import LoginPage from "./LoginPage";
import {Router} from "react-router-dom";
import {Provider} from "react-redux";
import { createStore } from "redux";
import store from "../../helpers/store";

afterEach(cleanup);
const location = { pathname: "/login" };

describe("LoginPage", () => {
    test("renders a header with the text \"Sign in to\"", () => {
        const { getByText } = render(
            <Provider store={store}>
                <Router location={ location }>
                    <LoginPage/>
                </Router>
            </Provider>
        );

        const headerElement = getByText("Sign in to");
        expect(headerElement).toBeInTheDocument();
    });

    test("initial login type should be caregiver", () => {
        const { getByText } = render(
            <Provider store={store}>
                <Router location={ location }>
                    <LoginPage/>
                </Router>
            </Provider>
        );


        const headerElement = getByText("Organization administrators can");
        expect(headerElement).toBeInTheDocument();
    });

    test("changes login type back to caregiver when clicked twice", () => {
        const { getByText } = render(
            <Provider store={store}>
                <Router location={ location }>
                    <LoginPage/>
                </Router>
            </Provider>
        );
        fireEvent.click(getByText("Login here"));
        let loginType = getByText("Caregivers can");
        expect(loginType).toBeInTheDocument();
        fireEvent.click(getByText("Login here"));
        loginType = getByText("Organization administrators can");
    });

    test("renders an email input field", () => {
        const { getByLabelText } = render(
            <Provider store={store}>
                <Router location={ location }>
                    <LoginPage/>
                </Router>
            </Provider>
        );
        const emailInput = getByLabelText("Email");
        expect(emailInput).toBeInTheDocument();
    });
    
    test("renders a password input field", () => {
        const { getByLabelText } = render(
            <Provider store={store}>
                <Router location={ location }>
                    <LoginPage/>
                </Router>
            </Provider>
        );
        const passwordInput = getByLabelText("Password");
        expect(passwordInput).toBeInTheDocument();
    });
    
    test("calls handleLogin when Login button is clicked", () => {
        const { getByText } = render(
            <Provider store={store}>
                <Router location={ location }>
                    <LoginPage/>
                </Router>
            </Provider>
        );
        const loginButton = getByText("Login");
        const handleLogin = jest.fn();
        loginButton.onclick = handleLogin;
        fireEvent.click(loginButton);
        expect(handleLogin).toHaveBeenCalled();
    });
    

});
