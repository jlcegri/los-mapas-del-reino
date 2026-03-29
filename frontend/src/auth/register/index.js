import "../../static/css/auth/authButton.css";
import "../../static/css/auth/authPage.css";
import tokenService from "../../services/token.service";
import FormGenerator from "../../components/formGenerator/formGenerator";
import { registerFormPlayers } from "./form/registerFormPlayers";
import { useRef, useState } from "react";

export default function Register() {
  let [authority, setAuthority] = useState("Player");

  const registerFormRef = useRef();

  function handleSubmit({ values }) {

    if (!registerFormRef.current.validate()) return;

    const request = {
      ...values,
      authority: authority,
    }

    fetch("/api/v1/auth/signup", {
      headers: { "Content-Type": "application/json" },
      method: "POST",
      body: JSON.stringify(request),
    })
      .then((res) => res.json().then((data) => ({ status: res.status, data })))
      .then(({ status, data }) => {
        if (status !== 200) {
          alert(data.message || "Error al registrarse");
          return;
        }
        const loginRequest = {
          username: request.username,
          password: request.password,
        };

        fetch("/api/v1/auth/signin", {
          headers: { "Content-Type": "application/json" },
          method: "POST",
          body: JSON.stringify(loginRequest),
        })
          .then((res) => res.json().then((data) => ({ status: res.status, data })))
          .then(({ status, data }) => {
            if (status !== 200) {
              alert(data.message || "Error al iniciar sesión");
              return;
            }

            tokenService.setUser(data);
            tokenService.updateLocalAccessToken(data.token);
            window.location.href = "/";
          })
          .catch((err) => {
            alert("Error de red al iniciar sesión: " + err.message);
          });
      })
      .catch((err) => {
        alert("Error de red al registrarse: " + err.message);
      });
  }
  return (
    <div className="auth-page-container">
      <h1>Register</h1>
      <div className="auth-form-container">
        <FormGenerator
          ref={registerFormRef}
          inputs={registerFormPlayers}
          onSubmit={handleSubmit}
          numberOfColumns={1}
          listenEnterKey
          buttonText="Save"
          buttonClassName="auth-button"
        />
      </div>
    </div>
  );
}
