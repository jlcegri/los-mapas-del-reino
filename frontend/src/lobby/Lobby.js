import '../static/css/lobby/lobby.css';
import tokenService from '../services/token.service';
import React, { useEffect, useState } from 'react';
import getIdFromUrl from "../util/getIdFromUrl";
import { useNavigate } from 'react-router-dom';
import Modal from './LobbyModal';

export default function Lobby() {
    const user = tokenService.getUser();
    const navigation = useNavigate();
    const welcome = `¡Bienvenido a la sala, ${user.username}!`;
    const jwt = tokenService.getLocalAccessToken();
    const matchId = getIdFromUrl(2);
    const [players, setPlayers] = useState([]);
    const [match, setMatch] = useState(null);
    const [playerId, setPlayerId] = useState(null);
    const [creatorId, setCreatorId] = useState(null);
    const [creatorCondition, setCreatorCondition] = useState(false);
    const [isLeaveModalOpen, setIsLeaveModalOpen] = useState(false);
    const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
    const [selectedMatch, setSelectedMatch] = useState(null);

    useEffect(() => {
        const interval = setInterval(fetchMatch, 1000);
        fetchMatch();

        return () => clearInterval(interval);
    }, [matchId]);

    useEffect(() => {
        async function fetchPlayer() {
            const playerResponse = await fetch(`/api/v1/players/user/${user.id}`, {
                headers: {
                    Authorization: `Bearer ${jwt}`,
                    "Content-Type": "application/json"
                },
            });
            const playerData = await playerResponse.json();
            if (playerData) {
                setPlayerId(playerData.id);
            }
        }
        fetchPlayer();
    }, []);

    async function fetchMatch() {
        try {
            const matchResponse = await fetch(`/api/v1/matches/${matchId}`, {
                headers: {
                    Authorization: `Bearer ${jwt}`,
                    "Content-Type": "application/json"
                },
            });

            if (!matchResponse.ok) {
                if (matchResponse.status === 404) {
                    navigation(`/`, { state: { matchDeleted: true } });
                    return;
                } else {
                    throw new Error(`Error en la API: ${matchResponse.status}`);
                }
            }
            
            const matchData = await matchResponse.json();
            if (matchData) {
                setMatch(matchData);

                if (Array.isArray(matchData.players)) {
                    setPlayers(matchData.players);
                    if (matchData.players.length === 0) {
                        await deleteMatch(matchId);
                    }
                    if (matchData.state === 'STARTED') {
                        navigation(`/matches/${matchId}`); 
                    }
                } else {
                    setPlayers([]);
                }
                setCreatorId(matchData.creatorId);
            }
        } catch (error) {
            console.error("Error fetching match:", error);
            navigation(`/`, { state: { matchDeleted: true } });
        }
    }


    useEffect(() => {
        if (match && match.creator.id === playerId) {
            setCreatorCondition(true);

        }
    }, [match, playerId]);

    async function startMatch() {
        try {
            const startMatchResponse = await fetch(`/api/v1/matches/start/${matchId}`, {
                method: 'PUT',
                headers: {
                    Authorization: `Bearer ${jwt}`,
                    "Content-Type": "application/json"
                },
            });
    
            const startMatchData = await startMatchResponse.json();
    
            if (startMatchData && startMatchData.state === "STARTED") {
                setMatch(startMatchData);
    
                startMatchData.players.forEach(player => {
                    if (player.id !== creatorId) {
                        navigation(`/matches/${matchId}`);
                    }
                });
    
                navigation(`/matches/${matchId}`);
            }
        } catch (error) {
            console.error("Error starting match:", error);
        }
    }
    
    

    async function leaveMatch(matchId) {
        try {
            const leaveMatchResponse = await fetch(`/api/v1/matches/leave/${matchId}`, {
                method: 'PUT',
                headers: {
                    Authorization: `Bearer ${jwt}`,
                    "Content-Type": "application/json"
                },
            });
            const leaveMatchData = await leaveMatchResponse.json();
            if (leaveMatchData) {
                navigation(`/matchList`);
            }
        } catch (error) {
            console.error("Error leaving match:", error);
        }
    }

    async function deleteMatch(matchId) {
        try {
            const deleteMatchResponse = await fetch(`/api/v1/matches/${matchId}`, {
                method: 'DELETE',
                headers: {
                    Authorization: `Bearer ${jwt}`,
                    "Content-Type": "application/json"
                },
            });
            if (deleteMatchResponse.ok) {
                navigation(`/`);
            }
        } catch (error) {
            console.error("Error deleting match:", error);
        }
    }

    return (
        <div className="lobby-page-container">
            <div className="lobby-div">
                <h1>{welcome}</h1>
                <div className="players-list">
                    <h3>Jugadores en la sala:</h3>
                    <ul>
                        {players.length === 0 ? (
                            <li>No hay jugadores en la sala</li>
                        ) : (
                            players.map((player, index) => (
                                <li key={index}>
                                    {`${player.user.username} ${player.id === creatorId ? ' ✦' : ''}`}
                                </li>
                            ))
                        )}
                    </ul>
                </div>
                <div className="buttons-container">
                    {creatorCondition && (
                        <button className="btn-iniciar" onClick={() => deleteMatch(matchId)}>Eliminar partida</button>
                    )}
                    {creatorCondition && players.length > 1 && (
                        <button className="btn-iniciar" onClick={() => startMatch(matchId)}>Comenzar partida</button>
                    )}
                    <button className="btn-salir" onClick={() => leaveMatch(matchId)}>Abandonar partida</button>
                </div>
            </div>
            <Modal
                isModalOpen={isLeaveModalOpen}
                toggleModal={() => setIsLeaveModalOpen(!isLeaveModalOpen)}
                onConfirm={() => leaveMatch(selectedMatch)}
            />
            <Modal
                isModalOpen={isDeleteModalOpen}
                toggleModal={() => setIsDeleteModalOpen(!isDeleteModalOpen)}
                onConfirm={() => deleteMatch(selectedMatch)}
            />
        </div>
    );
}
