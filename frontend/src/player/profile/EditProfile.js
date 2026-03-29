import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Form, Input, Button } from "reactstrap";
import tokenService from "../../services/token.service";
import "../../static/css/player/profile.css";
import getErrorModal from "../../util/getErrorModal";

const user = tokenService.getUser();
const jwt = tokenService.getLocalAccessToken();

export default function EditProfile() {
  const navigate = useNavigate();

  const [player, setPlayer] = useState(null);
  const [userData, setUserData] = useState({ username: "" });
  const [newPassword, setNewPassword] = useState("");
  const [changePassword, setChangePassword] = useState(false);
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);

  useEffect(() => {
    fetch(`/api/v1/players/user/${user.id}`, {
      headers: { Authorization: `Bearer ${jwt}` },
    })
      .then((res) => {
        if (!res.ok) throw new Error("No es jugador");
        return res.json();
      })
      .then((data) => setPlayer(data))
      .catch(() => {
        fetch(`/api/v1/users/${user.id}`, {
          headers: { Authorization: `Bearer ${jwt}` },
        })
          .then((res) => res.json())
          .then((data) => setUserData(data));
      });
  }, []);

  const handlePlayerChange = (e) => {
    const { name, value } = e.target;

    if (name === "username") {
      setPlayer((prev) => ({
        ...prev,
        user: {
          ...prev.user,
          username: value,
        },
      }));
    } else {
      setPlayer((prev) => ({
        ...prev,
        [name]: value,
      }));
    }
  };

  const handleUserChange = (e) => {
    const { name, value } = e.target;
    setUserData((prev) => ({ ...prev, [name]: value }));
  };

  const logoutAndRedirect = () => {
    alert("Perfil actualizado correctamente. Para validar sus nuevas credenciales vuelva a iniciar sesión.");
    tokenService.removeUser();
    navigate("/login");
  };

  const handlePlayerSubmit = (e) => {
    e.preventDefault();

    const updatedPlayer = {
      ...player,
      user: {
        id: player.user?.id || user.id,
        username: player.user?.username || user.username,
      },
    };

    if (changePassword && newPassword.trim() !== "") {
      updatedPlayer.user.password = newPassword;
    }

    fetch(`/api/v1/players/${player.id}`, {
      method: "PUT",
      headers: {
        Authorization: `Bearer ${jwt}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify(updatedPlayer),
    })
      .then(async (res) => {
        if (res.ok) {
          logoutAndRedirect();
        } else {
          const data = await res.json();
          if (data.message?.toLowerCase().includes("username")) {
            alert("Este nombre de usuario ya está en uso.");
          } else {
            alert(data.message || "Error al actualizar el perfil del jugador");
          }
        }
      })
      .catch((err) => {
        setMessage(err.message);
        setVisible(true);
      });
  };

  const handleUserSubmit = (e) => {
    e.preventDefault();


    const updatedUser = {
      ...userData,
      id: user.id,
    };

    if (changePassword && newPassword.trim() !== "") {
      updatedUser.password = newPassword;
    } else {
      delete updatedUser.password;
    }

    fetch(`/api/v1/users/${user.id}`, {
      method: "PUT",
      headers: {
        Authorization: `Bearer ${jwt}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify(updatedUser),
    })
      .then((res) => {
        if (res.ok) {
          logoutAndRedirect();
        } else {
          return res.text().then((text) => {
            throw new Error(text || "Error al actualizar el usuario");
          });
        }
      })
      .catch((err) => {
        setMessage(err.message);
        setVisible(true);
      });
  };

  const modal = getErrorModal(setVisible, visible, message);

  return (
    <div className="perfil-page-container">
      <h1>Editar Perfil</h1>
      {modal}

      {player ? (
        <Form onSubmit={handlePlayerSubmit}>
          <div className="hero-div">
            <h2>Username:</h2>
            <Input
              type="text"
              name="username"
              value={player.user?.username || ""}
              onChange={handlePlayerChange}
              required
            />

            <h2>Nombre:</h2>
            <Input
              type="text"
              name="firstName"
              value={player.firstName || ""}
              onChange={handlePlayerChange}
              required
            />

            <h2>Apellido:</h2>
            <Input
              type="text"
              name="lastName"
              value={player.lastName || ""}
              onChange={handlePlayerChange}
              required
            />

            <h2>Email:</h2>
            <Input
              type="email"
              name="email"
              value={player.email || ""}
              onChange={handlePlayerChange}
              required
            />

            <Button
              color="secondary"
              type="button"
              className="mt-3 mb-2"
              onClick={() => setChangePassword(!changePassword)}
            >
              {changePassword
                ? "Cancelar cambio de contraseña"
                : "Cambiar contraseña"}
            </Button>

            {changePassword && (
              <>
                <h2>Nueva contraseña:</h2>
                <Input
                  type="password"
                  name="password"
                  value={newPassword}
                  onChange={(e) => setNewPassword(e.target.value)}
                />
                {user.id && (
                  <small className="text-muted">
                    Deja en blanco para mantener la contraseña actual
                  </small>
                )}
              </>
            )}

            <Button color="primary" type="submit" className="mt-3">
              Guardar Cambios
            </Button>
          </div>
        </Form>
      ) : (
        <Form onSubmit={handleUserSubmit}>
          <div className="hero-div">
            <h2>Username:</h2>
            <Input
              type="text"
              name="username"
              value={userData.username || ""}
              onChange={handleUserChange}
              required
            />

            <Button
              color="secondary"
              type="button"
              className="mt-3 mb-2"
              onClick={() => setChangePassword(!changePassword)}
            >
              {changePassword
                ? "Cancelar cambio de contraseña"
                : "Cambiar contraseña"}
            </Button>

            {changePassword && (
              <>
                <h2>Nueva contraseña:</h2>
                <Input
                  type="password"
                  name="password"
                  value={newPassword}
                  onChange={(e) => setNewPassword(e.target.value)}
                />
              </>
            )}

            <Button color="primary" type="submit" className="mt-3">
              Guardar Cambios
            </Button>
          </div>
        </Form>
      )}
    </div>
  );
}
