package es.us.dp1.l6_02_24_25.los_mapas_del_reino.match;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import es.us.dp1.l6_02_24_25.los_mapas_del_reino.player.Player;

public class MatchFactory {
    
    public static Match createStandardMatch(Player player) {
        return new MatchBuilder()
                .withPlayers(new ArrayList<>(List.of(player)))
                .withStartDate(LocalDateTime.now())
                .withCreator(player)
                .withMode(MatchMode.STANDARD)
                .build();
    }

    public static Match createSlowMatch(Player player) {
        return new MatchBuilder()
                .withPlayers(new ArrayList<>(List.of(player)))
                .withStartDate(LocalDateTime.now())
                .withCreator(player)
                .withMode(MatchMode.SLOW)
                .build();
    }

    public static Match createFastMatch(Player player) {
        return new MatchBuilder()
                .withPlayers(new ArrayList<>(List.of(player)))
                .withStartDate(LocalDateTime.now())
                .withCreator(player)
                .withMode(MatchMode.FAST)
                .build();
    }
    
}
