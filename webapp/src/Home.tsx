import React from "react";
import { useNavigate} from "react-router-dom";
import {createUser} from "./Cognito";


function Home() {
    const nav = useNavigate();
    return (
        <div>
            <button onClick={() => {
                nav("/login");
            }}>Login</button>
            <button onClick={() => {
                nav("/sysadmin");
            }}>Sysadmin</button>
            <button onClick={() => {
                console.log("hello");
                const username = localStorage.getItem("username");
                if (!username) return;
                const cognitoUser = createUser(username);
                cognitoUser.getSession(function(err: any, succ: any) {
                    console.log(err);
                    console.log(succ);
                });
                cognitoUser.getUserAttributes(function(err, result) {
                    if (err) {
                        alert(err.message || JSON.stringify(err));
                        return;
                    }
                    console.log(result);
                });
            }}>User</button>
            <button onClick={() => {
                console.log("hello");
                const username = localStorage.getItem("username");
                if (!username) return;
                const cognitoUser = createUser(username);
                cognitoUser.signOut();
            }}>Logout</button>
        </div>
    );
}

export default Home;
