import { useState, useEffect } from "react";

//Displays a personalized, time-sensitive greeting to the user.
const getGreeting = () => {
  const hour = new Date().getHours();

  if (hour < 12) return "Good Morning";
  if (hour < 18) return "Good Afternoon";
  return "Good Evening";
};

const Greeting = (props) => {
  const [greeting, setGreeting] = useState(getGreeting());

  useEffect(() => {
    const interval = setInterval(() => {
      setGreeting(getGreeting());
    }, 60000);

    return () => clearInterval(interval);
  }, []);

  return (
    <div className="greeting-container">
      <h3>
        {greeting}, {props.name}!
      </h3>
    </div>
  );
};

export default Greeting;
