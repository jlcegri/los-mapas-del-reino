package es.us.dp1.l6_02_24_25.los_mapas_del_reino.match;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import es.us.dp1.l6_02_24_25.los_mapas_del_reino.player.Player;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.user.User;

@ExtendWith(MockitoExtension.class)
public class MatchFactoryTest {

    private Player testPlayer;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("testuser");

        testPlayer = new Player();
        testPlayer.setId(1);
        testPlayer.setFirstName("John");
        testPlayer.setLastName("Doe");
        testPlayer.setEmail("john@example.com");
        testPlayer.setUser(testUser);
    }

    @Test
    public void testCreateStandardMatch() {
        LocalDateTime beforeCreation = LocalDateTime.now();

        Match result = MatchFactory.createStandardMatch(testPlayer);

        assertNotNull(result, "Match should not be null");
        assertEquals(MatchMode.STANDARD, result.getMode(), "Mode should be STANDARD");
        assertEquals(testPlayer, result.getCreator(), "Creator should be the test player");
        
        assertNotNull(result.getPlayers(), "Players list should not be null");
        assertEquals(1, result.getPlayers().size(), "Should have exactly one player");
        assertTrue(result.getPlayers().contains(testPlayer), "Player should be in the players list");
        assertEquals(testPlayer, result.getPlayers().get(0), "First player should be the test player");
        
        assertNotNull(result.getStartDate(), "Start date should not be null");
        LocalDateTime afterCreation = LocalDateTime.now();
        assertTrue(result.getStartDate().isAfter(beforeCreation.minusSeconds(1)), 
                   "Start date should be after before creation time");
        assertTrue(result.getStartDate().isBefore(afterCreation.plusSeconds(1)), 
                   "Start date should be before after creation time");
    }

    @Test
    public void testCreateSlowMatch() {
        LocalDateTime beforeCreation = LocalDateTime.now();

        Match result = MatchFactory.createSlowMatch(testPlayer);

        assertNotNull(result, "Match should not be null");
        assertEquals(MatchMode.SLOW, result.getMode(), "Mode should be SLOW");
        assertEquals(testPlayer, result.getCreator(), "Creator should be the test player");
        
        assertNotNull(result.getPlayers(), "Players list should not be null");
        assertEquals(1, result.getPlayers().size(), "Should have exactly one player");
        assertTrue(result.getPlayers().contains(testPlayer), "Player should be in the players list");
        assertEquals(testPlayer, result.getPlayers().get(0), "First player should be the test player");
        
        assertNotNull(result.getStartDate(), "Start date should not be null");
        LocalDateTime afterCreation = LocalDateTime.now();
        assertTrue(result.getStartDate().isAfter(beforeCreation.minusSeconds(1)), 
                   "Start date should be after before creation time");
        assertTrue(result.getStartDate().isBefore(afterCreation.plusSeconds(1)), 
                   "Start date should be before after creation time");
    }

    @Test
    public void testCreateFastMatch() {
        LocalDateTime beforeCreation = LocalDateTime.now();

        Match result = MatchFactory.createFastMatch(testPlayer);

        assertNotNull(result, "Match should not be null");
        assertEquals(MatchMode.FAST, result.getMode(), "Mode should be FAST");
        assertEquals(testPlayer, result.getCreator(), "Creator should be the test player");
        
        assertNotNull(result.getPlayers(), "Players list should not be null");
        assertEquals(1, result.getPlayers().size(), "Should have exactly one player");
        assertTrue(result.getPlayers().contains(testPlayer), "Player should be in the players list");
        assertEquals(testPlayer, result.getPlayers().get(0), "First player should be the test player");
        
        assertNotNull(result.getStartDate(), "Start date should not be null");
        LocalDateTime afterCreation = LocalDateTime.now();
        assertTrue(result.getStartDate().isAfter(beforeCreation.minusSeconds(1)), 
                   "Start date should be after before creation time");
        assertTrue(result.getStartDate().isBefore(afterCreation.plusSeconds(1)), 
                   "Start date should be before after creation time");
    }

    @Test
    public void testCreateStandardMatchWithNullPlayer() {
        assertThrows(NullPointerException.class, 
                     () -> MatchFactory.createStandardMatch(null),
                     "Should throw NullPointerException when player is null");
    }

    @Test
    public void testCreateSlowMatchWithNullPlayer() {
        assertThrows(NullPointerException.class, 
                     () -> MatchFactory.createSlowMatch(null),
                     "Should throw NullPointerException when player is null");
    }

    @Test
    public void testCreateFastMatchWithNullPlayer() {
        assertThrows(NullPointerException.class, 
                     () -> MatchFactory.createFastMatch(null),
                     "Should throw NullPointerException when player is null");
    }

}