package es.us.dp1.l6_02_24_25.los_mapas_del_reino.match;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
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
import org.springframework.beans.factory.config.YamlProcessor.MatchStatus;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.us.dp1.l6_02_24_25.los_mapas_del_reino.exceptions.AccessDeniedException;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.exceptions.ResourceNotFoundException;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.player.Player;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.player.PlayerService;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.user.User;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.user.UserService;

@SpringBootTest
@AutoConfigureMockMvc
public class MatchRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MatchService matchService;

    @MockBean
    private PlayerService playerService;

    @MockBean
    private UserService userService;

    @InjectMocks
    private MatchRestController matchRestController;

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
    }

    @Test
    public void testFindAllSuccess() throws Exception {
        Match match1 = new Match();
        Match match2 = new Match();
        when(matchService.findAllMatches()).thenReturn(Arrays.asList(match1, match2));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/matches"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2));
    }

    @Test
    public void testFindAllNotFound() throws Exception {
        when(matchService.findAllMatches()).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/matches"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser(username = "testuser", authorities = {"PLAYER"})
    public void testFindMatchByIdSuccess() throws Exception {
        int matchId = 1;
        Integer userId = 123;
        
        User currentUser = new User();
        currentUser.setId(userId);
        
        Match match = new Match();
        match.setId(matchId);
        match.setState(MatchState.CREATED);
        
        when(userService.findCurrentUser()).thenReturn(currentUser);
        doReturn(ResponseEntity.ok(match)) // doReturn evita problemas de tipos
                .when(matchService).getMatchByIdWithAccessCheck(matchId, userId);


        mockMvc.perform(get("/api/v1/matches/{matchId}", matchId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(matchId))
                .andExpect(jsonPath("$.state").value("CREATED"));
        
        verify(userService).findCurrentUser();
        verify(matchService).getMatchByIdWithAccessCheck(matchId, userId);
    }

    @Test
    @WithMockUser(username = "testuser", authorities = {"PLAYER"})
    public void testFindMatchByIdNotFound() throws Exception {
        int matchId = 999;
        Integer userId = 123;
        
        User currentUser = new User();
        currentUser.setId(userId);
        
        when(userService.findCurrentUser()).thenReturn(currentUser);
        doReturn(ResponseEntity.notFound().build())
                .when(matchService).getMatchByIdWithAccessCheck(matchId, userId);

        mockMvc.perform(get("/api/v1/matches/{matchId}", matchId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        
        verify(userService).findCurrentUser();
        verify(matchService).getMatchByIdWithAccessCheck(matchId, userId);
    }

    @Test
    @WithMockUser(username = "testuser", authorities = {"PLAYER"})
    public void testCreateMatchSuccess() throws Exception {
        Integer userId = 123;
        
        User currentUser = new User();
        currentUser.setId(userId);
        
        Player player = new Player();
        player.setId(1);
        player.setUser(currentUser);
        
        MatchCreateRequest createRequest = new MatchCreateRequest();
        createRequest.setMode(MatchMode.STANDARD);
        
        Match createdMatch = new Match();
        createdMatch.setId(1);
        createdMatch.setState(MatchState.CREATED);
        createdMatch.setMode(MatchMode.STANDARD);
        createdMatch.setCreator(player);

        when(userService.findCurrentUser()).thenReturn(currentUser);
        when(playerService.findPlayerByUserId(userId)).thenReturn(player);
        when(matchService.createMatch(eq(player), any(MatchCreateRequest.class)))
                .thenReturn(createdMatch);

        mockMvc.perform(post("/api/v1/matches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.state").value("CREATED"))
                .andExpect(jsonPath("$.mode").value("STANDARD"));
        
        verify(userService).findCurrentUser();
        verify(playerService).findPlayerByUserId(userId);
        verify(matchService).createMatch(eq(player), any(MatchCreateRequest.class));
    }


    @Test
    public void testCreateMatchNegative() throws Exception {
        when(userService.findCurrentUser()).thenReturn(null);

        mockMvc.perform(post("/api/v1/matches")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

   @Test
   @WithMockUser(username = "testuser", authorities = {"PLAYER"})
   public void testCreateMatchPlayerNotFound() throws Exception {
        Integer userId = 123;
        
        User currentUser = new User();
        currentUser.setId(userId);
        
        when(userService.findCurrentUser()).thenReturn(currentUser);
        when(playerService.findPlayerByUserId(userId))
                .thenThrow(new ResourceNotFoundException("Player", "UserId", userId));

        mockMvc.perform(post("/api/v1/matches")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"mode\":\"STANDARD\"}"))
                .andExpect(status().isNotFound());
        
        verify(userService).findCurrentUser();
        verify(playerService).findPlayerByUserId(userId);
        verify(matchService, never()).createMatch(any(Player.class), any(MatchCreateRequest.class));
   }

    @Test
    public void testFindMatchsByPlayerIdSuccess() throws Exception {
        int playerId = 1;
        Match match = new Match();
        when(matchService.findAllMatchesByPlayerId(playerId)).thenReturn(Arrays.asList(match));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/matches/player/{playerId}", playerId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1));
    }

    @Test
    public void testFindMatchsByPlayerIdNotFound() throws Exception {
        int playerId = 1;
        when(matchService.findAllMatchesByPlayerId(anyInt())).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/matches/player/{playerId}", playerId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0));
    }

    @Test
    public void testFindCreatedMatchesSuccess() throws Exception {
        Player player = new Player();
        player.setId(1);
        
        Match match1 = new Match();
        match1.setId(1);
        match1.setState(MatchState.CREATED);
        match1.setPlayers(Arrays.asList(player));
        
        Match match2 = new Match();
        match2.setId(2);
        match2.setState(MatchState.CREATED);
        match2.setPlayers(Arrays.asList(player));
        
        when(matchService.getFilteredCreatedMatches()).thenReturn(Arrays.asList(match1, match2));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/matches/created"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2));
    }

    @Test
    public void testFindCreatedMatchesEmpty() throws Exception {
        when(matchService.getFilteredCreatedMatches()).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/matches/created"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0));
    }


     @Test
    public void testFindStartedMatchsSuccess() throws Exception {
        Match match = new Match();
        when(matchService.findStartedMatches()).thenReturn(Arrays.asList(match));

        mockMvc.perform(get("/api/v1/matches/started"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    public void testFindStartedMatchsNegative() throws Exception {
        when(matchService.findStartedMatches()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/matches/started"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    public void testFindFinishedMatchsSuccess() throws Exception {
        Match match = new Match();
        when(matchService.findFinishedMatches()).thenReturn(Arrays.asList(match));

        mockMvc.perform(get("/api/v1/matches/finished"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    public void testFindFinishedMatchsNegative() throws Exception {
        when(matchService.findFinishedMatches()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/matches/finished"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    public void testFinishMatchSuccess() throws Exception {
        Match match = new Match();
        match.setState(MatchState.CREATED); // Estado inicial
        int matchId = 1;

        when(matchService.findMatchById(matchId)).thenReturn(Optional.of(match));

        // Simula la actualización de estado al finalizar la partida
        doNothing().when(matchService).finishMatch(matchId);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/matches/{matchId}/finish", matchId))
                .andDo(MockMvcResultHandlers.print()) // Imprime el resultado para depuración
                .andExpect(MockMvcResultMatchers.status().isOk()); // Verifica que la respuesta es 200 OK
    }

    @Test
    public void testFinishMatchNegative() throws Exception {
        Integer matchId = 999;
        
        when(matchService.handleFinishMatch(matchId))
                .thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        mockMvc.perform(put("/api/v1/matches/{matchId}/finish", matchId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        
        verify(matchService).handleFinishMatch(matchId);
    }

    @Test
    public void testJoinMatchSuccess() throws Exception {
        
        Integer matchId = 1;
        Integer userId = 123;
        Integer playerId = 10;
    
        User user = new User();
        user.setId(userId);

        Player player = new Player();
        player.setId(playerId);
        
        Match match = new Match();
        match.setId(matchId);
        match.setPlayers(new ArrayList<>(List.of(player)));
        
        when(userService.findCurrentUser()).thenReturn(user);
        when(playerService.findPlayerByUserId(userId)).thenReturn(player);
        when(matchService.joinMatch(matchId, player)).thenReturn(match);
        
        
        mockMvc.perform(put("/api/v1/matches/join/{matchId}", matchId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(matchId))
                .andExpect(jsonPath("$.players.length()").value(1))
                .andExpect(jsonPath("$.players[0].id").value(playerId));
        
        
        verify(userService).findCurrentUser();
        verify(playerService).findPlayerByUserId(userId);
        verify(matchService).joinMatch(matchId, player);
    }

    @Test
    public void testJoinMatchServiceThrowsException() throws Exception {
        
        Integer matchId = 1;
        Integer userId = 123;
        
        User user = new User();
        user.setId(userId);
        
        Player player = new Player();
        player.setId(10);
        
        when(userService.findCurrentUser()).thenReturn(user);
        when(playerService.findPlayerByUserId(userId)).thenReturn(player);
        when(matchService.joinMatch(matchId, player))
                .thenThrow(new IllegalStateException("Match is full"));
        
       
        mockMvc.perform(put("/api/v1/matches/join/{matchId}", matchId))
                .andExpect(status().isInternalServerError());
        
        verify(userService).findCurrentUser();
        verify(playerService).findPlayerByUserId(userId);
        verify(matchService).joinMatch(matchId, player);
    }

    @Test
    public void testJoinMatchUserNotFound() throws Exception {
       
        Integer matchId = 1;
        
        when(userService.findCurrentUser()).thenThrow(new RuntimeException("User not found"));
        
        
        mockMvc.perform(put("/api/v1/matches/join/{matchId}", matchId))
                .andExpect(status().isInternalServerError());
        
        verify(userService).findCurrentUser();
        verify(playerService, never()).findPlayerByUserId(any());
        verify(matchService, never()).joinMatch(any(), any());
    }

    @Test
    public void testJoinMatchPlayerNotFound() throws Exception {
        
        Integer matchId = 1;
        Integer userId = 123;
        
        User user = new User();
        user.setId(userId);
        
        when(userService.findCurrentUser()).thenReturn(user);
        when(playerService.findPlayerByUserId(userId))
                .thenThrow(new RuntimeException("Player not found"));
        
        
        mockMvc.perform(put("/api/v1/matches/join/{matchId}", matchId))
                .andExpect(status().isInternalServerError());
        
        verify(userService).findCurrentUser();
        verify(playerService).findPlayerByUserId(userId);
        verify(matchService, never()).joinMatch(any(), any());
    }

    @Test
    public void testJoinMatchWithMultiplePlayers() throws Exception {
        
        Integer matchId = 1;
        Integer userId = 123;
        Integer playerId = 10;
        
        User user = new User();
        user.setId(userId);
        
        Player player1 = new Player();
        player1.setId(1);
        
        Player player2 = new Player();
        player2.setId(playerId);
        
        Match match = new Match();
        match.setId(matchId);
        match.setPlayers(new ArrayList<>(List.of(player1, player2)));
        
        when(userService.findCurrentUser()).thenReturn(user);
        when(playerService.findPlayerByUserId(userId)).thenReturn(player2);
        when(matchService.joinMatch(matchId, player2)).thenReturn(match);
        
        
        mockMvc.perform(put("/api/v1/matches/join/{matchId}", matchId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(matchId))
                .andExpect(jsonPath("$.players.length()").value(2));
        
        verify(userService).findCurrentUser();
        verify(playerService).findPlayerByUserId(userId);
        verify(matchService).joinMatch(matchId, player2);
    }

    @Test
    public void testLeaveMatchSuccess() throws Exception {
        Match match = new Match();
        match.setId(1);
        Player player = new Player();
        player.setId(10);
        match.setPlayers(new ArrayList<>(Arrays.asList(player, new Player())));
        es.us.dp1.l6_02_24_25.los_mapas_del_reino.user.User user = new es.us.dp1.l6_02_24_25.los_mapas_del_reino.user.User();
        user.setId(20);

        when(matchService.findMatchById(1))
                .thenReturn(Optional.of(match)) 
                .thenReturn(Optional.of(match));
        when(userService.findCurrentUser()).thenReturn(user);
        when(playerService.findPlayerByUserId(20)).thenReturn(player);

        mockMvc.perform(put("/api/v1/matches/leave/1"))
                .andExpect(status().isOk());
        verify(matchService).removePlayerFromMatch(1, 10);
        verify(matchService, never()).delete(1);
    }

   @Test
   public void testLeaveMatchUserNotFound() throws Exception {
    Integer matchId = 1;
    
    when(userService.findCurrentUser()).thenThrow(new RuntimeException("User not found"));

    mockMvc.perform(put("/api/v1/matches/leave/{matchId}", matchId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError());
    
    verify(userService).findCurrentUser();
    verify(playerService, never()).findPlayerByUserId(anyInt());
    verify(matchService, never()).removePlayerFromMatch(anyInt(), anyInt());
   }

   @Test
   public void testLeaveMatchMatchNotFound() throws Exception {
        Integer matchId = 999; // Match que no existe
        Integer userId = 123;
        Integer playerId = 1;
        
        User currentUser = new User();
        currentUser.setId(userId);
        
        Player player = new Player();
        player.setId(playerId);
        
        when(userService.findCurrentUser()).thenReturn(currentUser);
        when(playerService.findPlayerByUserId(userId)).thenReturn(player);
        when(matchService.removePlayerFromMatch(matchId, playerId))
                .thenThrow(new ResourceNotFoundException("Match", "ID", matchId));

        mockMvc.perform(put("/api/v1/matches/leave/{matchId}", matchId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        
        verify(userService).findCurrentUser();
        verify(playerService).findPlayerByUserId(userId);
        verify(matchService).removePlayerFromMatch(matchId, playerId);
   }

    @Test
    public void testDeleteMatchSuccess() throws Exception {
        mockMvc.perform(delete("/api/v1/matches/{matchId}", 1))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteMatchNegative() throws Exception {
        doThrow(new RuntimeException()).when(matchService).delete(anyInt());

        mockMvc.perform(delete("/api/v1/matches/{matchId}", 1))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testStartMatchSuccess() throws Exception {
        Integer matchId = 1;
        Match match = new Match();
        match.setId(matchId);
        match.setState(MatchState.STARTED);
        
        when(matchService.startMatch(matchId)).thenReturn(match);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/matches/start/{matchId}", matchId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(matchId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.state").value("STARTED"));
     }

     @Test
     public void testStartMatchNotFound() throws Exception {
        Integer matchId = 999;
        
        when(matchService.startMatch(matchId)).thenThrow(new ResourceNotFoundException("Match not found"));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/matches/start/{matchId}", matchId))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
     }

     @Test
     public void testUpdateMatchSuccess() throws Exception {
        Integer matchId = 1;
        Integer userId = 1;
        
        User currentUser = new User();
        currentUser.setId(userId);
        
        MatchUpdateRequest updateRequest = new MatchUpdateRequest();
        updateRequest.setVersion(1);
        updateRequest.setCurrentActivePlayer(2);
        updateRequest.setRound(3);
        
        Match updatedMatch = new Match();
        updatedMatch.setId(matchId);
        updatedMatch.setCurrentActivePlayer(2);
        updatedMatch.setRound(3);
        
        when(userService.findCurrentUser()).thenReturn(currentUser);
        when(matchService.updateMatch(matchId, updateRequest, userId))
                .thenReturn(new ResponseEntity<>(updatedMatch, HttpStatus.OK));

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/matches/{matchId}", matchId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print()); 
    }

    @Test
    void getWinnersSuccess() throws Exception {
        Player player = new Player();
        player.setId(1);
        when(matchService.getWinners(1)).thenReturn(List.of(player));

        mockMvc.perform(get("/api/v1/matches/1/winners"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getWinnersIllegalStateException() throws Exception {
        when(matchService.getWinners(1)).thenThrow(new IllegalStateException());

        mockMvc.perform(get("/api/v1/matches/1/winners"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetWinnersAccessDeniedException() throws Exception {
        Integer matchId = 1;
        
        when(matchService.getWinners(matchId)).thenThrow(new AccessDeniedException("Access denied"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/matches/{matchId}/winners", matchId))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }
}
