package es.us.dp1.l6_02_24_25.los_mapas_del_reino.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;


public interface UserRepository extends  CrudRepository<User, Integer>{			


	Optional<User> findByUsername(String username);

	Boolean existsByUsername(String username);

	Optional<User> findById(Integer id);
	
	@Query("SELECT u FROM User u WHERE u.authority.authority = :auth")
	Iterable<User> findAllByAuthority(String auth);
	
	@Query("SELECT u FROM User u WHERE u.authority.authority = 'ADMIN'")
	Iterable<User> findAllAdmins();

	@Query("SELECT COUNT(m) > 0 FROM Match m WHERE m.creator.user.id = :userId")
    boolean hasCreatedMatches(@Param("userId") Integer id);

    @Query("SELECT COUNT(m) > 0 FROM Match m JOIN m.players p WHERE p.user.id = :userId")
    boolean hasPlayedMatches(@Param("userId") Integer id);

    @Query("SELECT COUNT(m) > 0 FROM Match m JOIN m.winners w WHERE w.user.id = :userId")
    boolean hasWonMatches(@Param("userId") Integer id);
	
	
	
}
