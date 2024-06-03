package dot.help.persistence.repository;

import dot.help.model.Profile;
import org.springframework.stereotype.Component;

@Component
public interface ProfileRepository extends CrudRepository<Long, Profile> {

}

