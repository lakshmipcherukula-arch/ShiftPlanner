import {useState} from "react";
import "../styles/MyProfile.css";

function MyProfile() {
  
const employee = {
  name: "Lakshmi",
  employeeId: "Lakshmi2026",
  role: "Associate",
  Workplace: "ABC Warehouse"
};

const [address,setAddress] = useState("123 Manchester Rd, Manchester, MO");
const [isEditing,setIsEditing] = useState(false);
const [tempAddress,setTempAddress] = useState(address);

const handleSave = () => {
  setAddress(tempAddress);
  setIsEditing(false);
};
const handleCancel = () => {
  setTempAddress(address);
  setIsEditing(false);
};
return(
  <div className="profile-container">
    <h1> My Profile </h1>
    <hr className="divider" />
    <div className="profile-card">
      <div className="profile-details">
        <p><strong>Employee Name:</strong> {employee.name} </p>
        <p><strong>Employee Id:</strong> {employee.employeeId} </p>
        <p><strong>Employee Role:</strong> {employee.role} </p>
        <p><strong>Employee Workplace:</strong> {employee.Workplace} </p>

        <hr className="inner-divider"/>
        <div className="address-section">
          <label><strong>Home Address:</strong></label>

          {!isEditing ? (
            <div className="address-view">
              <span>{address}</span>
              <button className="btn-inline-edit" onClick={() => setIsEditing(true)}>Edit</button>
            </div>
          ) : ( <div className="address-edit">
                <input 
                  type="text" 
                  value={tempAddress} 
                  onChange={(e) => setTempAddress(e.target.value)}
                  className="address-input"
                  autoFocus
                />
                <div className="address-actions">
                  <button className="btn-save" onClick={handleSave}>Save</button>
                  <button className="btn-cancel" onClick={handleCancel}>Cancel</button>
                </div>
              </div>
              )
            }
        </div>
      </div>
    </div>
  </div>
);
}

export default MyProfile;
