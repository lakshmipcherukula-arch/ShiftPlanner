import React, { useState } from "react";
import Button from "./Button"; 
import "../styles/Contact.css";

//Allows users (employees) to send support messages or leave requests
function Contact() {
  const [subject, setSubject] = useState("");
  const [message, setMessage] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isSent, setIsSent] = useState(false); 

  const handleSubmit = (e) => {
    e.preventDefault();
    setIsSubmitting(true); 

    setTimeout(() => {
      setIsSubmitting(false);
      setIsSent(true);
      setSubject("");
      setMessage("");
    }, 1500);
  };
  if (isSent) {
    return (
      <div className="contact-page">
        <h1>Contact Us</h1>
        <hr />
        <br />
        <p className="success-message">Message sent successfully!</p>
      </div>
    );
  }

  return (
    <div className="contact-page">
      <h1>Contact Us</h1>
      <hr />
      <br/>
      
      <form onSubmit={handleSubmit}>
        <label><strong>Subject:</strong></label>
        <input 
          type="text" 
          value={subject}
          onChange={(e) => setSubject(e.target.value)}
          placeholder="Shift error,Leave Request" 
          required 
        />

        <label><strong>Message:</strong></label>
        <textarea 
          value={message}
          onChange={(e) => setMessage(e.target.value)}
          placeholder="Write your message here..." 
          rows="4" 
          required 
        />

        <Button type="submit" isLoading={isSubmitting}>
          Send Message
        </Button>
      </form>
      <h3>Guidelines:</h3>
      <br/>
      <ul>
        <li>Check your schedule before messaging.</li>
        <li>We reply within 24 hours.</li>
      </ul>
       <br/>

      <h3>Directory</h3>
      <table>
        <thead>
          <tr>
            <th>Team</th>
            <th>Email</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>Support</td>
            <td>help@shiftplanner.com</td>
          </tr>
          <tr>
            <td>Manager</td>
            <td>manager@shiftplanner.com</td>
          </tr>
        </tbody>
      </table>
    </div>
  );
}

export default Contact;