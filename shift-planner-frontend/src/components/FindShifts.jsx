import { useState } from "react";
import "../styles/FindShifts.css";
import Button from "./Button";

//Displays a list of unassigned open shifts

function FindShifts({ shifts,assignedShifts=[], onSelectShift }) {
  
  const [conflictShiftId,setConflictShiftId] = useState(null);
  const [successMessage, setSuccessMessage] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [selectedFilter, setSelectedFilter] = useState("All");

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

const getShiftCategory = (startTimeStr) =>{
  if(!startTimeStr) return "All";

  const hour = parseInt(startTimeStr.split(":")[0], 10);

  if(hour < 12) return "Morning";
  if(hour >= 12  && hour < 17) return "Afernoon";
  return "Evening";
};

const filteredShifts = shifts.filter((shift) => {

  if (selectedFilter === "All") return true;
  return getShiftCategory(shift.startTime) === selectedFilter;
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
    
    setSuccessMessage("Shift added to schedule successfully!");
    setTimeout(() => {
      setSuccessMessage("");
      }, 3000); 
  };  

return (
    <div className="findshifts-container">
      <h2> Available Shifts</h2>

      {/* Filter Button*/}
      <div className="shift-filters">
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
      {shifts.length === 0 ? (
        <p style={{ textAlign: "center", color:"gray" }}>
          No shifts available.
        </p>
        ) : (
          <div className="shifts-list">
            {shifts.map((shift) => {
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
