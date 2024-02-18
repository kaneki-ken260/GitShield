import { Box, useTheme } from "@mui/material";
import { tokens } from "../theme";

const ProgressCircleStatus = ({ findings, status, progress = 0.75, size = 40 }) => {
  const theme = useTheme();
  const colors = tokens(theme.palette.mode);

  if(findings==null) return null;

  // Calculate progress based on the formula
  const totalCount = findings.length;
  const statusCount = findings.filter((finding) => finding.status === status).length;
  const dynamicProgress = statusCount / totalCount;

  // Convert progress to degrees for the angle
  const angle = dynamicProgress * 360;

  return (
    <Box
      sx={{
        background: `radial-gradient(${colors.primary[400]} 55%, transparent 56%),
            conic-gradient(transparent 0deg ${angle}deg, ${colors.blueAccent[500]} ${angle}deg 360deg),
            ${colors.greenAccent[500]}`,
        borderRadius: "50%",
        width: `${size}px`,
        height: `${size}px`,
      }}
    />
  );
};

export default ProgressCircleStatus;
