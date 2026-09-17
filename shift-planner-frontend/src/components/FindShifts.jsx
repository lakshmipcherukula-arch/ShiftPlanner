import { useState,useEffect } from "react";
import "../styles/FindShifts.css";
import Button from "./Button";

//Displays a list of unassigned open shifts

function FindShifts({ assignedShifts=[], onSelectShift }) {

  const [shifts, setShifts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [conflictShiftId,setConflictShiftId] = useState(null);
  const [successMessage, setSuccessMessage] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  //filter states
  const [selectedFilter, setSelectedFilter] = useState("All");
  const [selectedDay, setSelectedDay]  =useState("All");

  useEffect(() => {
    const fetchFilteredShifts = async () => {
      setLoading(true);
      try {
        const url = `/shifts?type=${selectedFilter}&day=${selectedDay}`;

        const response = await fetch(url);
        if (!response.ok) {
          throw new Error("Unable to fetch shifts from server");
        }

        const data = await response.json();
        setShifts(data);
      } catch (err) {
        console.error("Error fetching filtered shifts:", err);
        setErrorMessage("Failed to load shifts from server.");
      } finally {
        setLoading(false);
      }
    };

    fetchFilteredShifts();
  }, [selectedFilter, selectedDay]);


  //Helper function to format ISO date strings (YYYY-MM-DD).
  //Appending 'T00:00:00' prevents timezone shifts from offsetting the local date.

  const formatShiftDate = (dateString) => {
    if (!dateString) return "";

    let dateObj;
      if (typeof dateString === "string" && dateString.includes("T")) {
    // Handles ISO/Java Date timestamp format
      dateObj = new Date(dateString);
      } else {
    // Handles simple date string format: "2026-07-22"
      dateObj = new Date(dateString + "T00:00:00");
      }
      return dateObj.toLocaleDateString("en-US", {
        weekday: "long",
        month: "short",
        day: "numeric",
      });
};


const assignedShiftIds = (assignedShifts || []).map((s) => s.shiftId || s.id);
const filteredShifts = (shifts || []).filter((shift) => {
  const currentId = shift.shiftId || shift.id;
  if (assignedShiftIds.includes(currentId)) {
    return false;
    }

  return true;
});
  //Checking for overlapping/conflict shifts

 const handleSelectClick = (selectedShift) => {
    // Standardize ID lookup to support shiftId or id
    const currentShiftId = selectedShift.shiftId || selectedShift.id;

    const hasConflict = assignedShifts.some((assigned) => {
      return (
        assigned.date === selectedShift.date &&
        assigned.startTime < selectedShift.endTime &&
        selectedShift.startTime < assigned.endTime
      );
    });

    if (hasConflict) {
        setConflictShiftId(currentShiftId);
        setTimeout(() => {
          setConflictShiftId(null);
        }, 5000);
        return; 
    }

    onSelectShift(currentShiftId);
    setShifts((prevShifts) =>
      prevShifts.filter((s) => (s.shiftId || s.id) !== currentShiftId)
    );
    
    setSuccessMessage("Shift added to schedule successfully!");
    setTimeout(() => {
      setSuccessMessage("");
      }, 1000); 
  };  

return (
    <div className="findshifts-container">
      <h2> Available Shifts</h2>

      {/* Filter Bar*/}
      <div className="shift-filters">
        <div className="time-of-day">
        {["All", "Morning", "Afternoon", "Evening"].map((category) =>(

          <button
            key = {category}
            type = "button"
            onClick={() => setSelectedFilter(category)}
            className = {`filter-btn ${selectedFilter === category ? "active" : ""}`}
            >
              {category}

            </button>
        ))}
      </div>

      {/* Day Dropdown */}
      
      <div className="filter-group">
          <label htmlFor="day-filter" className="filter-label">
            Filter by Day:
          </label>
      <select
        className="day-select-dropdown"
        value={selectedDay}
        onChange={(e) => setSelectedDay(e.target.value)}
      >
        <option value="All">All Days</option>
          <option value="Sunday">Sunday</option>
          <option value="Monday">Monday</option>
          <option value="Tuesday">Tuesday</option>
          <option value="Wednesday">Wednesday</option>
          <option value="Thursday">Thursday</option>
          <option value="Friday">Friday</option>
          <option value="Saturday">Saturday</option>
      </select>
      </div>
      
      </div>


      {successMessage && (
        <div style={{
          backgroundColor: "#d4edda", 
          color: "#155724",           
          padding: "10px",
          borderRadius: "5px",
          textAlign: "center",
          fontWeight: "bold",
          marginBottom: "15px"
        }}>
          {successMessage}
        </div>
      )}
      {filteredShifts.length === 0 ? (
        <p style={{ textAlign: "center", color:"gray" }}>
          No shifts available.
        </p>
        ) : (
          <div className="shifts-list">
            {filteredShifts.map((shift) => {
                const shiftId = shift.shiftId || shift.id;
                const isConflicting = conflictShiftId === shiftId;
              return( 
               <div key={shiftId} className="shift-card">
                <h3
                  className="shift-card-date"
                  style={{ color: "blue", margin: "0 0 10px 0" }}
                >
                {formatShiftDate(shift.date)}
                </h3>
                <p>
                  <strong>Start Time:</strong> {shift.startTime}
                </p>
                <p>
                  <strong>End Time:</strong>
                  {shift.endTime}
                </p>
                <p>
                 <strong>Hours:</strong>
                  {shift.hours}
                </p>
                {isConflicting && (
                  <p style={{ color: "red", fontWeight: "bold", fontSize: "0.85rem", margin: "10px 0" }}>
                     Overlaps with an existing shift on your schedule!
                  </p>
                )}
                
                <Button 
                 type="button" 
                 onClick={() => handleSelectClick(shift)}
                 style={{
                  backgroundColor: isConflicting ? "gray" : "blue",
                  color: "white",
                  border: "none",
                  padding: "0.5rem 1rem",
                  borderRadius: "4px",
                  cursor: isConflicting ? "not-allowed" : "pointer",
                  fontWeight: "bold",
                }}
                disabled={isConflicting}
              >
                Select Shift              
                </Button>

            </div>
            );
          })}
          {errorMessage && (
            <div style={{ color: "red", textAlign: "center", marginBottom: "15px" }}>
              {errorMessage}
            </div>
          )}
        </div>
      )}
    </div>
  );
}

export default FindShifts;
