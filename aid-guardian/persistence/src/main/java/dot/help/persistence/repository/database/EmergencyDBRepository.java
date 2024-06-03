package dot.help.persistence.repository.database;

import dot.help.model.Emergency;
import dot.help.model.FirstResponder;
import dot.help.model.EmergencyStatus;
import dot.help.model.User;
import dot.help.persistence.repository.EmergencyRepository;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
        double latitude = resultSet.getDouble("latitude");
        double longitude = resultSet.getDouble("longitude");
        String description = resultSet.getString("description");

        Long idReporter = resultSet.getLong("id_reporter");
        User reporter = getUserById(idReporter);

        FirstResponder responder = null;
        long idResponder = resultSet.getLong("id_responder");
        if (idResponder != 0) {
            responder = (FirstResponder) getUserById(idResponder);
            emergency = new Emergency(reporter, reportedAt, description, status, responder, latitude, longitude);
        }
        else {
            emergency = new Emergency(reporter, reportedAt, description, status, latitude, longitude);
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
        PreparedStatement saveStatement = connection.prepareStatement("INSERT INTO emergencies (id_reporter, reported_at, description, status, latitude, longitude) " +
                "VALUES (?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
        saveStatement.setLong(1, entity.getReporter().getId());
        saveStatement.setTimestamp(2, Timestamp.valueOf(entity.getReportedAt()));
        saveStatement.setString(3, entity.getDescription());
        saveStatement.setString(4, entity.getStatus().toString());
        saveStatement.setDouble(5, entity.getLatitude());
        saveStatement.setDouble(6, entity.getLongitude());

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
                "id_reporter = ?, reported_at = ?, description = ?, status = ?, id_responder = ?, latitude = ?, longitude = ? " +
                "WHERE  id_emergency = ?");
        updateStatement.setLong(1, entity.getReporter().getId());
        updateStatement.setTimestamp(2, Timestamp.valueOf(entity.getReportedAt()));
        updateStatement.setString(3, entity.getDescription());
        updateStatement.setString(4, entity.getStatus().toString());
        if(entity.getResponder() != null) {
            updateStatement.setLong(5, entity.getResponder().getId());
        }
        else {
            updateStatement.setLong(5, 0);
        }
        updateStatement.setDouble(6, entity.getLatitude());
        updateStatement.setDouble(7, entity.getLongitude());
        updateStatement.setLong(8, entity.getId());

        return updateStatement;
    }

    @Override
    public List<Emergency> findEmergenciesReportedBy(Long userId) {
        log.traceEntry("Retrieving emergencies reported by user with id: " + userId);
        List<Emergency> emergencies = new ArrayList<>();

        try(Connection connection = dbUtils.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT DISTINCT * FROM emergencies WHERE id_reporter = ?");
            preparedStatement.setLong(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                emergencies.add(extractEntity(resultSet));
            }
        } catch (SQLException sqlException) {
            log.error(sqlException);
            throw new RuntimeException(sqlException);
        }

        log.traceExit("Successfully retrieved emergencies reported by user: " + emergencies);
        return emergencies;
    }

    @Override
    public List<Emergency> findEmergenciesRespondedBy(Long userId) {
        log.traceEntry("Retrieving emergencies responded by user with id: " + userId);
        List<Emergency> emergencies = new ArrayList<>();

        try(Connection connection = dbUtils.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT DISTINCT * FROM emergencies WHERE id_responder = ?");
            preparedStatement.setLong(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                emergencies.add(extractEntity(resultSet));
            }
        } catch (SQLException sqlException) {
            log.error(sqlException);
            throw new RuntimeException(sqlException);
        }

        log.traceExit("Successfully retrieved emergencies responded by user: " + emergencies);
        return emergencies;
    }
}