import { useTheme } from "@mui/material";
import { ResponsiveBar } from "@nivo/bar";
import { tokens } from "../theme";

const BarChartTool = ({ findings, isDashboard = false }) => {
  const theme = useTheme();
  const colors = tokens(theme.palette.mode);

  if (!findings || !findings.length) return null;

  // Determine text color based on theme mode
  const labelTextColor = theme.palette.mode === "dark" ? "#fff" : "inherit";

  // Group the data by tool and security_severity_level, and calculate the count for each combination
  const groupedData = Object.values(
    findings.reduce((acc, curr) => {
      const key = curr.tool;
      const severity = curr.security_severity_Level;
      if (!acc[key]) {
        acc[key] = { tool: key };
      }
      if (!acc[key][severity]) {
        acc[key][severity] = 0;
      }
      acc[key][severity]++;
      return acc;
    }, {})
  );

  // Custom tooltip component to display count on hover
  const CustomTooltip = ({ id, value, indexValue }) => (
    <div style={{ background: "#fff", padding: "10px", border: "1px solid #ccc" }}>
      <p>{id}</p>
      <p>Count: {value}</p>
      <p>Severity level: {indexValue}</p>
    </div>
  );

  return (
    <ResponsiveBar
      data={groupedData}
      theme={{
        axis: {
          domain: {
            line: {
              stroke: colors.grey[100],
            },
          },
          legend: {
            text: {
              fill: colors.grey[100],
            },
          },
          ticks: {
            line: {
              stroke: colors.grey[100],
              strokeWidth: 1,
            },
            text: {
              fill: colors.grey[100],
            },
          },
        },
        legends: {
          text: {
            fill: colors.grey[100],
          },
        },
      }}
      keys={["critical", "high", "medium", "low"]} // Use the severity levels as keys
      indexBy="tool"
      margin={{ top: 50, right: 130, bottom: 50, left: 60 }}
      padding={0.3}
      valueScale={{ type: "linear" }}
      indexScale={{ type: "band", round: true }}
      colors={{ scheme: "nivo" }}
      borderColor={{ from: "color", modifiers: [["darker", "1.6"]] }}
      axisBottom={{
        tickSize: 5,
        tickPadding: 5,
        tickRotation: 0,
        legend: isDashboard ? undefined : "Tool",
        legendPosition: "middle",
        legendOffset: 32,
      }}
      axisLeft={{
        tickSize: 5,
        tickPadding: 5,
        tickRotation: 0,
        legend: isDashboard ? undefined : "Count",
        legendPosition: "middle",
        legendOffset: -40,
      }}
      enableLabel={false}
      labelSkipWidth={12}
      labelSkipHeight={12}
      labelTextColor={labelTextColor} // Set label text color based on theme mode
      tooltip={CustomTooltip} // Use custom tooltip component
      legends={[
        {
          dataFrom: "keys",
          anchor: "bottom-right",
          direction: "column",
          justify: false,
          translateX: 120,
          translateY: 0,
          itemsSpacing: 2,
          itemWidth: 100,
          itemHeight: 20,
          itemDirection: "left-to-right",
          itemOpacity: 0.85,
          symbolSize: 20,
          effects: [
            {
              on: "hover",
              style: {
                itemOpacity: 1,
              },
            },
          ],
        },
      ]}
      role="application"
      barAriaLabel={(e) => {
        return `${e.id}: ${e.formattedValue} in severity level: ${e.indexValue}`;
      }}
    />
  );
};

export default BarChartTool;
