package dot.help.server;

import dot.help.model.User;
import dot.help.persistence.repository.EmergencyRepository;
import dot.help.persistence.repository.ProfileRepository;
import dot.help.persistence.repository.UserRepository;
import dot.help.server.utils.CredentialChecker;
import dot.help.server.utils.PasswordEncoder;
import dot.help.services.IObserver;
import dot.help.services.IServices;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import dot.help.persistence.repository.UserRepository.CredentialType;

public class ServicesImpl implements IServices {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final EmergencyRepository emergencyRepository;

    private final int DEFAULT_THREADS_POOL = 5;
    private final Map<String, IObserver> loggedClients;
    private final Logger log = LogManager.getLogger(ServicesImpl.class);

    public ServicesImpl(UserRepository userRepository, ProfileRepository profileRepository, EmergencyRepository emergencyRepository) {
        log.info("Initializing ServicesImpl class with repository dependencies...");
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.emergencyRepository = emergencyRepository;
        loggedClients =  new ConcurrentHashMap<>();
    }

    @Override
    public User logIn(String credential, String password, IObserver client) {
        log.traceEntry("Logging in user: " + credential);

        CredentialType type;
        if (CredentialChecker.isEmail(credential)) {
            type = CredentialType.EMAIL;
        }
        else {
            type = CredentialType.USERNAME;

        }
        String hashedPassword = PasswordEncoder.hashPassword(password);

        Optional<User> loggedUser = userRepository.findByLoginCredentials(type, credential, hashedPassword);
        if(loggedUser.isEmpty()) {
            log.error("Provided login credentials are invalid");
            throw new IllegalArgumentException("Provided login credentials are invalid!");
        }

        if(loggedClients.get(loggedUser.get().getUsername()) != null) {
            log.error("User already logged in");
            throw new IllegalArgumentException("User already logged in!");
        }

        loggedClients.put(loggedUser.get().getUsername(), client);
        log.traceExit("User authenticated successfully: ", loggedUser.get().getUsername());
        return loggedUser.get();
    }

    @Override
    public void logOut(User user, IObserver client) {
        log.traceEntry("Logging out user: " + user.getUsername());
        var loggedClient = loggedClients.remove(user.getUsername());

        if(loggedClient == null) {
            log.error("User was not currently logged in");
            throw new IllegalArgumentException("User " + user.getUsername() + " is not logged in!");
        }

        log.traceExit("User logged out successfully: ", user.getUsername());
    }
}
