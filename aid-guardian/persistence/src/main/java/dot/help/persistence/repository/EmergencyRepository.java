package dot.help.persistence.repository;

import dot.help.model.Emergency;
import org.springframework.stereotype.Component;

@Component
public interface EmergencyRepository extends Repository<Long, Emergency> {
}

