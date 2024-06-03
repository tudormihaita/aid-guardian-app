package dot.help.api.controller;

import dot.help.api.utils.JwtTokenUtil;
import dot.help.model.Emergency;
import dot.help.model.FirstResponder;
import dot.help.model.User;
import dot.help.model.UserRole;
import dot.help.persistence.repository.EmergencyRepository;
import dot.help.persistence.repository.UserRepository;
import dot.help.persistence.utils.CredentialChecker;
import dot.help.persistence.utils.PasswordEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.StreamSupport;

@RestController
@CrossOrigin
@RequestMapping("/aid-guardian/users")
public class UserRESTController {
    private final UserRepository userRepository;
    private final EmergencyRepository emergencyRepository;
    private final JwtTokenUtil jwtProvider;

    private static final Logger log = LogManager.getLogger(UserRESTController.class);

    @Autowired
    public UserRESTController(UserRepository userRepository, EmergencyRepository emergencyRepository, JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.emergencyRepository = emergencyRepository;
        this.jwtProvider = jwtTokenUtil;
    }

    @PostMapping("/register")
    ResponseEntity<?> signUp(@RequestBody User user) {
        log.traceEntry("Signing up user: " + user.getUsername());

        String hashedPassword = PasswordEncoder.hashPassword(user.getPassword());
        user.setPassword(hashedPassword);

        // Implement check if user already exists
        Optional<User> existingUser = userRepository.findByLoginCredentials(UserRepository.CredentialType.USERNAME, user.getUsername(), hashedPassword);
        if(existingUser.isEmpty())
        {
            Optional<User> savedUser = userRepository.save(user);
            if(savedUser.isEmpty()) {
                log.error("Failed to sign up user: " + user.getUsername());
                return ResponseEntity.badRequest().body("Failed to sign up user");
            }
            else {
                log.traceExit("Signed up user: " + user.getUsername());
                return ResponseEntity.ok().body(savedUser.get());
            }
        }
        else
        {
            log.error("User already exists");
            return ResponseEntity.badRequest().body("User already exists");
        }
    }

    @PostMapping("/login")
    ResponseEntity<?> logIn(@RequestBody User user) {
        log.traceEntry("Logging in user: " + user.getUsername());

        String credential = user.getUsername();
        String password = user.getPassword();
        UserRepository.CredentialType type;

        String hashedPassword = PasswordEncoder.hashPassword(password);
        if (CredentialChecker.isEmail(credential)) {
            type = UserRepository.CredentialType.EMAIL;
        }
        else {
            type = UserRepository.CredentialType.USERNAME;
        }

        Optional<User> loggedUser = userRepository.findByLoginCredentials(type, credential, hashedPassword);
        if(loggedUser.isEmpty()) {
            log.error("Provided login credentials are invalid");
            return ResponseEntity.badRequest().body("Provided login credentials are invalid");
        }
        else {
            log.traceExit("User authenticated successfully: " + loggedUser.get().getUsername());
            User authenticatedUser = loggedUser.get();
            String token = jwtProvider.generateToken(authenticatedUser);

            Map<String, Object> response = new HashMap<>();
            response.put("authenticatedUser", authenticatedUser);
            response.put("accessToken", token);

            return ResponseEntity.ok().body(response);
        }
    }

    @PostMapping("/logout")
    ResponseEntity<?> logOut(@RequestBody User user) {
        log.traceEntry("Logging out user: " + user.getUsername());

        // TODO: invalidate token on logout
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @PutMapping("/{id}")
    ResponseEntity<?> toggleOnDutyStatus(@PathVariable Long id, @RequestParam("isOnDuty") boolean isOnDuty) {
        log.traceEntry("Toggling on duty status for user with id: " + id);


        Optional<User> user = userRepository.findOne(id);
        if (user.isEmpty()) {
            log.error("User with id " + id  + " not found");
            return ResponseEntity.badRequest().body("User not found");
        }

        if (user.get().getRole() != UserRole.FIRST_RESPONDER) {
            log.error("User is not a first responder");
            return ResponseEntity.badRequest().body("User is not a first responder");
        }

        FirstResponder firstResponder = (FirstResponder) user.get();
        firstResponder.setOnDuty(isOnDuty);

        Optional<User> updatedUser = userRepository.update(firstResponder);
        if (updatedUser.isEmpty()) {
            log.error("Failed to toggle on duty status for user: " + firstResponder.getUsername());
            return ResponseEntity.badRequest().body("Failed to toggle on duty status for user");
        }
        else {
            log.traceExit("Successfully toggled on duty status for user: " + firstResponder.getUsername());
            return ResponseEntity.ok().body(updatedUser.get());
        }
    }

    @GetMapping
    ResponseEntity<?> getAll() {
        log.traceEntry("Retrieving all users");

        User[] users = StreamSupport.stream(userRepository.findAll().spliterator(), false).toArray(User[]::new);

        log.traceExit("Successfully retrieved all users");
        return ResponseEntity.ok().body(users);
    }

    @GetMapping("/{id}/emergencies/reported")
    public ResponseEntity<?> getEmergenciesReportedByUser(@PathVariable Long id) {
        log.traceEntry("Retrieving emergencies for user: " + id);
        Emergency[] emergencies = emergencyRepository.findEmergenciesReportedBy(id).toArray(Emergency[]::new);
        return ResponseEntity.ok(emergencies);
    }

    @GetMapping("/{id}/emergencies/responded")
    public ResponseEntity<?> getEmergenciesRespondedByUser(@PathVariable Long id) {
        log.traceEntry("Retrieving emergencies responded by user: " + id);
        Emergency[] emergencies = emergencyRepository.findEmergenciesRespondedBy(id).toArray(Emergency[]::new);
        return ResponseEntity.ok(emergencies);
    }

}