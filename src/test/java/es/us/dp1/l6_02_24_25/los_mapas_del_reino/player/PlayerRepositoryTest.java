package es.us.dp1.l6_02_24_25.los_mapas_del_reino.player;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest 
public class PlayerRepositoryTest {

    @Autowired
    private PlayerRepository playerRepository;

    public final static int USER_ID = 15;

    @Test
    public void testFindByUserId() {
        Player player = playerRepository.findByUserId(USER_ID);
        assertEquals(Integer.valueOf(15), Integer.valueOf(player.getUser().getId()));
    
    }

    @Test
    public void testFindByUserIdNotFound() {
        Player player = playerRepository.findByUserId(214);
        assertEquals(null, player);
    }


    
}
