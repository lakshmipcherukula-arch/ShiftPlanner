import { useState } from "react";
import Calendar from "react-calendar";//Installed react-calendar library
import "react-calendar/dist/Calendar.css";
import "../styles/MySchedule.css";

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
    <div className="myschedule-container">
      <h2 className="myschedule-title"> My Schedule (Calendar View):</h2>

      {dropSuccessMessage && (

        <div className="drop-success-banner">
          {dropSuccessMessage}
        </div>
      )}

      <div className="calendar-wrapper">
        <Calendar onChange={setSelectedDate} value={selectedDate} />
      </div>

      <div className="shifts-section">
        <h3 className="shift-section-title">
          Shifts for {formattedDateString}:
        </h3>
        <br/>
        {displayedShifts.length === 0 ? (
          <p className="no-shifts-text">
            No shifts scheduled for this day.
          </p>
        ) : (
          displayedShifts.map((shift) => (
            <div
              key={shift.shiftId || shift.id}
              className="shift-card"
            >
              <p>
                <strong>Time:</strong> {shift.startTime} - {shift.endTime}
              </p>
              <p>
                <strong>Hours:</strong> {shift.hours} hrs
              </p>
              
              <button
                className="drop-shift-btn"
                onClick={() => handleDropClick(shift.shiftId || shift.id)}
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
