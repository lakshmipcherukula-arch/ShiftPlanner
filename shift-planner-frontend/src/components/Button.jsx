import "../styles/Button.css"; 
//Reuable Button Component
function Button({ 
  children, 
  onClick, 
  variant="primary", 
  isLoading=false,
  disabled = false,
  type = "button",
  style = {} 
}) {
  return (
    <button
      type={type}
      className={`custom-btn ${variant}`}
      onClick={onClick}
      disabled={disabled || isLoading}
      style={style}
    >
      {isLoading && <span className="btn-spinner" />}
      
      <span>{isLoading ? "Loading..." : children}</span>
    </button>
  );
}

export default Button;