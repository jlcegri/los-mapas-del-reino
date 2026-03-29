import React, { useState, useEffect } from 'react';
import '../App.css';
import '../static/css/home/home.css';
import logo from '../static/images/LMDR.jpg';
import { useNavigate, Link } from 'react-router-dom';
import tokenService from '../services/token.service';

export default function Home() {
    const jwt = tokenService.getLocalAccessToken();
    const user = tokenService.getUser();
    const navigate = useNavigate();
    const [isAdmin, setIsAdmin] = useState(false);
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [isInMatch, setIsInMatch] = useState(false);
    const [matchId, setMatchId] = useState(null);
    const [playerId, setPlayerId] = useState(null);
    const [matchStatus, setMatchStatus] = useState(null);
    const [isLoadingMatch, setIsLoadingMatch] = useState(true);

    const checkIfInMatch = async () => {
        if (!jwt || !user || !user.id) {
            console.log("No hay token o usuario no autenticado.");
            return;
        }
        setIsLoadingMatch(true);

        const createdMatchesResponse = await fetch(`/api/v1/matches/created`, {
            method: 'GET',
            headers: {
                Authorization: `Bearer ${jwt}`,
                "Content-Type": "application/json",
            },
        });

        if (!createdMatchesResponse.ok) {
            console.error("Error al obtener partidas creadas:", createdMatchesResponse.status);
            localStorage.clear();
            alert("Credenciales caducadas. Por favor, inicie sesión de nuevo.");
            window.location.reload()
            return;
        }

        const createdMatches = await createdMatchesResponse.json();

        const startedMatchesResponse = await fetch(`/api/v1/matches/started`, {
            method: 'GET',
            headers: {
                Authorization: `Bearer ${jwt}`,
                "Content-Type": "application/json",
            },
        });

        if (!startedMatchesResponse.ok) {
            console.error("Error al obtener partidas iniciadas:", startedMatchesResponse.status);
            localStorage.clear();
            alert("Credenciales caducadas. Por favor, inicie sesión de nuevo.");
            window.location.reload();
            return;
        }

        const startedMatches = await startedMatchesResponse.json();

        const allMatches = [...createdMatches, ...startedMatches];

        const activeMatch = allMatches.find((match) =>
            match.players.some((player) => player.user.id === user.id)
        );
        if (activeMatch) {
            setIsInMatch(true);
            setMatchId(activeMatch.id);
            setPlayerId(activeMatch.players.find((player) => player.user.id === user.id).id);
            setMatchStatus(activeMatch.state);
            console.log(`Usuario en partida activa con ID: ${activeMatch.id}`);
        } else {
            setIsInMatch(false);
            setMatchStatus(null);
            console.log("Usuario no está en ninguna partida activa.");
        }
        setIsLoadingMatch(false);
    };


    const crearLobby = async (mode) => {
        if (!user.username) {
            alert('Por favor, ingresa tu nombre de usuario.');
            return;
        }
        if (!jwt) {
            alert('No estás autenticado. Por favor, inicia sesión.');
            return;
        }

        const response = await fetch(`/api/v1/matches`, {
            method: 'POST',
            headers: {
                Authorization: `Bearer ${jwt}`,
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                mode: mode
            })
        });


        if (!response.ok) {
            throw new Error('Error al crear la partida');
        }

        const matchData = await response.json();
        const matchId = matchData.id;

        navigate(`/lobbies/${matchId}`);

    };


    useEffect(() => {
        const jwt = JSON.parse(window.localStorage.getItem("jwt"));
        if (jwt) {
            setIsLoggedIn(true);
        }
    }, []);


    const handleUnirmePartida = () => {
        if (!user.username) {
            alert('Por favor, ingresa tu nombre de usuario.');
            return;
        }

        navigate('/matchList');
    };

    const handleReconectar = async () => {
        if (!matchStatus) {
            alert("Aún no se ha cargado el estado de la partida. Intenta de nuevo en unos segundos.");
            console.warn("matchStatus aún no está definido al reconectar.");
            return;
        }

        console.log("Reconectando a la partida..." + matchStatus);
        if (matchStatus === "CREATED") {
            navigate(`/lobbies/${matchId}`);
        } else if (matchStatus === "STARTED") {
            try {
                const response = await fetch(`/api/v1/matches/${matchId}`, {
                    method: 'GET',
                    headers: {
                        Authorization: `Bearer ${jwt}`,
                        "Content-Type": "application/json"
                    },
                });

                if (!response.ok) {
                    if (response.status === 403) {
                        alert("No tienes permisos para acceder a esta partida. Por favor, vuelve a iniciar sesión.");
                        navigate("/");
                        return;
                    }
                    console.error("Error al obtener los datos del match:", response.statusText);
                    return;
                }

                const matchData = await response.json();
                const { criteriaA1, criteriaA2, criteriaB1, criteriaB2 } = matchData;

                if (
                    criteriaA1 == null ||
                    criteriaA2 == null ||
                    criteriaB1 == null ||
                    criteriaB2 == null
                ) {
                    navigate(`/matches/${matchId}`);
                } else {
                    navigate(`/matches/${matchId}/${playerId}`);
                }

            } catch (error) {
                console.error("Error al obtener datos del match:", error);
                alert("Hubo un error al reconectar. Intenta de nuevo.");
            }
        } else {
            alert("Estado de la partida desconocido.");
        }
    };

    useEffect(() => {
        if (isLoggedIn && user?.roles?.[0] === "ADMIN") {
            setIsAdmin(true);
        }
        checkIfInMatch();
    }, [isLoggedIn]);

    return (
        <div className="home-page-container">
            <div className="hero-div">
                <h1>Bienvenido a</h1>
                <img src={logo} alt="Logo del juego" />
                <h3>¡A por la Victoria!</h3>

                {!isLoggedIn && (
                    <p>¡Registrate o inicia sesión para jugar!</p>
                )}

                {isLoggedIn && !isAdmin && (
                    <div className="buttons-container">
                        {isInMatch ? (
                            <div>
                                <p>Ya estás en una partida</p>

                                <button
                                    onClick={handleReconectar}
                                    className="start-match-button"
                                    disabled={isLoadingMatch}
                                >
                                    Reconectar a partida
                                </button>

                            </div>
                        ) : (
                            <>
                                <p style={{ textAlign: "center", fontSize: "20px", marginBottom: "-10px" }}>¡Elige la velocidad de la partida!</p>
                                <div style={{ display: "flex", gap: "6px", justifyContent: "center", marginTop: "0" }}>
                                    <button onClick={() => {crearLobby("SLOW")}} className="btn-crear">
                                        Lento
                                    </button>
                                    <button onClick={() => {crearLobby("STANDARD")}} className="btn-crear">
                                        Estándar
                                    </button>
                                    <button onClick={() => {crearLobby("FAST")}} className="btn-crear">
                                        Rápido
                                    </button>
                                </div>
                                <p style={{ textAlign: "center", fontSize: "20px", marginBottom: "-10px" }}>También puedes unirte a una partida ya creada</p>
                                <div style={{ display: "flex", gap: "6px", justifyContent: "center", marginBottom: "1px" }}>
                                    <button onClick={handleUnirmePartida} className="btn-unirme">
                                        Listado de partidas
                                    </button>
                                </div>
                            </>
                        )}
                    </div>
                )}

                {isAdmin && (
                    <div className='admin-tools'>
                        <p>Admin tools:</p>
                        <Link to="/matches/started">
                            <button className='start-match-button'>Partidas en curso</button>
                        </Link>
                        <Link to="/matches/finished">
                            <button className='start-match-button'>Partidas terminadas</button>
                        </Link>
                    </div>
                )}

            </div>
        </div>
    );
}
