package es.us.dp1.l6_02_24_25.los_mapas_del_reino.match;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import es.us.dp1.l6_02_24_25.los_mapas_del_reino.player.Player;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.user.User;

@ExtendWith(MockitoExtension.class)
public class MatchBuilderTest {

    private Player testPlayer;
    private Player testPlayer2;
    private List<Player> testPlayers;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        User user1 = new User();
        user1.setId(1);
        user1.setUsername("player1");

        User user2 = new User();
        user2.setId(2);
        user2.setUsername("player2");

        testPlayer = new Player();
        testPlayer.setId(1);
        testPlayer.setFirstName("John");
        testPlayer.setLastName("Doe");
        testPlayer.setUser(user1);

        testPlayer2 = new Player();
        testPlayer2.setId(2);
        testPlayer2.setFirstName("Jane");
        testPlayer2.setLastName("Smith");
        testPlayer2.setUser(user2);

        testPlayers = new ArrayList<>(Arrays.asList(testPlayer, testPlayer2));
        testDateTime = LocalDateTime.of(2024, 1, 15, 10, 30, 0);
    }

    @Test
    public void testConstructor() {
        MatchBuilder builder = new MatchBuilder();

        assertNotNull(builder, "Builder should not be null");
        assertDoesNotThrow(() -> builder.build(), "Builder should be usable immediately after construction");
    }

    @Test
    public void testWithPlayers() {
        MatchBuilder builder = new MatchBuilder();

        MatchBuilder result = builder.withPlayers(testPlayers);

        assertSame(builder, result, "withPlayers should return the same builder instance");
        
        Match match = builder.build();
        assertEquals(testPlayers, match.getPlayers(), "Players should be set correctly");
        assertEquals(2, match.getPlayers().size(), "Should have 2 players");
        assertTrue(match.getPlayers().contains(testPlayer), "Should contain first test player");
        assertTrue(match.getPlayers().contains(testPlayer2), "Should contain second test player");
    }

    @Test
    public void testWithPlayersNull() {
        MatchBuilder builder = new MatchBuilder();

        MatchBuilder result = builder.withPlayers(null);

        assertSame(builder, result, "withPlayers should return the same builder instance");
        
        Match match = builder.build();
        assertNull(match.getPlayers(), "Players should be null when null is passed");
    }

    @Test
    public void testWithPlayersEmptyList() {
        MatchBuilder builder = new MatchBuilder();
        List<Player> emptyPlayers = new ArrayList<>();

        MatchBuilder result = builder.withPlayers(emptyPlayers);

        assertSame(builder, result, "withPlayers should return the same builder instance");
        
        Match match = builder.build();
        assertEquals(emptyPlayers, match.getPlayers(), "Empty players list should be set correctly");
        assertTrue(match.getPlayers().isEmpty(), "Players list should be empty");
    }

    @Test
    public void testWithStartDate() {
        MatchBuilder builder = new MatchBuilder();

        MatchBuilder result = builder.withStartDate(testDateTime);

        assertSame(builder, result, "withStartDate should return the same builder instance");
        
        Match match = builder.build();
        assertEquals(testDateTime, match.getStartDate(), "Start date should be set correctly");
    }

    @Test
    public void testWithStartDateNull() {
        MatchBuilder builder = new MatchBuilder();

        MatchBuilder result = builder.withStartDate(null);

        assertSame(builder, result, "withStartDate should return the same builder instance");
        
        Match match = builder.build();
        assertNull(match.getStartDate(), "Start date should be null when null is passed");
    }

    @Test
    public void testWithCreator() {
        MatchBuilder builder = new MatchBuilder();

        MatchBuilder result = builder.withCreator(testPlayer);

        assertSame(builder, result, "withCreator should return the same builder instance");
        
        Match match = builder.build();
        assertEquals(testPlayer, match.getCreator(), "Creator should be set correctly");
    }

    @Test
    public void testWithCreatorNull() {
        MatchBuilder builder = new MatchBuilder();

        MatchBuilder result = builder.withCreator(null);

        assertSame(builder, result, "withCreator should return the same builder instance");
        
        Match match = builder.build();
        assertNull(match.getCreator(), "Creator should be null when null is passed");
    }

    @Test
    public void testWithMode() {
        MatchBuilder builder = new MatchBuilder();

        MatchBuilder result = builder.withMode(MatchMode.STANDARD);

        assertSame(builder, result, "withMode should return the same builder instance");
        
        Match match = builder.build();
        assertEquals(MatchMode.STANDARD, match.getMode(), "Mode should be set correctly");
    }

    @Test
    public void testWithModeAllValues() {
        MatchMode[] modes = {MatchMode.STANDARD, MatchMode.FAST, MatchMode.SLOW};
        
        for (MatchMode mode : modes) {
            MatchBuilder builder = new MatchBuilder();

            builder.withMode(mode);
            Match match = builder.build();

            assertEquals(mode, match.getMode(), "Mode should be set correctly for " + mode);
        }
    }

    @Test
    public void testWithModeNull() {
        MatchBuilder builder = new MatchBuilder();

        MatchBuilder result = builder.withMode(null);

        assertSame(builder, result, "withMode should return the same builder instance");
        
        Match match = builder.build();
        assertNull(match.getMode(), "Mode should be null when null is passed");
    }

    @Test
    public void testBuildBasic() {
        MatchBuilder builder = new MatchBuilder();

        Match match = builder.build();

        assertNotNull(match, "Built match should not be null");
        
        assertEquals(MatchState.CREATED, match.getState(), "State should be CREATED");
        assertNotNull(match.getWinners(), "Winners should not be null");
        assertTrue(match.getWinners().isEmpty(), "Winners should be empty");
        assertNull(match.getFinishDate(), "Finish date should be null");
        assertNull(match.getCriteriaA1(), "CriteriaA1 should be null");
        assertNull(match.getCriteriaA2(), "CriteriaA2 should be null");
        assertNull(match.getCriteriaB1(), "CriteriaB1 should be null");
        assertNull(match.getCriteriaB2(), "CriteriaB2 should be null");
        assertEquals(0, match.getCurrentActivePlayer(), "Current active player should be 0");
        assertEquals(0, match.getCurrentPlayerTurn(), "Current player turn should be 0");
        assertNotNull(match.getDice(), "Dice should not be null");
        assertTrue(match.getDice().isEmpty(), "Dice should be empty");
        assertEquals(0, match.getRound(), "Round should be 0");
        assertFalse(match.getIsLastRound(), "IsLastRound should be false");
        assertFalse(match.getIsLastTurn(), "IsLastTurn should be false");
        assertNull(match.getTerritory(), "Territory should be null");
    }

    @Test
    public void testBuildWithAllProperties() {
        MatchBuilder builder = new MatchBuilder();

        Match match = builder
                .withPlayers(testPlayers)
                .withStartDate(testDateTime)
                .withCreator(testPlayer)
                .withMode(MatchMode.FAST)
                .build();

        assertNotNull(match, "Built match should not be null");
        
        assertEquals(testPlayers, match.getPlayers(), "Players should be set");
        assertEquals(testDateTime, match.getStartDate(), "Start date should be set");
        assertEquals(testPlayer, match.getCreator(), "Creator should be set");
        assertEquals(MatchMode.FAST, match.getMode(), "Mode should be set");
        
        assertEquals(MatchState.CREATED, match.getState(), "State should be CREATED");
        assertNotNull(match.getWinners(), "Winners should not be null");
        assertTrue(match.getWinners().isEmpty(), "Winners should be empty");
        assertEquals(0, match.getRound(), "Round should be 0");
    }

   
}