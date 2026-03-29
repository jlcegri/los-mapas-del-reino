package es.us.dp1.l6_02_24_25.los_mapas_del_reino.player;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import es.us.dp1.l6_02_24_25.los_mapas_del_reino.exceptions.ResourceNotFoundException;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.user.User;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.user.UserService;

import java.util.Optional;
import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private PlayerService playerService;

    private Player player;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1);
        user.setUsername("testuser");
        user.setPassword("password");

        player = new Player();
        player.setId(1);
        player.setUser(user);
    }

    @Test
    public void testFindAllSuccess() {
        
        List<Player> players = Arrays.asList(player);
        when(playerRepository.findAll()).thenReturn(players);

        Iterable<Player> result = playerService.findAll();

        assertNotNull(result);
        assertTrue(result.iterator().hasNext());
        verify(playerRepository).findAll();
    }

    @Test
    public void testFindAllEmpty() {
        when(playerRepository.findAll()).thenReturn(Arrays.asList());

        Iterable<Player> result = playerService.findAll();

        assertNotNull(result);
        assertFalse(result.iterator().hasNext());
        verify(playerRepository).findAll();
    }

    @Test
    public void testFindPlayerByIdSuccess() {
        when(playerRepository.findById(1)).thenReturn(Optional.of(player));

        Player result = playerService.findPlayerById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("testuser", result.getUser().getUsername());
        verify(playerRepository).findById(1);
    }

    @Test
    public void testFindPlayerByIdNotFound() {
        when(playerRepository.findById(1)).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> playerService.findPlayerById(1));
        verify(playerRepository, times(1)).findById(1);
    }

    @Test
    public void testFindPlayerByUserIdSuccess() {
        Player player = new Player();
        player.setId(1);

        when(playerRepository.findByUserId(1)).thenReturn(player);
        
        Player foundPlayer = playerService.findPlayerByUserId(1);

        assertNotNull(foundPlayer);
        assertEquals(1, foundPlayer.getId());
        verify(playerRepository, times(1)).findByUserId(1);
    }

    @Test
    public void testFindPlayerByUserIdNotFound() {
        when(playerRepository.findByUserId(1)).thenReturn(null);

        Player foundPlayer = playerService.findPlayerByUserId(1);

        assertNull(foundPlayer);
        verify(playerRepository, times(1)).findByUserId(1);
    }

    @Test
    @Transactional
    public void testSavePlayerSuccess() throws DataAccessException {
        Player player = new Player();

        when(playerRepository.save(player)).thenReturn(player);
        
        Player savedPlayer = playerService.savePlayer(player);

        assertNotNull(savedPlayer);
        verify(playerRepository, times(1)).save(player);
    }

    @Test
    @Transactional
    public void testSavePlayerDataAccessException() {
        Player player = new Player();

        when(playerRepository.save(player)).thenThrow(new DataAccessException("Error") {});
        
        assertThrows(DataAccessException.class, () -> playerService.savePlayer(player));
        verify(playerRepository, times(1)).save(player);
    }

    @Test
    @Transactional
    public void testUpdatePlayerSuccess() {
        Player inputPlayer = new Player();
        inputPlayer.setFirstName("John");
        inputPlayer.setLastName("Doe");
        inputPlayer.setEmail("john.doe@example.com");
        
        User inputUser = new User();
        inputUser.setUsername("newusername");
        inputUser.setPassword("newpassword");
        inputPlayer.setUser(inputUser);
        
        User existingUser = new User();
        existingUser.setId(1);
        existingUser.setUsername("oldusername");
        existingUser.setPassword("oldpassword");
        
        Player existingPlayer = new Player();
        existingPlayer.setId(1);
        existingPlayer.setFirstName("OldName");
        existingPlayer.setLastName("OldLastName");
        existingPlayer.setEmail("old@example.com");
        existingPlayer.setUser(existingUser);
        
        when(playerRepository.findById(1)).thenReturn(Optional.of(existingPlayer));
        when(encoder.encode("newpassword")).thenReturn("hashedNewPassword");
        when(playerRepository.save(existingPlayer)).thenReturn(existingPlayer);
        
        Player result = playerService.updatePlayer(inputPlayer, 1);
        
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals("newusername", result.getUser().getUsername());
        assertEquals("hashedNewPassword", result.getUser().getPassword());
        
        verify(playerRepository).findById(1);
        verify(encoder).encode("newpassword");
        verify(playerRepository).save(existingPlayer);
    }


    @Test
    @Transactional
    public void testUpdatePlayerOnlyUsername() {
        Player inputPlayer = new Player();
        inputPlayer.setFirstName("John");
        inputPlayer.setLastName("Doe");
        inputPlayer.setEmail("john@example.com");
        
        User inputUser = new User();
        inputUser.setUsername("newusername");
        inputPlayer.setUser(inputUser);
        
        User existingUser = new User();
        existingUser.setId(1);
        existingUser.setUsername("oldusername");
        existingUser.setPassword("oldpassword");
        
        Player existingPlayer = new Player();
        existingPlayer.setId(1);
        existingPlayer.setFirstName("OldName");
        existingPlayer.setUser(existingUser);
        
        when(playerRepository.findById(1)).thenReturn(Optional.of(existingPlayer));
        when(playerRepository.save(existingPlayer)).thenReturn(existingPlayer);
        
        Player result = playerService.updatePlayer(inputPlayer, 1);
        
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("newusername", result.getUser().getUsername());
        assertEquals("oldpassword", result.getUser().getPassword()); 
        
        verify(playerRepository).findById(1);
        verify(encoder, never()).encode(anyString());
        verify(playerRepository).save(existingPlayer);
    }

    @Test
    @Transactional
    public void testUpdatePlayerOnlyPassword() {
        Player inputPlayer = new Player();
        inputPlayer.setFirstName("John");
        inputPlayer.setLastName("Doe");
        inputPlayer.setEmail("john@example.com");
        
        User inputUser = new User();
        inputUser.setPassword("newpassword");
        inputPlayer.setUser(inputUser);
        
        User existingUser = new User();
        existingUser.setId(1);
        existingUser.setUsername("oldusername");
        existingUser.setPassword("oldpassword");
        
        Player existingPlayer = new Player();
        existingPlayer.setId(1);
        existingPlayer.setUser(existingUser);
        
        when(playerRepository.findById(1)).thenReturn(Optional.of(existingPlayer));
        when(encoder.encode("newpassword")).thenReturn("hashedNewPassword");
        when(playerRepository.save(existingPlayer)).thenReturn(existingPlayer);
        
        Player result = playerService.updatePlayer(inputPlayer, 1);
        
        assertNotNull(result);
        assertEquals("oldusername", result.getUser().getUsername());
        assertEquals("hashedNewPassword", result.getUser().getPassword());
        
        verify(playerRepository).findById(1);
        verify(encoder).encode("newpassword");
        verify(playerRepository).save(existingPlayer);
    }

    @Test
    @Transactional
    public void testUpdatePlayerWithNullUser() {
        Player inputPlayer = new Player();
        inputPlayer.setFirstName("John");
        inputPlayer.setLastName("Doe");
        inputPlayer.setEmail("john@example.com");
        
        User existingUser = new User();
        existingUser.setId(1);
        existingUser.setUsername("oldusername");
        existingUser.setPassword("oldpassword");
        
        Player existingPlayer = new Player();
        existingPlayer.setId(1);
        existingPlayer.setFirstName("OldName");
        existingPlayer.setUser(existingUser);
        
        when(playerRepository.findById(1)).thenReturn(Optional.of(existingPlayer));
        when(playerRepository.save(existingPlayer)).thenReturn(existingPlayer);
        
        Player result = playerService.updatePlayer(inputPlayer, 1);
        
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("oldusername", result.getUser().getUsername()); 
        assertEquals("oldpassword", result.getUser().getPassword()); 
        
        verify(playerRepository).findById(1);
        verify(encoder, never()).encode(anyString());
        verify(playerRepository).save(existingPlayer);
    }

    @Test
    @Transactional
    public void testUpdatePlayerNotFound() {
        Player inputPlayer = new Player();
        inputPlayer.setFirstName("John");
        inputPlayer.setLastName("Doe");
        inputPlayer.setEmail("john@example.com");
        
        when(playerRepository.findById(999)).thenReturn(Optional.empty());
        
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> playerService.updatePlayer(inputPlayer, 999)
        );
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertEquals("Error updating player", exception.getReason());
        
        verify(playerRepository).findById(999);
        verify(encoder, never()).encode(anyString());
        verify(playerRepository, never()).save(any(Player.class));
    }


    @Test
    @Transactional
    public void testDeletePlayerSuccess() throws DataAccessException {
        Player player = new Player();
        player.setId(1);

        when(playerRepository.findById(1)).thenReturn(Optional.of(player));

        playerService.deletePlayer(1);

        verify(playerRepository, times(1)).findById(1);
        verify(playerRepository, times(1)).delete(player);
    }

    @Test
    @Transactional
    public void testDeletePlayerNotFound() {
        when(playerRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> playerService.deletePlayer(1));
        verify(playerRepository, times(1)).findById(1);
    }
}
