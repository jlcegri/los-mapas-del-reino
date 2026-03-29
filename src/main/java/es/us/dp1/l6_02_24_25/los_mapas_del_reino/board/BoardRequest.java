package es.us.dp1.l6_02_24_25.los_mapas_del_reino.board;

public class BoardRequest {
    private int matchId;
    private int playerId;
    private Criteria criteriaA1;
    private Criteria criteriaA2;
    private Criteria criteriaB1;
    private Criteria criteriaB2;

    // Getters y setters
    public int getMatchId() {
        return matchId;
    }

    public void setMatchId(int matchId) {
        this.matchId = matchId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public Criteria getCriteriaA1() {
        return criteriaA1;
    }

    public void setCriteriaA1(Criteria criteriaA1) {
        this.criteriaA1 = criteriaA1;
    }

    public Criteria getCriteriaA2() {
        return criteriaA2;
    }

    public void setCriteriaA2(Criteria criteriaA2) {
        this.criteriaA2 = criteriaA2;
    }

    public Criteria getCriteriaB1() {
        return criteriaB1;
    }

    public void setCriteriaB1(Criteria criteriaB1) {
        this.criteriaB1 = criteriaB1;
    }

    public Criteria getCriteriaB2() {
        return criteriaB2;
    }

    public void setCriteriaB2(Criteria criteriaB2) {
        this.criteriaB2 = criteriaB2;
    }
}
