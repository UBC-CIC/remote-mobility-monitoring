import React from "react";
import { AppBar, Toolbar, Typography, Button } from "@mui/material";
import { useNavigate } from "react-router-dom";
import { logout } from "../../helpers/Cognito";
import { BiLogOut } from "react-icons/bi";

function AdminNavbar() {
    const nav = useNavigate();

    return (
        <div>
            <AppBar position="static">
                <Toolbar>
                    <Typography
                        variant="h6"
                        sx={{
                            flexGrow: 1,
                        }}
                    >
                        <img style={{ maxHeight: "30px" }} src = "/mobimon-logo.png" />
                    </Typography>
                    <Button
                        color="inherit"
                        sx={{
                            marginRight: 2,
                        }}
                        onClick={() => {
                            nav("/admindashboard");
                        }}
                    >
                        Dashboard
                    </Button>
                    <Button
                        color="inherit"
                        onClick={() => {
                            nav("/addcaregiver");
                        }}
                    >
                        Add Caregivers
                    </Button>
                    <Button
                        color="inherit"
                        onClick={() => {
                            logout();
                            nav("/login");
                        }}
                    >
                        Logout
                        <BiLogOut color = "white" />
                    </Button>
                </Toolbar>
            </AppBar>
        </div>
    );
}

export default AdminNavbar;
