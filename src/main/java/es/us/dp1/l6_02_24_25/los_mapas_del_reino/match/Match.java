package es.us.dp1.l6_02_24_25.los_mapas_del_reino.match;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import es.us.dp1.l6_02_24_25.los_mapas_del_reino.board.Criteria;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.model.BaseEntity;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.player.Player;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "matches")
public class Match extends BaseEntity{

    @Size(min = 1, max = 4, message = "La partida debe tener entre 1 y 4 jugadores")
    @ManyToMany
    @JoinTable(name = "match_players",
    joinColumns = @JoinColumn(name="match_id"),
    inverseJoinColumns = @JoinColumn(name="player_id")
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    public List<Player> players;

    @Enumerated(EnumType.STRING)
    MatchState state;
    
    LocalDateTime startDate;

    LocalDateTime finishDate;

    @ManyToOne(optional=false)
    @JoinColumn(name = "creator_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    Player creator;

    @ManyToMany
    @JoinTable(name = "match_winners",
    joinColumns = @JoinColumn(name = "match_id"), 
    inverseJoinColumns = @JoinColumn(name = "player_id")
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    List<Player> winners;

    @Enumerated(EnumType.STRING)
    private Criteria criteriaA1;

    @Enumerated(EnumType.STRING)
    private Criteria criteriaA2;

    @Enumerated(EnumType.STRING)
    private Criteria criteriaB1;

    @Enumerated(EnumType.STRING)
    private Criteria criteriaB2;

    private Integer currentActivePlayer;

    private Integer currentPlayerTurn;

    private List<Integer> dice;

    private Integer round;

    private Boolean isLastRound;

    private Boolean isLastTurn;
  
    private String territory;

    @Enumerated(EnumType.STRING)
    private MatchMode mode;

    @Version
    private Integer version;
}