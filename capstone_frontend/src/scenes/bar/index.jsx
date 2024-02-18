import { Box } from "@mui/material";
import Header from "../../components/Header";
import BarChartTool from "../../components/BarChartTool";

const Bar = () => {
  return (
    <Box m="20px">
      <Header title="Bar Chart" subtitle="Simple Bar Chart" />
      <Box height="75vh">
        <BarChartTool />
      </Box>
    </Box>
  );
};

export default Bar;
