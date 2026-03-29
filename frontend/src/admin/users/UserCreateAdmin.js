import { useRef, useState } from "react";
import FormGenerator from "../../components/formGenerator/formGenerator";
import { registerFormPlayers } from "../../auth/register/form/registerFormPlayers";
import { registerFormAdmins } from "../../auth/register/form/registerFormAdmins";
import useFetchData from "../../util/useFetchData";
import "../../static/css/auth/authPage.css";
import "../../static/css/auth/authButton.css";
import tokenService from "../../services/token.service";

const jwt = tokenService.getLocalAccessToken();

export default function UserCreateAdmin() {
  const [type, setType] = useState(null);
  const [authority, setAuthority] = useState(null);
  const registerFormRef = useRef();

  function handleButtonClick(event) {
    const target = event.target;
    let value = target.value;
    if (value === "Back") value = null;
    else setAuthority(value);
    setType(value);
  }

  const auths = useFetchData(`/api/v1/users/authorities`, jwt);

  const handleSubmit = ({ values }) => {
    if (!registerFormRef.current.validate()) return;
    const payload = {
      ...values,  
      authority: authority.toUpperCase(),
    };
    if (authority === "Admin") {
        fetch("/api/v1/auth/signup/admin", {
            headers: { "Content-Type": "application/json" },
            method: "POST",
            body: JSON.stringify(payload),
        })
            .then((res) => res.json())
            .then((data) => {
                if (data.message) {
                  alert(data.message);
                  if (data.message === "Admin user registered successfully!") {
                    window.location.href = "/users";
                  }
                } else {
                  window.location.href = "/users";
                }
            })
    } else if (authority === "Player") {
        fetch("/api/v1/auth/signup", {
            headers: { "Content-Type": "application/json" },
            method: "POST",
            body: JSON.stringify(payload),
        })
            .then((res) => res.json())
            .then((data) => {
                if (data.message) {
                  alert(data.message);
                  if (data.message === "User registered successfully!") {
                    window.location.href = "/users";
                  }
                } else {
                  window.location.href = "/users";
                }
            })
    } else {
        alert("Debes seleccionar un rol válido (Admin o Player)");
    }
  }

  if (type) {
    return (
      <div className="auth-page-container">
        <h2 className="text-center text-md">Rellena los datos del nuevo usuario</h2>
        <h2>Tipo Usuario: {type}</h2>
        <div className="auth-form-container">
          <FormGenerator
            ref={registerFormRef}
            inputs={
              type === "Player"
                ? registerFormPlayers
                : registerFormAdmins
            }
            onSubmit={handleSubmit}
            numberOfColumns={1}
            listenEnterKey
            buttonText="Crear"
            buttonClassName="auth-button"
          />
        </div>
      </div>
    );
  }

  return (
    <div className="auth-page-container">
      <div className="auth-form-container">
        <h1 className="text-center">Nuevo Usuario</h1>
        <h2 className="text-center text-md">Selecciona tipo de usuario</h2>
        <div className="options-row">
          <button className="auth-button" value="Player" onClick={handleButtonClick}>
            Player
          </button>
          <button className="auth-button" value="Admin" onClick={handleButtonClick}>
            Admin
          </button>
        </div>
      </div>
    </div>
  );
}