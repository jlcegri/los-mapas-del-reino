package es.us.dp1.l6_02_24_25.los_mapas_del_reino.auth;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.user.Authorities;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import es.us.dp1.l6_02_24_25.los_mapas_del_reino.auth.payload.request.AdminSignupRequest;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.auth.payload.request.PlayerSignupRequest;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.player.Player;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.player.PlayerService;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.user.AuthoritiesService;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.user.User;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.user.UserService;

@Service
public class AuthService {

	private final PasswordEncoder encoder;
	private final AuthoritiesService authoritiesService;
	private final UserService userService;
	private final PlayerService playerService;
	

	@Autowired
	public AuthService(PasswordEncoder encoder, AuthoritiesService authoritiesService, UserService userService,
			PlayerService playerService
			) {
		this.encoder = encoder;
		this.authoritiesService = authoritiesService;
		this.userService = userService;
		this.playerService = playerService;
	}

	@Transactional
	public void createAdminUser(@Valid AdminSignupRequest request) {
			if (userService.existsUser(request.getUsername())) {
				throw new IllegalArgumentException("El nombre de usuario ya está cogido");
			}
			User user = new User();
			user.setUsername(request.getUsername());
			user.setPassword(encoder.encode(request.getPassword()));
			Authorities role = authoritiesService.findByAuthority("ADMIN");
			user.setAuthority(role);
			userService.saveUser(user);
	}

	@Transactional
	public void createPlayerUser(@Valid PlayerSignupRequest request) {
		if (userService.existsUser(request.getUsername())) {
			throw new IllegalArgumentException("El nombre de usuario ya está cogido");
		}
		User user = new User();
		user.setUsername(request.getUsername());
		user.setPassword(encoder.encode(request.getPassword()));
		Authorities role = authoritiesService.findByAuthority("PLAYER");
		user.setAuthority(role);
		userService.saveUser(user);
		Player player = new Player();
		player.setFirstName(request.getFirstName());
		player.setLastName(request.getLastName());
		player.setEmail(request.getEmail());
		player.setUser(user);
		playerService.savePlayer(player);

	}
}
