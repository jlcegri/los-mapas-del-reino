import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import tokenService from '../../../services/token.service';
import '../../../App.css';
import '../../../static/css/matchList/matchList.css';

export default function FinishedMatchesList() {
    const [finishedMatches, setFinishedMatches] = useState([]);

    useEffect(() => {
        async function fetchMatches() {
            const jwt = tokenService.getLocalAccessToken();
            const matchesResponse = await fetch(`/api/v1/matches/finished`, {
                headers: {
                    Authorization: `Bearer ${jwt}`,
                    "Content-Type": "application/json"
                },
            });
            const matchesData = await matchesResponse.json();
            if (matchesData) {
                setFinishedMatches(matchesData);
            }
        }
        fetchMatches();
    }, []);

    return (
        <div className="matchList-page-container">
            <h1>Partidas terminadas</h1>
            {finishedMatches.length === 0 && <h2>No se han jugado partidas aún</h2>}
            {finishedMatches.length > 0 && (
                <div className="hero-div">
                    <div className="scrollable-div">
                        <table className="custom-table">
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
                                {Array.isArray(finishedMatches) &&
                                    finishedMatches.map((match, index) => (
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
