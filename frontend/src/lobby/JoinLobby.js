import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../static/css/lobby/joinLobby.css';
import tokenService from '../services/token.service';

export default function JoinLobbyPage() {
    const [lobbyCode, setLobbyCode] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();
    const user = tokenService.getUser();

    const handleJoinLobby = () => {
        const lobbies = JSON.parse(localStorage.getItem('lobbies')) || {};
        const lobby = lobbies[lobbyCode];

        if (lobby) {
            if (!lobby.players.includes(user.username)) {
                lobby.players.push(user.username);
                localStorage.setItem('lobbies', JSON.stringify(lobbies));
            }
            navigate(`/lobbies/${lobbyCode}`);
        } else {
            alert('Lobby no encontrado.');
        }
    };

    return (
        <div className="join-lobby-page-container">
            <div className="join-lobby-div">
                <h1>Unirse al Lobby</h1>
                <h3>Ingresa el código del lobby:</h3>
                <input
                    type="text"
                    value={lobbyCode}
                    onChange={(e) => setLobbyCode(e.target.value)}
                    placeholder="Código de Lobby"
                />
                {error && <p style={{ color: 'red' }}>{error}</p>}
                <button onClick={handleJoinLobby}>Unirse</button>
            </div>
        </div>
    );
}
