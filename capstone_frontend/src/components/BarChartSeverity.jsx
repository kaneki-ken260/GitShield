import { useTheme } from "@mui/material";
import { Bar } from 'react-chartjs-2';
import { Chart, registerables } from 'chart.js';
Chart.register(...registerables);

const BarChartSeverity = ({ findings, isDashboard = false }) => {
  const theme = useTheme();

  if (!findings || findings.length === 0) return null;

  // Group the data by severity level and status, and calculate the count for each combination
  const groupedData = Object.values(findings.reduce((acc, curr) => {
    const key = curr.security_severity_Level;
    if (!acc[key]) {
      acc[key] = {
        security_severity_level: curr.security_severity_Level,
        open: 0,
        mitigated: 0,
      };
    }
    acc[key][curr.status]++;
    return acc;
  }, {}));

  // Prepare chart data
  const chartData = {
    labels: groupedData.map(item => item.security_severity_level), // X-axis labels
    datasets: [{
      label: 'Count',
      data: groupedData.map(item => item.open + item.mitigated), // Y-axis data
      backgroundColor: [
        'rgba(255, 99, 132, 0.2)',
        'rgba(255, 159, 64, 0.2)',
        'rgba(255, 205, 86, 0.2)',
        'rgba(75, 192, 192, 0.2)'
      ],
      borderColor: [
        'rgb(255, 99, 132)',
        'rgb(255, 159, 64)',
        'rgb(255, 205, 86)',
        'rgb(75, 192, 192)'
      ],
      borderWidth: 1
    }]
  };

  const chartOptions = {
    scales: {
      y: {
        beginAtZero: true
      }
    }
  };

  return (
    <div style={{ height: "100%" }}> {/* Set height of the chart container */}
      <h2>Severity Bar Chart</h2>
      <Bar data={chartData} options={chartOptions} height={150} /> {/* Specify height here */}
    </div>
    
  );
};

export default BarChartSeverity;
