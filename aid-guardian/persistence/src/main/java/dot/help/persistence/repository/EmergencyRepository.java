package dot.help.persistence.repository;

import dot.help.model.Emergency;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface EmergencyRepository extends CrudRepository<Long, Emergency> {
    List<Emergency> findEmergenciesReportedBy(Long userId);
    List<Emergency> findEmergenciesRespondedBy(Long userId);
}

