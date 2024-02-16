import React, { useState, useEffect } from "react";
import { Box, Typography, useTheme, FormControl, InputLabel, Select, MenuItem, Button } from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import { tokens } from "../../theme";
import Header from "../../components/Header";
import CircularProgress from '@mui/material/CircularProgress';

const Team = () => {
  const theme = useTheme();
  const colors = tokens(theme.palette.mode);
  const [findings, setFindings] = useState([]);
  const [error, setError] = useState(null);
  const [tool, setTool] = useState("");
  const [severity, setSeverity] = useState("");
  const [status, setStatus] = useState("");
  const [pageSize, setPageSize] = useState(16); // Default page size
  const [totalPages, setTotalPages] = useState(0);
  const [currentPage, setCurrentPage] = useState(0);
  const [loading, setLoading] = useState(false);

  const columns = [
    { field: "id", headerName: "Finding Id" },
    { field: "severity", headerName: "Severity", type: "text", headerAlign: "left", align: "left" },
    { field: "status", headerName: "Status", flex: 1 },
    { field: "summary", headerName: "Summary", flex: 1 },
    { field: "tool", headerName: "Tool", flex: 1 },
    { field: "cve_id", headerName: "CVE ID", flex: 1 },
    { field: "pathIssue", headerName: "Path", flex: 1 }
  ];

  const fetchData = async () => {
    try {
      const response = await fetch(`http://localhost:8090/fetchFindings?page=${currentPage}&size=${pageSize}&severity=${severity}&tool=${tool}&status=${status}`);
      const jsonResponse  = await response.json();
      setFindings(jsonResponse.content);
      console.log(jsonResponse);
      // setTotalPages(response.data.totalPages);
      setLoading(true);
    } catch (error) {
      setError(error);
      console.error('Error fetching data:', error);
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

  const handleStatusChange = (event) => {
    setStatus(event.target.value);
  };

  useEffect(() => {
    fetchData();
  }, [currentPage,pageSize,severity, tool]);

  const handleScanNowClick = async () => {
    try {
      setLoading(true);
      await fetch("http://localhost:8090/fetch-and-save");
      console.log("Scan initiated successfully");
      fetchData();
    } catch (error) {
      console.error("Error initiating scan:", error);
    } finally {
      setLoading(false);
    }
  };

  const handlePageChange = (newPage) => {
    setCurrentPage(newPage);
  };

  const handlePageSizeChange = (event) => {
    setPageSize(event.target.value);
    setCurrentPage(0);
  };
  
  return (
    <Box m="20px">
      <Header title="Findings" subtitle="Manage all the vulnerabilities in your Repository" />
      <Box
        m="40px 0 0 0"
        height="75vh"
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
        }}
      >
        <Box display="flex" justifyContent="space-between" mb={2}>
          <Box display="flex">
            <FormControl sx={{ m: 1, minWidth: 120 }}>
              <InputLabel>Tool</InputLabel>
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
              <InputLabel>Severity</InputLabel>
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
              <InputLabel>Status</InputLabel>
              <Select
                value={status}
                onChange={handleStatusChange}
                labelId="status-label"
                id="status-select"
                defaultValue=""
                label="Status"
                sx={{ backgroundColor: "white" }}
              >
                <MenuItem value="">All</MenuItem>
                <MenuItem value="Mitigated">Mitigated</MenuItem>
                <MenuItem value="open">Open</MenuItem>
              </Select>
            </FormControl>
              <FormControl sx={{ m: 1, minWidth: 120 }}>
                <InputLabel>Rows per page</InputLabel>
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
          <Button onClick={handleScanNowClick} variant="contained" color="primary">
            {loading ? <CircularProgress size={24} color="inherit" /> : 'Scan Now'}
        </Button>
        </Box>
          {findings !== undefined && (
          <DataGrid
          checkboxSelection
          rows={findings}
          columns={columns}
          pagination
          pageSize={pageSize}
          rowCount={totalPages * pageSize} // Total number of rows for pagination
          onPageChange={handlePageChange} // Handler for page change
          paginationMode="server" // Specify server-side pagination mode
          page={currentPage} // Current page
          onPageNavigation={(direction) => {
            // Handle page navigation when "<" or ">" is clicked
            if (direction === 'back') {
              setCurrentPage(currentPage - 1);
            } else if (direction === 'forward') {
              setCurrentPage(currentPage + 1);
            }
          }}
          components={{
            pagination: (props) => (
              <Box display="flex" justifyContent="center" alignItems="center">
                <Typography>Go to Page:</Typography>
                <FormControl sx={{ marginLeft: 1 }}>
                  <Select
                    value={currentPage + 1}
                    onChange={(e) => setCurrentPage(e.target.value - 1)}
                  >
                    {[...Array(totalPages)].map((_, index) => (
                      <MenuItem key={index} value={index + 1}>
                        {index + 1}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Box>
            ),
          }}
        />
            )}
      </Box>
    </Box>
  );
};
export default Team;