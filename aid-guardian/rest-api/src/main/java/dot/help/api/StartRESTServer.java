package dot.help.api;

import dot.help.persistence.repository.EmergencyRepository;
import dot.help.persistence.repository.ProfileRepository;
import dot.help.persistence.repository.UserRepository;
import dot.help.persistence.repository.database.EmergencyDBRepository;
import dot.help.persistence.repository.database.ProfileDBRepository;
import dot.help.persistence.repository.database.UserDBRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Properties;

@ComponentScan(basePackages = "dot.help")
@SpringBootApplication
public class StartRESTServer {
    public static void main(String[] args) {
        SpringApplication.run(StartRESTServer.class, args);
    }

    @Bean(name="properties")
    public Properties databaseProperties() {
        Properties properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("application.properties"));
        } catch (Exception e) {
            System.err.println("Error loading server properties: " + e);
        }

        return properties;
    }

    @Bean
    public UserRepository userRepository() {
        return new UserDBRepository(databaseProperties());
    }

    @Bean
    public ProfileRepository profileRepository() {
        return new ProfileDBRepository(databaseProperties());
    }

    @Bean
    public EmergencyRepository emergencyRepository() {
        return new EmergencyDBRepository(databaseProperties());
    }
}
