import { Link } from "react-router-dom"; 
import logo from "../assets/logo.png";

const Header = ({ menuOpen, setMenuOpen }) => {
  return (
    <header
      className="application-header"
      style={{
        backgroundColor: "darkblue",
        color: "white",
        padding: "0 clamp(16px, 3vw, 24px)",
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center",
        borderBottom: "3px solid blue",
        height: "60px",
        boxSizing: "border-box",
      }}
    >
      <Link 
        to="/" 
        onClick={() => setMenuOpen(false)}
        style={{ 
          display: "flex", 
          alignItems: "center", 
          textDecoration: "none", 
          color: "white" 
        }}
      >
      <img 
          src={logo} 
          alt="Shift Planner Logo" 
          style={{ height: "56px", width: "auto", marginRight: "18px" }} 
        />
        <div style={{ fontSize: "clamp(1.2rem, 1.5vw + 0.5rem, 1.5rem)", fontWeight: "bold"}}>
          Shift Planner
        </div>
      </Link>
      <button 
        className="hamburger-btn" 
        onClick={() => setMenuOpen(!menuOpen)}
        style={{
          background: "none",
          border: "none",
          fontSize: "1.8rem",
          color: "white",
          cursor: "pointer"
        }}
      >
        {menuOpen ? "X" : "☰"} 
      </button>
    </header>
  );
};

export default Header;
