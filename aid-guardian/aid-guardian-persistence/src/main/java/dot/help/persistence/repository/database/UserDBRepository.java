package dot.help.persistence.repository.database;

import dot.help.model.FirstResponder;
import dot.help.model.User;
import dot.help.model.UserRole;
import dot.help.persistence.repository.UserRepository;
import dot.help.persistence.utils.DBUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class UserDBRepository extends AbstractDBRepository<Long, User> implements UserRepository {
    public UserDBRepository(Properties properties) {
        super(properties, "users");
    }

    @Override
    public User extractEntity(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        String email = resultSet.getString("email");
        String username = resultSet.getString("username");
        String password = resultSet.getString("password");
        UserRole role = UserRole.valueOf(resultSet.getString("role"));

        User user;
        if (role == UserRole.COMMUNITY_DISPATCHER) {
            user = new User(email, username, password);
            user.setId(id);
            return user;
        }
        else if (role == UserRole.FIRST_RESPONDER) {
            user = new FirstResponder(email, username, password);
            user.setId(id);
            return user;
        }
        else {
            log.error("Invalid role for user with id {}", id);
            throw new SQLException("Invalid role specified for user with id " + id);
        }
    }

    @Override
    protected PreparedStatement findStatement(Connection connection, Long id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE id_user = ?");
        preparedStatement.setLong(1, id);

        return preparedStatement;
    }

    @Override
    protected PreparedStatement saveStatement(Connection connection, User entity) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO users (email, username, password, role) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, entity.getEmail());
        preparedStatement.setString(2, entity.getUsername());
        preparedStatement.setString(3, entity.getPassword());
        preparedStatement.setString(4, entity.getRole().toString());

        return preparedStatement;
    }

    @Override
    protected PreparedStatement deleteStatement(Connection connection, Long id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM users WHERE id_user = ?");
        preparedStatement.setLong(1, id);

        return preparedStatement;
    }

    @Override
    protected PreparedStatement updateStatement(Connection connection, User entity) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE users SET email = ?, username = ?, password = ?, role = ? WHERE id_user = ?");
        preparedStatement.setString(1, entity.getEmail());
        preparedStatement.setString(2, entity.getUsername());
        preparedStatement.setString(3, entity.getPassword());
        preparedStatement.setString(4, entity.getRole().toString());
        preparedStatement.setLong(5, entity.getId());

        return preparedStatement;
    }

    @Override
    public Optional<User> findByLoginCredentials(CredentialType type, String credential, String password) {
        log.traceEntry("Finding user with login credentials {}", credential);

        try(Connection connection = dbUtils.getConnection()) {
            PreparedStatement preparedStatement;
            if (type == CredentialType.USERNAME) {
                preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
            } else {
                preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE email = ? AND password = ?");
            }
            preparedStatement.setString(1, credential);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                User user = extractEntity(resultSet);
                log.traceExit(user);
                return Optional.ofNullable(user);
            }

        } catch (SQLException sqlException) {
            log.error(sqlException);
            throw new RuntimeException(sqlException);
        }

        log.traceExit("No user found with credentials {}", credential);
        return Optional.empty();
    }

    @Override
    public List<User> findByRole(UserRole role) {
        log.traceEntry("Finding users with role {}", role);
        List<User> users = new ArrayList<>();

        try (Connection connection = dbUtils.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE role = ?");
            preparedStatement.setString(1, role.toString());

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                User user = extractEntity(resultSet);
                users.add(user);
            }

        } catch (SQLException sqlException) {
            log.error(sqlException);
            throw new RuntimeException(sqlException);
        }

        log.traceExit(users);
        return users;
    }
}
