import { useState, useEffect } from "react";
import { Routes, Route, useNavigate } from "react-router-dom";
import Topbar from "./scenes/global/Topbar";
import Sidebar from "./scenes/global/Sidebar";
import Dashboard from "./scenes/dashboard";
import { CssBaseline, ThemeProvider } from "@mui/material";
import { ColorModeContext, useMode } from "./theme";
import Tickets from "./scenes/Tickets";
import LoginPage from "./scenes/login";
import Findings from "./scenes/findings";
import LogOut from "./scenes/logout";
import Scans from "./scenes/scans";

function App() {
  const [theme, colorMode] = useMode();
  const [isSidebar, setIsSidebar] = useState(true);
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [role, setRole] = useState("jksdfjd");
  const [organizationId, setOrganizationId] = useState(localStorage.getItem("orgId"))
  const [accessToken, setAccessToken] = useState(localStorage.getItem("sessionToken"));
  const [userName, setUserName] = useState(localStorage.getItem("userName"));

  console.log("jijij "+localStorage.getItem("userName"));

  // Handling so that on refresh the user is routed to the same page.
  useEffect(() => {
    const sessionToken = localStorage.getItem("sessionToken");
    const currUserRole = localStorage.getItem("userRole");
    if (sessionToken) {
      setIsLoggedIn(true);
      setRole(currUserRole);
    }
  }, []);

  const navigate = useNavigate(); // In order to redirect the user back to the home route

  const handleIsLoggedIn = (flag) =>{
    setIsLoggedIn(flag);
  }

  const handleRole = (userRole) => {
      setRole(userRole);
  }

  const redirectToDefault = () => {
    // console.log("Ritwik")
    navigate("/scans");
  };

  // console.log(role);

  if(isLoggedIn)
  {
    if(role==="admin"){
      return (
        <ColorModeContext.Provider value={colorMode}>
          <ThemeProvider theme={theme}>
            <CssBaseline />
            <div className="app">
            <Sidebar isSidebar={isSidebar} userRole={role} userName={localStorage.getItem("userName")} />
              <main className="content">
                <Topbar setIsSidebar={setIsSidebar} />
                <Routes>
                  <Route path="/" element={<Dashboard />} />
                  {/* <Route path="/dashboard" element={<Dashboard />} /> */}
                  <Route path="/findings" element={<Findings />}  />
                  <Route path="/tickets" element={<Tickets />} />
                  <Route path="/logout" element={<LogOut onLogout={handleIsLoggedIn} redirectToDefault={redirectToDefault} />} />
                  <Route path="/scans" element={<Scans/>} />
                  <Route path="/runbook" element={<Scans/>} />
                </Routes>
              </main>
            </div>
          </ThemeProvider>
        </ColorModeContext.Provider>
      );
    }

    else{
      return (
        <ColorModeContext.Provider value={colorMode}>
          <ThemeProvider theme={theme}>
            <CssBaseline />
            <div className="app">
              <Sidebar isSidebar={isSidebar} userRole={role} userName={localStorage.getItem("userName")} />
              <main className="content">
                <Topbar setIsSidebar={setIsSidebar} />
                <Routes>
                  <Route path="/" element={<Dashboard />} />
                  {/* <Route path="/dashboard" element={<Dashboard />} /> */}
                  <Route path="/findings" element={<Findings />}  />
                  {/* <Route path="/tickets" element={<Tickets />} /> */}
                  <Route path="/logout" element={<LogOut onLogout={handleIsLoggedIn} redirectToDefault={redirectToDefault} />} />
                  <Route path="/scans" element={<Scans/>} />
                </Routes>
              </main>
            </div>
          </ThemeProvider>
        </ColorModeContext.Provider>
      );
    }
  }

  else
  return(
   <LoginPage onLogin={handleIsLoggedIn} assignRole={handleRole} />
    );
}

export default App;
