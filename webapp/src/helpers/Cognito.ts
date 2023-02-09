import * as AmazonCognitoIdentity from "amazon-cognito-identity-js";
import {userTypes, userTypesLength} from "./types";

export const createUser = (username: string, trim: boolean) => {
    const userPoolID = process.env.REACT_APP_USER_POOL_ID;
    const clientId = process.env.REACT_APP_CLIENT_ID;
    const poolData = {
        UserPoolId: userPoolID? userPoolID: "",
        ClientId: clientId? clientId : "",
    };
    try { 
        const userPool = new AmazonCognitoIdentity.CognitoUserPool(poolData);

        if (trim) {
            username = username.substring(userTypesLength);
        }
        const userData = {
            Username: username,
            Pool: userPool,
        };
        const cognitoUser = new AmazonCognitoIdentity.CognitoUser(userData);
        return cognitoUser;
    }
    catch (e) {
        return null;
    }
};

export const logout = () => {
    const username = localStorage.getItem("sub");
    if (!username) return;
    const cognitoUser = createUser(username, true);
    if (!cognitoUser) return;
    cognitoUser.signOut();
};

export const login = (cognitoUser: AmazonCognitoIdentity.CognitoUser, email: string, password: string, 
    callback: AmazonCognitoIdentity.IAuthenticationCallback) => {
    const authenticationData = {
        Username: email,
        Password: password,
    };
    const authenticationDetails = new AmazonCognitoIdentity.AuthenticationDetails(
        authenticationData
    );

    cognitoUser.authenticateUser(authenticationDetails, callback);
};

export const createUserAndGetSession = (username: string | null, trim: boolean, 
    onError: () => void, onSuccess: () => void): AmazonCognitoIdentity.CognitoUser | null => {
    if (! username) {
        onError();
        return createUser("", false);
    }
    const cognitoUser = createUser(username, trim);
    if (!cognitoUser) {
        onError();
        return createUser("", false);
    } 
    else {
        console.log(cognitoUser.getUsername());
        cognitoUser.getSession((err: Error) => {
            if (err) {
                console.log(err);
                onError();
            }
            else {
                onSuccess();
            }
        });
        return cognitoUser;
    }
};


