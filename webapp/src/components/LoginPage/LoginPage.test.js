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

    test("renders a header with the text Mobility Monitor", () => {
        const { getByText } = render(
            <Provider store={store}>
                <Router location={ location }>
                    <LoginPage/>
                </Router>
            </Provider>
        );

        const headerElement = getByText("Mobility Monitor");
        expect(headerElement).toBeInTheDocument();
    });

    test("renders a form with a email and password input", () => {
        const {getByPlaceholderText } = render(
            <Provider store={store}>
                <Router location={ location }>
                    <LoginPage/>
                </Router>
            </Provider>
        );

        const usernameInput = getByPlaceholderText("Email");
        const passwordInput = getByPlaceholderText("Password");
        expect(usernameInput).toBeInTheDocument();
        expect(passwordInput).toBeInTheDocument();
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

    test("changes login type to admin when clicked", () => {
        const { getByText } = render(
            <Provider store={store}>
                <Router location={ location }>
                    <LoginPage/>
                </Router>
            </Provider>
        );
        fireEvent.click(getByText("Login here"));
        const loginType = getByText("Mobility Monitor as admin");
        expect(loginType).toBeInTheDocument();
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

});
