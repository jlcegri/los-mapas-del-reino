package es.us.dp1.l6_02_24_25.los_mapas_del_reino.board;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.us.dp1.l6_02_24_25.los_mapas_del_reino.exceptions.ResourceNotFoundException;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.match.Match;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.match.MatchRepository;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.player.Player;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.player.PlayerRepository;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.user.User;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.user.UserService;

import org.springframework.web.context.WebApplicationContext;


@SpringBootTest
@AutoConfigureMockMvc
public class BoardRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private BoardRestController boardRestController;

    @MockBean
    private BoardService boardService;

    @MockBean
    private PlayerRepository playerRepository;

    @MockBean
    private MatchRepository matchRepository;

    @MockBean
    private BoardRepository boardRepository;

    @MockBean
    private UserService userService;

    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .build();
            objectMapper = new ObjectMapper();
    }

   @Test
    @WithMockUser(username = "player1", authorities = {"PLAYER"})
    public void testFindAll() throws Exception {
        reset(boardService);
        Board board = new Board();
        board.setId(1);
        Board board2 = new Board();
        board2.setId(2);
        List<Board> boards = List.of(board, board2);
        when(boardService.findAll()).thenReturn(boards);

        mockMvc.perform(get("/api/v1/boards"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    }

    @Test
    public void testFindAllBoardsEmpty() throws Exception {
        when(boardService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/boards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    public void testFindBoardsByMatchIdSuccess() throws Exception {
        Board board = new Board();
        int mathcId = 1;
        when(boardService.findBoardsByMatchId(mathcId)).thenReturn(Arrays.asList(board));

        mockMvc.perform(get("/api/v1/boards/match/{matchId}", mathcId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    public void testFindBoardsByMatchIdNotFound() throws Exception {
        when(boardService.findBoardsByMatchId(1)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/boards/match/{matchId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    public void testFindBoardsByPlayerIdSuccess() throws Exception {
        Board board = new Board();
        int playerId= 1;
        when(boardService.findBoardsByPlayerId(playerId)).thenReturn(Arrays.asList(board));

        mockMvc.perform(get("/api/v1/boards/player/{playerId}", playerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    public void testFindBoardsByPlayerIdNotFound() throws Exception {
        when(boardService.findBoardsByPlayerId(1)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/boards/player/{playerId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser(username = "testuser", authorities = {"PLAYER"})
    public void testFindBoardByMatchIdAndUserIdSuccess() throws Exception {
        Board board = new Board();
        board.setId(1);
        int matchId = 1;
        int userId = 123;
        
        User currentUser = new User();
        currentUser.setId(userId);
        
        when(userService.findCurrentUser()).thenReturn(currentUser);
        when(boardService.findBoardByMatchIdAndUserId(matchId, userId)).thenReturn(board);

        mockMvc.perform(get("/api/v1/boards/{matchId}/{playerId}", matchId, userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));
        
        verify(userService).findCurrentUser();
        verify(boardService).findBoardByMatchIdAndUserId(matchId, userId);
    }

    @Test
    @WithMockUser(username = "testuser", authorities = {"PLAYER"})
    public void testFindBoardByMatchIdAndUserIdBoardNotFound() throws Exception {
    
        int matchId = 999; // Match que no existe
        int userId = 123;
        
        User currentUser = new User();
        currentUser.setId(userId);
        
        when(userService.findCurrentUser()).thenReturn(currentUser);
        when(boardService.findBoardByMatchIdAndUserId(matchId, userId))
            .thenThrow(new ResourceNotFoundException("Board", "MatchId and PlayerId", matchId + ", " + userId));

        mockMvc.perform(get("/api/v1/boards/{matchId}/{playerId}", matchId, userId))
                .andExpect(status().isNotFound());
        
        verify(userService).findCurrentUser();
        verify(boardService).findBoardByMatchIdAndUserId(matchId, userId);
    }


    @Test
    @WithMockUser(username = "testuser", authorities = {"PLAYER"})
    public void testFindBoardByMatchIdAndUserIdMatchNotFound() throws Exception {
        int matchId = 999;
        int userId = 123;
        
        User currentUser = new User();
        currentUser.setId(userId);
        
        when(userService.findCurrentUser()).thenReturn(currentUser);
        when(boardService.findBoardByMatchIdAndUserId(matchId, userId))
            .thenThrow(new ResourceNotFoundException("Match", "ID", matchId));

        mockMvc.perform(get("/api/v1/boards/{matchId}/{playerId}", matchId, userId))
                .andExpect(status().isNotFound());
        
        verify(userService).findCurrentUser();
        verify(boardService).findBoardByMatchIdAndUserId(matchId, userId);
    }

    @Test
    @WithMockUser(username = "testuser", authorities = {"PLAYER"})
    public void testFindBoardByMatchIdAndUserIdWithNullCurrentUser() throws Exception {
        int matchId = 1;
        int userId = 123;
        
        when(userService.findCurrentUser()).thenReturn(null);

        mockMvc.perform(get("/api/v1/boards/{matchId}/{playerId}", matchId, userId))
                .andExpect(status().isInternalServerError());
        
        verify(userService).findCurrentUser();
        verify(boardService, never()).findBoardByMatchIdAndUserId(anyInt(), anyInt());
    }

    @Test
    public void testFindBoardByIdSuccess() throws Exception {
        Board board = new Board();
        board.setId(1);
        
        when(boardService.findBoardById(1)).thenReturn(board);

        mockMvc.perform(get("/api/v1/boards/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));
        
        verify(boardService).findBoardById(1);
    }

    @Test
    public void testFindBoardByIdNotFound() throws Exception {
        int boardId = 999;
        
        when(boardService.findBoardById(boardId))
            .thenThrow(new ResourceNotFoundException("Board", "ID", boardId));

        mockMvc.perform(get("/api/v1/boards/{id}", boardId))
                .andExpect(status().isNotFound());
        
        verify(boardService).findBoardById(boardId);
    }


    @Test
    public void testCreateBoardSuccess() throws Exception {
        BoardRequest boardRequest = new BoardRequest();
        boardRequest.setMatchId(1);
        boardRequest.setPlayerId(1);
        
        Board savedBoard = new Board();
        savedBoard.setId(1);
        
        when(boardService.createBoard(any(BoardRequest.class))).thenReturn(savedBoard);

        mockMvc.perform(post("/api/v1/boards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(boardRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));
        
        verify(boardService).createBoard(any(BoardRequest.class));
    }


    @Test
    public void testCreateBoardNegative() throws Exception {
        BoardRequest boardRequest = new BoardRequest();
        boardRequest.setMatchId(1);
        boardRequest.setPlayerId(1);
        
        when(boardService.createBoard(any(BoardRequest.class)))
            .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(post("/api/v1/boards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(boardRequest)))
                .andExpect(status().isInternalServerError());
        
        verify(boardService).createBoard(any(BoardRequest.class));
    }

    @Test
    public void testReturnBoardWebSocket() {
        // Arrange
        String gameId = "1";
        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setScore(100);
        
        // Act
        BoardDTO result = boardRestController.returnBoard(gameId, boardDTO);
        
        // Assert
        assertNotNull(result);
        assertEquals(boardDTO.getScore(), result.getScore());
        assertEquals(boardDTO, result); // Debe devolver el mismo objeto
    }

    @Test
    public void testReturnBoardWebSocketWithNullBoard() {
        String gameId = "game123";
        
        BoardDTO result = boardRestController.returnBoard(gameId, null);
        
        assertNull(result);
    }

    @Test
    public void testReturnBoardWebSocketWithNullGameId() {
        BoardDTO boardDTO = new BoardDTO();
        
        BoardDTO result = boardRestController.returnBoard(null, boardDTO);
        
        assertNotNull(result);
        assertEquals(boardDTO, result);
    }

    @Test
    public void testDeleteBoardByIdSuccess() throws Exception {
        Integer boardId = 1;
        
        doNothing().when(boardService).deleteBoardById(boardId);

        mockMvc.perform(delete("/api/v1/boards/{boardId}", boardId))
                .andExpect(status().isNoContent());
        
        verify(boardService).deleteBoardById(boardId);
    }

    @Test
    public void testDeleteBoardByIdNotFound() throws Exception {
        Integer boardId = 999;
        
        doThrow(new ResourceNotFoundException("Board", "ID", boardId))
            .when(boardService).deleteBoardById(boardId);

        mockMvc.perform(delete("/api/v1/boards/{boardId}", boardId))
                .andExpect(status().isNotFound());
        
        verify(boardService).deleteBoardById(boardId);
    }

    @Test
    public void testUpdateBoardScoreSuccess() throws Exception {
        Integer boardId = 1;
        Integer newScore = 150;
        
        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setScore(newScore);
        
        Board updatedBoard = new Board();
        updatedBoard.setId(boardId);
        updatedBoard.setScore(newScore);
        
        when(boardService.updateBoardScore(boardId, newScore)).thenReturn(updatedBoard);

        mockMvc.perform(put("/api/v1/boards/{boardId}/score", boardId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(boardDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(boardId))
                .andExpect(jsonPath("$.score").value(newScore));
        
        verify(boardService).updateBoardScore(boardId, newScore);
    }


    @Test
    public void testUpdateBoardScoreBoardNotFound() throws Exception {
        Integer boardId = 999;
        Integer newScore = 150;
        
        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setScore(newScore);
        
        when(boardService.updateBoardScore(boardId, newScore))
            .thenThrow(new ResourceNotFoundException("Board", "ID", boardId));

        mockMvc.perform(put("/api/v1/boards/{boardId}/score", boardId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(boardDTO)))
                .andExpect(status().isNotFound()); // Asumiendo que hay un @ExceptionHandler
        
        verify(boardService).updateBoardScore(boardId, newScore);
    }

    //Otros test de la clase BoardDTO

    @Test
    public void testEqualsAndHashCodeBoardDTO() {
        BoardDTO dto1 = new BoardDTO(1, 2, 100, "A1", "A2", "B1", "B2");
        BoardDTO dto2 = new BoardDTO(1, 2, 100, "A1", "A2", "B1", "B2");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    public void testToStringBoardDTO() {
        BoardDTO dto = new BoardDTO(1, 2, 100, "A1", "A2", "B1", "B2");
        String str = dto.toString();
        assertTrue(str.contains("playerId=1"));
        assertTrue(str.contains("matchId=2"));
}


    
}