package dot.help.server;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import dot.help.model.Profile;
import dot.help.model.User;
import dot.help.persistence.repository.ProfileRepository;
import dot.help.persistence.repository.database.ProfileDBRepository;
import dot.help.persistence.repository.database.UserDBRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class ProfileRoute {

    private ProfileRepository repository;

    //constructor
    ProfileRoute()
    {
        Properties serverProperties = new Properties();
        try {
            serverProperties.load(StartProtobufServer.class.getResourceAsStream("/server.properties"));
            serverProperties.list(System.out);
        } catch (IOException exception) {
            System.err.println("Cannot find server.properties: " + exception);
            return;
        }

        this.repository = new ProfileDBRepository(serverProperties);
    }

    @GetMapping("/profiles")
    List<Profile> all() {
        return (List<Profile>) repository.findAll();
    }


    //la sign in, se creeaza si un profil nou pentru user -- de implementat
    @PostMapping("/profiles")
    Optional<Profile> newProfile(@RequestBody Profile newProfile) {
        return repository.save(newProfile);
    }
}

