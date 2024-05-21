package dot.help.api.controller;

import dot.help.model.Emergency;
import dot.help.persistence.repository.EmergencyRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.StreamSupport;

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

    @GetMapping
    public ResponseEntity<?> getAll() {
        log.traceEntry("Retrieving all emergencies");
        Emergency[] emergencies = StreamSupport.stream(emergencyRepository.findAll().spliterator(), false).toArray(Emergency[]::new);
        return ResponseEntity.ok(emergencies);
    }
}
