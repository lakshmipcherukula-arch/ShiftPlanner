import {useState} from "react";
import { NavLink } from "react-router-dom";
import Header from "./Header";
import "../styles/Layout.css";
import Footer from "./Footer";

//Acts as a structural wrapper for the entire application.

function Layout({ children }) {
  const [menuOpen, setMenuOpen] = useState(false);

  const navLinks = [
    { name: "Home", path: "/" },
    { name: "My Schedule", path: "/schedule" },
    { name: "Find Shifts", path: "/find-shifts" },
    { name: "My Profile", path: "/profile" },
    { name: "Contact Us", path: "/contact" },
  ];

  return (
    <div className="app-container">
      <Header menuOpen={menuOpen} setMenuOpen={setMenuOpen} />
      <nav className={menuOpen ? "open" : ""}>
        <div className="nav-links">
        {navLinks.map((link) => (
          <NavLink key={link.name} to={link.path} onClick={() => setMenuOpen(false)}>
            {link.name}
          </NavLink>
        ))}
        </div>
      </nav>
      <main>{children}</main>
      <Footer/>
    </div>
  );
}

export default Layout;
