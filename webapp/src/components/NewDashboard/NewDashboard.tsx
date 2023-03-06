import Sidebar from "./sidebar";
import React, { useState } from "react";

// Define the patient list     give an example on how to call the sidebar function
type Patient = {
    name: string;
    gender: string;
    age: number;
    mobilityData: {
      date: string;
      stepLength: number;
      doubleSupportTime: number;
      walkingSpeed: number;
      walkingAsymmetry: number;
      distanceWalked: number;
    }[];
  };

const patients: Patient[] = [
    {
        name: "Alice",
        gender: "Female",
        age: 32,
        mobilityData: [
            {
                date: "2023-01-01",
                stepLength: 60,
                doubleSupportTime: 10,
                walkingSpeed: 1.5,
                walkingAsymmetry: 5,
                distanceWalked: 1000,
            },
        // ...more mobility data
        ],
    },
    // ...more patients
];
function NewDashboard(){


}

export default NewDashboard;