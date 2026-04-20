import React from "react";
import { useAuth } from "../context/AuthContext"
import { useNavigate } from "react-router-dom";


export default function LogoutPage() {
    const { logout } = useAuth();
    const navigate = useNavigate();

    React.useEffect(()=>{
        (async () => {
            await logout();
            navigate("/login");
        })();
    }, [])

    return (
        <h1>hello world</h1>
    )
}