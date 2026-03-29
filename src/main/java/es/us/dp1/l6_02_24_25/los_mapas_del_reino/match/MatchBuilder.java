package es.us.dp1.l6_02_24_25.los_mapas_del_reino.match;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import es.us.dp1.l6_02_24_25.los_mapas_del_reino.player.Player;

public class MatchBuilder {
    private Match match;

    public MatchBuilder() {
        this.match = new Match();
    }

    public MatchBuilder withPlayers(List<Player> players) {
        this.match.setPlayers(players);
        return this;
    }

    public MatchBuilder withStartDate(LocalDateTime startDate) {
        this.match.setStartDate(startDate);
        return this;
    }

    public MatchBuilder withCreator(Player creator) {
        this.match.setCreator(creator);
        return this;
    }

    public MatchBuilder withMode(MatchMode mode) {
        this.match.setMode(mode);
        return this;
    }

    public Match build() {
        match.setState(MatchState.CREATED);
        match.setWinners(new ArrayList<>());
        match.setFinishDate(null);
        match.setCriteriaA1(null);
        match.setCriteriaA2(null);
        match.setCriteriaB1(null);
        match.setCriteriaB2(null);
        match.setCurrentActivePlayer(0);
        match.setCurrentPlayerTurn(0);
        match.setDice(new ArrayList<>());
        match.setRound(0);
        match.setIsLastRound(false);
        match.setIsLastTurn(false);
        match.setTerritory(null);
        return match;
    }
}
