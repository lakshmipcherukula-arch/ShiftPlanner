import "../styles/Footer.css";

const Footer = () => {
  return (
    <footer className="simple-footer">
      <p>&copy; {new Date().getFullYear()} Shift Planner. All rights reserved.</p>
    </footer>
  );
};

export default Footer;