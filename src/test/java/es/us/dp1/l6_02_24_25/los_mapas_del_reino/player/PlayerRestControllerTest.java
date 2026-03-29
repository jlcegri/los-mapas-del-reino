package es.us.dp1.l6_02_24_25.los_mapas_del_reino.player;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.us.dp1.l6_02_24_25.los_mapas_del_reino.user.User;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.user.UserService;

@SpringBootTest
@AutoConfigureMockMvc
public class PlayerRestControllerTest {

    @MockBean
    private PlayerService playerService;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Player player1;
    private Player player2;
    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1);
        user.setUsername("user1");
        user.setPassword("password1");

        player1 = new Player();
        player1.setId(1);
        player1.setFirstName("First1");
        player1.setLastName("Last1");
        player1.setEmail("email1");
        player1.setUser(user);

        player2 = new Player();
        player2.setId(2);
        player2.setFirstName("First2");
        player2.setLastName("Last2");
        player2.setEmail("email2");
        player2.setUser(user);
    }

    @Test
    @WithMockUser(username = "player1", authorities = {"PLAYER"})
    public void testFindAll() throws Exception {
        List<Player> players = List.of(player1, player2);

        when(playerService.findAll()).thenReturn(players);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/players"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(player1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(player2.getId()));

        verify(playerService, times(1)).findAll();
    }

    @Test
    @WithMockUser(username = "player1", authorities = {"PLAYER"})
    public void testFindById() throws Exception {
        when(playerService.findPlayerById(player1.getId())).thenReturn(player1);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/players/{id}", player1.getId()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(player1.getId()));

        verify(playerService, times(1)).findPlayerById(player1.getId());
    }


    @Test
    @WithMockUser(username = "player1", authorities = {"PLAYER"})
    public void testFindByUserId() throws Exception {
        when(playerService.findPlayerByUserId(player1.getUser().getId())).thenReturn(player1);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/players/user/{id}", player1.getUser().getId()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(player1.getId()));

        verify(playerService, times(1)).findPlayerByUserId(player1.getUser().getId());
    }


    @Test
    @WithMockUser(username = "player1", authorities = {"PLAYER"})
    public void create_Positive() throws Exception {
        User user = new User();
        user.setId(1);
        user.setUsername("user1");
        user.setPassword("password1");

        Player player = new Player();
        player.setFirstName("First1");
        player.setLastName("Last1");
        player.setEmail("first1@email.com");
        player.setUser(user);

        Player savedPlayer = new Player();
        savedPlayer.setId(1);
        savedPlayer.setFirstName("First1");
        savedPlayer.setLastName("Last1");
        savedPlayer.setEmail("first1@email.com");
        savedPlayer.setUser(user);

        when(userService.findCurrentUser()).thenReturn(user);
        when(playerService.createPlayer(any(Player.class))).thenReturn(savedPlayer);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/players")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(player)))
            .andExpect(status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("First1"));
        }

    @Test
    @WithMockUser(username = "player1", authorities = {"PLAYER"})
    public void update_Positive() throws Exception {
        User user = new User();
        user.setId(1);
        user.setUsername("user1");
        user.setPassword("password1");

        Player player = new Player();
        player.setId(1);
        player.setFirstName("First1");
        player.setLastName("Last1");
        player.setEmail("first1@email.com");
        player.setUser(user);

        Player updatedPlayer = new Player();
        updatedPlayer.setId(1);
        updatedPlayer.setFirstName("Updated");
        updatedPlayer.setLastName("Last1");
        updatedPlayer.setEmail("updated@email.com");
        updatedPlayer.setUser(user);

        when(playerService.updatePlayer(any(Player.class), eq(1))).thenReturn(updatedPlayer);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/players/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(player)))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Updated"));
    }


    @Test
    @WithMockUser(username = "player1", authorities = {"PLAYER"})
    public void delete_Positive() throws Exception {
        Player player = new Player();
        player.setId(1);

        when(playerService.findPlayerById(1)).thenReturn(player);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/players/1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Player deleted!"));
        verify(playerService).deletePlayer(1);
    }

    @Test
    @WithMockUser(username = "player1", authorities = {"PLAYER"})
    public void delete_Negative_NotFound() throws Exception {
        when(playerService.findPlayerById(1)).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/players/1"))
                .andExpect(status().is4xxClientError());
        verify(playerService, never()).deletePlayer(1);
    }

}
