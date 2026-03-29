package es.us.dp1.l6_02_24_25.los_mapas_del_reino.board;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.l6_02_24_25.los_mapas_del_reino.exceptions.ResourceNotFoundException;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.match.Match;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.match.MatchRepository;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.player.Player;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.player.PlayerRepository;

@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final PlayerRepository playerRepository;
    private final MatchRepository matchRepository;

    @Autowired
    public BoardService(BoardRepository boardRepository, PlayerRepository playerRepository,
            MatchRepository matchRepository) {
        this.boardRepository = boardRepository;
        this.playerRepository = playerRepository;
        this.matchRepository = matchRepository;
    }

    @Transactional(readOnly = true)
    public List<Board> findAll() {
        return boardRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Board findBoardById(Integer id) throws DataAccessException {
        return boardRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Board", "ID", id));
    }

    @Transactional(readOnly = true)
    public List<Board> findBoardsByPlayerId(Integer id) {
        return boardRepository.findByPlayerId(id);
    }

    @Transactional(readOnly = true)
    public List<Board> findBoardsByMatchId(Integer id) {
        return boardRepository.findByMatchId(id);
    }

    @Transactional(readOnly = true)
    public Board findBoardByMatchIdAndUserId(Integer matchId, Integer userId) {
        if (!matchRepository.existsById(matchId)) {
            throw new ResourceNotFoundException("Match", "ID", matchId);
        }

        Integer playerId = playerRepository.findByUserId(userId).getId();
        return boardRepository.findByMatchIdAndPlayerId(matchId, playerId)
                .orElseThrow(() -> new ResourceNotFoundException("Board", "MatchId and PlayerId",
                        matchId + ", " + playerId));
    }

    @Transactional
    public Board updateBoard(Board board, Integer id) {
        Board boardToUpdate = findBoardById(id);
        BeanUtils.copyProperties(board, boardToUpdate, "id");
        return boardRepository.save(boardToUpdate);
    }

    @Transactional
    public Board createBoard(BoardRequest boardRequest) {
        try {
            Match match = matchRepository.findById(boardRequest.getMatchId())
                    .orElseThrow(() -> new ResourceNotFoundException("Partida no encontrada"));

            Player player = playerRepository.findById(boardRequest.getPlayerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Jugador no encontrado"));

            if (match.getCriteriaA1() == null || match.getCriteriaA2() == null ||
                    match.getCriteriaB1() == null || match.getCriteriaB2() == null) {

                match.setCriteriaA1(boardRequest.getCriteriaA1());
                match.setCriteriaA2(boardRequest.getCriteriaA2());
                match.setCriteriaB1(boardRequest.getCriteriaB1());
                match.setCriteriaB2(boardRequest.getCriteriaB2());

                matchRepository.save(match);
            }

            Board board = new Board();
            board.setMatch(match);
            board.setPlayer(player);
            board.setScore(0);

            return boardRepository.save(board);

        } catch (Exception e) {
            throw new RuntimeException("Error al crear el tablero", e);
        }
    }

    @Transactional
    public void deleteBoardById(Integer boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("Board", "ID", boardId));
        boardRepository.delete(board);
    }

    @Transactional
    public Board updateBoardScore(Integer boardId, Integer score) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("Board no encontrado"));
        board.setScore(score);
        return boardRepository.save(board);
    }
}
