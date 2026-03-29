import { useState, useEffect } from "react";
import { Link, useLocation } from "react-router-dom";
import { Button } from "reactstrap";
import tokenService from "../../services/token.service";
import getErrorModal from "../../util/getErrorModal";

export default function ProfileList() {
  const location = useLocation();
  const [player, setPlayer] = useState(location.state?.playerData || null);
  const [userData, setUserData] = useState(null);
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);

  const jwt = tokenService.getLocalAccessToken();
  const user = tokenService.getUser();

  useEffect(() => {
  console.log("Llamando API con JWT:", jwt);

  const fetchData = async () => {
    try {
      const response = await fetch(`/api/v1/players/user/${user.id}`, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${jwt}`,
          "Content-Type": "application/json",
        },
      });

      if (!response.ok) throw new Error(`Error: ${response.status} ${response.statusText}`);

      const data = await response.json();
      setPlayer(data);
    } catch (err) {
      console.error("Error al obtener Player:", err);

      // Intentamos cargar User
      try {
        const res = await fetch(`/api/v1/users/${user.id}`, {
          method: "GET",
          headers: {
            Authorization: `Bearer ${jwt}`,
            "Content-Type": "application/json",
          },
        });

        if (!res.ok) throw new Error(`Error user: ${res.status} ${res.statusText}`);

        const data = await res.json();
        setUserData(data);
      } catch (err2) {
        console.error("Error al obtener User:", err2);
        setMessage(err2.message);
        setVisible(true);
      }
    }
  };

  fetchData();
}, []);


  const modal = getErrorModal(setVisible, visible, message);

  return (
    <div className="perfil-page-container">
      <h1 className="text-center">Perfil</h1>
      {modal}
      <div className="hero-div">
        {player ? (
          <>
            <h2>Nombre:</h2>
            <h3>{player.firstName || ""}</h3>

            <h2>Apellido:</h2>
            <h3>{player.lastName || ""}</h3>

            <h2>Email:</h2>
            <h3>{player.email || ""}</h3>
          </>
        ) : userData ? (
          <>
            <h2>Username:</h2>
            <h3>{userData.username || ""}</h3>
          </>
        ) : (
          <p>Cargando perfil...</p>
        )}
      </div>
      <Button color="success" tag={Link} to="/editProfile">
        Editar Perfil
      </Button>
    </div>
  );
}
