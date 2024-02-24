import React, { useState, useEffect } from "react";
import {
  Box,
  Typography,
  useTheme,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
} from "@mui/material";
import { ColorModeContext, tokens } from "../../theme";
import Header from "../../components/Header";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";
import Pagination from "@mui/material/Pagination";

const Findings = ({ userRole }) => {
  const theme = useTheme();
  const colors = tokens(theme.palette.mode);
  const [findings, setFindings] = useState([]);
  const [error, setError] = useState(null);
  const [tool, setTool] = useState("");
  const [severity, setSeverity] = useState("");
  const [pageSize, setPageSize] = useState(20); // Default page size
  const [totalPages, setTotalPages] = useState(0);
  const [currentPage, setCurrentPage] = useState(0);
  const [loading, setLoading] = useState(false);
  const [toolSeverityChange, setToolSeverityChange] = useState(false);
  const [organizationId, setOrganizationId] = useState(localStorage.getItem("orgId"));
  const [accessToken, setAccessToken] = useState(localStorage.getItem("sessionToken"));

  console.log(localStorage.getItem("orgId"));
  console.log("Hello" + organizationId);

  const fetchData = async () => {
    try {
      const response = await fetch(
        `http://localhost:8090/fetchFindings?accessToken=${accessToken}&organizationId=${organizationId}&page=${currentPage}&size=${pageSize}&severity=${severity}&tool=${tool}`
      );
      const jsonResponse = await response.json();

      setFindings(jsonResponse.content);
      setTotalPages(jsonResponse.totalPages);
      setLoading(true);
    } catch (error) {
      setError(error);
      console.error("Error fetching data:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleToolChange = (event) => {
    setTool(event.target.value);
  };

  const handleSeverityChange = (event) => {
    setSeverity(event.target.value);
  };

useEffect(() => {
  fetchData();
}, [currentPage, pageSize, tool, severity]);

useEffect(() => {
  setToolSeverityChange(true); // Set the flag to true when tool or severity changes
}, [tool, severity]);

useEffect(() => {
  if (toolSeverityChange) {
    setCurrentPage(0); // Reset currentPage to 0 only if tool or severity changes
    setToolSeverityChange(false); // Reset the flag
  }
}, [toolSeverityChange]);

// To get the time difference
const getTimeDifferenceString = (updatedAt) => {
  const currentDate = new Date();
  const updatedDate = new Date(updatedAt);

  const timeDifference = Math.abs(currentDate - updatedDate) / 1000; // Difference in seconds

  // Define time intervals in seconds
  const intervals = {
    year: 31536000,
    month: 2592000,
    week: 604800,
    day: 86400,
    hour: 3600,
    minute: 60,
  };

  // Calculate the appropriate time interval
  for (let interval in intervals) {
    const value = Math.floor(timeDifference / intervals[interval]);
    if (value >= 1) {
      return value === 1 ? `${value} ${interval} ago` : `${value} ${interval}s ago`;
    }
  }

  return "Just now";
};

  // const handleScanNowClick = async () => {
  //   try {
  //     setLoading(true);
  //     await fetch(`http://localhost:8090/fetch-and-save?orgId=${organizationId}`);
  //     console.log("Scan initiated successfully");
  //     fetchData();
  //   } catch (error) {
  //     console.error("Error initiating scan:", error);
  //   } finally {
  //     setLoading(false);
  //   }
  // };

  const getSeverityColor = (severity) => {
    switch (severity) {
      case 'critical':
        return colors.redAccent[500];
      case 'high':
        return colors.redAccent[700];
      case 'medium':
        return colors.blueAccent[600];
      case 'low':
        return colors.greenAccent[600];
      default:
        return colors.grey[500];
    }
  };

  const handlePageChange = (event, newPage) => {
    setCurrentPage(newPage - 1);
  };

  const handlePageSizeChange = (event) => {
    setPageSize(event.target.value);
    setCurrentPage(0);
  };

  return (
    <Box m="20px">
      <Header
        title="Findings"
        subtitle="Manage all the vulnerabilities in your Repository"
      />
      <Box
        m="40px 0 0 0"
        sx={{
          "& .MuiDataGrid-root": {
            border: "none",
          },
          "& .MuiDataGrid-cell": {
            borderBottom: "none",
          },
          "& .name-column--cell": {
            color: colors.greenAccent[300],
          },
          "& .MuiDataGrid-columnHeaders": {
            backgroundColor: colors.blueAccent[700],
            borderBottom: "none",
          },
          "& .MuiDataGrid-virtualScroller": {
            backgroundColor: colors.primary[400],
          },
          "& .MuiDataGrid-footerContainer": {
            borderTop: "none",
            backgroundColor: colors.blueAccent[700],
          },
          "& .MuiCheckbox-root": {
            color: `${colors.greenAccent[200]} !important`,
          },
          "& .MuiFormControl-root": {
            color: theme.palette.mode === "light" ? "white" : "black",
          },
          "& .MuiInputBase-root": {
            color: theme.palette.mode === "light" ? "white" : "black",
            backgroundColor: theme.palette.mode === "light" ? "black" : "white",
          },
          "& .MuiMenuItem-root": {
            color: theme.palette.mode === "light" ? "black" : "white",
            backgroundColor: theme.palette.mode === "light" ? "white" : "black",
          },
          "& .MuiButton-root": {
            color: theme.palette.mode === "light" ? "white" : "black",
            backgroundColor: theme.palette.mode === "light" ? "black" : "white",
          },
          "& .MuiListItemIcon-root": {
            color: theme.palette.mode === "light" ? "white" : "black",
          },
        }}
      >
        <Box display="flex" justifyContent="space-between" mb={2}>
          <Box display="flex">
            <FormControl sx={{ m: 1, minWidth: 120 }}>
              <InputLabel sx={{ color: theme.palette.mode === "light" ? "white" : "black" }}>
                Tool
              </InputLabel>
              <Select
                value={tool}
                onChange={handleToolChange}
                labelId="tool-label"
                id="tool-select"
                defaultValue=""
                label="Tool"
                sx={{ backgroundColor: "white" }}
              >
                <MenuItem value="">All</MenuItem>
                <MenuItem value="CodeQL">CodeQL</MenuItem>
                <MenuItem value="Secret Scan">Secret Scan</MenuItem>
                <MenuItem value="Dependabot">Dependabot</MenuItem>
              </Select>
            </FormControl>
            <FormControl sx={{ m: 1, minWidth: 120 }}>
              <InputLabel sx={{ color: theme.palette.mode === "light" ? "white" : "black" }}>
                Severity
              </InputLabel>
              <Select
                value={severity}
                onChange={handleSeverityChange}
                labelId="severity-label"
                id="severity-select"
                defaultValue=""
                label="Severity"
                sx={{ backgroundColor: "white" }}
              >
                <MenuItem value="">All</MenuItem>
                <MenuItem value="critical">Critical</MenuItem>
                <MenuItem value="high">High</MenuItem>
                <MenuItem value="medium">Medium</MenuItem>
                <MenuItem value="low">Low</MenuItem>
                <MenuItem value="info">Info</MenuItem>
              </Select>
            </FormControl>
            <FormControl sx={{ m: 1, minWidth: 120 }}>
              <InputLabel sx={{ color: theme.palette.mode === "light" ? "white" : "black" }}>
                Rows per page
              </InputLabel>
              <Select
                value={pageSize}
                onChange={handlePageSizeChange}
                labelId="rows-per-page-label"
                id="rows-per-page-select"
                label="Rows per page"
                sx={{ backgroundColor: "white" }}
              >
                <MenuItem value={100}>All</MenuItem>
                <MenuItem value={5}>5</MenuItem>
                <MenuItem value={10}>10</MenuItem>
                <MenuItem value={20}>20</MenuItem>
                <MenuItem value={50}>50</MenuItem>
              </Select>
            </FormControl>
          </Box>
        </Box>
        <TableContainer component={Paper} sx={{ overflowX: "auto" }}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell><h2>Finding ID</h2></TableCell>
                <TableCell><h2>Severity</h2></TableCell>
                <TableCell><h2>Status</h2></TableCell>
                <TableCell><h2>Summary</h2></TableCell>
                <TableCell><h2>Tool</h2></TableCell>
                <TableCell><h2>CVE ID</h2></TableCell>
                <TableCell><h2>Path</h2></TableCell>
                <TableCell><h2>Updated</h2></TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {findings && findings.map((finding) => (
                <TableRow key={finding.id}>
                  <TableCell>{finding.id}</TableCell>
                  <TableCell>
                    <span style={{ backgroundColor: getSeverityColor(finding.security_severity_Level), color: '#fff', padding: '4px', borderRadius: '4px' }}>
                      {finding.security_severity_Level || "null"}
                    </span>
                  </TableCell>
                  <TableCell>{finding.status}</TableCell>
                  <TableCell>{finding.summary}</TableCell>
                  <TableCell>{finding.tool}</TableCell>
                  <TableCell>{finding.cve_id}</TableCell>
                  <TableCell>{finding.pathIssue}</TableCell>
                  <TableCell>{getTimeDifferenceString(finding.updatedAt)}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
        <Pagination
          count={totalPages}
          page={currentPage + 1}
          onChange={handlePageChange}
          shape="rounded"
          color="primary"
          size="large"
          sx={{
            "& .Mui-selected": {
              backgroundColor: colors.primary[400], // Apply background color to the selected page
              color: colors.grey[100], // Change text color of the selected page
            },
          }}
        />
      </Box>
    </Box>
  );
};
export default Findings;