import React, {useState, useEffect} from "react";
import {FaArrowRight, FaSearch} from "react-icons/fa";
import { useNavigate, useParams} from "react-router-dom";
import {ServiceHandler} from "../../helpers/ServiceHandler";
import AdminNavbar from "../Navbar/AdminNavbar";
import "./SharePatient.css";
import {decrypt} from "../../helpers/Crypto";

type caregiver = {
    caregiver_id: string,
    first_name: string,
    last_name: string
}


function SharePatient() {
    const nav = useNavigate();
    const [caregivers, setCaregivers]:React.SetStateAction<any> = useState([]);
    const {patientIdEncrypt} = useParams();
    const patientId = decrypt(patientIdEncrypt);

    const getOrg = () => {
        ServiceHandler.getOrg()
            .then((data: any) => {
                const sub = localStorage.getItem("sub");
                const caregiverArray = [];
                for (const caregiver of data.caregivers) {
                    if (caregiver.caregiver_id !== sub) {
                        caregiverArray.push(caregiver);
                    }
                }
                setCaregivers(caregiverArray);
                setFilteredCaregivers(caregiverArray);
            })
            .catch((err) => {
                console.log(err);
                getOrg();
            });
    };

    const updateCaregivers = () => {
        getOrg();
    };

    useEffect(() => {
        updateCaregivers();
    }, []);

    const handleShare = (caregiverId: string, caregiverName: string) => {
        ServiceHandler.sharePatient(caregiverId, patientId)
            .then((data) => {
                alert(`Patient metrics shared with ${caregiverName}`);
            })
            .catch((err) => console.log(err));
    };

    const [filteredCaregivers, setFilteredCaregivers] = useState(caregivers);

    const handleSearch = (e: React.ChangeEvent<HTMLInputElement>) => {
        e.preventDefault();
        let searchString = e.target.value;
        searchString = searchString.toLowerCase();
        searchString = searchString.replace(/\s/g, "");
        const searchCaregivers: Array<caregiver> = [];

        caregivers.forEach((caregiver: caregiver) => {
            let name = caregiver.first_name.concat(caregiver.last_name);
            name = name.toLowerCase();
            if (name.includes(searchString)) {
                searchCaregivers.push(caregiver);
            }
        });
        setFilteredCaregivers(searchCaregivers);
    };
    return (
        <>
            <AdminNavbar/>
            <div className="share-patient">
                <h2>Choose a caregiver to share the patient&apos;s metrics with:</h2>
                <svg xmlns="search" width="1.3em" height="1.3em" viewBox="0 -4 24 24"><path fill="currentColor" 
                    d="M15.5 14h-.79l-.28-.27A6.471 6.471 0 0 0 16 9.5A6.5 6.5 0 1 0 9.5 16c1.61 0 3.09-.59 
            4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5S14 
            7.01 14 9.5S11.99 14 9.5 14z"/></svg>
                <input onChange={handleSearch} placeholder={"Search ".concat(caregivers.length.toString())
                    .concat(" caregivers")}></input>
                <div className={filteredCaregivers.length === 1? "entry-view1":
                    filteredCaregivers.length === 2? "entry-view2": 
                        filteredCaregivers.length === 3? "entry-view3" : "entry-view"}>
                    {filteredCaregivers.map((caregiver: caregiver) => {
                        return <div key={caregiver.caregiver_id} className="entryd" onClick={(e) => {
                            e.preventDefault();
                            handleShare(caregiver.caregiver_id, `${caregiver.first_name} ${caregiver.last_name}`);
                        }}>
                            {caregiver.first_name} <br/> {caregiver.last_name}
                        </div>;
                    })} 
                </div>
            </div>

        </>
    );
}

export default SharePatient;
