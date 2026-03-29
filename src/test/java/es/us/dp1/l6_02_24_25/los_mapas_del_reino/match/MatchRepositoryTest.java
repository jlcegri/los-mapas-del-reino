package es.us.dp1.l6_02_24_25.los_mapas_del_reino.match;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class MatchRepositoryTest {
    
    @Mock
    private MatchRepository matchRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindAllSuccess() {
        Match match1 = new Match();
        Match match2 = new Match();
        when(matchRepository.findAll()).thenReturn(Arrays.asList(match1, match2));

        List<Match> matches = matchRepository.findAll();
        assertNotNull(matches);
        assertEquals(2, matches.size());
    }

    @Test
    public void testFindAllNotFound() {
        when(matchRepository.findAll()).thenReturn(Collections.emptyList());

        List<Match> matches = matchRepository.findAll();
        assertNotNull(matches);
        assertTrue(matches.isEmpty());
    }

    @Test
    public void testFindCreatedMatchesSuccess() {
        Match match = new Match();
        match.setState(MatchState.CREATED);
        when(matchRepository.findCreatedMatches()).thenReturn(Arrays.asList(match));

        List<Match> matches = matchRepository.findCreatedMatches();
        assertNotNull(matches);
        assertEquals(1, matches.size());
        assertEquals(MatchState.CREATED, matches.get(0).getState());
    }

    @Test
    public void testFindCreatedMatchesNotFound() {
        when(matchRepository.findCreatedMatches()).thenReturn(Collections.emptyList());

        List<Match> matches = matchRepository.findCreatedMatches();
        assertNotNull(matches);
        assertTrue(matches.isEmpty());
    }

    @Test
    public void testFindStartedMatchesSuccess() {
        Match match = new Match();
        match.setState(MatchState.STARTED);
        when(matchRepository.findStartedMatches()).thenReturn(Arrays.asList(match));

        List<Match> matches = matchRepository.findStartedMatches();
        assertNotNull(matches);
        assertEquals(1, matches.size());
        assertEquals(MatchState.STARTED, matches.get(0).getState());
    }

    @Test
    public void testFindStartedMatchesNotFound() {
        when(matchRepository.findStartedMatches()).thenReturn(Collections.emptyList());

        List<Match> matches = matchRepository.findStartedMatches();
        assertNotNull(matches);
        assertTrue(matches.isEmpty());
    }

    @Test
    public void testFindFinishedMatchesSuccess() {
        Match match = new Match();
        match.setState(MatchState.FINISHED);
        when(matchRepository.findFinishedMatches()).thenReturn(Arrays.asList(match));

        List<Match> matches = matchRepository.findFinishedMatches();
        assertNotNull(matches);
        assertEquals(1, matches.size());
        assertEquals(MatchState.FINISHED, matches.get(0).getState());
    }

    @Test
    public void testFindFinishedMatchesNotFound() {
        when(matchRepository.findFinishedMatches()).thenReturn(Collections.emptyList());

        List<Match> matches = matchRepository.findFinishedMatches();
        assertNotNull(matches);
        assertTrue(matches.isEmpty());
    }

    @Test
    public void testFindAllMatchesByPlayerIdSuccess() {
        Match match = new Match();
        when(matchRepository.findAllMatchesByPlayerId(1)).thenReturn(Arrays.asList(match));

        List<Match> matches = matchRepository.findAllMatchesByPlayerId(1);
        assertNotNull(matches);
        assertEquals(1, matches.size());
    }

    @Test
    public void testFindAllMatchesByPlayerIdNotFound() {
        when(matchRepository.findAllMatchesByPlayerId(1)).thenReturn(Collections.emptyList());

        List<Match> matches = matchRepository.findAllMatchesByPlayerId(1);
        assertNotNull(matches);
        assertTrue(matches.isEmpty());
    }
}
