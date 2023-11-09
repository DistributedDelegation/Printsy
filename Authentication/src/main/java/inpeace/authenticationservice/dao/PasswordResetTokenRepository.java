package inpeace.authenticationservice.dao;

import inpeace.authenticationservice.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, String> {

    PasswordResetToken findByToken(String token);
}
