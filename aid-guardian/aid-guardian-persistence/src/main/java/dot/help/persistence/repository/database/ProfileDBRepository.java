package dot.help.persistence.repository.database;

import dot.help.model.*;
import dot.help.persistence.repository.ProfileRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.Properties;

public class ProfileDBRepository extends AbstractDBRepository<Long, Profile> implements ProfileRepository {

    public ProfileDBRepository(Properties properties) {
        super(properties, "user_profiles");
    }

    @Override
    public Profile extractEntity(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id_user");
        String firstName = resultSet.getString("first_name");
        String lastName = resultSet.getString("last_name");
        GenderType gender = GenderType.valueOf(resultSet.getString("gender"));
        BloodGroupType bloodGroup = BloodGroupType.valueOf(resultSet.getString("blood_group"));
        LocalDate birthDate = resultSet.getDate("birthdate").toLocalDate();
        Double weight = resultSet.getDouble("weight");
        Double height = resultSet.getDouble("height");
        String medicalHistory = resultSet.getString("medical_history");
        Double score = resultSet.getDouble("score");

        User user = getUserById(id, resultSet);

        Profile profile = new Profile(user, firstName, lastName, gender, birthDate, bloodGroup, weight, height, medicalHistory, score);
        profile.setId(id);
        return profile;
    }

    private User getUserById(Long id, ResultSet resultSet) throws SQLException {
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

        return null;
    }

    @Override
    protected PreparedStatement findStatement(Connection connection, Long id) throws SQLException {
        PreparedStatement findStatement = connection.prepareStatement("SELECT * FROM user_profiles " +
                "INNER JOIN public.users u on u.id_user = user_profiles.id_user " +
                "WHERE user_profiles.id_user = ?");
        findStatement.setLong(1, id);

        return findStatement;
    }

    @Override
    protected PreparedStatement saveStatement(Connection connection, Profile entity) throws SQLException {
        PreparedStatement saveStatement = connection.prepareStatement("INSERT INTO user_profiles " +
                "(id_user, first_name, last_name, gender, birthdate, blood_group, height, weight, medical_history, score) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
        saveStatement.setLong(1, entity.getUser().getId());
        saveStatement.setString(2, entity.getFirstName());
        saveStatement.setString(3, entity.getLastName());
        saveStatement.setString(4, entity.getGender().toString());
        saveStatement.setDate(5, Date.valueOf(entity.getBirthDate()));
        saveStatement.setString(6, entity.getBloodGroup().toString());
        saveStatement.setDouble(7, entity.getHeight());
        saveStatement.setDouble(8, entity.getWeight());
        saveStatement.setString(9, entity.getMedicalHistory());
        saveStatement.setDouble(10, entity.getScore());

        return saveStatement;
    }

    @Override
    protected PreparedStatement deleteStatement(Connection connection, Long id) throws SQLException {
        PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM user_profiles WHERE id_user = ?");
        deleteStatement.setLong(1, id);

        return deleteStatement;
    }

    @Override
    protected PreparedStatement updateStatement(Connection connection, Profile entity) throws SQLException {
        PreparedStatement updateStatement = connection.prepareStatement("UPDATE user_profiles " +
                "SET first_name = ?, last_name = ?, gender = ?, birthdate = ?, blood_group = ?, height = ?, " +
                "weight = ?, medical_history = ?, score = ? " +
                "WHERE id_user = ?");

        updateStatement.setString(1, entity.getFirstName());
        updateStatement.setString(2, entity.getLastName());
        updateStatement.setString(3, entity.getGender().toString());
        updateStatement.setDate(4, Date.valueOf(entity.getBirthDate()));
        updateStatement.setString(5, entity.getBloodGroup().toString());
        updateStatement.setDouble(6, entity.getHeight());
        updateStatement.setDouble(7, entity.getWeight());
        updateStatement.setString(8, entity.getMedicalHistory());
        updateStatement.setDouble(9, entity.getScore());
        updateStatement.setLong(10, entity.getUser().getId());

        return updateStatement;
    }
}
