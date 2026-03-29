import React, { useState, useEffect } from 'react';
import '../../App.css';
import '../../static/css/matchList/matchList.css';
import tokenService from '../../services/token.service';
import { Link } from 'react-router-dom'; 
import { useNavigate } from 'react-router-dom';

export default function MatchList() {

    const [matches, setMatches] = useState([]);
    const navigation = useNavigate();

    useEffect(() => {
        const fetchMatches = async () => {
            try {
                const jwt = tokenService.getLocalAccessToken();
                const matchesResponse = await fetch(`/api/v1/matches/created`, {
                    headers: {
                        Authorization: `Bearer ${jwt}`,
                        "Content-Type": "application/json"
                    },
                });
                const matchesData = await matchesResponse.json();
                if (matchesData) {
                    const validMatches = matchesData.filter(match => match.players && match.players.length > 0);
                    setMatches(validMatches);
                }
            } catch (error) {
                console.error("Error fetching matches:", error);
            }
        };

        fetchMatches();

        const interval = setInterval(fetchMatches, 5000); 

        return () => clearInterval(interval); 
    }, []); 

    async function joinMatch(matchId) {
        try {
            const jwt = tokenService.getLocalAccessToken();
            const joinMatchResponse = await fetch(`/api/v1/matches/join/${matchId}`, {
                method: 'PUT',
                headers: {
                    Authorization: `Bearer ${jwt}`,
                    "Content-Type": "application/json"
                },
            });
            const joinMatchData = await joinMatchResponse.json();
            if (joinMatchData) {
                navigation(`/lobbies/${matchId}`);
            }
        } catch (error) {
            console.error("Error joining match:", error);
        }
    }

    return (
        <div className="matchList-page-container">
    <h1 style={{ fontSize: '28px', textAlign: 'center', marginBottom: '20px' }}>Partidas disponibles</h1>
    {matches.length === 0 && (
        <h2 style={{ fontSize: '22px', textAlign: 'center' }}>No hay partidas disponibles</h2>
    )}
    {matches.length > 0 && (
        <div className="hero-div" style={{ width: '100%', padding: '0', margin: '0' }}>
            <div className="scrollable-div" style={{ overflowX: 'auto', padding: '10px' }}>
                <table style={{
                    width: '100%',
                    margin: '0 auto',
                    borderCollapse: 'collapse',
                    fontSize: '20px', // Tamaño de letra más grande
                }}>
                    <thead>
                        <tr>
                            <th style={{
                                padding: '15px',
                                fontSize: '22px', // Aumentar tamaño de letra en encabezados
                                textAlign: 'center',
                                borderBottom: '2px solid gray'
                            }}>
                                Jugadores
                            </th>
                            <th style={{
                                padding: '15px',
                                fontSize: '22px',
                                textAlign: 'center',
                                borderBottom: '2px solid gray'
                            }}>
                                Creador
                            </th>
                            <th style={{
                                padding: '15px',
                                fontSize: '22px',
                                textAlign: 'center',
                                borderBottom: '2px solid gray'
                            }}>
                                Restantes
                            </th>
                            <th style={{
                                padding: '15px',
                                fontSize: '22px',
                                textAlign: 'center',
                                borderBottom: '2px solid gray'
                            }}>
                                Velocidad
                            </th>
                            <th style={{
                                padding: '15px',
                                fontSize: '22px',
                                textAlign: 'center',
                                borderBottom: '2px solid gray'
                            }}>
                                Acción
                            </th>
                        </tr>
                    </thead>
                    <tbody>
                        {Array.isArray(matches) &&
                            matches.map((match, index) => (
                                <tr key={index} style={{
                                    borderBottom: '1px solid #ddd',
                                }}>
                                    <td style={{
                                        padding: '15px',
                                        wordWrap: 'break-word',
                                    }}>
                                        {Array.isArray(match.players) &&
                                            match.players.map((player, playerIndex) => (
                                                `${player.user.username}${playerIndex < match.players.length - 1 ? ', ' : ''}`
                                            ))}
                                    </td>
                                    <td style={{
                                        padding: '15px',
                                    }}>
                                        {match.creator.user.username}
                                    </td>
                                    <td style={{
                                        padding: '15px',
                                    }}>
                                        {4 - match.players.length}
                                    </td>
                                    <td style={{
                                        padding: '15px',
                                    }}>
                                        {match.mode}
                                    </td>
                                    <td style={{
                                        padding: '15px',
                                    }}>
                                        <button
                                            className="start-match-button"
                                            onClick={() => joinMatch(match.id)}
                                            style={{
                                                padding: '10px 20px',
                                                fontSize: '18px',
                                                cursor: 'pointer',
                                            }}
                                        >
                                            Unirse a Partida
                                        </button>
                                    </td>
                                </tr>
                            ))}
                    </tbody>
                </table>
            </div>
        </div>
    )}
    <Link to="/">
        <button className="return-button" style={{
            marginTop: '20px',
            fontSize: '18px',
            padding: '10px 20px',
        }}>
            Volver
        </button>
    </Link>
</div>

    );
}
