import { ResponsiveLine } from "@nivo/line";
import { useTheme } from "@mui/material";
import { tokens } from "../theme";

const LineChart = ({ findings }) => {
  const theme = useTheme();
  const colors = tokens(theme.palette.mode);

  // Parse the createdAt dates from the findings data and count occurrences
  const data = findings.reduce((acc, { createdAt }) => {
    const date = new Date(createdAt).toISOString().split('T')[0]; // Extract date without time
    acc[date] = acc[date] ? acc[date] + 1 : 1;
    return acc;
  }, {});

  // Prepare data in the format expected by ResponsiveLine
  const chartData = Object.keys(data).map(date => ({ x: date, y: data[date] }));

  // Define line colors
  const lineColors = {
    CodeQL: colors.primary[500],
    Dependabot: colors.primary[600],
  };

  return (
    <ResponsiveLine
      data={[{ id: 'CodeQL', data: chartData }, { id: 'Dependabot', data: chartData }]}
      theme={{
        axis: {
          domain: {
            line: { stroke: colors.grey[100] },
          },
          legend: {
            text: { fill: colors.grey[100] },
          },
          ticks: {
            line: { stroke: colors.grey[100], strokeWidth: 1 },
            text: { fill: colors.grey[100] },
          },
        },
        tooltip: {
          container: { color: colors.primary[500] },
        },
      }}
      colors={d => lineColors[d.id]}
      margin={{ top: 50, right: 110, bottom: 50, left: 60 }}
      xScale={{ type: "point" }}
      yScale={{ type: "linear", stacked: true, min: 0, max: "auto" }}
      yFormat=" >-.2f"
      curve="catmullRom"
      axisBottom={{ orient: "bottom", tickRotation: 45 }}
      enableGridX={false}
      enableGridY={false}
      pointSize={8}
      pointBorderWidth={2}
      pointBorderColor={{ from: "color" }}
      pointLabelYOffset={-12}
      useMesh={true}
      legends={[
        {
          anchor: "bottom-right",
          direction: "column",
          justify: false,
          translateX: 100,
          translateY: 0,
          itemsSpacing: 0,
          itemDirection: "left-to-right",
          itemWidth: 80,
          itemHeight: 20,
          itemOpacity: 0.75,
          symbolSize: 12,
          symbolShape: "circle",
          symbolBorderColor: "rgba(0, 0, 0, .5)",
          effects: [{ on: "hover", style: { itemBackground: "rgba(0, 0, 0, .03)", itemOpacity: 1 } }],
        },
      ]}
    />
  );
};

export default LineChart;