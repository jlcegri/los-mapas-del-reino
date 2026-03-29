package es.us.dp1.l6_02_24_25.los_mapas_del_reino.board;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@Getter
@Setter
public class BoardDTO {

    private Integer playerId;
    private Integer matchId;
    private Integer score;
    private String criteriaA1;
    private String criteriaA2;
    private String criteriaB1;
    private String criteriaB2;
    
    
    public BoardDTO(Integer playerId, Integer matchId, Integer score, String criteriaA1, String criteriaA2, String criteriaB1, String criteriaB2) {
        this.playerId = playerId; 
        this.matchId = matchId;
        this.score = score;
        this.criteriaA1 = criteriaA1;
        this.criteriaA2 = criteriaA2;
        this.criteriaB1 = criteriaB1;
        this.criteriaB2 = criteriaB2;
    }
    
}
