import React, { useEffect } from 'react'

const LogOut = ({ onLogout, redirectToDefault }) => {

  useEffect(() => {
    // Remove sessionToken from localStorage
    localStorage.removeItem("sessionToken");
    // Call the onLogout function to update the isLoggedIn state in the parent component
    onLogout(false);
    redirectToDefault();
  }, [onLogout, redirectToDefault]);

  return (
    <div>Logging out...</div>
  );
};

export default LogOut;
