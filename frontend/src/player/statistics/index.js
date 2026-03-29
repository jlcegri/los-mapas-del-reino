
import "../../static/css/player/statistics.css";

import { useEffect, useState } from "react";
import tokenService from '../../services/token.service';

export default function Statistics() {

    const [matches, setMatches] = useState([]);
    const [userMatches, setUserMatches] = useState([]);
    const [globalStats, setGlobalStats] = useState({});
    const [userStats, setUserStats] = useState({});
    const [ranking, setRanking] = useState([]);
    const user = tokenService.getUser();

    useEffect(() => {
        async function fetchMatches() {
            const jwt = tokenService.getLocalAccessToken();
            const res = await fetch(`/api/v1/matches/finished`, {

                headers: {
                    Authorization: `Bearer ${jwt}`,
                    "Content-Type": "application/json"
                },
            });

            const data = await res.json();
            setMatches(data);
            const filtered = data.filter(m => m.players.some(p => p.user.id === user.id));
            setUserMatches(filtered);
        }
        fetchMatches();
    }, [user.id]);

    useEffect(() => {
        const computeStats = (matchList, isUserStats = false) => {
            let winCount = 0;
            let durations = [];
            let playersPerMatch = [];
            let playerWinMap = {};

            matchList.forEach(match => {
                if (match.startDate && match.finishDate) {
                    const start = new Date(match.startDate);
                    const end = new Date(match.finishDate);
                    const duration = (end - start) / 1000;
                    durations.push(duration);
                }

                if (match.players) {
                    playersPerMatch.push(match.players.length);
                }

                if (match.winners) {
                    match.winners.forEach(winner => {
                        const id = winner.user.id;
                        const username = winner.user.username;
                        if (!playerWinMap[id]) {
                            playerWinMap[id] = { wins: 0, username };
                        }
                        playerWinMap[id].wins++;
                        if (id === user.id) winCount++;
                    });
                }

            });

            const totalDuration = durations.reduce((a, b) => a + b, 0);
            const durationStats = {
                total: totalDuration,
                average: durations.length ? totalDuration / durations.length : 0,
                max: durations.length ? Math.max(...durations) : 0,
                min: durations.length ? Math.min(...durations) : 0
            };

            const totalPlayers = playersPerMatch.reduce((a, b) => a + b, 0);
            const playerCountStats = {
                average: playersPerMatch.length ? totalPlayers / playersPerMatch.length : 0,
                max: playersPerMatch.length ? Math.max(...playersPerMatch) : 0,
                min: playersPerMatch.length ? Math.min(...playersPerMatch) : 0
            };

            if (isUserStats) {
                setUserStats({
                    totalMatches: matchList.length,
                    winCount,
                    durationStats,
                    playerCountStats
                });
            } else {
                const sortedRanking = Object.entries(playerWinMap)
                    .sort((a, b) => b[1] - a[1])
                    .map(([id, wins]) => ({ id, wins }));
                setGlobalStats({
                    totalMatches: matchList.length,
                    durationStats,
                    playerCountStats
                });
                setRanking(sortedRanking);
            }

            if (!isUserStats) {
                const sortedRanking = Object.entries(playerWinMap)
                    .sort((a, b) => b[1].wins - a[1].wins)
                    .map(([id, data]) => ({
                        id,
                        username: data.username,
                        wins: data.wins
                    }));
                setGlobalStats({
                    totalMatches: matchList.length,
                    durationStats,
                    playerCountStats
                });
                setRanking(sortedRanking);
            }
        };

        if (matches.length) computeStats(matches);
        if (userMatches.length) computeStats(userMatches, true);

    }, [matches, userMatches, user.id]);

    const formatDuration = (value) => value?.toFixed(1);

    return (
        <div className="statistics">
        <div className="body">
            <div className="estadisticas">
                <h2>📊 Estadísticas Globales</h2>
                <p>Total de partidas jugadas: {globalStats.totalMatches}</p>

                <h3>Duración de partidas (segundos)</h3>
                <p>Total: {formatDuration(globalStats.durationStats?.total)}</p>
                <p>Media: {formatDuration(globalStats.durationStats?.average)}</p>
                <p>Máxima: {formatDuration(globalStats.durationStats?.max)}</p>
                <p>Mínima: {formatDuration(globalStats.durationStats?.min)}</p>

                <h3>Jugadores por partida</h3>
                <p>Media: {formatDuration(globalStats.playerCountStats?.average)}</p>
                <p>Máximo: {globalStats.playerCountStats?.max}</p>
                <p>Mínimo: {globalStats.playerCountStats?.min}</p>
            </div>

            <div className="estadisticas personales">
                <h2>👤 Tus Estadísticas</h2>
                <p>Partidas jugadas: {userStats.totalMatches}</p>
                <p>Partidas ganadas: {userStats.winCount}</p>

                <h3>Duración de tus partidas (segundos)</h3>
                <p>Total: {formatDuration(userStats.durationStats?.total)}</p>
                <p>Media: {formatDuration(userStats.durationStats?.average)}</p>
                <p>Máxima: {formatDuration(userStats.durationStats?.max)}</p>
                <p>Mínima: {formatDuration(userStats.durationStats?.min)}</p>

                <h3>Jugadores en tus partidas</h3>
                <p>Media: {formatDuration(userStats.playerCountStats?.average)}</p>
                <p>Máximo: {userStats.playerCountStats?.max}</p>
                <p>Mínimo: {userStats.playerCountStats?.min}</p>
            </div>

            <div className="ranking">
                <h2>🏆 Ranking Global</h2>
                <ol>
                    {ranking.map((entry, index) => (
                        <li key={index}>
                            {entry.username} — {entry.wins} victorias
                            {entry.id === user.id && " (Tú)"}
                        </li>
                    ))}
                </ol>
            </div>

            <div className="logros">
                <h2>🎖️ Logros Personales</h2>
                <ul>
                    <li>{userStats.winCount >= 10 ? "✅" : "❌"} 10 partidas ganadas</li>
                    <li>{userStats.winCount >= 20 ? "✅" : "❌"} 20 partidas ganadas</li>
                    <li>{userStats.winCount >= 50 ? "✅" : "❌"} 50 partidas ganadas</li>
                </ul>
            </div>
        </div>
        </div>
    );
}

