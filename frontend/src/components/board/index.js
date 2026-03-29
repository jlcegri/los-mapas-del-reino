import React, { useEffect, useState } from 'react';
import '../../App.css';
import { useParams, useNavigate } from 'react-router-dom';
import { HexGrid, Layout, Hexagon, Text } from "react-hexgrid";
import '../../static/css/board/board.css';
import "../../static/css/board/modal.css";
import InterrogationModal from "./utils/InterrogationModal";
import getErrorModal from "../../util/getErrorModal";
import tokenService from '../../services/token.service';
import '../../static/css/board/board.css';
import dice1 from '../../static/images/components/criterios/dice1.png';
import dice2 from '../../static/images/components/criterios/dice2.png';
import dice3 from '../../static/images/components/criterios/dice3.png';
import dice4 from '../../static/images/components/criterios/dice4.png';
import dice5 from '../../static/images/components/criterios/dice5.png';
import dice6 from '../../static/images/components/criterios/dice6.png';
import dice_rolling from '../../static/images/components/criterios/dice_rolling.gif';

import bosqueImg from '../../static/images/board/forest.png';
import rioImg from '../../static/images/board/river.png';
import puebloImg from '../../static/images/board/village.png';
import montañaImg from '../../static/images/board/mountain.png';
import praderaImg from '../../static/images/board/grassland.png';
import castilloImg from '../../static/images/board/castle.png';

import Modal from "./modal.js";
import ModalPoderInterrogacion from "./modalPoderInterrogacion.js";
import WinnerModal from './modalWinner.js';


const territoryImages = {
    bosque: bosqueImg,
    rio: rioImg,
    pueblo: puebloImg,
    montaña: montañaImg,
    pradera: praderaImg,
    castillo: castilloImg,
};

