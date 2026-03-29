import React, { useEffect, useState } from 'react';
import dice_rolling from '../../static/images/components/criterios/dice_rolling.gif';
import dice1 from '../../static/images/components/criterios/dice1.png';
import dice2 from '../../static/images/components/criterios/dice2.png';
import dice3 from '../../static/images/components/criterios/dice3.png';
import dice4 from '../../static/images/components/criterios/dice4.png';
import dice5 from '../../static/images/components/criterios/dice5.png';
import dice6 from '../../static/images/components/criterios/dice6.png';
import 'frontend/src/static/css/components/criterios/criterios.css';
import { useNavigate, Link } from 'react-router-dom';
import tokenService from '../../services/token.service';
import { useParams } from 'react-router-dom';

export default function Criterios() {
    const { matchId } = useParams();
    const jwt = tokenService.getLocalAccessToken();
    const navigate = useNavigate();
    const user = tokenService.getUser();
    const [dadoA1, setDadoA1] = useState(null);
    const [dadoA2, setDadoA2] = useState(null);
    const [dadoB1, setDadoB1] = useState(null);
    const [dadoB2, setDadoB2] = useState(null);
    const [isRollingA1, setIsRollingA1] = useState(false);
    const [isRollingA2, setIsRollingA2] = useState(false);
    const [isRollingB1, setIsRollingB1] = useState(false);
    const [isRollingB2, setIsRollingB2] = useState(false);
    const [hasRolledA1, setHasRolledA1] = useState(false);
    const [hasRolledA2, setHasRolledA2] = useState(false);
    const [hasRolledB1, setHasRolledB1] = useState(false);
    const [hasRolledB2, setHasRolledB2] = useState(false);
    const [playerId, setPlayerId] = useState(null);
    const [isCreator, setIsCreator] = useState(false);
    const [isStarted, setIsStarted] = useState(false);

    useEffect(() => {
        const fetchMatchData = async () => {
            try {
                const response = await fetch(`/api/v1/matches/${matchId}`, {
                    method: 'GET',
                    headers: {
                        Authorization: `Bearer ${jwt}`,
                        "Content-Type": "application/json"
                    },
                });

                if (!response.ok) {
                    // Leer el mensaje de error como texto
                    const errorMessage = await response.text();
                    throw new Error(errorMessage); // Lanzar error para catch
                }

                const data = await response.json();
                console.log('Datos de la partida:', data);
                setIsCreator(data.creator.id === playerId);
                if (data.criteriaA1 !== null && data.criteriaB1 !== null && data.criteriaA2 !== null && data.criteriaB2 !== null) {
                    setIsStarted(true);
                }
            } catch (error) {
                console.error('Error al obtener los datos de la partida:', error.message);
                alert('Error: ' + error.message);
            }
        };
    
        if (matchId && playerId) {
            // Llamada inicial para obtener los datos de la partida
            fetchMatchData();
    
            // Configurar polling para verificar periódicamente el estado
            const intervalId = setInterval(() => {
                fetchMatchData();
            }, 1000); // Intervalo de 1 segundo
    
            // Limpiar el intervalo al desmontar el componente
            return () => clearInterval(intervalId);
        }
    }, [matchId, playerId, jwt]);
    

    const Criteria = {
        UNO: 'UNO',
        DOS: 'DOS',
        TRES: 'TRES',
        CUATRO: 'CUATRO',
        CINCO: 'CINCO',
        SEIS: 'SEIS'
    };
    
    const getCriteriaFromDice = (diceValue) => {
        switch (diceValue) {
            case 1: return Criteria.UNO;
            case 2: return Criteria.DOS;
            case 3: return Criteria.TRES;
            case 4: return Criteria.CUATRO;
            case 5: return Criteria.CINCO;
            case 6: return Criteria.SEIS;
            default: return null;
        }
    };

    useEffect(() => {
        async function checkBoard() {
            try {
                const response = await fetch(`/api/v1/boards/${matchId}/${playerId}`, {
                    method: 'GET',
                    headers: {
                        Authorization: `Bearer ${jwt}`,
                        "Content-Type": "application/json"
                    },
                });

                if (!response.ok) {
                    const errorMessage = await response.text();
                    throw new Error(errorMessage);
                }

                const boardData = await response.json();

                if (boardData && boardData.length > 0) {
                    navigate(`/matches/${matchId}/${playerId}`);
                }
            } catch (error) {
                console.error('Error al verificar el tablero:', error);
            }
        }

        if (playerId && matchId) {
            checkBoard();
        }
    }, [matchId, playerId, jwt, navigate]);

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

    const lanzarDadoA1 = () => {
        if (hasRolledA1) return;
        setIsRollingA1(true);
        setTimeout(() => {
            setDadoA1(Math.floor(Math.random() * 6) + 1);
            setIsRollingA1(false);
            setHasRolledA1(true);
        }, 1000);
    };

    const lanzarDadoA2 = () => {
        if (hasRolledA2) return;
        if (!hasRolledA1) return alert('Primero debes lanzar el dado A1');
        setIsRollingA2(true);
        setTimeout(() => {
            const newDadoA2 = Math.floor(Math.random() * 6) + 1;
            if (newDadoA2 === dadoA1) {
                setIsRollingA2(false);
                alert('El segundo dado no puede ser igual al primero. Por favor, vuelva a lanzar');
                return;
            }
            setDadoA2(newDadoA2);
            setIsRollingA2(false);
            setHasRolledA2(true);
        }, 1000);
    };

    const lanzarDadoB1 = () => {
        if (hasRolledB1) return;
        setIsRollingB1(true);
        setTimeout(() => {
            setDadoB1(Math.floor(Math.random() * 6) + 1);
            setIsRollingB1(false);
            setHasRolledB1(true);
        }, 1000);
    };

    const lanzarDadoB2 = () => {
        if (hasRolledB2) return;
        if (!hasRolledB1) return alert('Primero debes lanzar el dado B1');
        setIsRollingB2(true);
        setTimeout(() => {
            const newDadoB2 = Math.floor(Math.random() * 6) + 1;
            if (newDadoB2 === dadoB1) {
                setIsRollingB2(false);
                alert('El segundo dado no puede ser igual al primero. Por favor, vuelva a lanzar');
                return;
            }
            setDadoB2(newDadoB2);
            setIsRollingB2(false);
            setHasRolledB2(true);
        }, 1000);
    };

    const crearTablero = async () => {
        const criteriaA1 = getCriteriaFromDice(dadoA1);
        const criteriaA2 = getCriteriaFromDice(dadoA2);
        const criteriaB1 = getCriteriaFromDice(dadoB1);
        const criteriaB2 = getCriteriaFromDice(dadoB2);
    
        const boardRequest = {
            matchId: matchId,
            playerId: playerId,
            criteriaA1: criteriaA1,
            criteriaA2: criteriaA2,
            criteriaB1: criteriaB1,
            criteriaB2: criteriaB2,
        };
    
        try {
            const response = await fetch(`/api/v1/boards`, {
                method: 'POST',
                headers: {
                    Authorization: `Bearer ${jwt}`,
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(boardRequest),
            });
    
            if (response.status === 409) {
                alert('Ya tienes un tablero para esta partida.');
                const existingBoard = await response.json();
                console.log('Tablero existente:', existingBoard);
                navigate(`/matches/${matchId}/${playerId}`);
            } else if (response.status === 201) {
                const boardData = await response.json();
                console.log('Tablero creado:', boardData);
                navigate(`/matches/${matchId}/${playerId}`);
            }
        } catch (error) {
            console.error('Error al crear el tablero:', error);
        }
    };
    
    
        

    return (
        <div className='home-page-container'>
            {isCreator ? (
                <>
                    <p>¡Comienza la partida!</p>
                    <p>El anfitrión debe lanzar los dados para elegir los criterios</p>
                    <div className='criterios'>
                        <div className='elements-container'>
                            <p className='texto'>Criterio A</p>
                            <div className='imagenes'>
                                {isRollingA1 ? (
                                    <img src={dice_rolling} alt="Rolling" className='elemento'/>
                                ) : (
                                    <>
                                        {(dadoA1 === null || dadoA1 === 1) && (
                                            <img src={dice1} alt="Dado A1" onClick={lanzarDadoA1} className='elemento' />
                                        )}
                                        {dadoA1 === 2 && (
                                            <img src={dice2} alt="Dado A1" onClick={lanzarDadoA1} className='elemento' />
                                        )}
                                        {dadoA1 === 3 && (
                                            <img src={dice3} alt="Dado A1" onClick={lanzarDadoA1} className='elemento'/>
                                        )}
                                        {dadoA1 === 4 && (
                                            <img src={dice4} alt="Dado A1" onClick={lanzarDadoA1} className='elemento'/>
                                        )}
                                        {dadoA1 === 5 && (
                                            <img src={dice5} alt="Dado A1" onClick={lanzarDadoA1} className='elemento'/>
                                        )}
                                        {dadoA1 === 6 && (
                                            <img src={dice6} alt="Dado A1" onClick={lanzarDadoA1} className='elemento'/>
                                        )}
                                    </>
                                )}
                                {isRollingA2 ? (
                                    <img src={dice_rolling} alt="Rolling" className='elemento'/>
                                ) : (
                                    <>
                                        {(dadoA2 === null || dadoA2 === 1) && (
                                            <img src={dice1} alt="Dado A2" onClick={lanzarDadoA2} className='elemento'/>
                                        )}
                                        {dadoA2 === 2 && (
                                            <img src={dice2} alt="Dado A2" onClick={lanzarDadoA2} className='elemento'/>
                                        )}
                                        {dadoA2 === 3 && (
                                            <img src={dice3} alt="Dado A2" onClick={lanzarDadoA2} className='elemento'/>
                                        )}
                                        {dadoA2 === 4 && (
                                            <img src={dice4} alt="Dado A2" onClick={lanzarDadoA2} className='elemento'/>
                                        )}
                                        {dadoA2 === 5 && (
                                            <img src={dice5} alt="Dado A2" onClick={lanzarDadoA2} className='elemento'/>
                                        )}
                                        {dadoA2 === 6 && (
                                            <img src={dice6} alt="Dado A2" onClick={lanzarDadoA2} className='elemento'/>
                                        )}
                                    </>
                                )}
                            </div>
                        </div>
                        <div className='elements-container'>
                            <p className='texto'>Criterio B</p>
                            <div className='imagenes'>
                                {isRollingB1 ? (
                                    <img src={dice_rolling} alt="Rolling" className='elemento'/>
                                ) : (
                                    <>
                                        {(dadoB1 === null || dadoB1 === 1) && (
                                            <img src={dice1} alt="Dado B1" onClick={lanzarDadoB1} className='elemento'/>
                                        )}
                                        {dadoB1 === 2 && (
                                            <img src={dice2} alt="Dado B1" onClick={lanzarDadoB1} className='elemento'/>
                                        )}
                                        {dadoB1 === 3 && (
                                            <img src={dice3} alt="Dado B1" onClick={lanzarDadoB1} className='elemento'/>
                                        )}
                                        {dadoB1 === 4 && (
                                            <img src={dice4} alt="Dado B1" onClick={lanzarDadoB1} className='elemento'/>
                                        )}
                                        {dadoB1 === 5 && (
                                            <img src={dice5} alt="Dado B1" onClick={lanzarDadoB1} className='elemento'/>
                                        )}
                                        {dadoB1 === 6 && (
                                            <img src={dice6} alt="Dado B1" onClick={lanzarDadoB1} className='elemento'/>
                                        )}
                                    </>
                                )}
                                {isRollingB2 ? (
                                    <img src={dice_rolling} alt="Rolling" className='elemento'/>
                                ) : (
                                    <>
                                        {(dadoB2 === null || dadoB2 === 1) && (
                                            <img src={dice1} alt="Dado B2" onClick={lanzarDadoB2} className='elemento'/>
                                        )}
                                        {dadoB2 === 2 && (
                                            <img src={dice2} alt="Dado B2" onClick={lanzarDadoB2} className='elemento'/>
                                        )}
                                        {dadoB2 === 3 && (
                                            <img src={dice3} alt="Dado B2" onClick={lanzarDadoB2} className='elemento'/>
                                        )}
                                        {dadoB2 === 4 && (
                                            <img src={dice4} alt="Dado B2" onClick={lanzarDadoB2} className='elemento'/>
                                        )}
                                        {dadoB2 === 5 && (
                                            <img src={dice5} alt="Dado B2" onClick={lanzarDadoB2} className='elemento'/>
                                        )}
                                        {dadoB2 === 6 && (
                                            <img src={dice6} alt="Dado B2" onClick={lanzarDadoB2} className='elemento'/>
                                        )}
                                    </>
                                )}
                            </div>
                        </div>
                    </div>
                    <button onClick={crearTablero} className="btn-crear">
                        Crear Tablero
                    </button>
                </>
            ) : (
                <>
                    {isStarted ? (
                        <button onClick={crearTablero} className="btn-crear">
                        Crear Tablero
                    </button>
                    ) : (
                        <p>Esperando a que el anfitrión elija los criterios...</p>
                    )}
                </>
            )}
        </div>
    );
    
}