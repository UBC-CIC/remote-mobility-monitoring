import React from "react";
import {useSelector} from "react-redux";
import PropTypes from "prop-types";
import {State} from "./store";
import { useNavigate, Navigate, useLocation} from "react-router-dom";
import {createUser} from "./Cognito";

const ProtectedRoute = ({children}: any) => {
    let cognitoUser = useSelector((state: State) => state.cognitoUser);
    const nav = useNavigate();
    const location = useLocation();
    if (cognitoUser) {
        return children;
    }
    const username = localStorage.getItem("username");
    if (!username) {
        return <Navigate to="/login" state={{ from: location}} replace />;
    }
    cognitoUser = createUser(username);
    let error = false;
    cognitoUser.getSession(function(err: Error) {
        if (err) {
            error = true;
        }
    });
    if(error) {
        return <Navigate to="/login" state={{ from: location}} replace />;
    }
    return children;
};

ProtectedRoute.propTypes = {
    children: PropTypes.any,
    onClickOut: PropTypes.func,
};

export default ProtectedRoute;
