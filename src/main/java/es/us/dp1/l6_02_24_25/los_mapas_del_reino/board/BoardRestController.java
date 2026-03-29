package es.us.dp1.l6_02_24_25.los_mapas_del_reino.board;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import es.us.dp1.l6_02_24_25.los_mapas_del_reino.exceptions.ResourceNotFoundException;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.user.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/boards")
@Tag(name = "Board", description = "The Board management API")
@CrossOrigin(origins = "http://localhost:3000")
public class BoardRestController {

    private final BoardService boardService;
    private final UserService userService;

    @Autowired
    public BoardRestController(BoardService boardService, UserService userService) {
        this.boardService = boardService;
        this.userService = userService;
    }

    @GetMapping()
    public ResponseEntity<List<Board>> findAll() {
        return ResponseEntity.ok(boardService.findAll());
    }

    @GetMapping("/match/{matchId}")
    public ResponseEntity<List<Board>> findBoardsByMatchId(@PathVariable("matchId") Integer matchId) {
        return ResponseEntity.ok(boardService.findBoardsByMatchId(matchId));
    }

    @GetMapping("/player/{playerId}")
    public ResponseEntity<List<Board>> findBoardsByPlayerId(@PathVariable("playerId") Integer playerId) {
        return ResponseEntity.ok(boardService.findBoardsByPlayerId(playerId));
    }

    @GetMapping("/{matchId}/{playerId}")
    public ResponseEntity<?> findBoardByMatchIdAndUserId(
            @PathVariable("matchId") Integer matchId,
            @PathVariable("playerId") Integer userId) {

        Integer currentUserId = userService.findCurrentUser().getId();

        if (!currentUserId.equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No puedes acceder al tablero de otro jugador");
        }

        Board board = boardService.findBoardByMatchIdAndUserId(matchId, userId);
        return ResponseEntity.ok(board);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Board> findBoardById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(boardService.findBoardById(id));
    }

    @PostMapping
    public ResponseEntity<Board> createBoard(@RequestBody BoardRequest boardRequest) {
        try {
            Board savedBoard = boardService.createBoard(boardRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedBoard);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @MessageMapping("boards/create/{gameId}")
    @SendTo("/topic/boards/create/{gameId}")
    public BoardDTO returnBoard(@DestinationVariable String gameId, BoardDTO board) {
        return board;
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> deleteBoardById(@PathVariable("boardId") Integer boardId) {
        try {
            boardService.deleteBoardById(boardId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{boardId}/score")
    public ResponseEntity<Board> updateBoardScore(@PathVariable("boardId") Integer boardId,
                                                  @RequestBody BoardDTO request) {
        Board updated = boardService.updateBoardScore(boardId, request.getScore());
        return ResponseEntity.ok(updated);
    }
}
