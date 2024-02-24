import React, { useState } from 'react'
import CircularProgress from "@mui/material/CircularProgress";
import { Button } from '@mui/material';

const Scans = () => {

    const [loading, setLoading] = useState(false);

    const [organizationId, setOrganizationId] = useState(localStorage.getItem("orgId"));
    const [accessToken, setAccessToken] = useState(localStorage.getItem("sessionToken"));

    console.log("Hello: " + accessToken)
    console.log("Hello21321: " + organizationId)
    

    const handleScanNowClick = async () => {
        try {
          setLoading(true);
          await fetch(`http://localhost:8090/fetch-and-save?accessToken=${accessToken}&organizationId=${organizationId}`);
          console.log("Scan initiated successfully");
        } catch (error) {
          console.error("Error initiating scan:", error);
        } finally {
          setLoading(false);
        }
      };
  return (
    <>
            <Button onClick={handleScanNowClick} variant="contained" color="primary">
            {loading ? <CircularProgress size={24} color="inherit" /> : "Scan Now"}
          </Button>
    </>
  )
}

export default Scans