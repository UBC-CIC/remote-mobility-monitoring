import React from "react";

const base_url = process.env.REACT_APP_API_URL;

export const ServiceHandler = {
    addCaregiver: (firstName: string, lastName: string, email: string, contactNumber: string) => {
        return new Promise((resolve, reject) => {
            if (! base_url) {
                reject(new Error("API URL is invalid"));
                return;
            }
            const url = base_url.concat("/caregivers/car-0fd5ed55-0ca2-4aa6-9582-6a39368210eb");
            const data = {};
            fetch(url, {
                method: "GET", 
                mode: "cors", 
                cache: "no-cache", 
                headers: {
                    "Content-Type": "application/json"
                },
            }).then((val) => {
                console.log(val);
            }).catch((err) => {
                console.log(err);
            });
            resolve("value");
        });

    }
};
