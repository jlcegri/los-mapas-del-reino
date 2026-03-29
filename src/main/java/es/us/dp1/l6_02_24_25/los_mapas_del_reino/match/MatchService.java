package es.us.dp1.l6_02_24_25.los_mapas_del_reino.match;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import es.us.dp1.l6_02_24_25.los_mapas_del_reino.board.Board;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.board.BoardRepository;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.exceptions.ConflictException;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.player.Player;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.player.PlayerRepository;

@Service
public class MatchService {

    MatchRepository matchRepository;
    PlayerRepository playerRepository;
    BoardRepository boardRepository;

    @Autowired
    public MatchService(MatchRepository matchRepository, PlayerRepository playerRepository,
            BoardRepository boardRepository) {
        this.matchRepository = matchRepository;
        this.playerRepository = playerRepository;
        this.boardRepository = boardRepository;
    }

    @Transactional(readOnly = true)
    public List<Match> findAllMatches() {
        return matchRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Match> findMatchById(Integer id) {
        return matchRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Match> findMatchWithPlayersById(Integer id) {
        return matchRepository.findMatchWithPlayersById(id);
    }

    @Transactional(readOnly = true)
    public List<Match> findAllMatchesByPlayerId(Integer playerId) {
        return matchRepository.findAllMatchesByPlayerId(playerId);
    }

    @Transactional(readOnly = true)
    public List<Match> findCreatedMatches() {
        return matchRepository.findCreatedMatches();
    }

    @Transactional(readOnly = true)
    public List<Match> findStartedMatches() {
        return matchRepository.findStartedMatches();
    }

    @Transactional(readOnly = true)
    public List<Match> findFinishedMatches() {
        return matchRepository.findFinishedMatches();
    }

    @Transactional
    public Match save(Match m) {
        return matchRepository.save(m);
    }

    @Transactional
    public void delete(Integer id) {
        matchRepository.deleteById(id);
    }

    @Transactional
    public Match createMatch(Player player, MatchCreateRequest createRequest) {
        Match match;
        switch (createRequest.getMode()) {
            case STANDARD:
                match = MatchFactory.createStandardMatch(player);
                break;
            case SLOW:
                match = MatchFactory.createSlowMatch(player);
                break;
            case FAST:
                match = MatchFactory.createFastMatch(player);
                break;
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid match mode");
        }
        return save(match);
    }

    @Transactional
    public Match joinMatch(Integer matchId, Player player) {
        Match match = matchRepository.findById(matchId).orElseThrow();
        if (!match.getPlayers().contains(player) && match.getPlayers().size() < 4) {
            match.getPlayers().add(player);
        }
        return save(match);
    }

    @Transactional
    public Match removePlayerFromMatch(Integer matchId, Integer playerId) {
        Match match = matchRepository.findById(matchId).orElse(null);
        Player player = playerRepository.findById(playerId).orElse(null);

        if (match == null || player == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Match or player not found");
        }

        if (!match.getPlayers().contains(player)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Player not in match");
        }

        if (match.getPlayers().size() == 1) {
            matchRepository.delete(match);
            return null;
        }

        match.getPlayers().remove(player);

        if (match.getCreator().getId().equals(player.getId())) {
            match.setCreator(match.getPlayers().get(0));
        }

        Board board = boardRepository.findByMatchIdAndPlayerId(matchId, playerId).orElse(null);
        if (board != null) {
            boardRepository.delete(board);
        }

        return matchRepository.save(match);
    }

    @Transactional
    public void finishMatch(Integer matchId) {
        Match match = matchRepository.findById(matchId).orElseThrow();
        match.setState(MatchState.FINISHED);
        match.setFinishDate(LocalDateTime.now());
        match.setWinners(getWinners(matchId));
        matchRepository.save(match);
    }

    @Transactional
    public ResponseEntity<?> getMatchByIdWithAccessCheck(Integer matchId, Integer userId) {
        Optional<Match> matchOptional = matchRepository.findMatchWithPlayersById(matchId);

        if (matchOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Match match = matchOptional.get();
        boolean jugadorEnPartida = match.getPlayers().stream()
                .anyMatch(player -> player.getUser().getId().equals(userId));

        if (!jugadorEnPartida) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tienes permiso para acceder a esta partida");
        }

        return ResponseEntity.ok(match);
    }

    @Transactional
    public Match startMatch(Integer matchId) {
        Match match = matchRepository.findById(matchId).orElseThrow();
        match.setState(MatchState.STARTED);
        return save(match);
    }

    @Transactional
    public ResponseEntity<Void> handleFinishMatch(Integer matchId) {
        Optional<Match> matchOptional = matchRepository.findById(matchId);
        if (matchOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        try {
            finishMatch(matchId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Transactional(readOnly = true)
    public List<Match> getFilteredCreatedMatches() {
        return matchRepository.findCreatedMatches().stream()
                .filter(match -> !match.getPlayers().isEmpty())
                .toList();
    }

    @Transactional
    public ResponseEntity<Match> updateMatch(Integer matchId, MatchUpdateRequest updateRequest, Integer userId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Match not found"));

        boolean esJugador = match.getPlayers().stream().anyMatch(p -> p.getUser().getId().equals(userId));
        if (!esJugador) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        if (!match.getVersion().equals(updateRequest.getVersion())) {
            throw new ConflictException("Concurrent modification of Match! Reload first!");
        }

        try {
            if (updateRequest.getCurrentActivePlayer() != null) {
                match.setCurrentActivePlayer(updateRequest.getCurrentActivePlayer());
            }

            if (updateRequest.getCurrentPlayerTurn() != null) {
                match.setCurrentPlayerTurn(updateRequest.getCurrentPlayerTurn());
            }

            if (updateRequest.getDice() != null) {
                match.setDice(updateRequest.getDice());
            }

            if (updateRequest.getRound() != null) {
                match.setRound(updateRequest.getRound());
            }

            if (updateRequest.getIsLastRound() != null) {
                match.setIsLastRound(updateRequest.getIsLastRound());
            }

            if (updateRequest.getIsLastTurn() != null) {
                match.setIsLastTurn(updateRequest.getIsLastTurn());
            }

            if (updateRequest.getTerritory() != null) {
                match.setTerritory(updateRequest.getTerritory());
            }

            return ResponseEntity.ok(save(match));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Transactional
    public List<Player> getWinners(Integer matchId) {
        List<Board> boards = boardRepository.findByMatchId(matchId);
        if (boards.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No boards found for match ID " + matchId);
        }

        int maxScore = boards.stream()
                .mapToInt(Board::getScore)
                .max()
                .orElse(0);

        return boards.stream()
                .filter(b -> b.getScore() != null && b.getScore() == maxScore)
                .map(Board::getPlayer)
                .collect(Collectors.toList());
    }
}