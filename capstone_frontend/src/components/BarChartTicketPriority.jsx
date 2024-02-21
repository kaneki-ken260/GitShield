import { useTheme } from "@mui/material";
import { ResponsiveBar } from "@nivo/bar";
import { tokens } from "../theme";

const BarChartTicketPriority = ({ tickets, isDashboard = false }) => {
  const theme = useTheme();
  const colors = tokens(theme.palette.mode);

  if (!tickets || !tickets.length) return null;

  // Group the data by priority and status, and calculate the count for each combination
  const groupedData = Object.values(
    tickets.reduce((acc, curr) => {
      const key = curr.priority;
      if (!acc[key]) {
        acc[key] = {
          priority: key,
          "To Do": 0,
          "In Progress": 0,
          Done: 0,
        };
      }
      acc[key][curr.status]++;
      return acc;
    }, {})
  );

  // Find the maximum count of tickets in any category
  const maxCount = Math.max(
    ...groupedData.map((item) =>
      Math.max(item["To Do"], item["In Progress"], item.Done)
    )
  );

  // Generate Y-axis tick values as integers from 0 to the maximum count
  const yAxisTicks = Array.from({ length: maxCount + 1 }, (_, i) => i);

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
      keys={["To Do", "Done", "In Progress"]} // Use the status as keys
      indexBy="priority"
      margin={{ top: 50, right: 130, bottom: 50, left: 60 }}
      padding={0.3}
      valueScale={{ type: "linear" }}
      indexScale={{ type: "band", round: true }}
      colors={{ scheme: "nivo" }}
      borderColor={{
        from: "color",
        modifiers: [["darker", "1.6"]],
      }}
      axisBottom={{
        tickSize: 5,
        tickPadding: 5,
        tickRotation: 0,
        legend: isDashboard ? undefined : "Priority Level",
        legendPosition: "middle",
        legendOffset: 32,
      }}
      axisLeft={{
        tickValues: yAxisTicks, // Use custom tick values
        tickSize: 5,
        tickPadding: 5,
        tickRotation: 0,
        legend: isDashboard ? undefined : "Count",
        legendPosition: "middle",
        legendOffset: -40,
        tickCount: maxCount + 1, // Ensure all values are displayed
      }}
      enableLabel={false}
      labelSkipWidth={12}
      labelSkipHeight={12}
      labelTextColor={{
        from: "color",
        modifiers: [["darker", 1.6]],
      }}
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
        return `${e.id}: ${e.formattedValue} in priority level: ${e.indexValue}`;
      }}
    />
  );
};

export default BarChartTicketPriority;
