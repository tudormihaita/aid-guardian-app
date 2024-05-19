package dot.help.persistence.repository.database;

import dot.help.model.Emergency;
import dot.help.model.FirstResponder;
import dot.help.model.EmergencyStatus;
import dot.help.model.User;
import dot.help.persistence.repository.EmergencyRepository;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Properties;

@Repository
public class EmergencyDBRepository extends AbstractDBRepository<Long, Emergency> implements EmergencyRepository {
    public EmergencyDBRepository(Properties properties) {
        super(properties, "emergencies");
    }

    @Override
    public Emergency extractEntity(ResultSet resultSet) throws SQLException {
        Emergency emergency;
        Long id = resultSet.getLong("id_emergency");
        LocalDateTime reportedAt = resultSet.getTimestamp("reported_at").toLocalDateTime();
        EmergencyStatus status = EmergencyStatus.valueOf(resultSet.getString("status"));
        String location = resultSet.getString("location");
        String description = resultSet.getString("description");

        Long idReporter = resultSet.getLong("id_reporter");
        User reporter = getUserById(idReporter);

        FirstResponder responder = null;
        Long idResponder = resultSet.getLong("id_responder");
        if (idResponder != 0) {
            responder = (FirstResponder) getUserById(idResponder);
            emergency = new Emergency(reporter, reportedAt, description, status, responder, location);
        }
        else {
            emergency = new Emergency(reporter, reportedAt, description, status, location);
        }

        emergency.setId(id);
        return emergency;
    }

    private User getUserById(Long id) {
        try(Connection connection = dbUtils.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE id_user = ?");
            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                User user;
                String email =  resultSet.getString("email");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String role = resultSet.getString("role");
                if(role.equals("FIRST_RESPONDER")) {
                    boolean onDuty = resultSet.getBoolean("on_duty");
                    user = new FirstResponder(email, username, password, onDuty);
                }
                else {
                    user = new User(email, username, password);
                }
                user.setId(id);
                return user;
            }

        } catch (SQLException sqlException) {
            log.error(sqlException);
            throw new RuntimeException(sqlException);
        }

        return null;
    }

    @Override
    protected PreparedStatement findStatement(Connection connection, Long id) throws SQLException {
        PreparedStatement findStatement = connection.prepareStatement("SELECT * FROM emergencies WHERE id_emergency = ?");
        findStatement.setLong(1, id);

        return findStatement;
    }

    @Override
    protected PreparedStatement saveStatement(Connection connection, Emergency entity) throws SQLException {
        PreparedStatement saveStatement = connection.prepareStatement("INSERT INTO emergencies (id_reporter, reported_at, description, status, location) " +
                "VALUES (?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
        saveStatement.setLong(1, entity.getReporter().getId());
        saveStatement.setTimestamp(2, Timestamp.valueOf(entity.getReportedAt()));
        saveStatement.setString(3, entity.getDescription());
        saveStatement.setString(4, entity.getStatus().toString());
        saveStatement.setString(5, entity.getLocation());

        return saveStatement;
    }

    @Override
    protected PreparedStatement deleteStatement(Connection connection, Long id) throws SQLException {
        PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM emergencies WHERE id_emergency = ?");
        deleteStatement.setLong(1, id);

        return deleteStatement;
    }

    @Override
    protected PreparedStatement updateStatement(Connection connection, Emergency entity) throws SQLException {
        PreparedStatement updateStatement = connection.prepareStatement("UPDATE emergencies SET " +
                "id_reporter = ?, reported_at = ?, description = ?, status = ?, location = ?, id_responder = ? " +
                "WHERE id_emergency = ?");
        updateStatement.setLong(1, entity.getReporter().getId());
        updateStatement.setTimestamp(2, Timestamp.valueOf(entity.getReportedAt()));
        updateStatement.setString(3, entity.getDescription());
        updateStatement.setString(4, entity.getStatus().toString());
        updateStatement.setString(5, entity.getLocation());
        if(entity.getResponder() != null) {
            updateStatement.setLong(6, entity.getResponder().getId());
        }
        else {
            updateStatement.setLong(6, 0);
        }
        updateStatement.setLong(7, entity.getId());

        return updateStatement;
    }
}