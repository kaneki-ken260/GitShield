import React, { useState } from "react";
import Drawer from "@mui/material/Drawer";
import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import ListItemIcon from "@mui/material/ListItemIcon";
import ListItemText from "@mui/material/ListItemText";
import DashboardIcon from "@mui/icons-material/Dashboard";
import FindInPageIcon from "@mui/icons-material/FindInPage";
import AssessmentIcon from "@mui/icons-material/Assessment";
import MenuIcon from "@mui/icons-material/Menu";
import armorcode from "../../assets/armorcode_logo.png";

import "./Sidebar.css";

const Sidebar = () => {
  const [open, setOpen] = useState(false);

  const handleDrawerOpen = () => {
    setOpen(true);
  };

  const handleDrawerClose = () => {
    setOpen(false);
  };

  return (
    <div className={`sidebar ${open ? "open" : "closed"}`}>
      <div
        className="menu-icon"
        onClick={open ? handleDrawerClose : handleDrawerOpen}
      >
        <MenuIcon />
      </div>
      <Drawer variant="permanent" open={open}>
        <List>
          <ListItem button>
            <div className="dashboard-image-container">
              <img
                src={armorcode}
                alt="Dashboard"
                className="dashboard-image"
              />
            </div>
          </ListItem>
          <ListItem button>
            <ListItemIcon>
              <DashboardIcon />
            </ListItemIcon>
            <ListItemText primary="Dashboard" />
          </ListItem>
          <ListItem button>
            <ListItemIcon>
              <FindInPageIcon />
            </ListItemIcon>
            <ListItemText primary="Findings" />
          </ListItem>
          <ListItem button>
            <ListItemIcon>
              <AssessmentIcon />
            </ListItemIcon>
            <ListItemText primary="Scans" />
          </ListItem>
        </List>
      </Drawer>
    </div>
  );
};

export default Sidebar;
