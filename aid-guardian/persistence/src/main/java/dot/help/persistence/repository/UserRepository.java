package dot.help.persistence.repository;

import dot.help.model.User;
import dot.help.model.UserRole;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface UserRepository extends Repository<Long, User> {
    enum CredentialType {
        USERNAME, EMAIL
    }
    Optional<User> findByLoginCredentials(CredentialType type, String credential, String password);

    List<User> findByRole(UserRole role);
}
