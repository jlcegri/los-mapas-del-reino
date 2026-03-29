import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import tokenService from '../../../services/token.service';
import '../../../App.css';
import '../../../static/css/matchList/matchList.css';

export default function StartedMatchesList() {
    const [matches, setMatches] = useState([]);

    useEffect(() => {
        async function fetchMatches() {
            const jwt = tokenService.getLocalAccessToken();
            const matchesStartedResponse = await fetch(`/api/v1/matches/started`, {
                headers: {
                    Authorization: `Bearer ${jwt}`,
                    "Content-Type": "application/json"
                },
            });
            const matchesStartedData = await matchesStartedResponse.json();
            const matchesCreatedResponse = await fetch(`/api/v1/matches/created`, {
                headers: {
                    Authorization: `Bearer ${jwt}`,
                    "Content-Type": "application/json"
                },
            });
            const matchesCreatedData = await matchesCreatedResponse.json();
            if (matchesStartedData&&matchesCreatedData) {
                const combinedMatches = [...matchesStartedData, ...matchesCreatedData];
                setMatches(combinedMatches);
            }
        }
        fetchMatches();
    }, []);

    return (
        <div className="matchList-page-container">
            <h1>Partidas en curso</h1>
            {matches.length === 0 && <h2>No hay partidas en curso</h2>}
            {matches.length > 0 && (
                <div className="hero-div">
                    <div className="scrollable-div">
                        <table className="custom-table">
                            <thead>
                                <tr className="table-header">
                                    <th>Jugadores</th>
                                    <th>Creador</th>
                                    <th>Estado</th>
                                </tr>
                            </thead>
                            <tbody>
                                {Array.isArray(matches) && matches.map((match, index) => (
                                    <React.Fragment key={index}>
                                        <tr className="table-row">
                                            <td className="table-items">
                                                {Array.isArray(match.players) && match.players.map((player, playerIndex) => (
                                                    `${player.user.username} ${playerIndex < match.players.length - 1 ? ', ' : ''}`
                                                ))}
                                            </td>
                                            <td className="table-items">
                                                {match.creator.user.username} 
                                            </td>
                                            <td className="table-items">
                                                {match.state} 
                                            </td>
                                        </tr>
                                    </React.Fragment>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            )}
            <Link to='/'>
                <button className="return-button">Volver</button>
            </Link>
        </div>
    );
}
