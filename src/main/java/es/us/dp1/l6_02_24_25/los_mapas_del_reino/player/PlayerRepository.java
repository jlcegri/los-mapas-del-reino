package es.us.dp1.l6_02_24_25.los_mapas_del_reino.player;

import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.CrudRepository;

public interface PlayerRepository extends CrudRepository<Player, Integer> {
    
    @Query("SELECT p FROM Player p WHERE p.user.id= :userId")
    Player findByUserId(Integer userId);

}
