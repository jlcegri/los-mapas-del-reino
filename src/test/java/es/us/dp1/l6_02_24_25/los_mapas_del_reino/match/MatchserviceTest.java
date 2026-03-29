package es.us.dp1.l6_02_24_25.los_mapas_del_reino.match;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import es.us.dp1.l6_02_24_25.los_mapas_del_reino.board.Board;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.board.BoardRepository;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.exceptions.ConflictException;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.player.Player;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.player.PlayerRepository;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@SpringBootTest
public class MatchserviceTest {

    @InjectMocks
    private MatchService matchService;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private BoardRepository boardRepository;

    private Match testMatch;
    private Player testPlayer1;
    private Player testPlayer2;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
        User user1 = new User();
        user1.setId(1);
        user1.setUsername("player1");

        User user2 = new User();
        user2.setId(2);
        user2.setUsername("player2");

        testPlayer1 = new Player();
        testPlayer1.setId(1);
        testPlayer1.setFirstName("John");
        testPlayer1.setLastName("Doe");
        testPlayer1.setUser(user1);

        testPlayer2 = new Player();
        testPlayer2.setId(2);
        testPlayer2.setFirstName("Jane");
        testPlayer2.setLastName("Smith");
        testPlayer2.setUser(user2);

        testMatch = new Match();
        testMatch.setId(1);
        testMatch.setCreator(testPlayer1);
        testMatch.setMode(MatchMode.STANDARD);
        testMatch.setState(MatchState.CREATED);
        testMatch.setStartDate(LocalDateTime.now());
        testMatch.setPlayers(new ArrayList<>(Arrays.asList(testPlayer1, testPlayer2)));
    }

    
    
    @Test
    public void testFindAllMatchesSuccess() {
        when(matchRepository.findAll()).thenReturn(Collections.singletonList(new Match()));

        List<Match> matches = matchService.findAllMatches();

        assertNotNull(matches);
        assertFalse(matches.isEmpty());
        verify(matchRepository, times(1)).findAll();
    }

    @Test
    public void testFindMatchByIdSuccess() {
        Match match = new Match();
        match.setId(1);

        when(matchRepository.findById(1)).thenReturn(Optional.of(match));

        Optional<Match> foundMatch = matchService.findMatchById(1);

        assertTrue(foundMatch.isPresent());
        assertEquals(1, foundMatch.get().getId());
        verify(matchRepository, times(1)).findById(1);
    }

    @Test
    public void testFindMatchByIdNotFound() {
        when(matchRepository.findById(1)).thenReturn(Optional.empty());

        Optional<Match> foundMatch = matchService.findMatchById(1);

        assertFalse(foundMatch.isPresent());
        verify(matchRepository, times(1)).findById(1);
    }

    @Test
    public void testFindMatchWithPlayersByIdSuccess() {
        Integer matchId = 1;
        when(matchRepository.findMatchWithPlayersById(matchId)).thenReturn(Optional.of(testMatch));

        Optional<Match> result = matchService.findMatchWithPlayersById(matchId);

        assertTrue(result.isPresent(), "Should return a match");
        assertEquals(testMatch, result.get(), "Should return the correct match");
        assertEquals(2, result.get().getPlayers().size(), "Should have 2 players");
        assertTrue(result.get().getPlayers().contains(testPlayer1), "Should contain player 1");
        assertTrue(result.get().getPlayers().contains(testPlayer2), "Should contain player 2");
        
        verify(matchRepository).findMatchWithPlayersById(matchId);
    }

    @Test
    public void testFindMatchWithPlayersByIdNotFound() {
        Integer matchId = 999;
        when(matchRepository.findMatchWithPlayersById(matchId)).thenReturn(Optional.empty());

        Optional<Match> result = matchService.findMatchWithPlayersById(matchId);

        assertTrue(result.isEmpty(), "Should return empty Optional when match not found");
        
        verify(matchRepository).findMatchWithPlayersById(matchId);
    }



    @Test
    public void testFindAllMatchesByPlayerIdSuccess() {
        Match match = new Match();
        match.setId(1);

        when(matchRepository.findAllMatchesByPlayerId(1)).thenReturn(Collections.singletonList(match));

        List<Match> matches = matchService.findAllMatchesByPlayerId(1);

        assertNotNull(matches);
        assertEquals(1, matches.size());
        verify(matchRepository, times(1)).findAllMatchesByPlayerId(1);
    }

    @Test
    public void testFindAllMatchesByPlayerIdNotFound() {
        when(matchRepository.findAllMatchesByPlayerId(1)).thenReturn(Collections.emptyList());

        List<Match> matches = matchService.findAllMatchesByPlayerId(1);

        assertNotNull(matches);
        assertTrue(matches.isEmpty());
        verify(matchRepository, times(1)).findAllMatchesByPlayerId(1);
    }

    @Test
    public void testFindCreatedMatchesSuccess() {
        when(matchRepository.findCreatedMatches()).thenReturn(Collections.singletonList(new Match()));

        List<Match> matches = matchService.findCreatedMatches();

        assertNotNull(matches);
        assertFalse(matches.isEmpty());
        verify(matchRepository, times(1)).findCreatedMatches();
    }

    @Test
    public void testFindStartedMatchesSuccess() {
        when(matchRepository.findStartedMatches()).thenReturn(Collections.singletonList(new Match()));

        List<Match> matches = matchService.findStartedMatches();

        assertNotNull(matches);
        assertFalse(matches.isEmpty());
        verify(matchRepository, times(1)).findStartedMatches();
    }

    @Test
    public void testFindFinishedMatchesSuccess() {
        when(matchRepository.findFinishedMatches()).thenReturn(Collections.singletonList(new Match()));

        List<Match> matches = matchService.findFinishedMatches();

        assertNotNull(matches);
        assertFalse(matches.isEmpty());
        verify(matchRepository, times(1)).findFinishedMatches();
    }

    @Test
    @Transactional
    public void testSaveMatchSuccess() {
        Match match = new Match();

        when(matchRepository.save(match)).thenReturn(match);

        Match savedMatch = matchService.save(match);

        assertNotNull(savedMatch);
        verify(matchRepository, times(1)).save(match);
    }

    @Test
    @Transactional
    public void testSaveMatchDataAccessException() {
        Match match = new Match();

        when(matchRepository.save(match)).thenThrow(new DataAccessException("Error") {});

        assertThrows(DataAccessException.class, () -> matchService.save(match));
        verify(matchRepository, times(1)).save(match);
    }

    @Test
    @Transactional
    public void testDeleteMatchSuccess() {
        doNothing().when(matchRepository).deleteById(1);

        matchService.delete(1);

        verify(matchRepository, times(1)).deleteById(1);
    }

    @Test
    @Transactional
    public void testDeleteMatchDataAccessException() {
        doThrow(new DataAccessException("Error") {}).when(matchRepository).deleteById(1);

        assertThrows(DataAccessException.class, () -> matchService.delete(1));
        verify(matchRepository, times(1)).deleteById(1);
    }

    @Test
    public void testCreateMatchStandardMode() {
        Player player = new Player();
        player.setId(1);
        player.setFirstName("Juan");
        player.setLastName("Martos");
        
        MatchCreateRequest createRequest = new MatchCreateRequest();
        createRequest.setMode(MatchMode.STANDARD);
        
        Match standardMatch = new Match();
        standardMatch.setId(1);
        standardMatch.setMode(MatchMode.STANDARD);
        standardMatch.setCreator(player);
        
        when(matchRepository.save(any(Match.class))).thenReturn(standardMatch);
        
        try (MockedStatic<MatchFactory> mockedFactory = mockStatic(MatchFactory.class)) {
            mockedFactory.when(() -> MatchFactory.createStandardMatch(player)).thenReturn(standardMatch);
            
            Match result = matchService.createMatch(player, createRequest);
            
            assertNotNull(result);
            assertEquals(MatchMode.STANDARD, result.getMode());
            assertEquals(player, result.getCreator());
            
            verify(matchRepository).save(standardMatch);
            mockedFactory.verify(() -> MatchFactory.createStandardMatch(player));
        }
    }

    @Test
    public void testCreateMatchSlowMode() {
        Player player = new Player();
        player.setId(1);
        
        MatchCreateRequest createRequest = new MatchCreateRequest();
        createRequest.setMode(MatchMode.SLOW);
        
        Match slowMatch = new Match();
        slowMatch.setId(1);
        slowMatch.setMode(MatchMode.SLOW);
        slowMatch.setCreator(player);
        
        when(matchRepository.save(any(Match.class))).thenReturn(slowMatch);
        
        try (MockedStatic<MatchFactory> mockedFactory = mockStatic(MatchFactory.class)) {
            mockedFactory.when(() -> MatchFactory.createSlowMatch(player)).thenReturn(slowMatch);
            
            Match result = matchService.createMatch(player, createRequest);
            
            assertNotNull(result);
            assertEquals(MatchMode.SLOW, result.getMode());
            assertEquals(player, result.getCreator());
            
            verify(matchRepository).save(slowMatch);
            mockedFactory.verify(() -> MatchFactory.createSlowMatch(player));
        }
    }

