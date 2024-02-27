import { useEffect, useState } from 'react';
import { GoogleOAuthProvider, GoogleLogin } from '@react-oauth/google';
import axios from 'axios';
import './index.css'; // Import the CSS file

const LoginPage = ({ onLogin, assignRole }) => {
    const [credential, setCredential] = useState("");

    useEffect(() => {
        if (credential) {
            sendCredentialToBackend();
        }
    }, [credential]);

    // Handle successful Google login
    const onSuccess = (response) => {
        setCredential(response.credential);
        sendCredentialToBackend(response.credential);
        onLogin(true);
    };

    // Handle failed Google login
    const onFailure = (error) => {
        console.error('Login failed:', error);
    };

    // Function to make the POST request to your backend using Axios
    const sendCredentialToBackend = async (credential) => {
        try {
            const response = await axios.post(
                'http://localhost:8090/auth',
                { },
                {
                    headers: {
                        'Content-Type': 'application/json',
                        // 'Authorization': `Bearer ${credential}` ,// Add authorization header
                        'credential': credential
                    }
                }
            );
            const userRole = response.data.role;
            const organizationId = response.data.orgId;
            const userName = response.data.name;
            console.log("samay: " + response.stringify)
            console.log(userName);
            assignRole(userRole);

            localStorage.setItem("sessionToken", credential)
            localStorage.setItem("userRole", userRole)
            localStorage.setItem("orgId",organizationId);
            localStorage.setItem("userName",userName);

        } catch (error) {
            console.error('Error sending credential to backend:', error);
        }
    };

    return (
        <GoogleOAuthProvider clientId={process.env.REACT_APP_GOOGLE_CLIENT_ID}>
            <div className="container">
                <div className="card">
                    <img src={`../../assets/user.png`} alt="Organization Logo" style={{ width: '100px', marginBottom: '20px' }} />
                    <h2>ArmorCode</h2>
                    <p>Please sign in with your Google account.</p>
                    <GoogleLogin
                        onSuccess={onSuccess}
                        onFailure={onFailure}
                        buttonText="Sign in with Google"
                        cookiePolicy={'single_host_origin'}
                        style={{ marginTop: '20px' }}
                    />
                </div>
            </div>
        </GoogleOAuthProvider>
    );
};

export default LoginPage;
