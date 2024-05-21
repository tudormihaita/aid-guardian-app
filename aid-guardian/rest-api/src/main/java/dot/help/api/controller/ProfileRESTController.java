package dot.help.api.controller;

import dot.help.model.Profile;
import dot.help.persistence.repository.ProfileRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/aid-guardian/user-profiles")
public class ProfileRESTController {
    private final ProfileRepository profileRepository;
    private static final Logger log = LogManager.getLogger(ProfileRESTController.class);

    @Autowired
    public ProfileRESTController(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @PostMapping
    ResponseEntity<?> registerProfile(@RequestBody Profile profile) {
        log.traceEntry("Registering profile for user: " + profile.getUser().getUsername());

        Optional<Profile> savedProfile = profileRepository.save(profile);
        if (savedProfile.isEmpty()) {
            log.error("Failed to register profile for user: " + profile.getUser().getUsername());
            return ResponseEntity.badRequest().body("Failed to register profile");
        } else {
            log.traceExit("Registered profile for user: " + profile.getUser().getUsername());
            return ResponseEntity.ok().body(savedProfile.get());
        }
    }

    @GetMapping("/{id}")
    ResponseEntity<?> getProfileById(@PathVariable Long id) {
        log.traceEntry("Finding profile with user id: " + id);

        Optional<Profile> profile = profileRepository.findOne(id);
        if (profile.isEmpty()) {
            log.error("Failed to find profile with id: " + id);
            return ResponseEntity.notFound().build();
        } else {
            log.traceExit("Successfully retrieved profile with id: " + id);
            return ResponseEntity.ok().body(profile.get());
        }
    }
}
