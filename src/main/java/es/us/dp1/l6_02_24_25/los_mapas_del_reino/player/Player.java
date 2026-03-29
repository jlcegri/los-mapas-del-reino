package es.us.dp1.l6_02_24_25.los_mapas_del_reino.player;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.model.Person;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "players")
public class Player extends Person {

    @OneToOne(cascade= {CascadeType.DETACH,CascadeType.REFRESH, CascadeType.PERSIST})
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @NotBlank
    @Column(nullable = false)
    @Email
    private String email;

}
