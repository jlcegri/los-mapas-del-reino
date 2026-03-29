package es.us.dp1.l6_02_24_25.los_mapas_del_reino.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.auth.payload.request.AdminSignupRequest;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.auth.payload.request.LoginRequest;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.auth.payload.request.PlayerSignupRequest;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.configuration.jwt.JwtUtils;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.configuration.services.UserDetailsImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.us.dp1.l6_02_24_25.los_mapas_del_reino.auth.AuthController;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.auth.AuthService;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.user.UserService;

/**
 * Test class for {@link OwnerRestController}
 *
 */

@WebMvcTest(value = AuthController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), excludeAutoConfiguration = {
		SecurityAutoConfiguration.class })
class AuthControllerTests {

	private static final String BASE_URL = "/api/v1/auth";

	@SuppressWarnings("unused")
	@Autowired
	private AuthController authController;

	@MockBean
	private AuthenticationManager authenticationManager;

	@MockBean
	private JwtUtils jwtUtils;

	@MockBean
	private UserService userService;

	@MockBean
	private AuthService authService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	private LoginRequest loginRequest;
	private PlayerSignupRequest playerSignupRequest;
	private AdminSignupRequest adminSignupRequestAdmin;
	private UserDetailsImpl userDetails;
	private String token;

	@BeforeEach
	public void setup() {
		loginRequest = new LoginRequest();
		loginRequest.setUsername("player");
		loginRequest.setPassword("password");

		playerSignupRequest = new PlayerSignupRequest();
		playerSignupRequest.setUsername("username");
		playerSignupRequest.setPassword("password");
		playerSignupRequest.setFirstName("Test");
		playerSignupRequest.setLastName("Test");
		playerSignupRequest.setEmail("test@example.com");
		playerSignupRequest.setAuthority("PLAYER");

		adminSignupRequestAdmin = new AdminSignupRequest();
    	adminSignupRequestAdmin.setUsername("adminuser");
   		adminSignupRequestAdmin.setPassword("adminpassword");
    	adminSignupRequestAdmin.setAuthority("ADMIN");

		userDetails = new UserDetailsImpl(1, loginRequest.getUsername(), loginRequest.getPassword(),
				List.of(new SimpleGrantedAuthority("PLAYER")));

		token = "JWT TOKEN";
	}

	@Test
	public void shouldAuthenticateAdminUser() throws Exception {
		Authentication auth = Mockito.mock(Authentication.class);

		UserDetailsImpl adminUserDetails = new UserDetailsImpl(1, "admin", "password",
				List.of(new SimpleGrantedAuthority("ADMIN")));

		when(this.jwtUtils.generateJwtToken(any(Authentication.class))).thenReturn(token);
		when(this.authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
		Mockito.doReturn(adminUserDetails).when(auth).getPrincipal();

		LoginRequest adminLoginRequest = new LoginRequest();
		adminLoginRequest.setUsername("admin");
		adminLoginRequest.setPassword("password");

		mockMvc.perform(post(BASE_URL + "/signin").with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(adminLoginRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value(adminLoginRequest.getUsername()))
				.andExpect(jsonPath("$.id").value(adminUserDetails.getId()))
				.andExpect(jsonPath("$.token").value(token));
	}

	@Test
	public void shouldAuthenticateUser() throws Exception {
		Authentication auth = Mockito.mock(Authentication.class);

		when(this.jwtUtils.generateJwtToken(any(Authentication.class))).thenReturn(token);
		when(this.authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
		Mockito.doReturn(userDetails).when(auth).getPrincipal();

		mockMvc.perform(post(BASE_URL + "/signin").with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest))).andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value(loginRequest.getUsername()))
				.andExpect(jsonPath("$.id").value(userDetails.getId())).andExpect(jsonPath("$.token").value(token));
	}

	@Test
	public void shouldValidateToken() throws Exception {
		when(this.jwtUtils.validateJwtToken(token)).thenReturn(true);

		mockMvc.perform(get(BASE_URL + "/validate").with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.param("token", token)).andExpect(status().isOk())
				.andExpect(jsonPath("$").value(true));
	}

	@Test
	public void shouldNotValidateToken() throws Exception {
		when(this.jwtUtils.validateJwtToken(token)).thenReturn(false);

		mockMvc.perform(get(BASE_URL + "/validate").with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.param("token", token)).andExpect(status().isOk())
				.andExpect(jsonPath("$").value(false));
	}

	@Test
	public void shouldRegisterPlayerUser() throws Exception {
		when(this.userService.existsUser(playerSignupRequest.getUsername())).thenReturn(false);
		doNothing().when(this.authService).createPlayerUser(playerSignupRequest);

		mockMvc.perform(post(BASE_URL + "/signup").with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(playerSignupRequest))).andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("User registered successfully!"));
	}

	@Test
	void shouldRegisterAdminUser() throws Exception {
		when(this.userService.existsUser(adminSignupRequestAdmin.getUsername())).thenReturn(false);
		doNothing().when(this.authService).createAdminUser(adminSignupRequestAdmin);

		mockMvc.perform(post(BASE_URL + "/signup/admin").with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(adminSignupRequestAdmin))).andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Admin user registered successfully!"));
	}

	@Test
	void shouldNotRegisterPlayerUserWithExistingUsername() throws Exception {
		 when(this.userService.existsUser(playerSignupRequest.getUsername())).thenReturn(true);
		doNothing().when(this.authService).createPlayerUser(playerSignupRequest);

		mockMvc.perform(post(BASE_URL + "/signup").with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(playerSignupRequest)))
				.andExpect(status().isOk()) 
				.andExpect(jsonPath("$.message").value("User registered successfully!"));
	}

	@Test
	void shouldNotRegisterAdminUserWithExistingUsername() throws Exception {
		when(this.userService.existsUser(adminSignupRequestAdmin.getUsername())).thenReturn(true);

		mockMvc.perform(post(BASE_URL + "/signup/admin").with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(adminSignupRequestAdmin))).andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Admin user registered successfully!"));
	}

}
