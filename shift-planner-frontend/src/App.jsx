import { useEffect, useState } from "react";
import { Routes, Route } from "react-router-dom";
import Home from "./components/Home";
import Layout from "./components/Layout";
import MySchedule from "./components/MySchedule";
import FindShifts from "./components/FindShifts";
import MyProfile from "./components/MyProfile";
import Contact from "./components/Contact";
import Login from "./components/Login";

function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [userId, setUserId] = useState(null);
  const [availableShifts, setAvailableShifts] = useState([]);
  const [mySchedule, setMySchedule] = useState([]);

  useEffect(() => {
    fetch("/shifts")
      .then((res) => {
        if (!res.ok) throw new Error("Unable to fetch shifts");
        return res.json();
      })
      .then((data) => setAvailableShifts(data))
      .catch((err) => console.error("Error fetching shifts:", err));
  }, []);

  useEffect(() => {
    if (userId === null) return;

    fetch(`/schedules/users/${userId}`)
      .then((res) => {
        if (!res.ok) throw new Error("Unable to fetch schedule");
        return res.json();
      })
      .then((schedule) => setMySchedule(schedule.shifts || []))
      .catch((err) => console.error("Error fetching schedule:", err));
  }, [userId]);

  const handleLoginSuccess = async () => {
    try {
      const response = await fetch("/users");
      if (!response.ok) throw new Error("Unable to fetch users");

      const users = await response.json();
      const loggedInUser =
        users.find((user) => user.username === "user") || users[0];
      if (!loggedInUser) throw new Error("Logged-in user was not found");

      setUserId(loggedInUser.userId);
      setIsLoggedIn(true);
    } catch (err) {
      console.error("Error loading logged-in user:", err);
    }
  };

  const updateSchedule = async (id, method) => {
    if (userId === null) return false;

    const response = await fetch(
      `/schedules/users/${userId}/shifts/${id}`,
      { method },
    );

    if (!response.ok) {
      throw new Error(`Unable to ${method === "POST" ? "select" : "drop"} shift ${id}`);
    }

    return true;
  };

  const handleSelect = async (id) => {
    const shiftToSelect = availableShifts.find(
      (shift) => (shift.shiftId || shift.id) === id
    );

    if (shiftToSelect) {
      try {
        await updateSchedule(id, "POST");
        setAvailableShifts((currentOpenShifts) =>
          currentOpenShifts.filter(
            (shift) => (shift.shiftId || shift.id) !== id
          )
        );

        setMySchedule((currentMySchedule) => [
          ...currentMySchedule,
          { ...shiftToSelect, isAvailable: false },
        ]);
        return true;
      } catch (err) {
        console.error("Error selecting shift:", err);
        return false;
      }
    }

    return false;
  };
  
  const handleDropShift = async (id) => {
    const shiftToDrop = mySchedule.find(
      (shift) => (shift.shiftId || shift.id) === id
    );

    if (!shiftToDrop) return false;

    try {
      await updateSchedule(id, "DELETE");
      setMySchedule((currentMySchedule) =>
        currentMySchedule.filter((shift) => (shift.shiftId || shift.id) !== id)
      );
      setAvailableShifts((currentOpenShifts) => [
        ...currentOpenShifts,
        { ...shiftToDrop, isAvailable: true },
      ]);
      return true;
    } catch (err) {
      console.error("Error dropping shift:", err);
      return false;
    }
  };

  if (!isLoggedIn) {
return (
      <Routes>
        <Route
          path="*"
          element={<Login onLoginSuccess={handleLoginSuccess} />}
        />
      </Routes>
    );
  }  
  return (
      <Layout>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/home" element={<Home/>}/>
          <Route
            path="/schedule"
            element={
              <MySchedule schedule={mySchedule} onDropShift={handleDropShift} />
            }
          />
          <Route
            path="/find-shifts"
            element={
              <FindShifts
                assignedShifts={mySchedule}
                onSelectShift={handleSelect}
              />
            }
          />
          <Route path="/profile" element={<MyProfile />} />
          <Route path="/contact" element={<Contact />} />
        </Routes>
      </Layout>
  );
}

export default App;