export default function Tablero() {
    const jwt = tokenService.getLocalAccessToken();
    const { matchId, playerId } = useParams();
    const [tablero, setTablero] = useState(null);
    const [hoveredHex, setHoveredHex] = useState(null);
    const [visible, setVisible] = useState(false);
    const [errorMessage, setErrorMessage] = useState(null);
    const [selectedHex, setSelectedHex] = useState(null);
    const [selectedTerritory, setSelectedTerritory] = useState("");
    const [esCreador, setEsCreador] = useState(false);
    const [length, setLength] = useState(null);
    const [dado1, setDado1] = useState(null);
    const [dado2, setDado2] = useState(null);
    const [dado3, setDado3] = useState(null);
    const [dado4, setDado4] = useState(null);
    const [dado5, setDado5] = useState(null);
    const [isRolling1, setIsRolling1] = useState(false);
    const [isRolling2, setIsRolling2] = useState(false);
    const [isRolling3, setIsRolling3] = useState(false);
    const [isRolling4, setIsRolling4] = useState(false);
    const [isRolling5, setIsRolling5] = useState(false);
    const [hasRolled1, setHasRolled1] = useState(false);
    const [hasRolled2, setHasRolled2] = useState(false);
    const [hasRolled3, setHasRolled3] = useState(false);
    const [hasRolled4, setHasRolled4] = useState(false);
    const [hasRolled5, setHasRolled5] = useState(false);
    const [criteriaA1, setCriteriaA1] = useState(null);
    const [criteriaA2, setCriteriaA2] = useState(null);
    const [criteriaB1, setCriteriaB1] = useState(null);
    const [criteriaB2, setCriteriaB2] = useState(null);
    const [dices, setDices] = useState([null]);
    const [isCurrentActivePlayer, setIsCurrentActivePlayer] = useState(false);
    const [isCurrentPlayerTurn, setIsCurrentPlayerTurn] = useState(false);
    const [matchData, setMatchData] = useState(null);
    const [numTerritorioASeleccionar, setNumTerritorioASeleccionar] = useState(null);
    const [dadoApartado, setDadoApartado] = useState(false);
    const [showTurnScreen, setShowTurnScreen] = useState(true);
    const [round, setRound] = useState(0);
    const [dadosInicializados, setDadosInicializados] = useState(false);
    const [territoriosColocados, setTerritoriosColocados] = useState(0);
    const [puedeSeleccionarTerritorios, setPuedeSeleccionarTerritorios] = useState(false);
    const [bloquearTerritorio, setBloquearTerritorio] = useState(false);
    const [showModal, setShowModal] = useState(false);
    const [modalPoderInterrogacionVisible, setModalPoderInterrogacionVisible] = useState(false);
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [puntuacionInterrogacion, setPuntuacionInterrogacion] = useState(0);
    const [ultimoTerritorioColocado, setUltimoTerritorioColocado] = useState(null);
    const [esPrimerTerritorioDelTurno, setEsPrimerTerritorioDelTurno] = useState(true);
    const [tiempoRestanteNoActivo, setTiempoRestanteNoActivo] = useState(null);
    const [tiempoRestanteActivo, setTiempoRestanteActivo] = useState(null);
    const [numeroJugadores, setNumeroJugadores] = useState(null);
    const [jugadoresInicializado, setJugadoresInicializado] = useState(false);
    const [ultimaRonda, setUltimaRonda] = useState(false);
    const [alertShown, setAlertShown] = useState(false);
    const [ganadores, setGanadores] = useState([]);
    const [bloquearDado, setBloquearDado] = useState(false);
    const [ultimoTurno, setUltimoTurno] = useState(false);
    const [numPraderasDisponibles, setNumPraderasDisponibles] = useState(0);
    const [numCastillosDisponibles, setNumCastillosDisponibles] = useState(0);
    const [numMontañasDisponibles, setNumMontañasDisponibles] = useState(0);
    const [numPueblosDisponibles, setNumPueblosDisponibles] = useState(0);
    const [numRiosDisponibles, setNumRiosDisponibles] = useState(0);
    const [numBosquesDisponibles, setNumBosquesDisponibles] = useState(0);
    const [numTerritoriosInicializados, setNumTerritoriosInicializados] = useState(false);
    const [matchMode, setMatchMode] = useState(null);
    const [aux1, setAux1] = useState(0);
    const [aux2, setAux2] = useState(0);
    const [aux3, setAux3] = useState(0);
    const [aux4, setAux4] = useState(0);
    const [aux5, setAux5] = useState(0);

    useEffect(() => {
        switch (matchMode) {
            case "SLOW":
                setTiempoRestanteActivo(240);
                setTiempoRestanteNoActivo(60);
                break;
            case "FAST":
                setTiempoRestanteActivo(120);
                setTiempoRestanteNoActivo(30);
                break;
            case "STANDARD":
                setTiempoRestanteActivo(180);
                setTiempoRestanteNoActivo(45);
                break;
        }
    }, [matchMode]);

    useEffect(() => {
        if (matchData && !numTerritoriosInicializados) {
            const numPlayers = matchData.players.length;
            if (numPlayers == 4) {
                setNumPraderasDisponibles(1);
                setNumCastillosDisponibles(1);
                setNumMontañasDisponibles(1);
                setNumPueblosDisponibles(1);
                setNumRiosDisponibles(1);
                setNumBosquesDisponibles(1);
            } else if (numPlayers == 3) {
                setNumPraderasDisponibles(2);
                setNumCastillosDisponibles(2);
                setNumMontañasDisponibles(2);
                setNumPueblosDisponibles(2);
                setNumRiosDisponibles(2);
                setNumBosquesDisponibles(2);
            } else if (numPlayers == 2) {
                setNumPraderasDisponibles(3);
                setNumCastillosDisponibles(3);
                setNumMontañasDisponibles(3);
                setNumPueblosDisponibles(3);
                setNumRiosDisponibles(3);
                setNumBosquesDisponibles(3);
            }
            setNumTerritoriosInicializados(true);
        }
    }, [matchData]);

    const handleStartTurn = () => {
        setShowTurnScreen(false);
        setDadosInicializados(true);
    };

    const navigate = useNavigate();

    const criterioMap = {
        UNO: "1",
        DOS: "2",
        TRES: "3",
        CUATRO: "4",
        CINCO: "5",
        SEIS: "6",
    };

    const actualizarUltimoTurno = async (matchId, jwt) => {
        let nextVersion = matchData.version;
        alert("Pulsa en finalizar partida para ver los resultados");
        setUltimoTurno(true);
        try {
            const response = await fetch(`/api/v1/matches/${matchId}`, {
                method: "PATCH",
                headers: {
                    Authorization: `Bearer ${jwt}`,
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    isLastTurn: true,
                    version: nextVersion
                }),
            });
            if (!response.ok) {
                throw new Error(`Error en la solicitud: ${response.statusText}`);
            }
            const data = await response.json();
            return data;
        } catch (error) {
            console.error('Error al actualizar el turno:', error);
            throw error;
        }
    };

    const lanzarDado1 = () => {
    if (localStorage.getItem("areDice1Thrown") === 'true' && aux1 === 0) {
        setHasRolled1(true);
        const dadoGuardado = localStorage.getItem("dado1");
        setDado1(dadoGuardado ? Number(dadoGuardado) : null);
        setAux1(1);
        return;
    }
    if (dadoApartado) {
        return;
    }
    if (hasRolled1) {
        setNumTerritorioASeleccionar(dado1);
        setDado1(0);
        setDadoApartado(true);
        setPuedeSeleccionarTerritorios(true);
        return;
    }
    setSelectedTerritory("");
    setIsRolling1(true);
    setTimeout(() => {
        const nuevoDado = Math.floor(Math.random() * 6) + 1;
        setDado1(nuevoDado);
        setIsRolling1(false);
        setHasRolled1(true);
        localStorage.setItem("dado1", nuevoDado);
        localStorage.setItem("areDice1Thrown", true);
    }, 1000);
    if (ultimaRonda) {
        actualizarUltimoTurno(matchId, jwt);
    }
};

    const lanzarDado2 = () => {
    if (localStorage.getItem("areDice2Thrown") === 'true' && aux2 === 0) {
        setHasRolled2(true);
        const dadoGuardado = localStorage.getItem("dado2");
        setDado2(dadoGuardado ? Number(dadoGuardado) : null);
        setAux2(2);
        return;
    }
    if (dadoApartado) {
        return;
    }
    if (hasRolled2) {
        setNumTerritorioASeleccionar(dado2);
        setDado2(0);
        setDadoApartado(true);
        setPuedeSeleccionarTerritorios(true);
        return;
    }
    setSelectedTerritory("");
    setIsRolling2(true);
    setTimeout(() => {
        const nuevoDado = Math.floor(Math.random() * 6) + 1;
        setDado2(nuevoDado);
        setIsRolling2(false);
        setHasRolled2(true);
        localStorage.setItem("dado2", nuevoDado);
        localStorage.setItem("areDice2Thrown", true);
    }, 1000);
    if (ultimaRonda) {
        actualizarUltimoTurno(matchId, jwt);
    }
};

    const lanzarDado3 = () => {
    if (localStorage.getItem("areDice3Thrown") === 'true' && aux3 === 0) {
        setHasRolled3(true);
        const dadoGuardado = localStorage.getItem("dado3");
        setDado3(dadoGuardado ? Number(dadoGuardado) : null);
        setAux3(3);
        return;
    }
    if (dadoApartado) {
        return;
    }
    if (hasRolled3) {
        setNumTerritorioASeleccionar(dado3);
        setDado3(0);
        setDadoApartado(true);
        setPuedeSeleccionarTerritorios(true);
        return;
    }
    setSelectedTerritory("");
    setIsRolling3(true);
    setTimeout(() => {
        const nuevoDado = Math.floor(Math.random() * 6) + 1;
        setDado3(nuevoDado);
        setIsRolling3(false);
        setHasRolled3(true);
        localStorage.setItem("dado3", nuevoDado);
        localStorage.setItem("areDice3Thrown", true);
    }, 1000);
    if (ultimaRonda) {
        actualizarUltimoTurno(matchId, jwt);
    }
};

    const lanzarDado4 = () => {
    if (localStorage.getItem("areDice4Thrown") === 'true' && aux4 === 0) {
        setHasRolled4(true);
        const dadoGuardado = localStorage.getItem("dado4");
        setDado4(dadoGuardado ? Number(dadoGuardado) : null);
        setAux4(4);
        return;
    }
    if (dadoApartado) {
        return;
    }
    if (hasRolled4) {
        setNumTerritorioASeleccionar(dado4);
        setDado4(0);
        setDadoApartado(true);
        setPuedeSeleccionarTerritorios(true);
        return;
    }
    setSelectedTerritory("");
    setIsRolling4(true);
    setTimeout(() => {
        const nuevoDado = Math.floor(Math.random() * 6) + 1;
        setDado4(nuevoDado);
        setIsRolling4(false);
        setHasRolled4(true);
        localStorage.setItem("dado4", nuevoDado);
        localStorage.setItem("areDice4Thrown", true);
    }, 1000);
    if (ultimaRonda) {
        actualizarUltimoTurno(matchId, jwt);
    }
};

    const lanzarDado5 = () => {
    if (localStorage.getItem("areDice5Thrown") === 'true' && aux5 === 0) {
        setHasRolled5(true);
        const dadoGuardado = localStorage.getItem("dado5");
        setDado5(dadoGuardado ? Number(dadoGuardado) : null);
        setAux5(5);
        return;
    }
    if (dadoApartado) {
        return;
    }
    if (hasRolled5) {
        setNumTerritorioASeleccionar(dado5);
        setDado5(0);
        setDadoApartado(true);
        setPuedeSeleccionarTerritorios(true);
        return;
    }
    setSelectedTerritory("");
    setIsRolling5(true);
    setTimeout(() => {
        const nuevoDado = Math.floor(Math.random() * 6) + 1;
        setDado5(nuevoDado);
        setIsRolling5(false);
        setHasRolled5(true);
        localStorage.setItem("dado5", nuevoDado);
        localStorage.setItem("areDice5Thrown", true);
    }, 1000);
    if (ultimaRonda) {
        actualizarUltimoTurno(matchId, jwt);
    }
};

    const escogerDado1 = () => {
        if (!bloquearDado) {
            setNumTerritorioASeleccionar(dado1);
            setPuedeSeleccionarTerritorios(true);
            setBloquearDado(true);
        }
    };

    const escogerDado2 = () => {
        if (!bloquearDado) {
            setNumTerritorioASeleccionar(dado2);
            setPuedeSeleccionarTerritorios(true);
            setBloquearDado(true);
        }
    };

    const escogerDado3 = () => {
        if (!bloquearDado) {
            setNumTerritorioASeleccionar(dado3);
            setPuedeSeleccionarTerritorios(true);
            setBloquearDado(true);
        }
    };

    const escogerDado4 = () => {
        if (!bloquearDado) {
            setNumTerritorioASeleccionar(dado4);
            setPuedeSeleccionarTerritorios(true);
            setBloquearDado(true);
        }
    };

    const escogerDado5 = () => {
        if (!bloquearDado) {
            setNumTerritorioASeleccionar(dado5);
            setPuedeSeleccionarTerritorios(true);
            setBloquearDado(true);
        }
    };




    const getNeighbors = (tablero, q, r, s) => {
        const directions = [
            { q: 1, r: -1, s: 0 },
            { q: 1, r: 0, s: -1 },
            { q: 0, r: 1, s: -1 },
            { q: -1, r: 1, s: 0 },
            { q: -1, r: 0, s: 1 },
            { q: 0, r: -1, s: 1 },
        ];
        return directions
            .map(dir => tablero.casillas.find(casilla =>
                casilla.q === q + dir.q && casilla.r === r + dir.r && casilla.s === s + dir.s
            ))
            .filter(vecino => vecino);
    };

    const groupByLine = (tablero) => {
        return Object.values(tablero.casillas.reduce((acc, casilla) => {
            if (!acc[casilla.r]) acc[casilla.r] = [];
            acc[casilla.r].push(casilla);
            return acc;
        }, {}));
    };

    const isOnBorder = (tablero, q, r, s) => {
        const maxQ = Math.max(...tablero.casillas.map(c => c.q));
        const minQ = Math.min(...tablero.casillas.map(c => c.q));
        const maxR = Math.max(...tablero.casillas.map(c => c.r));
        const minR = Math.min(...tablero.casillas.map(c => c.r));
        const maxS = Math.max(...tablero.casillas.map(c => c.s));
        const minS = Math.min(...tablero.casillas.map(c => c.s));
        return q === maxQ || q === minQ || r === maxR || r === minR || s === maxS || s === minS;
    };

    const getTerritoryGroups = (tablero, criterio) => {
        const visited = new Set();
        const groups = [];

        const dfs = (casilla, grupo) => {
            const key = `${casilla.q},${casilla.r},${casilla.s}`;
            if (visited.has(key)) return;
            visited.add(key);
            grupo.push(casilla);

            const neighbors = tablero.casillas.filter(c => {
                if (!criterio(c)) return false;
                return isNeighbor(casilla.q, casilla.r, casilla.s, c.q, c.r, c.s);
            });

            for (const neighbor of neighbors) {
                dfs(neighbor, grupo);
            }
        };

        for (const casilla of tablero.casillas) {
            if (criterio(casilla)) {
                const key = `${casilla.q},${casilla.r},${casilla.s}`;
                if (!visited.has(key)) {
                    const grupo = [];
                    dfs(casilla, grupo);
                    groups.push(grupo);
                }
            }
        }
        return groups;
    };


    function connectOppositeSides(tablero, territorio, eje) {
        const values = tablero.casillas.map(c => c[eje]);
        const minVal = Math.min(...values);
        const maxVal = Math.max(...values);
        const edgeMin = tablero.casillas.filter(c =>
            c[eje] === minVal && c.territorio === territorio
        );
        const edgeMax = tablero.casillas.filter(c =>
            c[eje] === maxVal && c.territorio === territorio
        );
        if (edgeMin.length === 0 || edgeMax.length === 0) {
            return false;
        }
        const visited = new Set();
        function dfs(actual) {
            if (edgeMax.some(b => b.q === actual.q && b.r === actual.r && b.s === actual.s)) {
                return true;
            }
            visited.add(`${actual.q},${actual.r},${actual.s}`);
            const neighbors = tablero.casillas.filter(c => {
                if (c.territorio !== territorio) return false;
                const key = `${c.q},${c.r},${c.s}`;
                if (visited.has(key)) return false;
                return isNeighbor(actual.q, actual.r, actual.s, c.q, c.r, c.s);
            });
            for (const n of neighbors) {
                if (dfs(n)) return true;
            }
            return false;
        }
        for (const start of edgeMin) {
            if (dfs(start)) {
                return true;
            }
        }
        return false;
    }

    const isNeighbor = (q1, r1, s1, q2, r2, s2) => {
        const diffs = [
            { dq: 1, dr: -1, ds: 0 },
            { dq: 1, dr: 0, ds: -1 },
            { dq: 0, dr: 1, ds: -1 },
            { dq: -1, dr: 1, ds: 0 },
            { dq: -1, dr: 0, ds: 1 },
            { dq: 0, dr: -1, ds: 1 },
        ];
        return diffs.some(diff => q1 + diff.dq === q2 && r1 + diff.dr === r2 && s1 + diff.ds === s2);
    };

    const criterios = {
        "A1": (tablero) => {
            // 2PV por cada castillo rodeado por 6 casillas QUE NO ESTEN VACIAS
            return tablero.casillas.reduce((score, casilla) => {
                if (casilla.territorio === "castillo") {
                    const vecinos = getNeighbors(tablero, casilla.q, casilla.r, casilla.s);
                    const todosLlenos = vecinos.every(vecino => vecino.territorio);
                    if (todosLlenos) {
                        score += 2;
                    }
                }
                return score;
            }, 0);
        },
        "A2": (tablero) => {
            // 3 PV por cada pradera adyacente a una montaña y un río, +1 extra si también conecta con un bosque
            return tablero.casillas.reduce((score, casilla) => {
                if (casilla.territorio === "pradera") {
                    const vecinos = getNeighbors(tablero, casilla.q, casilla.r, casilla.s);
                    const tienemontaña = vecinos.some(vecino => vecino.territorio === "montaña");
                    const tienerio = vecinos.some(vecino => vecino.territorio === "rio");
                    const tienebosque = vecinos.some(vecino => vecino.territorio === "bosque");
                    if (tienemontaña && tienerio) {
                        score += 3;
                        if (tienebosque) {
                            score += 1;
                        }
                    }
                }
                return score;
            }, 0);
        },
        "A3": (tablero) => {
            // 2 PV por cada bosque en el grupo más pequeño (mínimo 2 grupos de bosques)
            const bosqueGrupos = getTerritoryGroups(tablero, (casilla) => casilla.territorio === "bosque");
            if (bosqueGrupos.length < 2) return 0;
            const grupoMasPequeño = Math.min(...bosqueGrupos.map(grupo => grupo.length));
            return grupoMasPequeño * 2;
        },
        "A4": (tablero) => {
            // 2 PV por cada línea donde aparezcan praderas y ríos
            const filas = groupByLine(tablero);
            return filas.reduce((score, fila) => {
                const tienepradera = fila.some(casilla => casilla.territorio === "pradera");
                const tienerio = fila.some(casilla => casilla.territorio === "rio");
                if (tienepradera && tienerio) {
                    score += 2;
                }
                return score;
            }, 0);
        },
        "A5": (tablero) => {
            // 5 PV por cada grupo de pueblos
            const groups = getTerritoryGroups(tablero, (casilla) => casilla.territorio === "pueblo");
            const puebloGroups = groups.filter(group => group[0]?.territorio === "pueblo");
            return puebloGroups.length * 5;
        },
        "A6": (tablero) => {
            // 1 PV por cada montaña en los bordes del mapa
            return tablero.casillas.reduce((score, casilla) => {
                if (casilla.territorio === "montaña" && isOnBorder(tablero, casilla.q, casilla.r, casilla.s)) {
                    score += 1;
                }
                return score;
            }, 0);
        },
        "B1": (tablero) => {
            // 1 PV por cada montaña en el grupo más grande (mínimo 2 grupos)
            const montanaGrupos = getTerritoryGroups(tablero, (casilla) => casilla.territorio === "montaña");
            if (montanaGrupos.length < 2) return 0;
            const grupoMasGrande = Math.max(...montanaGrupos.map(grupo => grupo.length));
            return grupoMasGrande;
        },
        "B2": (tablero) => {
            // 1 PV por cada pradera conectada con al menos un pueblo y +3 PV si también conecta con 2 castillos
            return tablero.casillas.reduce((score, casilla) => {
                if (casilla.territorio === "pradera") {
                    const vecinos = getNeighbors(tablero, casilla.q, casilla.r, casilla.s);
                    const conectapueblo = vecinos.some(vecino => vecino.territorio === "pueblo");
                    const castillosConectados = vecinos.filter(vecino => vecino.territorio === "castillo").length;
                    if (conectapueblo) {
                        score += 1;
                        if (castillosConectados >= 2) {
                            score += 3;
                        }
                    }
                }
                return score;
            }, 0);
        },
        "B3": (tablero) => {
            // 10 PV por conectar dos caras opuestas del mapa con bosques
            const territory = "bosque";
            let score = 0;
            if (connectOppositeSides(tablero, territory, "q")) {
                score += 10;
            }
            if (connectOppositeSides(tablero, territory, "r")) {
                score += 10;
            }
            if (connectOppositeSides(tablero, territory, "s")) {
                score += 10;
            }
            return score;
        },
        "B4": (tablero) => {
            // 12 PV por cada castillo que conecte con un territorio de cada tipo
            return tablero.casillas.reduce((score, casilla) => {
                if (casilla.territorio === "castillo") {
                    const vecinos = getNeighbors(tablero, casilla.q, casilla.r, casilla.s);
                    const tiposConectados = new Set(vecinos.map(vecino => vecino.territorio));
                    if (["pradera", "bosque", "rio", "montaña", "pueblo"].every(tipo => tiposConectados.has(tipo))) {
                        score += 12;
                    }
                }
                return score;
            }, 0);
        },
        "B5": (tablero) => {
            // 2 PV por cada río que conecte con al menos dos bosques
            return tablero.casillas.reduce((score, casilla) => {
                if (casilla.territorio === "rio") {
                    const vecinos = getNeighbors(tablero, casilla.q, casilla.r, casilla.s);
                    const bosquesConectados = vecinos.filter(vecino => vecino.territorio === "bosque").length;
                    if (bosquesConectados >= 2) {
                        score += 2;
                    }
                }
                return score;
            }, 0);
        },
        "B6": (tablero) => {
            // 8 PV por cada pueblo que conecte con río, bosque o montaña y ningún otro pueblo
            return tablero.casillas.reduce((score, casilla) => {
                if (casilla.territorio === "pueblo") {
                    const vecinos = getNeighbors(tablero, casilla.q, casilla.r, casilla.s);
                    const tieneRio = vecinos.some(v => v.territorio === "rio");
                    const tieneBosque = vecinos.some(v => v.territorio === "bosque");
                    const tieneMontana = vecinos.some(v => v.territorio === "montaña");
                    const noConectaPueblo = vecinos.every(v => v.territorio !== "pueblo");
                    if (tieneRio && tieneBosque && tieneMontana && noConectaPueblo) {
                        score += 8;
                    }
                }
                return score;
            }, 0);
        },
    };

    const puntuacionActual =
        (criterios[criteriaA1] ? criterios[criteriaA1](tablero) : 0)
        + (criterios[criteriaA2] ? criterios[criteriaA2](tablero) : 0)
        + (criterios[criteriaB1] ? criterios[criteriaB1](tablero) : 0)
        + (criterios[criteriaB2] ? criterios[criteriaB2](tablero) : 0);

    function calcularPuntuacion(tablero, criterios) {
        return (
            (criterios[criteriaA1] ? criterios[criteriaA1](tablero) : 0) +
            (criterios[criteriaA2] ? criterios[criteriaA2](tablero) : 0) +
            (criterios[criteriaB1] ? criterios[criteriaB1](tablero) : 0) +
            (criterios[criteriaB2] ? criterios[criteriaB2](tablero) : 0)
        );
    }

    const modal = getErrorModal(setVisible, visible, errorMessage);

    const saveBoard = (tablero) => {
        localStorage.setItem("tablero", JSON.stringify(tablero));
    };

    const loadBoard = () => {
        const tableroGuardado = localStorage.getItem("tablero");
        return tableroGuardado ? JSON.parse(tableroGuardado) : null;
    };

    const generateHexagonCoordinates = (radius) => {
        const coordinates = [];
        for (let q = -radius; q <= radius; q++) {
            for (let r = Math.max(-radius, -q - radius); r <= Math.min(radius, -q + radius); r++) {
                const s = -q - r;
                let tipoCasilla = "EMPTY";

                // Reglas para tipo de casilla
                if (q === 0 && r === 0 && s === 0) {
                    tipoCasilla = "POWER_INTERROGATION";
                } else if (
                    (q === 0 && r === -3 && s === 3) ||
                    (q === 3 && r === -3 && s === 0) ||
                    (q === -3 && r === 3 && s === 0) ||
                    (q === 0 && r === 3 && s === -3)
                ) {
                    tipoCasilla = "POWER_PLUSMINUS";
                }

                coordinates.push({ q, r, s, tipoCasilla, territorio: null });
            }
        }
        return coordinates;
    }

    useEffect(() => {
        const tableroGuardado = loadBoard();
        if (tableroGuardado) {
            setTablero(tableroGuardado);
        } else {
            const basicTablero = {
                casillas: generateHexagonCoordinates(4),
            };
            setTablero(basicTablero);
            saveBoard(basicTablero);
        }
    }, []);

    const abandonarPartida = async () => {
        localStorage.removeItem("tablero");
        const basicTablero = {
            casillas: generateHexagonCoordinates(4),
        };
        setTablero(basicTablero);
        saveBoard(basicTablero);
        try {
            const getBoardIdResponse = await fetch(`/api/v1/boards`, {
                method: 'GET',
                headers: {
                    Authorization: `Bearer ${jwt}`,
                    "Content-Type": "application/json"
                },
            });

            if (getBoardIdResponse.ok) {
                const boardData = await getBoardIdResponse.json();
                const playerBoard = boardData.find(board => board.player.id == playerId);
                if (playerBoard) {
                    const boardId = playerBoard.id;
                    const deleteBoardResponse = await fetch(`/api/v1/boards/${boardId}`, {
                        method: 'DELETE',
                        headers: {
                            Authorization: `Bearer ${jwt}`,
                            "Content-Type": "application/json"
                        },
                    });

                    if (deleteBoardResponse.ok) {
                        try {
                            const leaveMatchResponse = await fetch(`/api/v1/matches/leave/${matchId}`, {
                                method: 'PUT',
                                headers: {
                                    Authorization: `Bearer ${jwt}`,
                                    "Content-Type": "application/json"
                                },
                            });

                            if (leaveMatchResponse.ok) {
                                navigate(`/`);
                            } else {
                                console.error("Error al dejar la partida:", leaveMatchResponse.status);
                                navigate(`/`);
                            }
                        } catch (error) {
                            console.error("Error al realizar la solicitud para dejar la partida:", error, "Serás redirigido al lobby.");
                            navigate(`/`);
                        }
                    } else {
                        console.error("Error al eliminar el tablero del backend. serás redirigido al lobby.");
                        navigate(`/`);
                    }
                } else {
                    console.error("No se encontró el tablero del jugador. Serás redirigido al lobby.");
                    navigate(`/`);
                }
            } else {
                console.error("Error al obtener los tableros del backend. Serás redirigido al lobby. ");
                navigate(`/`);
            }
        } catch (error) {
            console.error("Error al realizar la solicitud de eliminación:", error, "Serás redirigido al lobby.");
            navigate(`/`);

        }
    };

    const PoderInterrogacionModal = ({ isVisible, onClose, message }) => {
        if (!isVisible) return null;

        return (
            <div className="modal-overlay">
                <div className="modal-content">
                    <p>¡Poder utilizado correctamente!
                        Tu puntuación en el poder es de: {puntuacionInterrogacion}</p>
                    <button onClick={onClose}>Cerrar</button>
                </div>
            </div>
        );
    };

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
                if (response.status === 403) {
                    alert("No tienes permisos para acceder a esta partida. Por favor, vuelve a iniciar sesión.");
                    navigate("/");
                    return;
                }
                console.error("Error al obtener los datos del match:", response.statusText);
                return;
            }

            if (response.ok) {
                const data = await response.json();

                console.log("Fetched match data:", data);

                setEsCreador(Number(data.creator.id) === Number(playerId));
                setLength(Number(data.players.length));
                setCriteriaA1(`A${criterioMap[data.criteriaA1]}`);
                setCriteriaA2(`A${criterioMap[data.criteriaA2]}`);
                setCriteriaB1(`B${criterioMap[data.criteriaB1]}`);
                setCriteriaB2(`B${criterioMap[data.criteriaB2]}`);
                setDices(data.dice);
                if (data.dice.length === 0) {
                    setDices([null, null, null, null, null]);
                }
                setDadosInicializados(true);
                setIsCurrentPlayerTurn(Number(data.players[data.currentPlayerTurn]?.id) === Number(playerId));
                setIsCurrentActivePlayer(Number(data.players[data.currentActivePlayer]?.id) === Number(playerId));
                setMatchData(data);
                setDado1(dices[0]);
                setDado2(dices[1]);
                setDado3(dices[2]);
                setDado4(dices[3]);
                setDado5(dices[4]);
                setRound(Number(data.round));
                setUltimaRonda(data.isLastRound);
                setUltimoTurno(data.isLastTurn);
                if (!jugadoresInicializado && (data.players.length !== null)) {
                    setNumeroJugadores(data.players.length);
                    setJugadoresInicializado(true);
                }
                setSelectedTerritory(data.territory);
                setMatchMode(data.mode);
            } else {
                console.error("Error al obtener los datos del match:", response.statusText);
            }
        } catch (error) {
            console.error("Error al realizar el fetch del match:", error);
        }
    };

    const [shouldFetch, setShouldFetch] = useState(true);

    useEffect(() => {
        fetchMatchData();
        if (tablero && tablero.casillas) {
            console.log("Casillas libres:", tablero.casillas.filter(casilla => !casilla.territorio).length);
        }
        console.log("Casillas a seleccionar:", numTerritorioASeleccionar);
        if (matchId && playerId && shouldFetch) {
            // Llamada inicial para obtener los datos de la partida
            fetchMatchData();

            // Configurar polling para verificar periódicamente el estado
            const intervalId = setInterval(() => {
                if (isCurrentPlayerTurn) {
                    setShouldFetch(false);
                } else {
                    fetchMatchData();
                }
            }, 1000); // Intervalo de 1 segundo

            // Limpiar el intervalo al desmontar el componente
            return () => clearInterval(intervalId);
        }
    }, [matchId, playerId, isCurrentPlayerTurn, shouldFetch]);

    useEffect(() => {
        const interval = setInterval(() => {
            if (!jugadoresInicializado && matchData !== null && matchData.players.length !== null) {
                setNumeroJugadores(matchData.players.length);
                setJugadoresInicializado(true);
            }
            console.log(jugadoresInicializado);
            console.log(matchData);
        }, 1000);

        return () => clearInterval(interval);
    }, [jugadoresInicializado, matchData]);

    const nextTurn = async () => {
        try {
            setHasRolled1(false);
            setHasRolled2(false);
            setHasRolled3(false);
            setHasRolled4(false);
            setHasRolled5(false);
            setDadoApartado(false);
            switch (matchMode) {
                case "SLOW":
                    setTiempoRestanteActivo(240);
                    setTiempoRestanteNoActivo(60);
                    break;
                case "FAST":
                    setTiempoRestanteActivo(120);
                    setTiempoRestanteNoActivo(30);
                    break;
                case "STANDARD":
                    setTiempoRestanteActivo(180);
                    setTiempoRestanteNoActivo(45);
                    break;
            }
            localStorage.setItem("areDice1Thrown", false);
            localStorage.setItem("areDice2Thrown", false);
            localStorage.setItem("areDice3Thrown", false);
            localStorage.setItem("areDice4Thrown", false);
            localStorage.setItem("areDice5Thrown", false);
            localStorage.setItem("dado1", 0);
            localStorage.setItem("dado2", 0);
            localStorage.setItem("dado3", 0);
            localStorage.setItem("dado4", 0);
            localStorage.setItem("dado5", 0);
            setAux1(0);
            setAux2(0);
            setAux3(0);
            setAux4(0);
            setAux5(0);
            if (!jugadoresInicializado) {
                setNumeroJugadores(length);
            }

            let activePlayer = Number(matchData.currentActivePlayer);
            let currentTurn = Number(matchData.currentPlayerTurn);
            let round = Number(matchData.round);
            const playerCount = matchData.players.length;
            let ultima = ultimaRonda;
            let newTerritory = selectedTerritory;
            let nextVersion = 0;
            let newActivePlayer = 0;
            let nextTurn = 0;
            let nextRound = 0;
            let diceValues = [null, null, null, null, null];

            const setDiceWithZero = (...dados) => {
                return dados.map(d => d === 0 ? 0 : d);
            };

            const setSingleZero = (...dados) => {
                return dados.map(d => d === 0 ? 0 : null);
            };

            function getZeroCombo2P(d1, d2, d3) {
                if (d1 === 0 && d2 === 0) return [0, 0, d3, null, null];
                else if (d1 === 0 && d3 === 0) return [0, d2, 0, null, null];
                else if (d2 === 0 && d3 === 0) return [d1, 0, 0, null, null];
            }

            function getZeroCombo3P(d1, d2, d3, d4) {
                if (d1 === 0 && d2 === 0 && d3 === 0) return [0, 0, 0, d4, null];
                else if (d1 === 0 && d2 === 0 && d4 === 0) return [0, 0, d3, 0, null];
                else if (d1 === 0 && d3 === 0 && d4 === 0) return [0, d2, 0, 0, null];
                else if (d2 === 0 && d3 === 0 && d4 === 0) return [d1, 0, 0, 0, null];
                else if (d1 === 0 && d2 === 0) return [0, 0, d3, d4, null];
                else if (d1 === 0 && d3 === 0) return [0, d2, 0, d4, null];
                else if (d1 === 0 && d4 === 0) return [0, d2, d3, 0, null];
                else if (d2 === 0 && d3 === 0) return [d1, 0, 0, d4, null];
                else if (d2 === 0 && d4 === 0) return [d1, 0, d3, 0, null];
                else if (d3 === 0 && d4 === 0) return [d1, d2, 0, 0, null];
            }

            function getZeroCombo4P(d1, d2, d3, d4, d5) {
                if (d1 === 0 && d2 === 0 && d3 === 0 && d4 === 0) return [0, 0, 0, 0, d5];
                else if (d1 === 0 && d2 === 0 && d3 === 0 && d5 === 0) return [0, 0, 0, d4, 0];
                else if (d1 === 0 && d2 === 0 && d4 === 0 && d5 === 0) return [0, 0, d3, 0, 0];
                else if (d1 === 0 && d3 === 0 && d4 === 0 && d5 === 0) return [0, d2, 0, 0, 0];
                else if (d2 === 0 && d3 === 0 && d4 === 0 && d5 === 0) return [d1, 0, 0, 0, 0];
                else if (d1 === 0 && d2 === 0 && d3 === 0) return [0, 0, 0, d4, d5];
                else if (d1 === 0 && d2 === 0 && d4 === 0) return [0, 0, d3, 0, d5];
                else if (d1 === 0 && d2 === 0 && d5 === 0) return [0, 0, d3, d4, 0];
                else if (d1 === 0 && d3 === 0 && d4 === 0) return [0, d2, 0, 0, d5];
                else if (d1 === 0 && d3 === 0 && d5 === 0) return [0, d2, 0, d4, 0];
                else if (d1 === 0 && d4 === 0 && d5 === 0) return [0, d2, d3, 0, 0];
                else if (d2 === 0 && d3 === 0 && d4 === 0) return [d1, 0, 0, 0, d5];
                else if (d2 === 0 && d3 === 0 && d5 === 0) return [d1, 0, 0, d4, 0];
                else if (d2 === 0 && d4 === 0 && d5 === 0) return [d1, 0, d3, 0, 0];
                else if (d3 === 0 && d4 === 0 && d5 === 0) return [d1, d2, 0, 0, 0];
                else if (d1 === 0 && d2 === 0) return [0, 0, d3, d4, d5];
                else if (d1 === 0 && d3 === 0) return [0, d2, 0, d4, d5];
                else if (d1 === 0 && d4 === 0) return [0, d2, d3, 0, d5];
                else if (d1 === 0 && d5 === 0) return [0, d2, d3, d4, 0];
                else if (d2 === 0 && d3 === 0) return [d1, 0, 0, d4, d5];
                else if (d2 === 0 && d4 === 0) return [d1, 0, d3, 0, d5];
                else if (d2 === 0 && d5 === 0) return [d1, 0, d3, d4, 0];
                else if (d3 === 0 && d4 === 0) return [d1, d2, 0, 0, d5];
                else if (d3 === 0 && d5 === 0) return [d1, d2, 0, d4, 0];
                else if (d4 === 0 && d5 === 0) return [d1, d2, d3, 0, 0];
            }

            if (playerCount === 2) {
                if (round === 0) {  // 2 jugadores - Ronda 0
                    if (activePlayer === 0 && currentTurn === 0) diceValues = setDiceWithZero(dado1, dado2, dado3);
                    else if (activePlayer === 0 && currentTurn === 1) diceValues = setSingleZero(dado1, dado2, dado3);
                    else if (activePlayer === 1 && currentTurn === 0) diceValues = [null, null, null, null, null];
                    else if (activePlayer === 1 && currentTurn === 1) diceValues = getZeroCombo2P(dado1, dado2, dado3);
                }
                if (round === 1) {  // 2 jugadores - Ronda 1
                    if (activePlayer === 1 && currentTurn === 0) diceValues = setSingleZero(dado1, dado2, dado3);
                    else if (activePlayer === 1 && currentTurn === 1) diceValues = setDiceWithZero(dado1, dado2, dado3);
                    else if (activePlayer === 0 && currentTurn === 1) diceValues = [null, null, null, null, null];
                    else if (activePlayer === 0 && currentTurn === 0) diceValues = getZeroCombo2P(dado1, dado2, dado3);
                }
            }

            if (playerCount === 3) {
                if (round === 0) {  // 3 jugadores - Ronda 0
                    if (activePlayer === 1 && currentTurn === 1 || activePlayer === 1 && currentTurn === 2 || activePlayer === 2 && currentTurn === 2 || activePlayer === 2 && currentTurn === 0) diceValues = getZeroCombo3P(dado1, dado2, dado3, dado4);
                    else if ((activePlayer === 0 && currentTurn === 0) || (activePlayer === 0 && currentTurn === 1)) diceValues = setDiceWithZero(dado1, dado2, dado3, dado4);
                    else if ((activePlayer === 0 && currentTurn === 2) || (activePlayer === 1 && currentTurn === 0)) diceValues = setSingleZero(dado1, dado2, dado3, dado4);
                    else if (activePlayer === 2 && currentTurn === 1) diceValues = [null, null, null, null, null];
                }
                if (round === 1) {  // 3 jugadores - Ronda 1
                    if ((activePlayer === 2 && currentTurn === 2) || (activePlayer === 2 && currentTurn === 0) || (activePlayer === 0 && currentTurn === 0) || (activePlayer === 0 && currentTurn === 1)) diceValues = getZeroCombo3P(dado1, dado2, dado3, dado4);
                    else if ((activePlayer === 1 && currentTurn === 1) || (activePlayer === 1 && currentTurn === 2)) diceValues = setDiceWithZero(dado1, dado2, dado3, dado4);
                    else if ((activePlayer === 1 && currentTurn === 0) || (activePlayer === 2 && currentTurn === 1)) diceValues = setSingleZero(dado1, dado2, dado3, dado4);
                    else if ((activePlayer === 0 && currentTurn === 2)) diceValues = [null, null, null, null, null];
                }
                if (round === 2) {  // 3 jugadores - Ronda 2
                    if ((activePlayer === 0 && currentTurn === 0) || (activePlayer === 0 && currentTurn === 1) || (activePlayer === 1 && currentTurn === 1) || (activePlayer === 1 && currentTurn === 2)) diceValues = getZeroCombo3P(dado1, dado2, dado3, dado4);
                    else if ((activePlayer === 2 && currentTurn === 2) || (activePlayer === 2 && currentTurn === 0)) diceValues = setDiceWithZero(dado1, dado2, dado3, dado4);
                    else if ((activePlayer === 2 && currentTurn === 1) || (activePlayer === 0 && currentTurn === 2)) diceValues = setSingleZero(dado1, dado2, dado3, dado4);
                    else if (activePlayer === 1 && currentTurn === 0) diceValues = [null, null, null, null, null];
                }
            }
            if (playerCount === 4) {
                if (round === 0) {  // 4 jugadores - Ronda 0
                    if ((activePlayer === 1 && currentTurn === 1) || (activePlayer === 1 && currentTurn === 2) || (activePlayer === 1 && currentTurn === 3)
                        || (activePlayer === 2 && currentTurn === 2) || (activePlayer === 2 && currentTurn === 3) || (activePlayer === 2 && currentTurn === 0)
                        || (activePlayer === 3 && currentTurn === 3) || (activePlayer === 3 && currentTurn === 0) || (activePlayer === 3 && currentTurn === 1)) diceValues = getZeroCombo4P(dado1, dado2, dado3, dado4, dado5);
                    else if (activePlayer === 0 && currentTurn === 0 || activePlayer === 0 && currentTurn === 1 || activePlayer === 0 && currentTurn === 2) diceValues = setDiceWithZero(dado1, dado2, dado3, dado4, dado5);
                    else if (activePlayer === 0 && currentTurn === 3 || activePlayer === 1 && currentTurn === 0 || activePlayer === 2 && currentTurn === 1) diceValues = setSingleZero(dado1, dado2, dado3, dado4, dado5);
                    else if (activePlayer === 3 && currentTurn === 2) diceValues = [null, null, null, null, null];
                }
                if (round === 1) {  // 4 jugadores - Ronda 1
                    if ((activePlayer === 2 && currentTurn === 2) || (activePlayer === 2 && currentTurn === 3) || (activePlayer === 2 && currentTurn === 0)
                        || (activePlayer === 3 && currentTurn === 3) || (activePlayer === 3 && currentTurn === 0) || (activePlayer === 3 && currentTurn === 1)
                        || (activePlayer === 0 && currentTurn === 0) || (activePlayer === 0 && currentTurn === 1) || (activePlayer === 0 && currentTurn === 2)) diceValues = getZeroCombo4P(dado1, dado2, dado3, dado4, dado5);
                    else if (activePlayer === 1 && currentTurn === 1 || activePlayer === 1 && currentTurn === 2 || activePlayer === 1 && currentTurn === 3) diceValues = setDiceWithZero(dado1, dado2, dado3, dado4, dado5);
                    else if (activePlayer === 1 && currentTurn === 0 || activePlayer === 2 && currentTurn === 1 || activePlayer === 3 && currentTurn === 2) diceValues = setSingleZero(dado1, dado2, dado3, dado4, dado5);
                    else if (activePlayer === 0 && currentTurn === 3) diceValues = [null, null, null, null, null];
                }
                if (round === 2) {  // 4 jugadores - Ronda 2
                    if ((activePlayer === 3 && currentTurn === 3) || (activePlayer === 3 && currentTurn === 0) || (activePlayer === 3 && currentTurn === 1)
                        || (activePlayer === 0 && currentTurn === 0) || (activePlayer === 0 && currentTurn === 1) || (activePlayer === 0 && currentTurn === 2)
                        || (activePlayer === 1 && currentTurn === 1) || (activePlayer === 1 && currentTurn === 2) || (activePlayer === 1 && currentTurn === 3)) diceValues = getZeroCombo4P(dado1, dado2, dado3, dado4, dado5);
                    else if ((activePlayer === 2 && currentTurn === 2) || (activePlayer === 2 && currentTurn === 3) || (activePlayer === 2 && currentTurn === 0)) diceValues = setDiceWithZero(dado1, dado2, dado3, dado4, dado5);
                    else if ((activePlayer === 2 && currentTurn === 1) || (activePlayer === 3 && currentTurn === 2) || (activePlayer === 0 && currentTurn === 3)) diceValues = setSingleZero(dado1, dado2, dado3, dado4, dado5);
                    else if (activePlayer === 1 && currentTurn === 0) diceValues = [null, null, null, null, null];
                }
                if (round === 3) {  // 4 jugadores - Ronda 3
                    if ((activePlayer === 0 && currentTurn === 0) || (activePlayer === 0 && currentTurn === 1) || (activePlayer === 0 && currentTurn === 2)
                        || (activePlayer === 1 && currentTurn === 1) || (activePlayer === 1 && currentTurn === 2) || (activePlayer === 1 && currentTurn === 3)
                        || (activePlayer === 2 && currentTurn === 2) || (activePlayer === 2 && currentTurn === 3) || (activePlayer === 2 && currentTurn === 0)) diceValues = getZeroCombo4P(dado1, dado2, dado3, dado4, dado5);
                    else if ((activePlayer === 3 && currentTurn === 3) || (activePlayer === 3 && currentTurn === 0) || (activePlayer === 3 && currentTurn === 1)) diceValues = setDiceWithZero(dado1, dado2, dado3, dado4, dado5);
                    else if ((activePlayer === 3 && currentTurn === 2) || (activePlayer === 0 && currentTurn === 3) || (activePlayer === 1 && currentTurn === 0)) diceValues = setSingleZero(dado1, dado2, dado3, dado4, dado5);
                    else if (activePlayer === 2 && currentTurn === 1) diceValues = [null, null, null, null, null];
                }
            }
            setNumTerritorioASeleccionar(null);

            let transitions = {};
            if (playerCount === 2) {
                transitions = {
                    '0-0-0': { nextRound: 0, newActivePlayer: 0, nextTurn: 1 },
                    '0-0-1': { nextRound: 0, newActivePlayer: 1, nextTurn: 1 },
                    '0-1-1': { nextRound: 0, newActivePlayer: 1, nextTurn: 0 },
                    '0-1-0': { nextRound: 1, newActivePlayer: 1, nextTurn: 1 },
                    '1-1-1': { nextRound: 1, newActivePlayer: 1, nextTurn: 0 },
                    '1-1-0': { nextRound: 1, newActivePlayer: 0, nextTurn: 0 },
                    '1-0-0': { nextRound: 1, newActivePlayer: 0, nextTurn: 1 },
                    '1-0-1': { nextRound: 0, newActivePlayer: 0, nextTurn: 0 }
                };
            }
            else if (playerCount === 3) {
                transitions = {
                    '0-0-0': { nextRound: 0, newActivePlayer: 0, nextTurn: 1 },
                    '0-0-1': { nextRound: 0, newActivePlayer: 0, nextTurn: 2 },
                    '0-0-2': { nextRound: 0, newActivePlayer: 1, nextTurn: 1 },
                    '0-1-1': { nextRound: 0, newActivePlayer: 1, nextTurn: 2 },
                    '0-1-2': { nextRound: 0, newActivePlayer: 1, nextTurn: 0 },
                    '0-1-0': { nextRound: 0, newActivePlayer: 2, nextTurn: 2 },
                    '0-2-2': { nextRound: 0, newActivePlayer: 2, nextTurn: 0 },
                    '0-2-0': { nextRound: 0, newActivePlayer: 2, nextTurn: 1 },
                    '0-2-1': { nextRound: 1, newActivePlayer: 1, nextTurn: 1 },
                    '1-1-1': { nextRound: 1, newActivePlayer: 1, nextTurn: 2 },
                    '1-1-2': { nextRound: 1, newActivePlayer: 1, nextTurn: 0 },
                    '1-1-0': { nextRound: 1, newActivePlayer: 2, nextTurn: 2 },
                    '1-2-2': { nextRound: 1, newActivePlayer: 2, nextTurn: 0 },
                    '1-2-0': { nextRound: 1, newActivePlayer: 2, nextTurn: 1 },
                    '1-2-1': { nextRound: 1, newActivePlayer: 0, nextTurn: 0 },
                    '1-0-0': { nextRound: 1, newActivePlayer: 0, nextTurn: 1 },
                    '1-0-1': { nextRound: 1, newActivePlayer: 0, nextTurn: 2 },
                    '1-0-2': { nextRound: 2, newActivePlayer: 2, nextTurn: 2 },
                    '2-2-2': { nextRound: 2, newActivePlayer: 2, nextTurn: 0 },
                    '2-2-0': { nextRound: 2, newActivePlayer: 2, nextTurn: 1 },
                    '2-2-1': { nextRound: 2, newActivePlayer: 0, nextTurn: 0 },
                    '2-0-0': { nextRound: 2, newActivePlayer: 0, nextTurn: 1 },
                    '2-0-1': { nextRound: 2, newActivePlayer: 0, nextTurn: 2 },
                    '2-0-2': { nextRound: 2, newActivePlayer: 1, nextTurn: 1 },
                    '2-1-1': { nextRound: 2, newActivePlayer: 1, nextTurn: 2 },
                    '2-1-2': { nextRound: 2, newActivePlayer: 1, nextTurn: 0 },
                    '2-1-0': { nextRound: 0, newActivePlayer: 0, nextTurn: 0 }
                };
            }
            else if (playerCount === 4) {
                transitions = {
                    '0-0-0': { nextRound: 0, newActivePlayer: 0, nextTurn: 1 },
                    '0-0-1': { nextRound: 0, newActivePlayer: 0, nextTurn: 2 },
                    '0-0-2': { nextRound: 0, newActivePlayer: 0, nextTurn: 3 },
                    '0-0-3': { nextRound: 0, newActivePlayer: 1, nextTurn: 1 },
                    '0-1-1': { nextRound: 0, newActivePlayer: 1, nextTurn: 2 },
                    '0-1-2': { nextRound: 0, newActivePlayer: 1, nextTurn: 3 },
                    '0-1-3': { nextRound: 0, newActivePlayer: 1, nextTurn: 0 },
                    '0-1-0': { nextRound: 0, newActivePlayer: 2, nextTurn: 2 },
                    '0-2-2': { nextRound: 0, newActivePlayer: 2, nextTurn: 3 },
                    '0-2-3': { nextRound: 0, newActivePlayer: 2, nextTurn: 0 },
                    '0-2-0': { nextRound: 0, newActivePlayer: 2, nextTurn: 1 },
                    '0-2-1': { nextRound: 0, newActivePlayer: 3, nextTurn: 3 },
                    '0-3-3': { nextRound: 0, newActivePlayer: 3, nextTurn: 0 },
                    '0-3-0': { nextRound: 0, newActivePlayer: 3, nextTurn: 1 },
                    '0-3-1': { nextRound: 0, newActivePlayer: 3, nextTurn: 2 },
                    '0-3-2': { nextRound: 1, newActivePlayer: 1, nextTurn: 1 },
                    '1-1-1': { nextRound: 1, newActivePlayer: 1, nextTurn: 2 },
                    '1-1-2': { nextRound: 1, newActivePlayer: 1, nextTurn: 3 },
                    '1-1-3': { nextRound: 1, newActivePlayer: 1, nextTurn: 0 },
                    '1-1-0': { nextRound: 1, newActivePlayer: 2, nextTurn: 2 },
                    '1-2-2': { nextRound: 1, newActivePlayer: 2, nextTurn: 3 },
                    '1-2-3': { nextRound: 1, newActivePlayer: 2, nextTurn: 0 },
                    '1-2-0': { nextRound: 1, newActivePlayer: 2, nextTurn: 1 },
                    '1-2-1': { nextRound: 1, newActivePlayer: 3, nextTurn: 3 },
                    '1-3-3': { nextRound: 1, newActivePlayer: 3, nextTurn: 0 },
                    '1-3-0': { nextRound: 1, newActivePlayer: 3, nextTurn: 1 },
                    '1-3-1': { nextRound: 1, newActivePlayer: 3, nextTurn: 2 },
                    '1-3-2': { nextRound: 1, newActivePlayer: 0, nextTurn: 0 },
                    '1-0-0': { nextRound: 1, newActivePlayer: 0, nextTurn: 1 },
                    '1-0-1': { nextRound: 1, newActivePlayer: 0, nextTurn: 2 },
                    '1-0-2': { nextRound: 1, newActivePlayer: 0, nextTurn: 3 },
                    '1-0-3': { nextRound: 2, newActivePlayer: 2, nextTurn: 2 },
                    '2-2-2': { nextRound: 2, newActivePlayer: 2, nextTurn: 3 },
                    '2-2-3': { nextRound: 2, newActivePlayer: 2, nextTurn: 0 },
                    '2-2-0': { nextRound: 2, newActivePlayer: 2, nextTurn: 1 },
                    '2-2-1': { nextRound: 2, newActivePlayer: 3, nextTurn: 3 },
                    '2-3-3': { nextRound: 2, newActivePlayer: 3, nextTurn: 0 },
                    '2-3-0': { nextRound: 2, newActivePlayer: 3, nextTurn: 1 },
                    '2-3-1': { nextRound: 2, newActivePlayer: 3, nextTurn: 2 },
                    '2-3-2': { nextRound: 2, newActivePlayer: 0, nextTurn: 0 },
                    '2-0-0': { nextRound: 2, newActivePlayer: 0, nextTurn: 1 },
                    '2-0-1': { nextRound: 2, newActivePlayer: 0, nextTurn: 2 },
                    '2-0-2': { nextRound: 2, newActivePlayer: 0, nextTurn: 3 },
                    '2-0-3': { nextRound: 2, newActivePlayer: 1, nextTurn: 1 },
                    '2-1-1': { nextRound: 2, newActivePlayer: 1, nextTurn: 2 },
                    '2-1-2': { nextRound: 2, newActivePlayer: 1, nextTurn: 3 },
                    '2-1-3': { nextRound: 2, newActivePlayer: 1, nextTurn: 0 },
                    '2-1-0': { nextRound: 3, newActivePlayer: 3, nextTurn: 3 },
                    '3-3-3': { nextRound: 3, newActivePlayer: 3, nextTurn: 0 },
                    '3-3-0': { nextRound: 3, newActivePlayer: 3, nextTurn: 1 },
                    '3-3-1': { nextRound: 3, newActivePlayer: 3, nextTurn: 2 },
                    '3-3-2': { nextRound: 3, newActivePlayer: 0, nextTurn: 0 },
                    '3-0-0': { nextRound: 3, newActivePlayer: 0, nextTurn: 1 },
                    '3-0-1': { nextRound: 3, newActivePlayer: 0, nextTurn: 2 },
                    '3-0-2': { nextRound: 3, newActivePlayer: 0, nextTurn: 3 },
                    '3-0-3': { nextRound: 3, newActivePlayer: 1, nextTurn: 1 },
                    '3-1-1': { nextRound: 3, newActivePlayer: 1, nextTurn: 2 },
                    '3-1-2': { nextRound: 3, newActivePlayer: 1, nextTurn: 3 },
                    '3-1-3': { nextRound: 3, newActivePlayer: 1, nextTurn: 0 },
                    '3-1-0': { nextRound: 3, newActivePlayer: 2, nextTurn: 2 },
                    '3-2-2': { nextRound: 3, newActivePlayer: 2, nextTurn: 3 },
                    '3-2-3': { nextRound: 3, newActivePlayer: 2, nextTurn: 0 },
                    '3-2-0': { nextRound: 3, newActivePlayer: 2, nextTurn: 1 },
                    '3-2-1': { nextRound: 0, newActivePlayer: 0, nextTurn: 0 }
                };
            }

            const key = `${round}-${activePlayer}-${currentTurn}`;
            const transition = transitions[key];

            if (transition) {
                nextTurn = transition.nextTurn;
                newActivePlayer = transition.newActivePlayer;
                nextRound = transition.nextRound;
            }

            nextVersion = matchData.version;



            // Actualizacion del backend
            const response = await fetch(`/api/v1/matches/${matchId}`, {
                method: "PATCH",
                headers: {
                    Authorization: `Bearer ${jwt}`,
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    currentActivePlayer: newActivePlayer,
                    currentPlayerTurn: nextTurn,
                    dice: diceValues,
                    round: nextRound,
                    isLastRound: ultima,
                    territory: newTerritory,
                    version: nextVersion
                }),
            });

            if (response.status === 409) {
                throw new Error("Recarga la página para obtener los datos más recientes.");
            }

            // Refrescar datos de la partida para asegurarnos de que los estados se actualicen correctamente
            const updatedData = await response.json();
            setMatchData(updatedData); // Asegurarte de que el estado tenga los datos más recientes
            await fetchMatchData(); // Refrescar estados derivados como dices, etc.
            setShowTurnScreen(true);
            setShouldFetch(true);
            const puntos = calcularPuntuacion(tablero, criterios);
            enviarPuntuacion(puntos);
            setBloquearDado(false);
        } catch (error) {
            console.error("Error al avanzar el turno:", error);
        }
    };


    const handleHexClick = (q, r, s) => {
        console.log(`Click en q:${q}, r:${r}, s:${s}`);
        console.log("Numero de territorios a seleccionar:", numTerritorioASeleccionar);
        console.log("Numero de castillos disponibles:", numCastillosDisponibles);
        console.log("Numero de pueblos disponibles:", numPueblosDisponibles);
        console.log("Numero de rios disponibles:", numRiosDisponibles);
        console.log("Numero de bosques disponibles:", numBosquesDisponibles);
        console.log("Numero de montañas disponibles:", numMontañasDisponibles);
        console.log("Numero de praderas disponibles:", numPraderasDisponibles);
        setSelectedHex({ q, r, s });
        const criterioBusquedaLibre = (casilla) => !casilla.territorio;
        const libres = tablero.casillas.filter(criterioBusquedaLibre);
        console.log("Casillas libres:", libres);

        const gruposLibres = getTerritoryGroups(tablero, criterioBusquedaLibre);
        console.log("Grupos libres:", gruposLibres);
        const grupoMasGrande = gruposLibres.reduce((max, grupo) => Math.max(max, grupo.length), 0);
        console.log("Grupo más grande:", grupoMasGrande);
        if (numTerritorioASeleccionar === 0) {
            setNumTerritorioASeleccionar(0);
            setTerritoriosColocados(0);
            setSelectedHex(null);
            setPuedeSeleccionarTerritorios(false);
            setEsPrimerTerritorioDelTurno(true);
            setBloquearTerritorio(false);
        }
        if (numTerritorioASeleccionar > 0 && numTerritorioASeleccionar > grupoMasGrande) {
            alert("No se pueden asignar todos los territorios: activando última ronda, pase turno para que sus compañeros terminen la ronda.");
            setPuedeSeleccionarTerritorios(false);
            if (!ultimaRonda) {
                setUltimaRonda(true);
            }
            console.log("Ultima ronda:", ultimaRonda);
        }
    };

    const isNeighborOccupied = (q, r, s) => {
        const neighbors = [
            { q: q + 1, r: r - 1, s: s },
            { q: q - 1, r: r + 1, s: s },
            { q: q, r: r - 1, s: s + 1 },
            { q: q, r: r + 1, s: s - 1 },
            { q: q + 1, r: r, s: s - 1 },
            { q: q - 1, r: r, s: s + 1 },
        ];

        return neighbors.some((neighbor) =>
            tablero.casillas.some((casilla) =>
                casilla.q === neighbor.q && casilla.r === neighbor.r && casilla.s === neighbor.s && casilla.territorio
            )
        );
    };

    const isNeighborOfLastPlaced = (q, r, s) => {
        if (!ultimoTerritorioColocado) return true; // Permitir si no hay último territorio colocado

        const neighbors = [
            { q: ultimoTerritorioColocado.q + 1, r: ultimoTerritorioColocado.r - 1, s: ultimoTerritorioColocado.s },
            { q: ultimoTerritorioColocado.q - 1, r: ultimoTerritorioColocado.r + 1, s: ultimoTerritorioColocado.s },
            { q: ultimoTerritorioColocado.q, r: ultimoTerritorioColocado.r - 1, s: ultimoTerritorioColocado.s + 1 },
            { q: ultimoTerritorioColocado.q, r: ultimoTerritorioColocado.r + 1, s: ultimoTerritorioColocado.s - 1 },
            { q: ultimoTerritorioColocado.q + 1, r: ultimoTerritorioColocado.r, s: ultimoTerritorioColocado.s - 1 },
            { q: ultimoTerritorioColocado.q - 1, r: ultimoTerritorioColocado.r, s: ultimoTerritorioColocado.s + 1 },
        ];

        return neighbors.some((neighbor) => neighbor.q === q && neighbor.r === r && neighbor.s === s);
    };

    const handlePowerInterrogation = (casilla) => {
        setTablero((prevTablero) => {
            const updatedCasillas = prevTablero.casillas.map((c) =>
                c.q === casilla.q && c.r === casilla.r && c.s === casilla.s
                    ? { ...c, territorio: selectedTerritory }
                    : c
            );

            const updatedTablero = { ...prevTablero, casillas: updatedCasillas };

            const puntuacionActual = calcularPuntuacion(updatedTablero, criterios);
            setPuntuacionInterrogacion(puntuacionActual);

            console.log("Puntuación Interrogación calculada:", puntuacionActual);

            saveBoard(updatedTablero);
            return updatedTablero;
        });

        setModalPoderInterrogacionVisible(true);
    };

    const handleAssignTerritory = () => {
        if (!selectedHex || !selectedTerritory || !puedeSeleccionarTerritorios) return;

        // Verificar si la casilla ya tiene un territorio
        const casillaSeleccionada = tablero.casillas.find(
            (casilla) => casilla.q === selectedHex.q && casilla.r === selectedHex.r && casilla.s === selectedHex.s
        );

        if (casillaSeleccionada && casillaSeleccionada.territorio) {
            alert("No puedes colocar un territorio en una casilla que ya tiene un territorio.");
            return;
        }

        //Restricciones de adyacendcia
        const esAdyacente = esPrimerTerritorioDelTurno
            ? isNeighborOccupied(selectedHex.q, selectedHex.r, selectedHex.s)
            : isNeighborOfLastPlaced(selectedHex.q, selectedHex.r, selectedHex.s);

        if (esAdyacente || tablero.casillas.every((casilla) => !casilla.territorio)) {
            setTablero((prevTablero) => {
                const updatedCasillas = prevTablero.casillas.map((casilla) => {
                    if (casilla.q === selectedHex.q && casilla.r === selectedHex.r && casilla.s === selectedHex.s) {
                        if (casilla.tipoCasilla === "POWER_PLUSMINUS" && !(numTerritorioASeleccionar === 1)) {
                            setShowModal(true);
                        }
                        if (casilla.tipoCasilla === "POWER_INTERROGATION") {
                            handlePowerInterrogation(casilla);
                        }
                        return { ...casilla, territorio: selectedTerritory };
                    }
                    return casilla;
                });

                const updatedTablero = { ...prevTablero, casillas: updatedCasillas };

                saveBoard(updatedTablero);

                return updatedTablero;
            });

            setNumTerritorioASeleccionar(numTerritorioASeleccionar - 1);
            console.log("Territorios restantes:", numTerritorioASeleccionar);
            setTerritoriosColocados(territoriosColocados + 1);
            setUltimoTerritorioColocado({ q: selectedHex.q, r: selectedHex.r, s: selectedHex.s });
            console.log("poder+-:", tablero.casillas.tipoCasilla === "POWER_PLUSMINUS");

            if (numTerritorioASeleccionar <= 1 || (numTerritorioASeleccionar === 0 && tablero.casillas.tipoCasilla === "POWER_PLUSMINUS")) {
                setNumTerritorioASeleccionar(0);
                setTerritoriosColocados(0);
                setSelectedHex(null);
                setPuedeSeleccionarTerritorios(false);
                setEsPrimerTerritorioDelTurno(true);
                setBloquearTerritorio(false);
            } else {
                setBloquearTerritorio(true);
                setEsPrimerTerritorioDelTurno(false); // Ya no es el primer territorio del turno
            }
        } else {
            //si es el primero en colocar es a un territorio adyacente cualquiera, sino al último colocado
            alert("Debes seleccionar una casilla adyacente a un territorio ya asignado o al último colocado.");
        }
    };

    const handleTerritoryChange = (e) => {
        const selected = e.target.value;
        if (selected === "pradera") {
            if (numPraderasDisponibles <= 0) {
                alert("No puedes elegir más praderas");
                return;
            }
            console.log("Antes de restar praderas:", numPraderasDisponibles);
            setNumPraderasDisponibles((prev) => {
                console.log("Después de restar praderas:", prev - 1);
                return prev - 1;
            });
            setSelectedTerritory(selected);
            setBloquearTerritorio(true);
        } else if (selected === "montaña") {
            if (numMontañasDisponibles <= 0) {
                alert("No puedes elegir más montañas");
                return;
            }
            console.log("Antes de restar montañas:", numMontañasDisponibles);
            setNumMontañasDisponibles((prev) => {
                console.log("Después de restar montañas:", prev - 1);
                return prev - 1;
            });
            setSelectedTerritory(selected);
            setBloquearTerritorio(true);
        } else if (selected === "bosque") {
            if (numBosquesDisponibles <= 0) {
                alert("No puedes elegir más bosques");
                return;
            }
            console.log("Antes de restar bosques:", numBosquesDisponibles);
            setNumBosquesDisponibles((prev) => {
                console.log("Después de restar bosques:", prev - 1);
                return prev - 1;
            });
            setSelectedTerritory(selected);
            setBloquearTerritorio(true);
        } else if (selected === "rio") {
            if (numRiosDisponibles <= 0) {
                alert("No puedes elegir más ríos");
                return;
            }
            console.log("Antes de restar ríos:", numRiosDisponibles);
            setNumRiosDisponibles((prev) => {
                console.log("Después de restar ríos:", prev - 1);
                return prev - 1;
            });
            setSelectedTerritory(selected);
            setBloquearTerritorio(true);
        } else if (selected === "castillo") {
            if (numCastillosDisponibles <= 0) {
                alert("No puedes elegir más castillos");
                return;
            }
            console.log("Antes de restar castillos:", numCastillosDisponibles);
            setNumCastillosDisponibles((prev) => {
                console.log("Después de restar castillos:", prev - 1);
                return prev - 1;
            });
            setSelectedTerritory(selected);
            setBloquearTerritorio(true);
        } else if (selected === "pueblo") {
            if (numPueblosDisponibles <= 0) {
                alert("No puedes elegir más pueblos");
                return;
            }
            console.log("Antes de restar pueblos:", numPueblosDisponibles);
            setNumPueblosDisponibles((prev) => {
                console.log("Después de restar pueblos:", prev - 1);
                return prev - 1;
            });
            setSelectedTerritory(selected);
            setBloquearTerritorio(true);
        }
        setSelectedTerritory(selected);
        setBloquearTerritorio(true);
    };

    useEffect(() => {
        if (ultimaRonda && !alertShown) {
            alert('¡Estamos en la última ronda!');
            setAlertShown(true);
        }
    }, [ultimaRonda, alertShown]);

    async function enviarPuntuacion(puntuacion) {
        try {
            if (puntuacionInterrogacion > 0) {
                puntuacion += puntuacionInterrogacion;
            }
            const getBoardIdResponse = await fetch(`/api/v1/boards`, {
                method: 'GET',
                headers: {
                    Authorization: `Bearer ${jwt}`,
                    "Content-Type": "application/json"
                },
            });
            if (getBoardIdResponse.ok) {
                const boardData = await getBoardIdResponse.json();
                const playerBoard = boardData.find(board => board.player.id == playerId);
                if (playerBoard) {
                    const boardId = playerBoard.id;
                    const response = await fetch(`/api/v1/boards/${boardId}/score`, {
                        method: 'PUT',
                        headers: {
                            Authorization: `Bearer ${jwt}`,
                            'Content-Type': 'application/json',
                        },
                        body: JSON.stringify({ score: puntuacion }),
                    });

                    if (!response.ok) {
                        throw new Error('Error al enviar la puntuación');
                    }
                    const updatedBoard = await response.json();
                    console.log('Puntuación inter:', puntuacionInterrogacion);
                    console.log('Puntuación actualizada:', updatedBoard);
                }
            }
        } catch (error) {
            console.error(error);
        }
    }

    const obtenerGanadores = async () => {
        try {
            const response = await fetch(`/api/v1/matches/${matchId}/winners`, {
                method: 'GET',
                headers: {
                    Authorization: `Bearer ${jwt}`,
                    'Content-Type': 'application/json',
                },
            });
            if (!response.ok) {
                throw new Error('Error al obtener los ganadores');
            }

            const winners = await response.json();
            console.log('Ganadores:', winners);
            setGanadores(winners);
        } catch (error) {
            console.error('Error al intentar obtener los ganadores:', error);
            setGanadores([]);
        }
    };

    const finalizarPartida = async () => {
        localStorage.removeItem("tablero");
        const basicTablero = {
            casillas: generateHexagonCoordinates(4),
        };
        setTablero(basicTablero);
        saveBoard(basicTablero);
        try {
            const getBoardIdResponse = await fetch(`/api/v1/boards`, {
                method: 'GET',
                headers: {
                    Authorization: `Bearer ${jwt}`,
                    "Content-Type": "application/json"
                },
            });

            if (getBoardIdResponse.ok) {
                const boardData = await getBoardIdResponse.json();
                const playerBoard = boardData.find(board => board.player.id == playerId);
                if (playerBoard) {
                    const boardId = playerBoard.id;
                    const deleteBoardResponse = await fetch(`/api/v1/boards/${boardId}`, {
                        method: 'DELETE',
                        headers: {
                            Authorization: `Bearer ${jwt}`,
                            "Content-Type": "application/json"
                        },
                    });

                    if (deleteBoardResponse.ok) {
                        const response = await fetch(`/api/v1/matches/${matchId}/finish`, {
                            method: 'PUT',
                            headers: {
                                Authorization: `Bearer ${jwt}`,
                                'Content-Type': 'application/json',
                            },
                        });

                        if (!response.ok) {
                            if (response.status === 404) {
                                throw new Error('Match no encontrado');
                            } else if (response.status === 400) {
                                throw new Error('Error en los datos enviados');
                            } else {
                                throw new Error('Error interno del servidor');
                            }
                        } else {
                            console.log('Partida finalizada correctamente');
                            navigate('/');
                        }
                    }
                }
            }
        } catch (error) {
            console.error('Error al intentar finalizar la partida:', error);
        }
    };

    useEffect(() => {
        const temporizador = setInterval(() => {
            setTiempoRestanteNoActivo((prevTime) => {
                if (!isCurrentActivePlayer && isCurrentPlayerTurn) {
                    if (prevTime === 0) {
                        abandonarPartida();
                        alert("Por inactividad, se te ha expulsado de la partida.");
                    } else {
                        return prevTime - 1;
                    }
                } else {
                    return tiempoRestanteNoActivo;
                }
            });
        }, 1000);
        return () => clearInterval(temporizador);
    }, [isCurrentPlayerTurn, abandonarPartida, isCurrentActivePlayer]);

    const handleShowModal = async () => {
        await obtenerGanadores(); // Espera a que se resuelva la promesa
        console.log("Ganadores:", ganadores);
        setIsModalVisible(true); // Muestra el modal
    };

    useEffect(() => {
        const temporizador = setInterval(() => {
            setTiempoRestanteActivo((prevTime) => {
                if (isCurrentActivePlayer && isCurrentPlayerTurn) {
                    if (prevTime === 0) {
                        abandonarPartida();
                        alert("Por inactividad, se te ha expulsado de la partida.");
                    } else {
                        return prevTime - 1;
                    }
                } else {
                    return tiempoRestanteActivo;
                }
            });
        }, 1000);

        return () => clearInterval(temporizador);
    }, [isCurrentPlayerTurn, abandonarPartida, isCurrentActivePlayer]);

    useEffect(() => {
        const interval = setInterval(async () => {
            try {
                const res = await fetch(`/api/v1/matches/${matchId}`, {
                    headers: { Authorization: `Bearer ${jwt}` },
                });
                if (res.status === 404) {
                    alert("La partida fue eliminada. Redirigiendo al lobby...");
                    abandonarPartida();
                    return;
                }
                const data = await res.json();
                console.log("Estado de la partida:", data);
                if (data.players?.length === 1) {
                    alert("El otro jugador ha abandonado la partida. Redirigiendo al lobby...");
                    abandonarPartida();
                }
            } catch (error) {
                console.error("Error al verificar el estado de la partida:", error);
            }
        }, 5000);
        return () => clearInterval(interval);
    }, [matchId, jwt, navigate]);

    return (
        <div className="board-container">
            {modal}
            <div className="game-board">
                <HexGrid width={750} height={700} viewBox="-50 -50 100 100">
                    <Layout
                        size={{ x: 6, y: 6 }}
                        flat={false}
                        spacing={1.1}
                        origin={{ x: 0, y: 0 }}
                    >
                        {tablero && tablero.casillas.map((casilla, i) => (
                            <Hexagon
                                key={i}
                                q={casilla.q}
                                s={casilla.s}
                                r={casilla.r}
                                className={`hexagon ${casilla.territorio ? `territory-${casilla.territorio}` : ""}`}
                                onMouseEnter={() => setHoveredHex(i)}
                                onMouseLeave={() => setHoveredHex(null)}
                                onClick={() => handleHexClick(casilla.q, casilla.r, casilla.s)}
                            >
                                {(casilla.tipoCasilla !== "EMPTY" || casilla.territorio) && (
                                    <g>
                                        {casilla.territorio && (
                                            <image
                                                href={territoryImages[casilla.territorio]}
                                                x="-5.5"
                                                y="-5.5"
                                                width="11"
                                                height="11"
                                                //adapta la imagen al hexágono
                                                clipPath="polygon(50% 0%, 100% 25%, 100% 75%, 50% 100%, 0% 75%, 0% 25%)"
                                            />
                                        )}
                                        {casilla.tipoCasilla === "POWER_INTERROGATION" && (
                                            <Text className="hexagon-text">❓</Text>
                                        )}
                                        {casilla.tipoCasilla === "POWER_PLUSMINUS" && (
                                            <Text className="hexagon-text">+1 / -1</Text>
                                        )}
                                    </g>
                                )}
                            </Hexagon>
                        ))}
                    </Layout>
                </HexGrid>
            </div>

            {showTurnScreen && isCurrentPlayerTurn && (
                <div className="turn-screen">
                    <h2>¡Es tu turno!</h2>
                    <button onClick={handleStartTurn}>¡Comenzar!</button>
                    <p>TERRITORIO A DIBUJAR: {selectedTerritory}</p>
                </div>
            )}

            {!showTurnScreen && isCurrentPlayerTurn && isCurrentActivePlayer && (
                <div className='dados'>
                    <div className='elements-container'>
                        <div className='imagenes'>
                            {/* Dado 1 */}
                            {isRolling1 ? (
                                <img src={dice_rolling} alt="Rolling" className='elemento' />
                            ) : (
                                <>
                                    {(dado1 === null || dado1 === 1) && (
                                        <img src={dice1} alt="Dado 1" onClick={lanzarDado1} className='elemento' />
                                    )}
                                    {dado1 === 2 && (
                                        <img src={dice2} alt="Dado 1" onClick={lanzarDado1} className='elemento' />
                                    )}
                                    {dado1 === 3 && (
                                        <img src={dice3} alt="Dado 1" onClick={lanzarDado1} className='elemento' />
                                    )}
                                    {dado1 === 4 && (
                                        <img src={dice4} alt="Dado 1" onClick={lanzarDado1} className='elemento' />
                                    )}
                                    {dado1 === 5 && (
                                        <img src={dice5} alt="Dado 1" onClick={lanzarDado1} className='elemento' />
                                    )}
                                    {dado1 === 6 && (
                                        <img src={dice6} alt="Dado 1" onClick={lanzarDado1} className='elemento' />
                                    )}
                                </>
                            )}

                            {/* Dado 2 */}
                            {isRolling2 ? (
                                <img src={dice_rolling} alt="Rolling" className='elemento' />
                            ) : (
                                <>
                                    {(dado2 === null || dado2 === 1) && (
                                        <img src={dice1} alt="Dado 2" onClick={lanzarDado2} className='elemento' />
                                    )}
                                    {dado2 === 2 && (
                                        <img src={dice2} alt="Dado 2" onClick={lanzarDado2} className='elemento' />
                                    )}
                                    {dado2 === 3 && (
                                        <img src={dice3} alt="Dado 2" onClick={lanzarDado2} className='elemento' />
                                    )}
                                    {dado2 === 4 && (
                                        <img src={dice4} alt="Dado 2" onClick={lanzarDado2} className='elemento' />
                                    )}
                                    {dado2 === 5 && (
                                        <img src={dice5} alt="Dado 2" onClick={lanzarDado2} className='elemento' />
                                    )}
                                    {dado2 === 6 && (
                                        <img src={dice6} alt="Dado 2" onClick={lanzarDado2} className='elemento' />
                                    )}
                                </>
                            )}

                            {/* Dado 3 */}
                            {isRolling3 ? (
                                <img src={dice_rolling} alt="Rolling" className='elemento' />
                            ) : (
                                <>
                                    {(dado3 === null || dado3 === 1) && (
                                        <img src={dice1} alt="Dado 3" onClick={lanzarDado3} className='elemento' />
                                    )}
                                    {dado3 === 2 && (
                                        <img src={dice2} alt="Dado 3" onClick={lanzarDado3} className='elemento' />
                                    )}
                                    {dado3 === 3 && (
                                        <img src={dice3} alt="Dado 3" onClick={lanzarDado3} className='elemento' />
                                    )}
                                    {dado3 === 4 && (
                                        <img src={dice4} alt="Dado 3" onClick={lanzarDado3} className='elemento' />
                                    )}
                                    {dado3 === 5 && (
                                        <img src={dice5} alt="Dado 3" onClick={lanzarDado3} className='elemento' />
                                    )}
                                    {dado3 === 6 && (
                                        <img src={dice6} alt="Dado 3" onClick={lanzarDado3} className='elemento' />
                                    )}
                                </>
                            )}

                            {/* Dado 4 (se muestra si length >= 3) */}
                            {length >= 3 && (
                                isRolling4 ? (
                                    <img src={dice_rolling} alt="Rolling" className='elemento' />
                                ) : (
                                    <>
                                        {(dado4 === null || dado4 === 1) && (
                                            <img src={dice1} alt="Dado 4" onClick={lanzarDado4} className='elemento' />
                                        )}
                                        {dado4 === 2 && (
                                            <img src={dice2} alt="Dado 4" onClick={lanzarDado4} className='elemento' />
                                        )}
                                        {dado4 === 3 && (
                                            <img src={dice3} alt="Dado 4" onClick={lanzarDado4} className='elemento' />
                                        )}
                                        {dado4 === 4 && (
                                            <img src={dice4} alt="Dado 4" onClick={lanzarDado4} className='elemento' />
                                        )}
                                        {dado4 === 5 && (
                                            <img src={dice5} alt="Dado 4" onClick={lanzarDado4} className='elemento' />
                                        )}
                                        {dado4 === 6 && (
                                            <img src={dice6} alt="Dado 4" onClick={lanzarDado4} className='elemento' />
                                        )}
                                    </>
                                )
                            )}

                            {/* Dado 5 (se muestra si length === 4) */}
                            {length === 4 && (
                                isRolling5 ? (
                                    <img src={dice_rolling} alt="Rolling" className='elemento' />
                                ) : (
                                    <>
                                        {(dado5 === null || dado5 === 1) && (
                                            <img src={dice1} alt="Dado 5" onClick={lanzarDado5} className='elemento' />
                                        )}
                                        {dado5 === 2 && (
                                            <img src={dice2} alt="Dado 5" onClick={lanzarDado5} className='elemento' />
                                        )}
                                        {dado5 === 3 && (
                                            <img src={dice3} alt="Dado 5" onClick={lanzarDado5} className='elemento' />
                                        )}
                                        {dado5 === 4 && (
                                            <img src={dice4} alt="Dado 5" onClick={lanzarDado5} className='elemento' />
                                        )}
                                        {dado5 === 5 && (
                                            <img src={dice5} alt="Dado 5" onClick={lanzarDado5} className='elemento' />
                                        )}
                                        {dado5 === 6 && (
                                            <img src={dice6} alt="Dado 5" onClick={lanzarDado5} className='elemento' />
                                        )}
                                    </>
                                )
                            )}
                        </div>
                    </div>
                </div>
            )}

            {!showTurnScreen && isCurrentPlayerTurn && !isCurrentActivePlayer && (
                <div className='dados'>
                    <div className='elements-container'>
                        <div className='imagenes'>
                            {/* Dado 1 */}
                            {isRolling1 ? (
                                <img src={dice_rolling} alt="Rolling" className='elemento' />
                            ) : (
                                <>
                                    {dices[0] === 1 && (
                                        <img src={dice1} alt="Dado 1" onClick={escogerDado1} className='elemento' />
                                    )}
                                    {dices[0] === 2 && (
                                        <img src={dice2} alt="Dado 1" onClick={escogerDado1} className='elemento' />
                                    )}
                                    {dices[0] === 3 && (
                                        <img src={dice3} alt="Dado 1" onClick={escogerDado1} className='elemento' />
                                    )}
                                    {dices[0] === 4 && (
                                        <img src={dice4} alt="Dado 1" onClick={escogerDado1} className='elemento' />
                                    )}
                                    {dices[0] === 5 && (
                                        <img src={dice5} alt="Dado 1" onClick={escogerDado1} className='elemento' />
                                    )}
                                    {dices[0] === 6 && (
                                        <img src={dice6} alt="Dado 1" onClick={escogerDado1} className='elemento' />
                                    )}
                                </>
                            )}

                            {/* Dado 2 */}
                            {isRolling2 ? (
                                <img src={dice_rolling} alt="Rolling" className='elemento' />
                            ) : (
                                <>
                                    {dices[1] === 1 && (
                                        <img src={dice1} alt="Dado 2" onClick={escogerDado2} className='elemento' />
                                    )}
                                    {dices[1] === 2 && (
                                        <img src={dice2} alt="Dado 2" onClick={escogerDado2} className='elemento' />
                                    )}
                                    {dices[1] === 3 && (
                                        <img src={dice3} alt="Dado 2" onClick={escogerDado2} className='elemento' />
                                    )}
                                    {dices[1] === 4 && (
                                        <img src={dice4} alt="Dado 2" onClick={escogerDado2} className='elemento' />
                                    )}
                                    {dices[1] === 5 && (
                                        <img src={dice5} alt="Dado 2" onClick={escogerDado2} className='elemento' />
                                    )}
                                    {dices[1] === 6 && (
                                        <img src={dice6} alt="Dado 2" onClick={escogerDado2} className='elemento' />
                                    )}
                                </>
                            )}

                            {/* Dado 3 */}
                            {isRolling3 ? (
                                <img src={dice_rolling} alt="Rolling" className='elemento' />
                            ) : (
                                <>
                                    {(dices[2] === null || dices[2] === 1) && (
                                        <img src={dice1} alt="Dado 3" onClick={escogerDado3} className='elemento' />
                                    )}
                                    {dices[2] === 2 && (
                                        <img src={dice2} alt="Dado 3" onClick={escogerDado3} className='elemento' />
                                    )}
                                    {dices[2] === 3 && (
                                        <img src={dice3} alt="Dado 3" onClick={escogerDado3} className='elemento' />
                                    )}
                                    {dices[2] === 4 && (
                                        <img src={dice4} alt="Dado 3" onClick={escogerDado3} className='elemento' />
                                    )}
                                    {dices[2] === 5 && (
                                        <img src={dice5} alt="Dado 3" onClick={escogerDado3} className='elemento' />
                                    )}
                                    {dices[2] === 6 && (
                                        <img src={dice6} alt="Dado 3" onClick={escogerDado3} className='elemento' />
                                    )}
                                </>
                            )}

                            {/* Dado 4 (se muestra si length >= 3) */}
                            {length >= 3 && (
                                isRolling4 ? (
                                    <img src={dice_rolling} alt="Rolling" className='elemento' />
                                ) : (
                                    <>
                                        {dices[3] === 1 && (
                                            <img src={dice1} alt="Dado 4" onClick={escogerDado4} className='elemento' />
                                        )}
                                        {dices[3] === 2 && (
                                            <img src={dice2} alt="Dado 4" onClick={escogerDado4} className='elemento' />
                                        )}
                                        {dices[3] === 3 && (
                                            <img src={dice3} alt="Dado 4" onClick={escogerDado4} className='elemento' />
                                        )}
                                        {dices[3] === 4 && (
                                            <img src={dice4} alt="Dado 4" onClick={escogerDado4} className='elemento' />
                                        )}
                                        {dices[3] === 5 && (
                                            <img src={dice5} alt="Dado 4" onClick={escogerDado4} className='elemento' />
                                        )}
                                        {dices[3] === 6 && (
                                            <img src={dice6} alt="Dado 4" onClick={escogerDado4} className='elemento' />
                                        )}
                                    </>
                                )
                            )}

                            {/* Dado 5 (se muestra si length === 4) */}
                            {length === 4 && (
                                isRolling5 ? (
                                    <img src={dice_rolling} alt="Rolling" className='elemento' />
                                ) : (
                                    <>
                                        {(dices[4] === null || dices[4] === 1) && (
                                            <img src={dice1} alt="Dado 5" onClick={escogerDado5} className='elemento' />
                                        )}
                                        {dices[4] === 2 && (
                                            <img src={dice2} alt="Dado 5" onClick={escogerDado5} className='elemento' />
                                        )}
                                        {dices[4] === 3 && (
                                            <img src={dice3} alt="Dado 5" onClick={escogerDado5} className='elemento' />
                                        )}
                                        {dices[4] === 4 && (
                                            <img src={dice4} alt="Dado 5" onClick={escogerDado5} className='elemento' />
                                        )}
                                        {dices[4] === 5 && (
                                            <img src={dice5} alt="Dado 5" onClick={escogerDado5} className='elemento' />
                                        )}
                                        {dices[4] === 6 && (
                                            <img src={dice6} alt="Dado 5" onClick={escogerDado5} className='elemento' />
                                        )}
                                    </>
                                )
                            )}
                        </div>
                    </div>
                </div>
            )}

            <div style={{ textAlign: 'center', marginTop: '20px' }}>
                <p style={{ color: 'white', marginBottom: '5px' }}>Criterios: {criteriaA1}, {criteriaA2}, {criteriaB1}, {criteriaB2}</p>
                {isCurrentPlayerTurn && !isCurrentActivePlayer && (<p style={{ color: 'white', marginBottom: '5px' }}>Tiempo restante: {tiempoRestanteNoActivo}</p>)}
                {isCurrentPlayerTurn && isCurrentActivePlayer && (<p style={{ color: 'white', marginBottom: '5px' }}>Tiempo restante: {tiempoRestanteActivo}</p>)}
                {isCurrentPlayerTurn && isCurrentActivePlayer && (<p style={{ color: 'red', marginBottom: '5px' }}>AVISO: SI SE ACABA EL TIEMPO FINALIZARÁ LA PARTIDA</p>)}
            </div>

            <div className="form-container">
                <form
                    onSubmit={(e) => {
                        e.preventDefault();
                        handleAssignTerritory();
                    }}
                >
                    {isCurrentActivePlayer && (
                        <label>
                            Territorio:
                            <select
                                value={selectedTerritory}
                                onChange={handleTerritoryChange}
                                disabled={!puedeSeleccionarTerritorios || bloquearTerritorio || !isCurrentActivePlayer}
                            >
                                <option value="" disabled>
                                    Selecciona un territorio
                                </option>
                                <option value="bosque">Bosque</option>
                                <option value="rio">Río</option>
                                <option value="pueblo">Pueblo</option>
                                <option value="montaña">Montaña</option>
                                <option value="pradera">Pradera</option>
                                <option value="castillo">Castillo</option>
                            </select>
                        </label>
                    )}
                    <button type="submit" disabled={!puedeSeleccionarTerritorios}>Asignar Territorio</button>
                </form>

            </div>
            <div
                className={`button-container ${numTerritorioASeleccionar !== null ? "two-buttons" : "single-button"
                    }`}
            >
                {(numTerritorioASeleccionar !== null && numTerritorioASeleccionar === 0 && !ultimaRonda) || ultimaRonda ? (
                    <button className="game-button" onClick={nextTurn}>
                        Pasar turno
                    </button>
                ) : null}

                <button className="game-button" onClick={abandonarPartida}>
                    Abandonar Partida
                </button>
                {ultimaRonda && ultimoTurno && (
                    <button className="game-button" onClick={() => handleShowModal(true)}>
                        Finalizar Partida
                    </button>
                )}

            </div>
            {showModal && (
                <Modal
                    isOpen={showModal}
                    onClose={() => setShowModal(false)}
                    onConfirm={(value) => {
                        setNumTerritorioASeleccionar((prev) => Math.max(0, prev + value));
                        setShowModal(false);
                    }}
                />
            )}
            {modalPoderInterrogacionVisible && (
                <ModalPoderInterrogacion
                    isVisible={modalPoderInterrogacionVisible}
                    onClose={() => setModalPoderInterrogacionVisible(false)}
                    message={`${puntuacionInterrogacion} puntos`}
                />)}
            {isModalVisible && (
                <WinnerModal
                    isVisible={isModalVisible}
                    onClose={() => { setIsModalVisible(false); finalizarPartida(); }}
                    message={ganadores.length > 0 ? ganadores.map(w => w.user.username).join(', ') : "Sin ganadores"}
                    message2={() => {
                        const puntos = calcularPuntuacion(tablero, criterios) + puntuacionInterrogacion;
                        return `Tu puntuación ha sido de: ${puntos}`;
                    }}
                />
            )}
        </div>
    );
}