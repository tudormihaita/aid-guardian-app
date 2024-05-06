package dot.help.server;

import dot.help.model.*;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import dot.help.persistence.repository.UserRepository.CredentialType;

public class ServicesImpl implements IServices {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final EmergencyRepository emergencyRepository;

    private final int DEFAULT_THREADS_POOL = 1;
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
    public void registerUser(Profile profile, IObserver client) {

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

    @Override
    public Profile findUserProfile(User user, IObserver client) {
        log.traceEntry("Finding profile for user: " + user.getUsername());

        Optional<Profile> profile = profileRepository.findOne(user.getId());
        if (profile.isEmpty()) {
            log.error("Profile not found for user: " + user.getUsername());
            throw new IllegalArgumentException("Profile not found for user: " + user.getUsername());
        }

        log.traceExit(profile.get());
        return profile.get();
    }

    @Override
    public void reportEmergency(Emergency emergency, IObserver client) {
        log.traceEntry("Reporting emergency: " + emergency);

        Optional<Emergency> reportedEmergency = emergencyRepository.save(emergency);
        if(reportedEmergency.isEmpty()) {
            log.error("Failed to report emergency: " + emergency);
            throw new IllegalArgumentException("Failed to report emergency: " + emergency);
        }

        log.info("Notifying all clients about the reported emergency...");
        notifyReportedEmergency(reportedEmergency.get());
        log.traceExit("Emergency reported successfully: " + reportedEmergency.get());
    }

    @Override
    public void respondToEmergency(FirstResponder responder, Emergency emergency, IObserver client) {
        log.traceEntry("Responding to emergency: " + emergency);

        Emergency respondedEmergency = new Emergency(emergency.getReporter(), emergency.getReportedAt(), emergency.getDescription(),
                Status.Responded, emergency.getLocation());
        respondedEmergency.setResponder(responder);
        respondedEmergency.setId(emergency.getId());

        Optional<Emergency> updatedEmergency = emergencyRepository.update(respondedEmergency);
        if(updatedEmergency.isEmpty()) {
            log.error("Failed to respond to emergency: " + emergency);
            throw new IllegalArgumentException("Failed to respond to emergency: " + emergency);
        }

        log.info("Notifying all clients about the responded emergency...");
        notifyRespondedEmergency(updatedEmergency.get());
        log.traceExit("Emergency responded successfully: " + updatedEmergency.get());
    }

    private void notifyReportedEmergency(Emergency emergency) {
        log.info("Notifying all clients about the reported emergency...");
        ExecutorService executor = Executors.newFixedThreadPool(DEFAULT_THREADS_POOL);
        for(var client:loggedClients.values())
        {
            executor.execute(()->{
                    client.emergencyReported(emergency);
            });
        }
        executor.shutdown();
    }

    private void notifyRespondedEmergency(Emergency emergency) {
        log.info("Notifying all clients about the responded emergency...");
        ExecutorService executor = Executors.newFixedThreadPool(DEFAULT_THREADS_POOL);
        for(var client:loggedClients.values())
        {
            executor.execute(()->{
                client.emergencyResponded(emergency);
            });
        }
        executor.shutdown();
    }
}
