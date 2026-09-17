import { useState } from "react";
import Calendar from "react-calendar";//Installed react-calendar library
import "react-calendar/dist/Calendar.css";

//Provides an interactive calendar interface where employees can select a date to view their assigned shifts for that day. 
//It also allows users to "drop" an assigned shift.

function MySchedule({ schedule, onDropShift }) {
  //Validation: Ensures schedule is always treated as an array, avoiding runtime crashes
  const selectedShifts = Array.isArray(schedule) ? schedule : [];

  const [selectedDate, setSelectedDate] = useState(new Date());

  const [dropSuccessMessage, setDropSuccessMessage] = useState("");
  
  const formatCalendarDate = (dateObj) => {
    const year = dateObj.getFullYear();
    const month = String(dateObj.getMonth() + 1).padStart(2, "0");// padding ensures single digit months and days are formatted as "01", "02"
    const day = String(dateObj.getDate()).padStart(2, "0");
    return `${year}-${month}-${day}`;
  };

  const formattedDateString = formatCalendarDate(selectedDate);

//Filters the user's schedule to only display shifts scheduled for the active calendar date
  
const displayedShifts = selectedShifts.filter(
    (shift) => shift.date === formattedDateString,
  );

  const handleDropClick = async (shiftId) => {
    if (onDropShift) {
      await onDropShift(shiftId);
    }
    setDropSuccessMessage("Shift dropped successfully!");
    setTimeout(() => {
      setDropSuccessMessage("");
    }, 2000);
  };
  return (
    <div className="myschedule-container" style={{ padding: "1rem" }}>
      <h2 style={{color: "navy"}}>My Schedule (Calendar View):</h2>

      <div
        style={{
          display: "flex",
          justifyContent: "center",
          marginBottom: "20px",
        }}
      >
        <Calendar onChange={setSelectedDate} value={selectedDate} />
      </div>

      <div className="shifts-section">
        <h3 style={{color:"navy"}}>Shifts for {formattedDateString}:</h3>
        <br/>
        {displayedShifts.length === 0 ? (
          <p style={{color:"navy", fontSize:"large"}}>No shifts scheduled for this day.</p>
        ) : (
          displayedShifts.map((shift) => (
            <div
              key={shift.shiftId || shift.id}
              style={{
                borderLeft: "5px solid blue",
                padding: "12px",
                margin: "10px 0",
                background: "white",
                borderRadius: "4px",
                boxShadow: "0 2px 4px blue",
                width: "auto"
              }}
            >
              <p style={{ margin: "4px 0" }}>
                <strong>Time:</strong> {shift.startTime} - {shift.endTime}
              </p>
              <p style={{ margin: "4px 0" }}>
                <strong>Hours:</strong> {shift.hours} hrs
              </p>
              <button
                onClick={() => onDropShift(shift.shiftId || shift.id)}
                style={{
                  backgroundColor: "red",
                  color: "white",
                  border: "none",
                  padding: "5px 10px",
                  borderRadius: "4px",
                  cursor: "pointer",
                  marginTop: "5px",
                }}
              >
                Drop Shift
              </button>
            </div>
          ))
        )}
      </div>
    </div>
  );
}

export default MySchedule;