@Test
    public void testCreateMatchFastMode() {
        Player player = new Player();
        player.setId(1);
        
        MatchCreateRequest createRequest = new MatchCreateRequest();
        createRequest.setMode(MatchMode.FAST);
        
        Match fastMatch = new Match();
        fastMatch.setId(1);
        fastMatch.setMode(MatchMode.FAST);
        fastMatch.setCreator(player);
        
        when(matchRepository.save(any(Match.class))).thenReturn(fastMatch);
        
        try (MockedStatic<MatchFactory> mockedFactory = mockStatic(MatchFactory.class)) {
            mockedFactory.when(() -> MatchFactory.createFastMatch(player)).thenReturn(fastMatch);
            
            Match result = matchService.createMatch(player, createRequest);
            
            assertNotNull(result);
            assertEquals(MatchMode.FAST, result.getMode());
            assertEquals(player, result.getCreator());
            
            verify(matchRepository).save(fastMatch);
            mockedFactory.verify(() -> MatchFactory.createFastMatch(player));
        }
    }

    @Test
    public void testCreateMatchInvalidModeNull() {
        Player player = new Player();
        player.setId(1);
        
        MatchCreateRequest createRequest = new MatchCreateRequest();
        createRequest.setMode(null); // Modo nulo
        
        NullPointerException exception = assertThrows(
            NullPointerException.class, 
            () -> matchService.createMatch(player, createRequest)
        );
        
        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    @Transactional
    public void testJoinMatchSuccess() {
        Integer matchId = 1;
        
        Player existingPlayer = new Player();
        existingPlayer.setId(1);
        
        Player newPlayer = new Player();
        newPlayer.setId(2);
        
        Match match = new Match();
        match.setId(matchId);
        match.setPlayers(new ArrayList<>(List.of(existingPlayer)));
        
        Match savedMatch = new Match();
        savedMatch.setId(matchId);
        savedMatch.setPlayers(new ArrayList<>(List.of(existingPlayer, newPlayer)));
        
        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
        when(matchRepository.save(match)).thenReturn(savedMatch);
        
        Match result = matchService.joinMatch(matchId, newPlayer);
        
        assertNotNull(result);
        assertTrue(result.getPlayers().contains(newPlayer));
        assertEquals(2, result.getPlayers().size());
        verify(matchRepository).findById(matchId);
        verify(matchRepository).save(match);
    }

    @Test
    @Transactional
    public void testJoinMatchPlayerAlreadyInMatch() {
        Integer matchId = 1;
        
        Player player = new Player();
        player.setId(1);
        
        Match match = new Match();
        match.setId(matchId);
        match.setPlayers(new ArrayList<>(List.of(player)));
        
        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
        when(matchRepository.save(match)).thenReturn(match);
        
        Match result = matchService.joinMatch(matchId, player);
        
        assertNotNull(result);
        assertEquals(1, result.getPlayers().size()); // No se debe duplicar
        assertTrue(result.getPlayers().contains(player));
        verify(matchRepository).findById(matchId);
        verify(matchRepository).save(match);
    }

    @Test
    @Transactional
    public void testJoinMatchFull() {
        Integer matchId = 1;
        
        Player player1 = new Player();
        player1.setId(1);
        Player player2 = new Player();
        player2.setId(2);
        Player player3 = new Player();
        player3.setId(3);
        Player player4 = new Player();
        player4.setId(4);
        Player player5 = new Player();
        player5.setId(5);
        
        Match match = new Match();
        match.setId(matchId);
        match.setPlayers(new ArrayList<>(List.of(player1, player2, player3, player4))); // 4 players (full)
        
        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
        when(matchRepository.save(match)).thenReturn(match);
        
        Match result = matchService.joinMatch(matchId, player5);
        
        assertNotNull(result);
        assertEquals(4, result.getPlayers().size()); // Should remain 4
        assertFalse(result.getPlayers().contains(player5)); // New player should not be added
        verify(matchRepository).findById(matchId);
        verify(matchRepository).save(match);
    }

    @Test
    @Transactional
    public void testJoinMatchNotFound() {
        Integer matchId = 999;
        Player player = new Player();
        player.setId(1);
        
        when(matchRepository.findById(matchId)).thenReturn(Optional.empty());
        
        assertThrows(
            NoSuchElementException.class,
            () -> matchService.joinMatch(matchId, player)
        );
        
        verify(matchRepository).findById(matchId);
        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    @Transactional
    public void testJoinMatchWith3PlayersSuccess() {
        Integer matchId = 1;
        
        Player player1 = new Player();
        player1.setId(1);
        Player player2 = new Player();
        player2.setId(2);
        Player player3 = new Player();
        player3.setId(3);
        Player newPlayer = new Player();
        newPlayer.setId(4);
        
        Match match = new Match();
        match.setId(matchId);
        match.setPlayers(new ArrayList<>(List.of(player1, player2, player3))); // 3 players
        
        Match savedMatch = new Match();
        savedMatch.setId(matchId);
        savedMatch.setPlayers(new ArrayList<>(List.of(player1, player2, player3, newPlayer)));
        
        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
        when(matchRepository.save(match)).thenReturn(savedMatch);
        
        Match result = matchService.joinMatch(matchId, newPlayer);
        
        assertNotNull(result);
        assertTrue(result.getPlayers().contains(newPlayer));
        assertEquals(4, result.getPlayers().size()); // Should be exactly 4 now
        verify(matchRepository).save(match);
    }

    @Test
    @Transactional
    public void testJoinMatchWithNullPlayer() {
        Integer matchId = 1;
        Player nullPlayer = null;
        
        Match match = new Match();
        match.setId(matchId);
        match.setPlayers(new ArrayList<>());
        
        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
        when(matchRepository.save(match)).thenReturn(match);
        
        Match result = matchService.joinMatch(matchId, nullPlayer);
        
        assertNotNull(result);
        assertTrue(result.getPlayers().contains(nullPlayer));
        assertEquals(1, result.getPlayers().size());
        verify(matchRepository).save(match);
    }

    @Test
    @Transactional
    public void testRemovePlayerFromMatchSuccess() {
        Integer matchId = 1;
        Integer playerId = 1;
        
        Player player1 = new Player();
        player1.setId(1);
        
        Player player2 = new Player();
        player2.setId(2);
        
        Match match = new Match();
        match.setId(matchId);
        match.setPlayers(new ArrayList<>(List.of(player1, player2)));
        match.setCreator(player2); // Creator is NOT the player being removed
        
        Board board = new Board();
        board.setPlayer(player1);
        
        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player1));
        when(boardRepository.findByMatchIdAndPlayerId(matchId, playerId)).thenReturn(Optional.of(board));
        when(matchRepository.save(match)).thenReturn(match);
        
        
        Match result = matchService.removePlayerFromMatch(matchId, playerId);
        
        
        assertNotNull(result);
        assertFalse(result.getPlayers().contains(player1));
        assertEquals(1, result.getPlayers().size());
        verify(matchRepository).findById(matchId);
        verify(playerRepository).findById(playerId);
        verify(boardRepository).findByMatchIdAndPlayerId(matchId, playerId);
        verify(boardRepository).delete(board);
        verify(matchRepository).save(match);
        verify(matchRepository, never()).delete(any(Match.class));
    }

    @Test
    @Transactional
    public void testRemovePlayerFromMatchNoBoardToDelete() {
        Integer matchId = 1;
        Integer playerId = 1;
        
        Player player1 = new Player();
        player1.setId(1);
        
        Player player2 = new Player();
        player2.setId(2);
        
        Match match = new Match();
        match.setId(matchId);
        match.setPlayers(new ArrayList<>(List.of(player1, player2)));
        match.setCreator(player2);
        
        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player1));
        when(boardRepository.findByMatchIdAndPlayerId(matchId, playerId)).thenReturn(Optional.empty());
        when(matchRepository.save(match)).thenReturn(match);
        
        Match result = matchService.removePlayerFromMatch(matchId, playerId);
        
        assertNotNull(result);
        assertFalse(result.getPlayers().contains(player1));
        verify(boardRepository, never()).delete(any(Board.class)); // No board to delete
        verify(matchRepository).save(match);
    }

    @Test
    @Transactional
    public void testRemovePlayerFromMatchNotFound() {
        Integer matchId = 999;
        Integer playerId = 1;
        
        when(matchRepository.findById(matchId)).thenReturn(Optional.empty());
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(new Player()));
        
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> matchService.removePlayerFromMatch(matchId, playerId)
        );
        
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Match or player not found"));
        verify(matchRepository).findById(matchId);
        verify(matchRepository, never()).save(any(Match.class));
        verify(matchRepository, never()).delete(any(Match.class));
    }

    @Test
    @Transactional
    public void testRemovePlayerFromMatchPlayerNotFound() {
        Integer matchId = 1;
        Integer playerId = 999;
        
        Match match = new Match();
        match.setId(matchId);
        
        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
        when(playerRepository.findById(playerId)).thenReturn(Optional.empty());
        
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> matchService.removePlayerFromMatch(matchId, playerId)
        );
        
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Match or player not found"));
        verify(playerRepository).findById(playerId);
        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    @Transactional
    public void testRemovePlayerFromMatchPlayerNotInMatch() {
        
        Integer matchId = 1;
        Integer playerId = 1;
        
        Player player1 = new Player();
        player1.setId(1);
        
        Player player2 = new Player();
        player2.setId(2);
        
        Match match = new Match();
        match.setId(matchId);
        match.setPlayers(new ArrayList<>(List.of(player2)));
        
        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player1));
        
        
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> matchService.removePlayerFromMatch(matchId, playerId)
        );
        
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Player not in match"));
        verify(matchRepository, never()).save(any(Match.class));
        verify(matchRepository, never()).delete(any(Match.class));
    }

    @Test
    @Transactional
    public void testRemovePlayerFromMatchBothNullParameters() {
        
        Integer matchId = 1;
        Integer playerId = 1;
        
        when(matchRepository.findById(matchId)).thenReturn(Optional.empty());
        when(playerRepository.findById(playerId)).thenReturn(Optional.empty());
        
       
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> matchService.removePlayerFromMatch(matchId, playerId)
        );
        
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Match or player not found"));
    }

    @Test
    @Transactional
    public void testRemovePlayerFromMatchLastPlayerDeletesMatch() {
        Integer matchId = 1;
        Integer playerId = 1;
        
        Match singlePlayerMatch = new Match();
        singlePlayerMatch.setId(1);
        singlePlayerMatch.setCreator(testPlayer1);
        singlePlayerMatch.setPlayers(new ArrayList<>(Arrays.asList(testPlayer1)));
        
        when(matchRepository.findById(matchId)).thenReturn(Optional.of(singlePlayerMatch));
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(testPlayer1));

        Match result = matchService.removePlayerFromMatch(matchId, playerId);

        assertNull(result, "Should return null when match is deleted");
        
        verify(matchRepository).findById(matchId);
        verify(playerRepository).findById(playerId);
        verify(matchRepository).delete(singlePlayerMatch);
        verify(boardRepository, never()).findByMatchIdAndPlayerId(any(), any());
        verify(matchRepository, never()).save(any());
    }

    @Test
    @Transactional
    public void testRemovePlayerFromMatchCreatorChanges() {
        Integer matchId = 1;
        Integer playerId = 1; 
        
        when(matchRepository.findById(matchId)).thenReturn(Optional.of(testMatch));
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(testPlayer1));
        when(boardRepository.findByMatchIdAndPlayerId(matchId, playerId)).thenReturn(Optional.empty());
        when(matchRepository.save(testMatch)).thenReturn(testMatch);

        Match result = matchService.removePlayerFromMatch(matchId, playerId);

        assertNotNull(result, "Should return updated match");
        assertEquals(1, result.getPlayers().size(), "Should have 2 players left");
        assertFalse(result.getPlayers().contains(testPlayer1), "Should not contain removed creator");
        assertEquals(testPlayer2, result.getCreator(), "Creator should change to first remaining player");
        
        verify(matchRepository).findById(matchId);
        verify(playerRepository).findById(playerId);
        verify(boardRepository).findByMatchIdAndPlayerId(matchId, playerId);
        verify(matchRepository).save(testMatch);
    }


    @Test
    @Transactional
    public void testFinishMatchSuccess() {
        
        Integer matchId = 1;
        Match match = new Match();
        match.setId(matchId);
        match.setState(MatchState.STARTED);
        match.setFinishDate(null);
        
        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
        when(matchRepository.save(match)).thenReturn(match);
        
        matchService.finishMatch(matchId);
        
        assertEquals(MatchState.FINISHED, match.getState());
        assertNotNull(match.getFinishDate());
        verify(matchRepository).findById(matchId);
        verify(matchRepository).save(match);
    }

    @Test
    public void testFinishMatchNotFound() {
        Integer matchId = 999;
        when(matchRepository.findById(matchId)).thenReturn(Optional.empty());
        
        assertThrows(
            NoSuchElementException.class,
            () -> matchService.finishMatch(matchId)
        );
        
        verify(matchRepository).findById(matchId);
        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    @Transactional
    public void testGetMatchByIdWithAccessCheckSuccess() {
        Integer matchId = 1;
        Integer userId = 1;
        
        User user = new User();
        user.setId(userId);
        
        Player player = new Player();
        player.setId(1);
        player.setUser(user);
        
        Match match = new Match();
        match.setId(matchId);
        match.setPlayers(List.of(player));
        
        when(matchRepository.findMatchWithPlayersById(matchId)).thenReturn(Optional.of(match));
        
        ResponseEntity<?> result = matchService.getMatchByIdWithAccessCheck(matchId, userId);
        
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(match, result.getBody());
        verify(matchRepository).findMatchWithPlayersById(matchId);
    }

    @Test
    @Transactional
    public void testGetMatchByIdWithAccessCheckUserNotInMatch() {
        Integer matchId = 1;
        Integer userId = 999; 
        
        when(matchRepository.findMatchWithPlayersById(matchId)).thenReturn(Optional.of(testMatch));

        ResponseEntity<?> result = matchService.getMatchByIdWithAccessCheck(matchId, userId);

        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
        assertEquals("No tienes permiso para acceder a esta partida", result.getBody());
        
        verify(matchRepository).findMatchWithPlayersById(matchId);
    }

    @Test
    public void testGetMatchByIdWithAccessCheckNotFound() {
        Integer matchId = 999;
        Integer userId = 1;
        
        when(matchRepository.findMatchWithPlayersById(matchId)).thenReturn(Optional.empty());
        
        ResponseEntity<?> result = matchService.getMatchByIdWithAccessCheck(matchId, userId);
        
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        verify(matchRepository).findMatchWithPlayersById(matchId);
    }

    @Test
    @Transactional
    public void testStartMatchSuccess() {
        Integer matchId = 1;
        Match match = new Match();
        match.setId(matchId);
        match.setState(MatchState.CREATED);
        
        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
        when(matchRepository.save(match)).thenReturn(match);
        
        Match result = matchService.startMatch(matchId);
        
        assertNotNull(result);
        assertEquals(MatchState.STARTED, result.getState());
        verify(matchRepository).findById(matchId);
        verify(matchRepository).save(match);
    }

    @Test
    public void testStartMatchNotFound() {
        Integer matchId = 999;
        when(matchRepository.findById(matchId)).thenReturn(Optional.empty());
        
        assertThrows(
            NoSuchElementException.class,
            () -> matchService.startMatch(matchId)
        );
        
        verify(matchRepository).findById(matchId);
        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    @Transactional
    public void testHandleFinishMatchSuccess() {
        Integer matchId = 1;
        Match match = new Match();
        match.setId(matchId);
        
        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
        when(matchRepository.save(match)).thenReturn(match);
        
        ResponseEntity<Void> result = matchService.handleFinishMatch(matchId);
        
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(matchRepository, times(2)).findById(matchId); // Called twice: handleFinishMatch + finishMatch
    }

    @Test
    public void testHandleFinishMatchNotFound() {
        Integer matchId = 999;
        when(matchRepository.findById(matchId)).thenReturn(Optional.empty());
        
        ResponseEntity<Void> result = matchService.handleFinishMatch(matchId);
        
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        verify(matchRepository).findById(matchId);
    }

    @Test
    @Transactional
    public void testHandleFinishMatchInternalServerError() {
        Integer matchId = 1;
        Match match = new Match();
        match.setId(matchId);
        
        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
        when(matchRepository.save(match)).thenThrow(new RuntimeException("Database error"));
        
        ResponseEntity<Void> result = matchService.handleFinishMatch(matchId);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        verify(matchRepository, times(2)).findById(matchId);
    }


    @Test
    public void testGetFilteredCreatedMatchesSuccess() {
        Player player = new Player();
        player.setId(1);
        
        Match matchWithPlayers = new Match();
        matchWithPlayers.setId(1);
        matchWithPlayers.setPlayers(List.of(player));
        
        Match matchWithoutPlayers = new Match();
        matchWithoutPlayers.setId(2);
        matchWithoutPlayers.setPlayers(new ArrayList<>());
        
        List<Match> createdMatches = List.of(matchWithPlayers, matchWithoutPlayers);
        
        when(matchRepository.findCreatedMatches()).thenReturn(createdMatches);
        
        List<Match> result = matchService.getFilteredCreatedMatches();
        
        assertEquals(1, result.size());
        assertEquals(matchWithPlayers, result.get(0));
        verify(matchRepository).findCreatedMatches();
    }

    @Test
    public void testGetFilteredCreatedMatchesEmpty() {
        when(matchRepository.findCreatedMatches()).thenReturn(new ArrayList<>());
        
        List<Match> result = matchService.getFilteredCreatedMatches();
        
        assertTrue(result.isEmpty());
        verify(matchRepository).findCreatedMatches();
    }

    @Test
    @Transactional
    public void testUpdateMatchSuccess() {
        Integer matchId = 1;
        Integer userId = 1;
        Integer version = 1;
        
        User user = new User();
        user.setId(userId);
        
        Player player = new Player();
        player.setId(1);
        player.setUser(user);
        
        Match match = new Match();
        match.setId(matchId);
        match.setVersion(version);
        match.setPlayers(List.of(player));
        match.setCurrentActivePlayer(0);
        
        MatchUpdateRequest updateRequest = new MatchUpdateRequest();
        updateRequest.setVersion(version);
        updateRequest.setCurrentActivePlayer(1);
        updateRequest.setRound(2);
        
        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
        when(matchRepository.save(match)).thenReturn(match);
        
        ResponseEntity<Match> result = matchService.updateMatch(matchId, updateRequest, userId);
        
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(match, result.getBody());
        assertEquals(1, match.getCurrentActivePlayer());
        assertEquals(2, match.getRound());
        verify(matchRepository).save(match);
    }

    @Test
    public void testUpdateMatchNotFound() {
        Integer matchId = 999;
        Integer userId = 1;
        MatchUpdateRequest updateRequest = new MatchUpdateRequest();
        
        when(matchRepository.findById(matchId)).thenReturn(Optional.empty());
        
        assertThrows(
            ResponseStatusException.class,
            () -> matchService.updateMatch(matchId, updateRequest, userId)
        );
        
        verify(matchRepository).findById(matchId);
        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    @Transactional
    public void testUpdateMatchUserNotInMatch() {
        Integer matchId = 1;
        Integer userId = 999; // Usuario que no está en la partida
        Integer version = 1;
        
        testMatch.setVersion(version);
        
        MatchUpdateRequest updateRequest = new MatchUpdateRequest();
        updateRequest.setVersion(version);
        updateRequest.setCurrentActivePlayer(1);
        
        when(matchRepository.findById(matchId)).thenReturn(Optional.of(testMatch));
        
        ResponseEntity<Match> result = matchService.updateMatch(matchId, updateRequest, userId);
        
        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
        assertNull(result.getBody());
        
        verify(matchRepository).findById(matchId);
        verify(matchRepository, never()).save(any());
    }

    @Test
    @Transactional
    public void testUpdateMatchWithDice() {
        Integer matchId = 1;
        Integer userId = 1;
        Integer version = 1;
        
        testMatch.setVersion(version);
        
        List<Integer> diceValues = Arrays.asList(1, 2, 3, 4, 5, 6);
        
        MatchUpdateRequest updateRequest = new MatchUpdateRequest();
        updateRequest.setVersion(version);
        updateRequest.setDice(diceValues);
        
        when(matchRepository.findById(matchId)).thenReturn(Optional.of(testMatch));
        when(matchRepository.save(testMatch)).thenReturn(testMatch);
        
        ResponseEntity<Match> result = matchService.updateMatch(matchId, updateRequest, userId);
        
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(testMatch, result.getBody());
        assertEquals(diceValues, testMatch.getDice());
        
        verify(matchRepository).findById(matchId);
        verify(matchRepository).save(testMatch);
    }

    @Test
    @Transactional
    public void testUpdateMatchWithTerritory() {
        Integer matchId = 1;
        Integer userId = 1;
        Integer version = 1;
        
        testMatch.setVersion(version);
        
        String territory = "TestTerritory";
        
        MatchUpdateRequest updateRequest = new MatchUpdateRequest();
        updateRequest.setVersion(version);
        updateRequest.setTerritory(territory);
        
        when(matchRepository.findById(matchId)).thenReturn(Optional.of(testMatch));
        when(matchRepository.save(testMatch)).thenReturn(testMatch);
        
        ResponseEntity<Match> result = matchService.updateMatch(matchId, updateRequest, userId);
        
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(testMatch, result.getBody());
        assertEquals(territory, testMatch.getTerritory());
        
        verify(matchRepository).findById(matchId);
        verify(matchRepository).save(testMatch);
    }

    @Test
    @Transactional
    public void testUpdateMatchInternalServerError() {
        Integer matchId = 1;
        Integer userId = 1;
        Integer version = 1;
        
        testMatch.setVersion(version);
        
        MatchUpdateRequest updateRequest = new MatchUpdateRequest();
        updateRequest.setVersion(version);
        updateRequest.setCurrentActivePlayer(1);
        
        when(matchRepository.findById(matchId)).thenReturn(Optional.of(testMatch));
        when(matchRepository.save(testMatch)).thenThrow(new RuntimeException("Database error"));
        
        ResponseEntity<Match> result = matchService.updateMatch(matchId, updateRequest, userId);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertNull(result.getBody());
        
        verify(matchRepository).findById(matchId);
        verify(matchRepository).save(testMatch);
    }

    @Test
    public void testGetWinnersSuccess() {
        Player player1 = new Player();
        Player player2 = new Player();
        Board board1 = new Board();
        board1.setPlayer(player1);
        board1.setScore(10);
        Board board2 = new Board();
        board2.setPlayer(player2);
        board2.setScore(10);

        when(boardRepository.findByMatchId(1)).thenReturn(List.of(board1, board2));

        List<Player> winners = matchService.getWinners(1);

        assertTrue(winners.contains(player1));
        assertTrue(winners.contains(player2));
    }

    @Test
    public void testGetWinnersNegativeNoBoards() {
        when(boardRepository.findByMatchId(1)).thenReturn(Collections.emptyList());
        assertThrows(ResponseStatusException.class, () -> matchService.getWinners(1));
    }

}
