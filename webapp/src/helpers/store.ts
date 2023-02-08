import { createStore } from "redux";
import * as AmazonCognitoIdentity from "amazon-cognito-identity-js";

const initialState = {
    cognitoUser: null,
};

export interface State {
  cognitoUser: any;
}

interface Action {
  type: string;
  payload: any;
}

const reducer = (state = initialState, action: Action) => {
    switch (action.type) {
    case "USER":
        return { ...state, cognitoUser: action.payload };
    default:
        return state;
    }
};

const store = createStore(reducer);

export default store;
