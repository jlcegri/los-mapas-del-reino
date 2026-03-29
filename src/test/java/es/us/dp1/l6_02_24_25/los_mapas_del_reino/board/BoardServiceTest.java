package es.us.dp1.l6_02_24_25.los_mapas_del_reino.board;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.l6_02_24_25.los_mapas_del_reino.exceptions.ResourceNotFoundException;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.match.Match;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.match.MatchRepository;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.player.Player;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.player.PlayerRepository;

public class BoardServiceTest {

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private MatchRepository matchRepository;

    @InjectMocks
    private BoardService boardService;

    private Match testMatch;
    private Player testPlayer;
    private BoardRequest testBoardRequest;
    private Board testBoard;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        testPlayer = new Player();
        testPlayer.setId(1);
        testPlayer.setFirstName("John");
        testPlayer.setLastName("Doe");

        testMatch = new Match();
        testMatch.setId(1);
        testMatch.setCriteriaA1(null);
        testMatch.setCriteriaA2(null);
        testMatch.setCriteriaB1(null);
        testMatch.setCriteriaB2(null);

        testBoardRequest = new BoardRequest();
        testBoardRequest.setMatchId(1);
        testBoardRequest.setPlayerId(1);
        testBoardRequest.setCriteriaA1(Criteria.UNO);
        testBoardRequest.setCriteriaA2(Criteria.DOS);
        testBoardRequest.setCriteriaB1(Criteria.TRES);
        testBoardRequest.setCriteriaB2(Criteria.CUATRO);

