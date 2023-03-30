import React from "react";
import {userTypes, getCaregiverId, getIdToken} from "./types";

// Get userID from local storage
const sub = localStorage.getItem("sub");

/* 
* For now the org_id is hard coded as discussed with CIC since we 
* decided to have a single tenant architecture. This can be changed in the future
* if its decided to make the architecture support multi-tenant.
* */
const org_id = process.env.REACT_APP_ORG_ID;

type response = {
    status: number,
    json: () => Promise<object>,
    text: () => Promise<string>,
}

/*
 * This is the ServiceHandler object that will be eported to be used by all the other
 * pages to make the relevant API calls to the backend.
* */
export const ServiceHandler = {
    addCaregiver: (firstName: string, lastName: string, email: string, contactNumber: string) => {
        const idToken = getIdToken();
        /*
         * Add Caregiver API called by admin users to create new caregivers for their organization.
        * */
        const base_url =createBaseUrl("admin");
        const url = base_url.concat("/caregivers");
        const data = {
            "email": email,
            "first_name": firstName,
            "last_name": lastName,
            "title": "Caregiver",
            "phone_number": contactNumber,
            "organization_id": org_id,
        };
        const req = fetch(url, {
            method: "POST", 
            mode: "cors", 
            cache: "no-cache", 
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json",
                "Authorization": `Bearer ${idToken}`
            },
            body: JSON.stringify(data),
        });
        return addCallbacks(req);
    },
    addPatient: (email: string) => {
        const idToken = getIdToken();
        const base_url =createBaseUrl("caregiver");
        
        const url = base_url.concat("/caregivers/").concat(getCaregiverId()).concat("/patients");
        const data = {
            "patient_email": email,
            "send_email": true
        };
        const req = fetch(url, {
            method: "POST", 
            mode: "cors", 
            cache: "no-cache", 
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json",
                "Authorization": `Bearer ${idToken}`
            },
            body: JSON.stringify(data),
        });
        return addCallbacks(req);
    },
    deletePatient: (patientId: string) => {
        const idToken = getIdToken();
        const base_url = createBaseUrl("caregiver");
        const url = base_url.concat("/patients/").concat(patientId);
        const req = fetch(url, {
            method: "DELETE", 
            mode: "cors", 
            cache: "no-cache", 
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json"
            },
        });
        return addCallbacks(req);
    },
    getAllPatients: () => {
        const idToken = getIdToken();
        const base_url =createBaseUrl("caregiver");
        const caregiverId = getCaregiverId();
        const url = base_url.concat("/caregivers/").concat(caregiverId).concat("/patients");
        const req = fetch(url, {
            method: "GET", 
            mode: "cors", 
            cache: "no-cache", 
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json",
                "Authorization": `Bearer ${idToken}`
            },
        });
        return addCallbacks(req);
    },
    getPatient: (patientId: string) => {
        const idToken = getIdToken();
        const base_url =createBaseUrl("caregiver");
        const caregiverId = getCaregiverId();
        const url = base_url.concat("/patients/").concat(patientId);
        const req = fetch(url, {
            method: "GET", 
            mode: "cors", 
            cache: "no-cache", 
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json",
                "Authorization": `Bearer ${idToken}`
            },
        });
        return addCallbacks(req);
    },
    getOrg: () => {
        const idToken = getIdToken();
        const base_url =createBaseUrl("admin");
        if (!org_id) throw new Error("Check organization ID");
        const url = base_url.concat("/organizations/").concat(org_id);
        const req = fetch(url, {
            method: "GET", 
            mode: "cors", 
            cache: "no-cache", 
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json",
                "Authorization": `Bearer ${idToken}`
            },
        });
        return addCallbacks(req);
    },
    deleteCaregiver: (carId: string) => {
        const idToken = getIdToken();
        const base_url =createBaseUrl("admin");
        const url = base_url.concat("/caregivers/").concat(carId);
        const req = fetch(url, {
            method: "DELETE", 
            mode: "cors", 
            cache: "no-cache", 
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json",
                "Authorization": `Bearer ${idToken}`
            },
        });
        return addCallbacks(req);
    },
};

/*
 * This is a failsafe function. We already check that the user has logged in
 * using ProtectedRoute. However, in case that fails we check here again before
 * allowing users to make an API call. Note here that this is just security in the
 * frontend and the backed must still authenticate tokens. This will also throw an
 * error if the env variable API_URL is not defined. 
* */
const createBaseUrl = (loginType: string) => {
    // TODO: Don't throw errors
    const base_url = process.env.REACT_APP_API_URL;
    if (!base_url) {
        const errMsg = "API URL is invalid";
        alert(errMsg);
        throw new Error(errMsg);
    }
    return base_url;
};

/*
 * This function takes in an argument p of type Promise and adds 
 * the necessary callbacks to the promise. If the response is valid
 * and has a 200 code, we parse the response and return it. If there is an
 * error then we return the error. This function can be called by all
 * the API functions to register the required callbacks.
* */
const addCallbacks = (p: Promise<response>) => {
    return p.then((res: response) => {
        console.log(res.status);
        if (res.status === 200) {
            return res.json();
        }
        else {
            return res.text()
                .then((data: any) => {
                    throw new Error(data);
                });
        }
    });
};

