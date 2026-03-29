package es.us.dp1.l6_02_24_25.los_mapas_del_reino.board;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@DataJpaTest
public class BoardRepositoryTest {

    @MockBean
    private BoardRepository mockBoardRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindAllSuccess() {
        Board board1 = new Board();
        Board board2 = new Board();
        when(mockBoardRepository.findAll()).thenReturn(Arrays.asList(board1, board2));

        List<Board> boards = mockBoardRepository.findAll();
        assertNotNull(boards);
        assertEquals(2, boards.size());
    }

    @Test
    public void testFindAllNotFound() {
        when(mockBoardRepository.findAll()).thenReturn(Collections.emptyList());

        List<Board> boards = mockBoardRepository.findAll();
        assertNotNull(boards);
        assertTrue(boards.isEmpty());
    }

    @Test
    public void testFindByPlayerIdSuccess() {
        Board board = new Board();
        when(mockBoardRepository.findByPlayerId(1)).thenReturn(Arrays.asList(board));

        List<Board> boards = mockBoardRepository.findByPlayerId(1);
        assertNotNull(boards);
        assertEquals(1, boards.size());
    }

    @Test
    public void testFindByPlayerIdNotFound() {
        when(mockBoardRepository.findByPlayerId(1)).thenReturn(Collections.emptyList());

        List<Board> boards = mockBoardRepository.findByPlayerId(1);
        assertNotNull(boards);
        assertTrue(boards.isEmpty());
    }

    @Test
    public void testFindByMatchIdSuccess() {
        Board board = new Board();
        when(mockBoardRepository.findByMatchId(1)).thenReturn(Arrays.asList(board));

        List<Board> boards = mockBoardRepository.findByMatchId(1);
        assertNotNull(boards);
        assertEquals(1, boards.size());
    }

    @Test
    public void testFindByMatchIdNotFound() {
        when(mockBoardRepository.findByMatchId(1)).thenReturn(Collections.emptyList());

        List<Board> boards = mockBoardRepository.findByMatchId(1);
        assertNotNull(boards);
        assertTrue(boards.isEmpty());
    }

    @Test
    public void testFindByMatchIdAndPlayerIdSuccess() {
        Board board = new Board();
        when(mockBoardRepository.findByMatchIdAndPlayerId(1, 1)).thenReturn(Optional.of(board));

        Optional<Board> result = mockBoardRepository.findByMatchIdAndPlayerId(1, 1);
        assertTrue(result.isPresent());
        assertEquals(board, result.get());
    }

    @Test
    public void testFindByMatchIdAndPlayerIdNotFound() {
        when(mockBoardRepository.findByMatchIdAndPlayerId(1, 1)).thenReturn(Optional.empty());

        Optional<Board> result = mockBoardRepository.findByMatchIdAndPlayerId(1, 1);
        assertFalse(result.isPresent());
    }
}
