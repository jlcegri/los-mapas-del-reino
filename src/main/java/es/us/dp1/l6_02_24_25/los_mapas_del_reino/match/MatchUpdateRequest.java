package es.us.dp1.l6_02_24_25.los_mapas_del_reino.match;

import java.util.List;


public class MatchUpdateRequest {
    private Integer currentActivePlayer;
    private Integer currentPlayerTurn;  
    private List<Integer> dice;         
    private Integer round;
    private Boolean isLastRound;
    private Boolean isLastTurn;
    private String territory;
    private Integer version;

    // Getters y setters
    public Integer getCurrentActivePlayer() {
        return currentActivePlayer;
    }

    public void setCurrentActivePlayer(Integer currentActivePlayer) {
        this.currentActivePlayer = currentActivePlayer;
    }

    public Integer getCurrentPlayerTurn() {
        return currentPlayerTurn;
    }

    public void setCurrentPlayerTurn(Integer currentPlayerTurn) {
        this.currentPlayerTurn = currentPlayerTurn;
    }

    public List<Integer> getDice() {
        return dice;
    }

    public void setDice(List<Integer> dice) {
        this.dice = dice;
    }

    public Integer getRound() {
        return round;
    }

    public void setRound(Integer round) {
        this.round = round;
    }

    public Boolean getIsLastRound() {
        return isLastRound;
    }

    public void setIsLastRound(Boolean isLastRound) {
        this.isLastRound = isLastRound;
    }

    public Boolean getIsLastTurn() {
        return isLastTurn;
    }

    public void setIsLastTurn(Boolean isLastTurn) {
        this.isLastTurn = isLastTurn;
    }
    public String getTerritory() {
        return territory;
    }

    public void setTerritory(String territory) {
        this.territory = territory;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

}
