import React, {useState, useEffect} from "react";
import { QRCode } from "react-qr-svg";
import { useNavigate, useParams} from "react-router-dom";
import CaregiverNavbar from "../Navbar/CaregiverNavbar";
import {FaArrowLeft} from "react-icons/fa";
import {ServiceHandler} from "../../helpers/ServiceHandler";
import {getCaregiverId} from "../../helpers/types";
import {decrypt} from "../../helpers/Crypto";
import "./VerifyPatient.css";

function VerifyPatient() {
    const [authCode, setAuthCode] = useState("");
    const [windowDimensions, setWindowDimensions] = useState(getWindowDimensions());
    const [loaded, setLoaded] = useState(false);
    const nav = useNavigate();
    const {emailEncrypt} = useParams();
    const caregiverId = getCaregiverId();
    const email = decrypt(emailEncrypt);

    function getWindowDimensions() {
        const { innerWidth: width, innerHeight: height } = window;
        return {
            width,
            height
        };
    }

    function getCode() {
        if (!email) return;
        ServiceHandler.addPatient(email)
            .then((code: any) => {
                setAuthCode(code.auth_code);
                setLoaded(true);
            })
            .catch(err => console.log(err));
    }

    useEffect(() => {
        getCode();
    }, []);

    useEffect(() => {
        function handleResize() {
            setWindowDimensions(getWindowDimensions());
        }

        window.addEventListener("resize", handleResize);
        return () => window.removeEventListener("resize", handleResize);
    }, []);

    return (
        <>
            <CaregiverNavbar/>
            <div className="verify-patient">
                <div className="icon" onClick={() => nav("/dashboard")}><FaArrowLeft size="15px"/> Dashboard</div>
                {loaded?
                    <>
                        <div className="text-wrapper">
                            <h2>&nbsp;Verify Patient</h2>
                            <p className="desc">
                                <b>Patient email: {email}</b><br/>
                    Scan the qr code below using your patient&#39;s Mobility Monitor app to link them to your dashboard.
                    Alternatively, we have also sent them an email with instructions if they wish to link their account remotely.
                            </p>
                        </div>
                        <div className='qr-wrapper'>
                    
                            <QRCode
                                level="Q"
                                style={{ width: windowDimensions.width/6 }}
                                value={JSON.stringify({
                                    "auth_code": authCode,
                                    "caregiver_id": caregiverId
                                })}
                            />
                        </div>
                    </>: null}
            </div>
        </>
    );
}

export default VerifyPatient;
