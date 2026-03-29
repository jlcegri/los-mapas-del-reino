package es.us.dp1.l6_02_24_25.los_mapas_del_reino.auth;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.auth.payload.response.JwtResponse;

import es.us.dp1.l6_02_24_25.los_mapas_del_reino.configuration.jwt.JwtUtils;

import es.us.dp1.l6_02_24_25.los_mapas_del_reino.configuration.services.UserDetailsImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.us.dp1.l6_02_24_25.los_mapas_del_reino.auth.payload.request.AdminSignupRequest;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.auth.payload.request.LoginRequest;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.auth.payload.request.PlayerSignupRequest;
import es.us.dp1.l6_02_24_25.los_mapas_del_reino.auth.payload.response.MessageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.security.authentication.BadCredentialsException;


@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "The Authentication API based on JWT")
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final JwtUtils jwtUtils;
	private final AuthService authService;

	@Autowired
	public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils, AuthService authService) {
		this.jwtUtils = jwtUtils;
		this.authenticationManager = authenticationManager;
		this.authService = authService;
	}

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		try{
			Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

			SecurityContextHolder.getContext().setAuthentication(authentication);
			String jwt = jwtUtils.generateJwtToken(authentication);

			UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
			List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
				.collect(Collectors.toList());

			return ResponseEntity.ok().body(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), roles));
		}catch(BadCredentialsException exception){
			return ResponseEntity.badRequest().body("Bad Credentials!");
		}
	}

	@GetMapping("/validate")
	public ResponseEntity<Boolean> validateToken(@RequestParam String token) {
		Boolean isValid = jwtUtils.validateJwtToken(token);
		return ResponseEntity.ok(isValid);
	}

	
	@PostMapping("/signup")
	public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody PlayerSignupRequest signUpRequest) {
		authService.createPlayerUser(signUpRequest);
		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}

	@PostMapping("/signup/admin")
	public ResponseEntity<MessageResponse> registerAdmin(@Valid @RequestBody AdminSignupRequest signUpRequest) {
		try {
			authService.createAdminUser(signUpRequest);
			return ResponseEntity.ok(new MessageResponse("Admin user registered successfully!"));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
		}
	}


}
