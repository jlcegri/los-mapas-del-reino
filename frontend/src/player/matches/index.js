import React, { useEffect, useState } from 'react';
import tokenService from '../../services/token.service';
import '../../static/css/player/playerMatches.css'
import { Link } from 'react-router-dom';


export default function PlayerMatches() {
  const [player, setPlayer] = useState({});
  const [playerMatches, setPlayerMatches] = useState([]);

  useEffect(() => {
    async function fetchPlayer() {
      const user = tokenService.getUser();
      const jwt = tokenService.getLocalAccessToken();

      const playerResponse = await fetch(`/api/v1/players/user/${user.id}`, {
        headers: {
          Authorization: `Bearer ${jwt}`,
          "Content-Type": "application/json"
        },
      });
      const playerData = await playerResponse.json();
      if (playerData) {
        setPlayer(playerData);
      }
    }

    fetchPlayer();
  }, []);

  useEffect(() => {
    async function fetchMatches() {
      if (player) {
        const jwt = tokenService.getLocalAccessToken();
        const matchesResponse = await fetch(`/api/v1/matches/player/${player.id}`, {
          headers: {
            Authorization: `Bearer ${jwt}`,
            "Content-Type": "application/json"
          },
        });
        const matchesData = await matchesResponse.json();
        if (matchesData) {
          setPlayerMatches(matchesData);
        }
      }
    }

    fetchMatches();
  }, [player]); // This effect depends on the player state

  return (
    <div className="playerMatches-page-container">
      <h1>Mis Partidas</h1>
      {playerMatches.length === 0 && <h2>No tienes partidas todavía</h2>}
      {playerMatches.length > 0 && (
        <div className="hero-div">
          <div className="scrollable-div">
            <table className="custom-table" >
              <thead>
                <tr>
                  <th className='table-header'>
                    <h2>Jugadores</h2>
                  </th>
                  <th className='table-header'>
                    <h2>Inicio</h2>
                  </th>
                  <th className='table-header'>
                    <h2>Final</h2>
                  </th>
                  <th className='table-header'>
                    <h2>Creador</h2>
                  </th>
                  <th className='table-header'>
                    <h2>Ganadores</h2>
                  </th>
                </tr>
              </thead>
              <tbody>
                {Array.isArray(playerMatches) &&
                  playerMatches.map((match, index) => (
                    <tr className='table-header'>
                      <td className='table-items'>
                        {Array.isArray(match.players) &&
                          match.players.map(
                            (player, playerIndex) =>
                              `${player.user.username}${playerIndex < match.players.length - 1
                                ? ", "
                                : ""
                              }`
                          )}
                      </td>
                      <td className='table-items'>
                        {new Date(match.startDate).toLocaleTimeString(
                          "es-Es",
                          {
                            day: "2-digit",
                            month: "2-digit",
                            year: "numeric",
                          }
                        )}
                      </td>
                      <td className='table-items'>
                        {new Date(match.finishDate).toLocaleTimeString(
                          "es-Es",
                          {
                            day: "2-digit",
                            month: "2-digit",
                            year: "numeric",
                          }
                        )}
                      </td>
                      <td className='table-items'>
                        {match.creator.user.username}
                      </td>
                      <td className="table-items">
                        {match.winners.length > 0
                          ? match.winners
                            .map((player) => player.user.username)
                            .join(", ")
                          : "Sin definir"}
                      </td>
                    </tr>
                  ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
      <Link to="/">
        <button className="return-button">Volver</button>
      </Link>
    </div>
  );
}
