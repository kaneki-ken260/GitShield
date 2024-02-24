import { useEffect, useState } from 'react';
import { GoogleOAuthProvider, GoogleLogin } from '@react-oauth/google';
import axios from 'axios';

const LoginPage = ({ onLogin, assignRole }) => {
    const [credential, setCredential] = useState("");

    useEffect(() => {
        if (credential) {
            sendCredentialToBackend();
        }
    }, [credential]);

    // Handle successful Google login
    const onSuccess = (response) => {
        // console.log('Login successful:', response);
        // Extract the credential field from the response and set it in state
        setCredential(response.credential);
        // Call the onLogin callback function to set isLoggedIn to true

        sendCredentialToBackend(response.credential);

        onLogin(true);
    };

    // Handle failed Google login
    const onFailure = (error) => {
        console.error('Login failed:', error);
        // Add your logic to handle the failed login, e.g., show an error message to the user.
    };

    // Function to make the POST request to your backend using Axios
    const sendCredentialToBackend = async (credential) => {
        try {
            // console.log("Hola" + credential);
            const response = await axios.post(
                'http://localhost:8090/auth',
                { credential},
                {
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${credential}` // Add authorization header
                    }
                }
            );
            // Since the call was successful assign the role to the user from backend
            // console.log(response);
            const userRole = response.data.role;
            const organizationId = response.data.orgId;
            assignRole(userRole);

            localStorage.setItem("sessionToken", credential)
            localStorage.setItem("userRole", userRole)
            localStorage.setItem("orgId",organizationId);

            // console.log("Hello " + localStorage.getItem("sessionToken"))
            // console.log(localStorage.getItem("userRole"))

        } catch (error) {
            console.error('Error sending credential to backend:', error);
            // Add your error handling logic here
        }
    };

    return (
        <GoogleOAuthProvider clientId={process.env.REACT_APP_GOOGLE_CLIENT_ID}>
            <div className="container">
                <div className="left">
                    <img src={`../../assets/user.png`} alt="Organization Logo" />
                </div>
                <div className="right">
                    <h2>Login</h2>
                    <p>Please sign in with your Google account.</p>
                    {/* GoogleLogin component from @react-oauth/google */}
                    <GoogleLogin
                        onSuccess={onSuccess}
                        onFailure={onFailure}
                        buttonText="Sign in with Google"
                        cookiePolicy={'single_host_origin'}
                    />
                </div>
            </div>
        </GoogleOAuthProvider>
    );
};

export default LoginPage;
