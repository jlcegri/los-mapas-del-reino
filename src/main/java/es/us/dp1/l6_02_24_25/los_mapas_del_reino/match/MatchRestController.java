package es.us.dp1.l6_02_24_25.los_mapas_del_reino.match;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import es.us.dp1.l6_02_24_25.los_mapas_del_reino.player.Player;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.player.PlayerService;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.user.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/matches")
@Tag(name = "Matches", description = "The Match management API")
@SecurityRequirement(name = "bearerAuth")
public class MatchRestController {

    private final MatchService matchService;
    private final PlayerService playerService;
    private final UserService userService;

    @Autowired
    public MatchRestController(MatchService matchService, PlayerService playerService, UserService userService) {
        this.matchService = matchService;
        this.playerService = playerService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<Match>> findAll() {
        return new ResponseEntity<>(matchService.findAllMatches(), HttpStatus.OK);
    }

    @GetMapping("{matchId}")
    public ResponseEntity<?> findMatchById(@PathVariable("matchId") int matchId) {
        return matchService.getMatchByIdWithAccessCheck(matchId, userService.findCurrentUser().getId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Match> createMatch(@RequestBody MatchCreateRequest createRequest) {
        Integer userId = userService.findCurrentUser().getId();
        Player player = playerService.findPlayerByUserId(userId);
        Match match = matchService.createMatch(player, createRequest);
        return new ResponseEntity<>(match, HttpStatus.CREATED);
    }

    @GetMapping("/player/{playerId}")
    public ResponseEntity<List<Match>> findMatchsByPlayerId(@PathVariable("playerId") Integer playerId) {
        return new ResponseEntity<>(matchService.findAllMatchesByPlayerId(playerId), HttpStatus.OK);
    }

    @PutMapping("/{matchId}/finish")
    public ResponseEntity<Void> finishMatch(@PathVariable Integer matchId) {
        return matchService.handleFinishMatch(matchId);
    }

    @GetMapping("/created")
    public ResponseEntity<List<Match>> findCreatedMatches() {
        return new ResponseEntity<>(matchService.getFilteredCreatedMatches(), HttpStatus.OK);
    }

    @GetMapping("/started")
    public ResponseEntity<List<Match>> findStartedMatchs() {
        return new ResponseEntity<>(matchService.findStartedMatches(), HttpStatus.OK);
    }

    @GetMapping("/finished")
    public ResponseEntity<List<Match>> findFinishedMatchs() {
        return new ResponseEntity<>(matchService.findFinishedMatches(), HttpStatus.OK);
    }

    @PutMapping("/join/{matchId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Match> joinMatch(@PathVariable("matchId") Integer matchId) {
        Integer userId = userService.findCurrentUser().getId();
        Player player = playerService.findPlayerByUserId(userId);
        Match match = matchService.joinMatch(matchId, player);
        return new ResponseEntity<>(match, HttpStatus.OK);
    }

    @PutMapping("/leave/{matchId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Match> leaveMatch(@PathVariable("matchId") Integer matchId) {
        Integer userId = userService.findCurrentUser().getId();
        Player player = playerService.findPlayerByUserId(userId);
        Match updatedMatch = matchService.removePlayerFromMatch(matchId, player.getId());
        return new ResponseEntity<>(updatedMatch, HttpStatus.OK);
    }

    @DeleteMapping("{matchId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> deleteMatch(@PathVariable("matchId") Integer matchId) {
        matchService.delete(matchId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/start/{matchId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Match> startMatch(@PathVariable("matchId") Integer matchId) {
        Match match = matchService.startMatch(matchId);
        return new ResponseEntity<>(match, HttpStatus.OK);
    }

    @PatchMapping("/{matchId}")
    public ResponseEntity<Match> updateMatch(
            @PathVariable Integer matchId,
            @RequestBody MatchUpdateRequest updateRequest) {

        Integer userId = userService.findCurrentUser().getId();
        return matchService.updateMatch(matchId, updateRequest, userId);
    }

    @GetMapping("/{matchId}/winners")
    public ResponseEntity<List<Player>> getWinners(@PathVariable Integer matchId) {
        try {
            List<Player> winners = matchService.getWinners(matchId);
            return ResponseEntity.ok(winners);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
