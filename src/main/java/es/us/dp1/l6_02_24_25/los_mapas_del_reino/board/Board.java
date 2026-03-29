package es.us.dp1.l6_02_24_25.los_mapas_del_reino.board;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.match.Match;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.model.BaseEntity;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.player.Player;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "board")
public class Board extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "match_id")
    private Match match;
    
    @OneToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    Player player;

    @Min(0)
    private Integer score;
}
