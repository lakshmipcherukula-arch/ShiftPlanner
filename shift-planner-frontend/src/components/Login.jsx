import {useState} from "react";
import { useNavigate } from "react-router-dom";
import "../styles/Login.css";
import Button from "./Button";

const Login = ({onLoginSuccess}) => {

const [userName,setUserName] = useState("");
const [password,setPassword] = useState("");
const [errorMessage,setErrorMessage] = useState("");
const [successMessage,setSuccessMessage] = useState("");
const [isLoggingIn, setIsLoggingIn] = useState(false);

const navigate = useNavigate();

const presetUser = "Lakshmi";
const presetPassword = "shiftplanner";

const handleSubmit = (e) => {
    e.preventDefault();

    if(userName === presetUser && password === presetPassword){
        setErrorMessage("");
        setSuccessMessage("Login Successful! Redirecting to the home page...");
        setIsLoggingIn(true);

        setTimeout(() => {
        setIsLoggingIn(false)
        if(onLoginSuccess){
            onLoginSuccess();
        }
        navigate("/");
    }, 2000); 

    }else {
        setSuccessMessage("");
        setErrorMessage("Invalid username or password. Try Lakshmi/shiftplanner");
    }
};

return(
    <div className="login-container">
        <div className="login-wrapper">
      
            <div className="login-header">
                <h1>Shift Planner</h1>
            </div>
            <form className="login-card" onSubmit={handleSubmit}>
                <h2> Log In </h2>
                <p className="login-subtitle"><strong>Username: Lakshmi</strong> 
                <br/> <strong>Password: shiftplanner</strong></p>
                {errorMessage && <p className="login-error-msg">{errorMessage}</p>}
                {successMessage && <p className="login-success-msg">{successMessage}</p>}
                <div className="input-group">
                    <label>Username:</label>
                    <input type="text"
                       value={userName}
                       onChange={(e) => setUserName(e.target.value)}
                       placeholder="Enter username"
                       required
                    />
                </div>
                <div className="input-group">
                <label>Password:</label>
                <input type="password"
                       value={password}
                       onChange={(e) => setPassword(e.target.value)}
                       placeholder="Enter Password"
                       required
                />
            </div>
            <br/>
            <Button type="submit" isLoading={isLoggingIn} 
            style={{ width: "100%" }}>Login</Button>
            </form>
        </div>
    </div>
);
};
export default Login;