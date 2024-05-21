package dot.help.api.controller;

import dot.help.model.User;
import dot.help.persistence.repository.UserRepository;
import dot.help.persistence.utils.CredentialChecker;
import dot.help.persistence.utils.PasswordEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.stream.StreamSupport;

@RestController
@CrossOrigin
@RequestMapping("/aid-guardian/users")
public class UserRESTController {
    private final UserRepository userRepository;
    private static final Logger log = LogManager.getLogger(UserRESTController.class);

    @Autowired
    public UserRESTController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    ResponseEntity<?> signUp(@RequestBody User user) {
        log.traceEntry("Signing up user: " + user.getUsername());

        // TODO: Implement check if user already exists

        String hashedPassword = PasswordEncoder.hashPassword(user.getPassword());
        user.setPassword(hashedPassword);
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
            return ResponseEntity.ok().body(loggedUser.get());
        }
    }

    @PostMapping("/logout")
    ResponseEntity<?> logOut(@RequestBody User user) {
        log.traceEntry("Logging out user: " + user.getUsername());

        // TODO: what should happen on logout?
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @GetMapping
    ResponseEntity<?> getAll() {
        log.traceEntry("Retrieving all users");

        User[] users = StreamSupport.stream(userRepository.findAll().spliterator(), false).toArray(User[]::new);

        log.traceExit("Successfully retrieved all users");
        return ResponseEntity.ok().body(users);
    }

}