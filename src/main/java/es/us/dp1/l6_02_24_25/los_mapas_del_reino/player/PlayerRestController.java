package es.us.dp1.l6_02_24_25.los_mapas_del_reino.player;

import java.net.URISyntaxException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import es.us.dp1.l6_02_24_25.los_mapas_del_reino.auth.payload.response.MessageResponse;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.util.RestPreconditions;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/players")
@SecurityRequirement(name = "bearerAuth")
public class PlayerRestController {

    private final PlayerService playerService;

    @Autowired
    public PlayerRestController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping
    public ResponseEntity<List<Player>> findAll() {
        return new ResponseEntity<>((List<Player>) playerService.findAll(), HttpStatus.OK);
    }

    @GetMapping(value = "{playerId}")
    public ResponseEntity<Player> findById(@PathVariable("playerId") int id) {
        return new ResponseEntity<>(playerService.findPlayerById(id), HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Player> findByUserId(@PathVariable("userId") int userId) {
        return new ResponseEntity<>(playerService.findPlayerByUserId(userId), HttpStatus.OK);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Player> create(@RequestBody @Valid Player player) throws URISyntaxException {
        Player savedPlayer = this.playerService.createPlayer(player);
        return new ResponseEntity<>(savedPlayer, HttpStatus.CREATED);
    }

    @PutMapping(value = "{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Player> update(@PathVariable("id") int id, @RequestBody @Valid Player player) {
        Player playerUpdated = playerService.updatePlayer(player, id);
        return new ResponseEntity<>(playerUpdated, HttpStatus.OK);
    }     

    @DeleteMapping(value = "{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MessageResponse> delete(@PathVariable("id") int id) {
        RestPreconditions.checkNotNull(playerService.findPlayerById(id), "Player", "ID", id);
        playerService.deletePlayer(id);
        return new ResponseEntity<>(new MessageResponse("Player deleted!"), HttpStatus.OK);
    }
}