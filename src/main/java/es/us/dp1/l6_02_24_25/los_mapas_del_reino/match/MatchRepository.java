package es.us.dp1.l6_02_24_25.los_mapas_del_reino.match;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface MatchRepository extends CrudRepository<Match, Integer> {

    List<Match> findAll();

    @Query("SELECT m FROM Match m WHERE m.state = 'CREATED' AND SIZE(m.players) > 0")
    List<Match> findCreatedMatches();

    @Query("SELECT m FROM Match m WHERE m.state = STARTED")
    List<Match> findStartedMatches();

    @Query("SELECT m FROM Match m WHERE m.state = FINISHED")
    List<Match> findFinishedMatches();

    @Query("SELECT m FROM Match m JOIN m.players p WHERE p.id = :playerId")
    List<Match> findAllMatchesByPlayerId(@Param("playerId") int playerId);

    @Query("SELECT m FROM Match m LEFT JOIN FETCH m.players WHERE m.id = :id")
    Optional<Match> findMatchWithPlayersById(@Param("id") Integer id);
}