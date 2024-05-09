package dot.help.server;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import dot.help.model.Profile;
import dot.help.model.User;
import dot.help.model.UserRole;
import dot.help.persistence.repository.UserRepository;
import dot.help.persistence.repository.database.UserDBRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class UserRoute {
        private UserRepository repository;

        UserRoute() {
            Properties serverProperties = new Properties();
            try {
                serverProperties.load(StartProtobufServer.class.getResourceAsStream("/server.properties"));
                serverProperties.list(System.out);
            } catch (IOException exception) {
                System.err.println("Cannot find server.properties: " + exception);
                return;
            }

            this.repository = new UserDBRepository(serverProperties);
        }

        @GetMapping("/users")
        List<User> all() {
            return (List<User>) repository.findAll();
        }


        //de implementat la sign in button, inca nu e facut
        @PostMapping("/users")
        Optional<User> newUser(@RequestBody User newUser) {
            return repository.save(newUser);
        }


        @PostMapping("/login")
        Optional<User> loginUser(@RequestBody User newUser) {
            System.out.println("Login user: " + newUser.getUsername() + " " + newUser.getPassword());
            var usr = repository.findByLoginCredentials(UserRepository.CredentialType.USERNAME, newUser.getUsername(), newUser.getPassword());

            if (usr.isEmpty()) {
                usr = repository.findByLoginCredentials(UserRepository.CredentialType.EMAIL, newUser.getEmail(), newUser.getPassword());
            }
            System.out.println("RASPUSNS" + usr);
            return usr;
        }

        //luam UserRoles
        @GetMapping("/roles")
        public UserRole[] getRoles() {
            return UserRole.values();
        }

        // Single item
//        @GetMapping("/Users/{id}")
//        User one(@PathVariable Long id) {
//
//            return repository.findById(id)
//                    .orElseThrow(() -> new UserNotFoundException(id));
//        }
//
//        @PutMapping("/Users/{id}")
//        User replaceUser(@RequestBody User newUser, @PathVariable Long id) {
//
//            return repository.findById(id)
//                    .map(User -> {
//                        User.setName(newUser.getName());
//                        User.setRole(newUser.getRole());
//                        return repository.save(User);
//                    })
//                    .orElseGet(() -> {
//                        newUser.setId(id);
//                        return repository.save(newUser);
//                    });
//        }
//
//        @DeleteMapping("/Users/{id}")
//        void deleteUser(@PathVariable Long id) {
//            repository.deleteById(id);
//        }
//
}
