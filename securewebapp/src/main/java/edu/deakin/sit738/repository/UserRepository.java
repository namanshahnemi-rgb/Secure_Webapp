package edu.deakin.sit738.repository;

import edu.deakin.sit738.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    // Example native query (for demonstration; avoid concatenation in real code)
    @Query(value = "SELECT * FROM app_user WHERE username = ?1", nativeQuery = true)
    User findByUsernameNative(String username);
}