        testBoard = new Board();
        testBoard.setId(1);
        testBoard.setMatch(testMatch);
        testBoard.setPlayer(testPlayer);
        testBoard.setScore(0);
    }

    @Test
    public void testFindAllSuccess() {
        Board board1 = new Board();
        Board board2 = new Board();
        when(boardRepository.findAll()).thenReturn(Arrays.asList(board1, board2));

        List<Board> boards = boardService.findAll();
        assertNotNull(boards);
        assertEquals(2, boards.size());
    }

    @Test
    public void testFindAllNotFound() {
        when(boardRepository.findAll()).thenReturn(Collections.emptyList());

        List<Board> boards = boardService.findAll();
        assertNotNull(boards);
        assertTrue(boards.isEmpty());
    }

    @Test
    public void testFindBoardByIdSuccess() {
        Board board = new Board();
        when(boardRepository.findById(anyInt())).thenReturn(Optional.of(board));

        Board result = boardService.findBoardById(1);
        assertNotNull(result);
        assertEquals(board, result);
    }

    @Test
    public void testFindBoardByIdNotFound() {
        when(boardRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            boardService.findBoardById(1);
        });
    }

    @Test
    public void testFindBoardsByPlayerIdSuccess() {
        Board board = new Board();
        when(boardRepository.findByPlayerId(anyInt())).thenReturn(Arrays.asList(board));

        List<Board> boards = boardService.findBoardsByPlayerId(1);
        assertNotNull(boards);
        assertEquals(1, boards.size());
    }

    @Test
    public void testFindBoardsByPlayerIdNotFound() {
        when(boardRepository.findByPlayerId(anyInt())).thenReturn(Collections.emptyList());

        List<Board> boards = boardService.findBoardsByPlayerId(1);
        assertNotNull(boards);
        assertTrue(boards.isEmpty());
    }

    @Test
    public void testFindBoardsByMatchIdSuccess() {
        Board board = new Board();
        when(boardRepository.findByMatchId(anyInt())).thenReturn(Arrays.asList(board));

        List<Board> boards = boardService.findBoardsByMatchId(1);
        assertNotNull(boards);
        assertEquals(1, boards.size());
    }

    @Test
    public void testFindBoardsByMatchIdNotFound() {
        when(boardRepository.findByMatchId(anyInt())).thenReturn(Collections.emptyList());

        List<Board> boards = boardService.findBoardsByMatchId(1);
        assertNotNull(boards);
        assertTrue(boards.isEmpty());
    }

    @Test
    public void testFindBoardByMatchIdAndUserIdSuccess() {

        Player player = new Player();
        player.setId(1);
        Board board = new Board();
        when(matchRepository.existsById(1)).thenReturn(true);
        when(playerRepository.findByUserId(anyInt())).thenReturn(player);
        when(boardRepository.findByMatchIdAndPlayerId(anyInt(), anyInt())).thenReturn(Optional.of(board));

        Board result = boardService.findBoardByMatchIdAndUserId(1, 1);
        assertNotNull(result);
        assertEquals(board, result);
    }

    @Test
    public void testFindBoardByMatchIdAndUserIdNotFound() {
        Player player = new Player();
        player.setId(1);
        when(playerRepository.findByUserId(anyInt())).thenReturn(player);
        when(matchRepository.existsById(1)).thenReturn(true);
        when(boardRepository.findByMatchIdAndPlayerId(anyInt(), anyInt())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
        boardService.findBoardByMatchIdAndUserId(1, 1);
        });
    
        assertEquals("Board not found with MatchId and PlayerId: '1, 1'", exception.getMessage());
        
        verify(matchRepository).existsById(1);
        verify(playerRepository).findByUserId(1);
        verify(boardRepository).findByMatchIdAndPlayerId(1, 1);
    }

    @Test
    @Transactional
    public void testUpdateBoardSuccess() {
        Board board = new Board();
        Board updatedBoard = new Board();
        when(boardRepository.findById(anyInt())).thenReturn(Optional.of(board));
        when(boardRepository.save(any(Board.class))).thenReturn(updatedBoard);

        Board result = boardService.updateBoard(updatedBoard, 1);
        assertNotNull(result);
        assertEquals(updatedBoard, result);
    }

    @Test
    @Transactional
    public void testUpdateBoardNotFound() {
        Board updatedBoard = new Board();
        when(boardRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            boardService.updateBoard(updatedBoard, 1);
        });
    }

    @Test
    @Transactional
    public void testCreateBoardSuccess() {
        when(matchRepository.findById(testBoardRequest.getMatchId())).thenReturn(Optional.of(testMatch));
        when(playerRepository.findById(testBoardRequest.getPlayerId())).thenReturn(Optional.of(testPlayer));
        when(matchRepository.save(testMatch)).thenReturn(testMatch);
        when(boardRepository.save(any(Board.class))).thenReturn(testBoard);

        Board result = boardService.createBoard(testBoardRequest);

        assertNotNull(result, "Board should not be null");
        assertEquals(testBoard, result, "Should return the created board");
        assertEquals(testMatch, result.getMatch(), "Board should have correct match");
        assertEquals(testPlayer, result.getPlayer(), "Board should have correct player");
        assertEquals(0, result.getScore(), "Board should have initial score of 0");

        assertEquals(Criteria.UNO, testMatch.getCriteriaA1());
        assertEquals(Criteria.DOS, testMatch.getCriteriaA2());
        assertEquals(Criteria.TRES, testMatch.getCriteriaB1());
        assertEquals(Criteria.CUATRO, testMatch.getCriteriaB2());

        verify(matchRepository).findById(testBoardRequest.getMatchId());
        verify(playerRepository).findById(testBoardRequest.getPlayerId());
        verify(matchRepository).save(testMatch);
        verify(boardRepository).save(any(Board.class));
    }

    @Test
    @Transactional
    public void testCreateBoardMatchNotFound() {
        when(matchRepository.findById(testBoardRequest.getMatchId())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, 
                                                  () -> boardService.createBoard(testBoardRequest));
        
        assertEquals("Error al crear el tablero", exception.getMessage());
        assertTrue(exception.getCause() instanceof ResourceNotFoundException);
        assertEquals("Partida no encontrada", exception.getCause().getMessage());

        verify(matchRepository).findById(testBoardRequest.getMatchId());
        verify(playerRepository, never()).findById(any());
        verify(matchRepository, never()).save(any());
        verify(boardRepository, never()).save(any());
    }

    @Test
    @Transactional
    public void testCreateBoardPlayerNotFound() {
        when(matchRepository.findById(testBoardRequest.getMatchId())).thenReturn(Optional.of(testMatch));
        when(playerRepository.findById(testBoardRequest.getPlayerId())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, 
                                                  () -> boardService.createBoard(testBoardRequest));
        
        assertEquals("Error al crear el tablero", exception.getMessage());
        assertTrue(exception.getCause() instanceof ResourceNotFoundException);
        assertEquals("Jugador no encontrado", exception.getCause().getMessage());

        verify(matchRepository).findById(testBoardRequest.getMatchId());
        verify(playerRepository).findById(testBoardRequest.getPlayerId());
        verify(matchRepository, never()).save(any());
        verify(boardRepository, never()).save(any());
    }


    @Test
    @Transactional
    public void testCreateBoardWithNullCriteria() {
        BoardRequest requestWithNullCriteria = new BoardRequest();
        requestWithNullCriteria.setMatchId(1);
        requestWithNullCriteria.setPlayerId(1);
        requestWithNullCriteria.setCriteriaA1(null);
        requestWithNullCriteria.setCriteriaA2(null);
        requestWithNullCriteria.setCriteriaB1(null);
        requestWithNullCriteria.setCriteriaB2(null);

        when(matchRepository.findById(requestWithNullCriteria.getMatchId())).thenReturn(Optional.of(testMatch));
        when(playerRepository.findById(requestWithNullCriteria.getPlayerId())).thenReturn(Optional.of(testPlayer));
        when(matchRepository.save(testMatch)).thenReturn(testMatch);
        when(boardRepository.save(any(Board.class))).thenReturn(testBoard);

        Board result = boardService.createBoard(requestWithNullCriteria);

        assertNotNull(result, "Board should not be null");
        assertEquals(testBoard, result, "Should return the created board");

        assertNull(testMatch.getCriteriaA1());
        assertNull(testMatch.getCriteriaA2());
        assertNull(testMatch.getCriteriaB1());
        assertNull(testMatch.getCriteriaB2());

        verify(matchRepository).findById(requestWithNullCriteria.getMatchId());
        verify(playerRepository).findById(requestWithNullCriteria.getPlayerId());
        verify(matchRepository).save(testMatch);
        verify(boardRepository).save(any(Board.class));
    }

    @Test
    @Transactional
    public void testCreateBoardRepositoryException() {
        when(matchRepository.findById(testBoardRequest.getMatchId())).thenReturn(Optional.of(testMatch));
        when(playerRepository.findById(testBoardRequest.getPlayerId())).thenReturn(Optional.of(testPlayer));
        when(matchRepository.save(testMatch)).thenReturn(testMatch);
        when(boardRepository.save(any(Board.class))).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, 
                                                  () -> boardService.createBoard(testBoardRequest));
        
        assertEquals("Error al crear el tablero", exception.getMessage());
        assertEquals("Database error", exception.getCause().getMessage());

        verify(matchRepository).findById(testBoardRequest.getMatchId());
        verify(playerRepository).findById(testBoardRequest.getPlayerId());
        verify(matchRepository).save(testMatch);
        verify(boardRepository).save(any(Board.class));
    }

    @Test
    @Transactional
    public void testCreateBoardWithNullBoardRequest() {
        RuntimeException exception = assertThrows(RuntimeException.class, 
                                                  () -> boardService.createBoard(null));
        
        assertEquals("Error al crear el tablero", exception.getMessage());
        assertTrue(exception.getCause() instanceof NullPointerException);
    }

    @Test
    @Transactional
    public void testDeleteBoardByIdSuccess() {
        Integer boardId = 1;
        Board boardToDelete = new Board();
        boardToDelete.setId(boardId);
        boardToDelete.setScore(100);
        
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(boardToDelete));
        doNothing().when(boardRepository).delete(boardToDelete);
        
        boardService.deleteBoardById(boardId);
        
        verify(boardRepository).findById(boardId);
        verify(boardRepository).delete(boardToDelete);
    }


    @Test
    @Transactional
    public void testDeleteBoardByIdNotFound() {
        Integer boardId = 999;
        
        when(boardRepository.findById(boardId)).thenReturn(Optional.empty());
        
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class, 
            () -> boardService.deleteBoardById(boardId)
        );
        
        assertEquals("Board not found with ID: '" + boardId + "'", exception.getMessage());
        
        verify(boardRepository).findById(boardId);
        verify(boardRepository, never()).delete(any());
    }

    @Test
    @Transactional
    public void testDeleteBoardByIdWithNullId() {
        Integer boardId = null;
        
        when(boardRepository.findById(null)).thenReturn(Optional.empty());
        
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class, 
            () -> boardService.deleteBoardById(boardId)
        );
        
        verify(boardRepository).findById(null);
        verify(boardRepository, never()).delete(any());
    }

    @Test
    @Transactional
    public void testUpdateBoardScoreSuccess() {
        Integer boardId = 1;
        Integer newScore = 150;
        
        Board existingBoard = new Board();
        existingBoard.setId(boardId);
        existingBoard.setScore(50);
        existingBoard.setMatch(testMatch);
        existingBoard.setPlayer(testPlayer);
        
        Board updatedBoard = new Board();
        updatedBoard.setId(boardId);
        updatedBoard.setScore(newScore);
        updatedBoard.setMatch(testMatch);
        updatedBoard.setPlayer(testPlayer);
        
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(existingBoard));
        when(boardRepository.save(existingBoard)).thenReturn(updatedBoard);
        
        Board result = boardService.updateBoardScore(boardId, newScore);
        
        assertNotNull(result, "Result should not be null");
        assertEquals(updatedBoard, result, "Should return the updated board");
        assertEquals(newScore, existingBoard.getScore(), "Board score should be updated");
        
        verify(boardRepository).findById(boardId);
        verify(boardRepository).save(existingBoard);
    }

    @Test
    @Transactional
    public void testUpdateBoardScoreNotFound() {
        Integer boardId = 999;
        Integer newScore = 100;
        
        when(boardRepository.findById(boardId)).thenReturn(Optional.empty());
        
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class, 
            () -> boardService.updateBoardScore(boardId, newScore)
        );
        
        assertEquals("Board no encontrado", exception.getMessage());
        
        verify(boardRepository).findById(boardId);
        verify(boardRepository, never()).save(any());
    }




}
