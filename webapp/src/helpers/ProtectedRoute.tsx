import React from "react";
import PropTypes from "prop-types";
import { useNavigate, Navigate, useLocation} from "react-router-dom";
import {userTypes} from "./types";

const ProtectedRoute = ({type, children}: Props) => {
    // TODO: remove this line after testing
    return children;
    // const nav = useNavigate();
    // const location = useLocation();
    // const username = localStorage.getItem("username");
    // /* If there is no stored username then this will navigate you
    // * to the login page.
    // * */
    // if (!username) {
    // return <Navigate to="/login" state={{ from: location}} replace />;
    // }
    // /* If the stored username is not of the correct type then this navigates you to
    // * the login page. for example if the stored username is adm-... and they try to
    // * access a caregiver page then this will block the request.
    // * */
    // if (!username.startsWith(userTypes[type])) {
    // return <Navigate to="/login" state={{ from: location}} replace />;
    // }
    // return children;
};

type Props = {
    type: string, 
    children: React.ReactElement,
}

export default ProtectedRoute;
