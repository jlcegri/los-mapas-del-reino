package es.us.dp1.l6_02_24_25.los_mapas_del_reino.player;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import es.us.dp1.l6_02_24_25.los_mapas_del_reino.exceptions.ResourceNotFoundException;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.user.UserService;

@Service
public class PlayerService {
    @Autowired
    private PlayerRepository playerRepository;
    
    @Autowired
    private final UserService userService;

    public PlayerService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    private PasswordEncoder encoder;

    @Transactional(readOnly = true)
    public Iterable<Player> findAll() throws DataAccessException {
        return playerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Player findPlayerById(int id) throws DataAccessException {
        return this.playerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Player", "ID", id));
    }

    @Transactional(readOnly = true)
    public Player findPlayerByUserId(Integer userId) {
        return playerRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Player savePlayer(Player player) throws DataAccessException {
        playerRepository.save(player);
        return player;
    }

    @Transactional
    public Player createPlayer(Player player) {
        try {
            Player newPlayer = new Player();
            BeanUtils.copyProperties(player, newPlayer, "id");
            newPlayer.setUser(userService.findCurrentUser());
            return savePlayer(newPlayer);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating player");
        }
    }

    @Transactional
    public Player updatePlayer(Player player, int id) throws DataAccessException {
        try {
            Player toUpdate = findPlayerById(id);
            BeanUtils.copyProperties(player, toUpdate, "id", "user");

            if (player.getUser() != null) {
                if (player.getUser().getUsername() != null) {
                    toUpdate.getUser().setUsername(player.getUser().getUsername());
                }

                if (player.getUser().getPassword() != null && !player.getUser().getPassword().isBlank()) {
                    String hashedPassword = encoder.encode(player.getUser().getPassword());
                    toUpdate.getUser().setPassword(hashedPassword);
                }
            }

            return savePlayer(toUpdate);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating player");
        }
    }

    @Transactional
    public void deletePlayer(int id) throws DataAccessException {
        Player toDelete = findPlayerById(id);
        playerRepository.delete(toDelete);
    }
}