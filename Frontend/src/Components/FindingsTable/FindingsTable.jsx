import React, { useState, useEffect } from 'react';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import axios from 'axios';
import './FindingsTable.css';
import FormControl from '@mui/material/FormControl';
import InputLabel from '@mui/material/InputLabel';
import Select from '@mui/material/Select';
import MenuItem from '@mui/material/MenuItem';
import Grid from '@mui/material/Grid';

const FindingsTable = () => {
  const [findings, setFindings] = useState([]);
  const [error, setError] = useState(null);
  const [filterTool, setFilterTool] = useState('');
  const [filterSeverity, setFilterSeverity] = useState('');
  const [filterStatus, setFilterStatus] = useState('');

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await axios.get('http://localhost:8090/fetch-and-save');
        setFindings(response.data);
      } catch (error) {
        setError(error);
        console.error('Error fetching data:', error);
      }
    };

    fetchData();
  }, []);

  const handleToolChange = (event) => {
    setFilterTool(event.target.value);
  };

  const handleSeverityChange = (event) => {
    setFilterSeverity(event.target.value);
  };

  const handleStatusChange = (event) => {
    setFilterStatus(event.target.value);
  };

  return (
    <div>
      {error && <div>Error: {error.message}</div>}
      <Grid container spacing={2} className="filters">
        <Grid item>
          <FormControl sx={{ m: 1, minWidth: 120 }}>
            <InputLabel>Tool</InputLabel>
            <Select
              value={filterTool}
              onChange={handleToolChange}
              labelId="tool-label"
              id="tool-select"
              defaultValue=""
              label="Tool"
              sx={{ backgroundColor: 'white' }} // Set background color
            >
              <MenuItem value="">All</MenuItem>
              <MenuItem value="CodeQL">CodeQL</MenuItem>
              <MenuItem value="Secret Scan">Secret Scan</MenuItem>
              <MenuItem value="Dependabot">Dependabot</MenuItem>
            </Select>
          </FormControl>
        </Grid>
        <Grid item>
          <FormControl sx={{ m: 1, minWidth: 120 }}>
            <InputLabel>Severity</InputLabel>
            <Select
              value={filterSeverity}
              onChange={handleSeverityChange}
              labelId="tool-label"
              id="tool-select"
              defaultValue=""
              label="Tool"
              sx={{ backgroundColor: 'white' }} // Set background color
            >
              <MenuItem value="">All</MenuItem>
              <MenuItem value="critical">Critical</MenuItem>
              <MenuItem value="high">High</MenuItem>
              <MenuItem value="medium">Medium</MenuItem>
              <MenuItem value="low">Low</MenuItem>
              <MenuItem value="info">Info</MenuItem>
            </Select>
          </FormControl>
        </Grid>
        <Grid item>
          <FormControl sx={{ m: 1, minWidth: 120 }}>
            <InputLabel>Status</InputLabel>
            <Select
              value={filterStatus}
              onChange={handleStatusChange}
              labelId="tool-label"
              id="tool-select"
              defaultValue=""
              label="Tool"
              sx={{ backgroundColor: 'white' }} // Set background color
            >
              <MenuItem value="">All</MenuItem>
              <MenuItem value="fixed">Fixed</MenuItem>
              <MenuItem value="open">Open</MenuItem>
            </Select>
          </FormControl>
        </Grid>
      </Grid>
      <TableContainer component={Paper} sx={{ overflowX: 'auto' }}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Finding ID</TableCell>
              <TableCell>Severity</TableCell>
              <TableCell>Status</TableCell>
              <TableCell>Summary</TableCell>
              <TableCell>Tool</TableCell>
              <TableCell>CVE ID</TableCell>
              <TableCell>Path</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {findings.content && findings.content.map((finding) => (
              (!filterTool || finding.tool === filterTool) &&
              (!filterSeverity || finding.security_severity_Level === filterSeverity) &&
              (!filterStatus || finding.status === filterStatus) &&
              <TableRow key={finding.id}>
                <TableCell>{finding.id}</TableCell>
                <TableCell className={finding.security_severity_Level}>
                  {finding.security_severity_Level}
                </TableCell>
                <TableCell>{finding.status}</TableCell>
                <TableCell>{finding.summary}</TableCell>
                <TableCell>{finding.tool}</TableCell>
                <TableCell>{finding.cve_id}</TableCell>
                <TableCell>{finding.pathIssue}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </div>
  );
};

export default FindingsTable;
