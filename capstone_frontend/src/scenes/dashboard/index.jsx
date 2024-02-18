import { Box, Button, IconButton, Typography, useTheme } from "@mui/material";
import { tokens } from "../../theme";
import DownloadOutlinedIcon from "@mui/icons-material/DownloadOutlined";
import ReceiptOutlinedIcon from "@mui/icons-material/Email";
import PointOfSaleIcon from "@mui/icons-material/PointOfSale";
import PersonAddIcon from "@mui/icons-material/PersonAdd";
import TrafficIcon from "@mui/icons-material/Traffic";
import Header from "../../components/Header";
import BarChartSeverity from "../../components/BarChartSeverity";
import BarChartTool from "../../components/BarChartTool";
import StatBox from "../../components/StatBox";
import ProgressCircle from "../../components/ProgressCircle";
import { useState, useEffect } from "react";
import ProgressCircleStatus from "../../components/ProgressCircleStatus";

const Dashboard = () => {
  const theme = useTheme();
  const colors = tokens(theme.palette.mode);
  const [findings, setFindings] = useState([]);
  const [codeQLfindings, setCodeQLfindings] = useState([]);
  const [dependabotFindings, setDependabotFindings] = useState([]);
  const [secretScanningFindings, setSecretScanningFindings] = useState([]);

  const [totalElements, setTotalElements] = useState(0);
  const [totalElementsCodeQL, setTotalElementsCodeQL] = useState(0);
  const [totalElementsDependabot, setTotalElementsDependabot] = useState(0);
  const [totalElementsSecretScanning, setTotalElementsSecretScanning] =
    useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchFindings();
    fetchCodeQLFindings();
    fetchDependabotFindings();
    fetchSecretScanningFindings();
  }, []);

  const fetchFindings = async () => {
    try {
      // console.log(findings.content);
      const response = await fetch("http://localhost:8090/allFindings");
      if (!response.ok) {
        throw new Error("Failed to fetch findings");
      }
      const data = await response.json();
      setFindings(data);
      setTotalElements(data.numberOfElements);
      setLoading(false);
    } catch (error) {
      setError(error);
      setLoading(false);
    }
  };

  const fetchCodeQLFindings = async () => {
    try {
      const response = await fetch("http://localhost:8090/codeQLFindings");
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
      const response = await fetch("http://localhost:8090/dependabotFindings");
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
      const response = await fetch(
        "http://localhost:8090/secretScanningFindings"
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

  return (
    <Box m="20px">
      {/* HEADER */}
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
      </Box>

      {/* GRID & CHARTS */}
      <Box
        display="grid"
        gridTemplateColumns="repeat(12, 1fr)"
        gridAutoRows="140px"
        gap="20px"
      >
        {/* ROW 1 */}
        <Box
          gridColumn="span 3"
          backgroundColor={colors.primary[400]}
          display="flex"
          alignItems="center"
          justifyContent="center"
        >
          <StatBox
            title={totalElements}
            subtitle="Total Findings"
            progress="1"
            increase="100%"
            icon={
              <ReceiptOutlinedIcon
                sx={{ color: colors.greenAccent[600], fontSize: "26px" }}
              />
            }
          />
        </Box>
        <Box
          gridColumn="span 3"
          backgroundColor={colors.primary[400]}
          display="flex"
          alignItems="center"
          justifyContent="center"
        >
          <StatBox
            title={totalElementsCodeQL}
            subtitle="CodeQL Findings"
            progress={totalElementsCodeQL / totalElements}
            increase={(totalElementsCodeQL / totalElements) * 100 + "%"}
            icon={
              <PointOfSaleIcon
                sx={{ color: colors.greenAccent[600], fontSize: "26px" }}
              />
            }
          />
        </Box>
        <Box
          gridColumn="span 3"
          backgroundColor={colors.primary[400]}
          display="flex"
          alignItems="center"
          justifyContent="center"
        >
          <StatBox
            title={totalElementsDependabot}
            subtitle="Dependabot Findings"
            progress={totalElementsDependabot / totalElements}
            increase={(totalElementsDependabot / totalElements) * 100 + "%"}
            icon={
              <PersonAddIcon
                sx={{ color: colors.greenAccent[600], fontSize: "26px" }}
              />
            }
          />
        </Box>
        <Box
          gridColumn="span 3"
          backgroundColor={colors.primary[400]}
          display="flex"
          alignItems="center"
          justifyContent="center"
        >
          <StatBox
            title={totalElementsSecretScanning}
            subtitle="Secret Scan Findings"
            progress={totalElementsSecretScanning / totalElements}
            increase={(totalElementsSecretScanning / totalElements) * 100 + "%"}
            icon={
              <TrafficIcon
                sx={{ color: colors.greenAccent[600], fontSize: "26px" }}
              />
            }
          />
        </Box>

        {/* ROW 3 */}
        {findings && findings.content && (
          <Box
            gridColumn="span 4"
            gridRow="span 2"
            backgroundColor={colors.primary[400]}
            p="30px"
          >
            <Typography variant="h5" fontWeight="600">
              Findings by Status
            </Typography>
            <Box
              display="flex"
              alignItems="center"
              justifyContent="space-between" // Add space between ProgressCircle components
              mt="25px"
            >
              {/* ProgressCircle for "open" status */}
              <Box>
                <ProgressCircleStatus
                  size={130}
                  status="open"
                  findings={findings.content}
                />
                <Typography
                  variant="subtitle1"
                  fontWeight="600"
                  sx={{ mt: "10px" }}
                >
                  Open:{" "}
                  {
                    findings.content.filter((item) => item.status === "open")
                      .length
                  }{" "}
                  / {findings.content.length}
                </Typography>
              </Box>
              {/* Add some space between the ProgressCircle components */}
              <Box mx={2} />
              {/* ProgressCircle for "mitigated" status */}
              <Box>
                <ProgressCircleStatus
                  size={130}
                  status="mitigated"
                  findings={findings.content}
                />
                <Typography
                  variant="subtitle1"
                  fontWeight="600"
                  sx={{ mt: "10px" }}
                >
                  Mitigated:{" "}
                  {
                    findings.content.filter(
                      (item) => item.status === "mitigated"
                    ).length
                  }{" "}
                  / {findings.content.length}
                </Typography>
              </Box>
            </Box>
          </Box>
        )}

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
            Findings by severity
          </Typography>
          <Box height="250px" mt="-20px">
            <BarChartSeverity findings={findings.content} isDashboard={true} />
          </Box>
        </Box>
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
            Findings by Tool
          </Typography>
          <Box height="250px" mt="-20px">
            <BarChartTool findings={findings.content} isDashboard={true} />
          </Box>
        </Box>
      </Box>
    </Box>
  );
};

export default Dashboard;
