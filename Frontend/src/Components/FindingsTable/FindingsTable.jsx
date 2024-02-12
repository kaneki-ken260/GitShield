import React, { useState, useEffect } from 'react';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import axios from 'axios';
import './FindingsTable.css'

const FindingsTable = () => {
  const [findings, setFindings] = useState([]);
  const [error, setError] = useState(null);

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

  return (
    <div>
      {error && <div>Error: {error.message}</div>}
      <TableContainer component={Paper} className='findingsTable'>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Finding ID</TableCell>
              <TableCell>Severity</TableCell>
              <TableCell>Status</TableCell>
              <TableCell>Summary</TableCell>
              <TableCell>Tool</TableCell>
              <TableCell>CVE ID</TableCell>
              <TableCell>Created At</TableCell>
              <TableCell>Updated At</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {findings.content && findings.content.map((finding) => (
              <TableRow key={finding.id}>
                <TableCell>{finding.id}</TableCell>
                <TableCell className={finding.security_severity_Level}>
                  {finding.security_severity_Level}
                </TableCell>
                <TableCell>{finding.state}</TableCell>
                <TableCell>{finding.summary}</TableCell>
                <TableCell>{finding.tool}</TableCell>
                <TableCell>{finding.cve_value}</TableCell>
                <TableCell>{finding.created_at}</TableCell>
                <TableCell>{finding.updated_at}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </div>
  );
};

export default FindingsTable;
