package dot.help.api.controller;

import dot.help.model.Emergency;
import dot.help.persistence.repository.EmergencyRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@CrossOrigin
@RequestMapping("/aid-guardian/emergencies")
public class EmergencyRESTController {
    private final EmergencyRepository emergencyRepository;
    private final static Logger log = LogManager.getLogger(EmergencyRESTController.class);

    @Autowired
    public EmergencyRESTController(EmergencyRepository emergencyRepository) {
        this.emergencyRepository = emergencyRepository;
    }

    @PostMapping
    public ResponseEntity<?> createEmergency(@RequestBody Emergency emergency) {
        log.traceEntry("Creating reported emergency: " + emergency);

        Optional<Emergency> reportedEmergency = emergencyRepository.save(emergency);
        if (reportedEmergency.isEmpty()) {
            log.error("Failed to create emergency: " + emergency);
            return ResponseEntity.badRequest().body("Failed to create emergency");
        }

        log.traceExit("Created emergency: " + reportedEmergency.get());
        return ResponseEntity.ok(reportedEmergency.get());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmergency(@RequestBody Emergency emergency) {
        log.traceEntry("Updating responded emergency: " + emergency);

        Optional<Emergency> updatedEmergency = emergencyRepository.update(emergency);
        if (updatedEmergency.isEmpty()) {
            log.error("Failed to update emergency: " + emergency);
            return ResponseEntity.badRequest().body("Failed to update emergency");
        }

        log.traceExit("Updated emergency: " + updatedEmergency.get());
        return ResponseEntity.ok(updatedEmergency.get());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmergency(@PathVariable Long id) {
        log.traceEntry("Deleting aborted emergency with id: " + id);

        Optional<Emergency> deletedEmergency = emergencyRepository.delete(id);
        if (deletedEmergency.isEmpty()) {
            log.error("Failed to delete emergency with id: " + id);
            return ResponseEntity.badRequest().body("Failed to delete emergency");
        }

        log.traceExit("Deleted emergency: " + deletedEmergency.get());
        return ResponseEntity.ok(deletedEmergency.get());
    }
}
