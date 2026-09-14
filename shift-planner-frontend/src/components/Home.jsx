import Greeting from "./Greeting";
import "../styles/Home.css";

const Home = () => {
  const name = "Lakshmi";

  return (
    <div className="home-container">
      <div className="home-card">
        <Greeting name={name} />
        <div className="home-messagebox">
          <p>Welcome to Shift Planner Application.</p>
          <br/>
          <p>Manage and plan your shifts effortlessly.</p>
        </div>
      </div>
    </div>
  );
};

export default Home;
