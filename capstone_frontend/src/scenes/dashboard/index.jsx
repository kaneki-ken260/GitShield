import { Box, Button, IconButton, Typography, useTheme } from "@mui/material";
import { tokens } from "../../theme";
import DownloadOutlinedIcon from "@mui/icons-material/DownloadOutlined";
import ReceiptOutlinedIcon from "@mui/icons-material/Email";
import PointOfSaleIcon from "@mui/icons-material/PointOfSale";
import PersonAddIcon from "@mui/icons-material/PersonAdd";
import TrafficIcon from "@mui/icons-material/Traffic";
import Header from "../../components/Header";
import BarChartSeverity from "../../components/BarChartSeverity";
// import BarChartTool from "../../components/BarChartTool";
import StatBox from "../../components/StatBox";
import ProgressCircle from "../../components/ProgressCircle";
import { useState, useEffect } from "react";
// import ProgressCircleStatus from "../../components/ProgressCircleStatus";
// import BarChartTicketPriority from "../../components/BarChartTicketPriority";
import axios from "axios";

const Dashboard = () => {
  const theme = useTheme();
  const colors = tokens(theme.palette.mode);
  const [findings, setFindings] = useState([]);
  const [codeQLfindings, setCodeQLfindings] = useState([]);
  const [dependabotFindings, setDependabotFindings] = useState([]);
  const [secretScanningFindings, setSecretScanningFindings] = useState([]);
  const [tickets, setTickets] = useState([]);

  const [totalElements, setTotalElements] = useState(0);
  const [totalElementsCodeQL, setTotalElementsCodeQL] = useState(0);
  const [totalElementsDependabot, setTotalElementsDependabot] = useState(0);
  const [totalElementsSecretScanning, setTotalElementsSecretScanning] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [organizationId, setOrganizationId] = useState(localStorage.getItem("orgId"));
  const [accessToken, setAccessToken] = useState(localStorage.getItem("sessionToken"));
  const [tool, setTool] = useState("");
  const [severity, setSeverity] = useState("");
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize, setPageSize] = useState(20);

  useEffect(() => {
    
    const fetchFindings = async () => {
      try {
        // console.log(findings.content);
        const response = await axios.post(`http://localhost:8090/fetchFindings?page=${currentPage}&size=${pageSize}&severity=${severity}&tool=${tool}`,
        { },
            {
                headers: {
                    'Content-Type': 'application/json',
                    // 'Authorization': `Bearer ${accessToken}` ,// Add authorization header
                    'accessToken': accessToken,
                    'organizationId':organizationId
                }
            }
        );
  
        // const data = await response.json();
        setFindings(response.data.content);
        setTotalElements(response.data.numberOfElements);
        setLoading(false);
      } catch (error) {
        setError(error);
        setLoading(false);
      }
    };

    fetchFindings();

  }, []);

  const fetchTickets = async () => {
    try {
      // console.log(findings.content);
      const response = await axios.post("http://localhost:8090/fetchTickets",
      { },
          {
              headers: {
                  'Content-Type': 'application/json',
                  // 'Authorization': `Bearer ${accessToken}` ,// Add authorization header
                  'accessToken': accessToken,
                  'organizationId':organizationId
              }
          }
      );
      if (!response.ok) {
        throw new Error("Failed to fetch Tickets");
      }
      const data = await response.json();
      setTickets(data);
      setLoading(false);
    } catch (error) {
      setError(error);
      setLoading(false);
    }
  };

  const fetchCodeQLFindings = async () => {
    try {
      const response = await axios.post("http://localhost:8090/codeQLFindings",
      { },
          {
              headers: {
                  'Content-Type': 'application/json',
                  // 'Authorization': `Bearer ${accessToken}` ,// Add authorization header
                  'accessToken': accessToken,
                  'organizationId':organizationId
              }
          }
      );
      if (!response.ok) {
        throw new Error("Failed to fetch findings");
      }
      const data = await response.json();
      setCodeQLfindings(data);
      setTotalElementsCodeQL(data.numberOfElements);
      setLoading(false);
    } catch (error) {
      setError(error);
      setLoading(false);
    }
  };

  const fetchDependabotFindings = async () => {
    try {
      const response = await axios.post("http://localhost:8090/dependabotFindings",
      { },
          {
              headers: {
                  'Content-Type': 'application/json',
                  // 'Authorization': `Bearer ${accessToken}` ,// Add authorization header
                  'accessToken': accessToken,
                  'organizationId':organizationId
              }
          }
      );
      if (!response.ok) {
        throw new Error("Failed to fetch findings");
      }
      const data = await response.json();
      setDependabotFindings(data);
      setTotalElementsDependabot(data.numberOfElements);
      setLoading(false);
    } catch (error) {
      setError(error);
      setLoading(false);
    }
  };

  const fetchSecretScanningFindings = async () => {
    try {
      const response = await axios.post(
        "http://localhost:8090/secretScanningFindings",
        { },
          {
              headers: {
                  'Content-Type': 'application/json',
                  // 'Authorization': `Bearer ${accessToken}` ,// Add authorization header
                  'accessToken': accessToken,
                  'organizationId':organizationId
              }
          }
      );
      if (!response.ok) {
        throw new Error("Failed to fetch findings");
      }
      const data = await response.json();
      setSecretScanningFindings(data);
      setTotalElementsSecretScanning(data.numberOfElements);
      setLoading(false);
    } catch (error) {
      setError(error);
      setLoading(false);
    }
  };

  // console.log(findings)

  return (
    <Box m="20px">
      {/* HEADER
      <Box display="flex" justifyContent="space-between" alignItems="center">
        <Header title="DASHBOARD" subtitle="Welcome to your dashboard" />

        <Box>
          <Button
            sx={{
              backgroundColor: colors.blueAccent[700],
              color: colors.grey[100],
              fontSize: "14px",
              fontWeight: "bold",
              padding: "10px 20px",
            }}
          >
            <DownloadOutlinedIcon sx={{ mr: "10px" }} />
            Download Reports
          </Button>
        </Box>
      </Box> */}

      {/* GRID & CHARTS */}
      <Box
          gridColumn="span 4"
          gridRow="span 2"
          backgroundColor={colors.primary[400]}
        >
          <Typography
            variant="h5"
            fontWeight="600"
            sx={{ padding: "30px 30px 0 30px" }}
          >
            Findings by Severity
          </Typography>
          <Box height="150px" mt="-20px">
            <BarChartSeverity findings={findings} isDashboard={true} />
          </Box>
        </Box>
    </Box>
  );
};

export default Dashboard;