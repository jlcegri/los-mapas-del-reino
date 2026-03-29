package es.us.dp1.l6_02_24_25.los_mapas_del_reino.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.l6_02_24_25.los_mapas_del_reino.auth.AuthService;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.auth.payload.request.AdminSignupRequest;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.auth.payload.request.PlayerSignupRequest;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.user.AuthoritiesService;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.user.User;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.user.UserService;

@SpringBootTest
public class AuthServiceTests {

	@Autowired
	protected AuthService authService;
	@Autowired
	protected UserService userService;	
	@Autowired
	protected AuthoritiesService authoritiesService;

	@Test
	@Transactional
	public void shouldCreateAdminUser() {
		AdminSignupRequest request = createAdminRequest("ADMIN", "admin2");
		int userFirstCount = ((Collection<User>) this.userService.findAll()).size();
		this.authService.createAdminUser(request);
		int userLastCount = ((Collection<User>) this.userService.findAll()).size();
		assertEquals(userFirstCount + 1, userLastCount);
	}
	
	
	private AdminSignupRequest createAdminRequest(String auth, String username) {
		AdminSignupRequest request = new AdminSignupRequest();
		request.setAuthority(auth);
		request.setPassword("prueba");
		request.setUsername(username);

		if(auth == "ADMIN") {
			User adminUser = new User();
			adminUser.setUsername("adminTest");
			adminUser.setPassword("adminTest");
			adminUser.setAuthority(authoritiesService.findByAuthority("ADMIN"));
			userService.saveUser(adminUser);			
		}

		return request;
	}
	@Test
	@Transactional
	public void shouldCreatePlayerUser() {
		PlayerSignupRequest request = createRequest("PLAYER", "playertest");
		int userFirstCount = ((Collection<User>) this.userService.findAll()).size();
		//int playerFirstCount = ((Collection<Player>) this.playerService.findAll()).size();
		this.authService.createPlayerUser(request);
		int userLastCount = ((Collection<User>) this.userService.findAll()).size();
		//int playerLastCount = ((Collection<Player>) this.playerService.findAll()).size();
		assertEquals(userFirstCount + 1, userLastCount);
		//assertEquals(playFirstCount + 1, playerLastCount);
	}

	private PlayerSignupRequest createRequest(String auth, String username) {
		PlayerSignupRequest request = new PlayerSignupRequest();
		request.setAuthority(auth);
		request.setFirstName("prueba");
		request.setLastName("prueba");
		request.setEmail("prueba@example.com");
		request.setPassword("prueba");
		request.setUsername(username);

		if(auth == "PLAYER") {
			User playerUser = new User();
			playerUser.setUsername("playerTest");
			playerUser.setPassword("playerTest");
			playerUser.setAuthority(authoritiesService.findByAuthority("PLAYER"));
			userService.saveUser(playerUser);			
		}

		return request;
	}

}
