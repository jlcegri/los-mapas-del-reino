package es.us.dp1.l6_02_24_25.los_mapas_del_reino.board;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Query;


public interface BoardRepository extends CrudRepository<Board, Integer> {
    
    List<Board> findAll();

    @Query("SELECT b FROM Board b WHERE b.player.id= :playerId")
    List<Board> findByPlayerId(Integer playerId);

    @Query("SELECT b FROM Board b WHERE b.match.id = :matchId")
    List<Board> findByMatchId(Integer matchId);

    @Query("SELECT b FROM Board b WHERE b.player.id= :playerId AND b.match.id= :matchId")
    Optional<Board> findByMatchIdAndPlayerId(Integer matchId, Integer playerId);
}
