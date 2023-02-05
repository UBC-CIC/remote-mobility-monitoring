import * as AmazonCognitoIdentity from "amazon-cognito-identity-js";

export const createUser = (email: string) => {
    const userPoolId = "secret";
    const poolData = {
        UserPoolId: userPoolId,
        ClientId: "secret", // Your client id here
    };
    const userPool = new AmazonCognitoIdentity.CognitoUserPool(poolData);

    const userData = {
        Username: email,
        Pool: userPool,
    };
    const cognitoUser = new AmazonCognitoIdentity.CognitoUser(userData);
    return cognitoUser;
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


