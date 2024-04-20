package dot.help.persistence.repository.database;

import dot.help.model.Entity;
import dot.help.persistence.repository.Repository;
import dot.help.persistence.utils.DBUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public abstract class AbstractDBRepository<ID, E extends Entity<ID>> implements Repository<ID, E> {
    protected final String table;
    protected final DBUtils dbUtils;
    protected static final Logger logger = LogManager.getLogger(AbstractDBRepository.class);

    protected AbstractDBRepository(Properties properties, String table) {
        logger.info("Initializing {} with properties: {}", this.getClass().getSimpleName(), properties);
        this.table = table;
        dbUtils = new DBUtils(properties);
    }


    public abstract E extractEntity(ResultSet resultSet) throws SQLException;
    protected abstract PreparedStatement findStatement(Connection connection, ID id) throws SQLException;
    protected abstract PreparedStatement saveStatement(Connection connection, E entity) throws SQLException;
    protected abstract PreparedStatement deleteStatement(Connection connection, ID id) throws SQLException;
    protected abstract PreparedStatement updateStatement(Connection connection, E entity) throws SQLException;


    @Override
    public Optional<E> findOne(ID id) throws IllegalArgumentException {
        logger.traceEntry("Finding entity with id {} in {}", id, table);
        Connection connection = dbUtils.getConnection();

        try(PreparedStatement findStatement = findStatement(connection, id)) {
            ResultSet resultSet = findStatement.executeQuery();
            if(resultSet.next()) {
                E entity = extractEntity(resultSet);
                logger.traceExit(entity);
                return Optional.ofNullable(entity);
            }
        }
        catch (SQLException sqlException) {
            logger.error(sqlException);
            throw new RuntimeException(sqlException);
        }

        logger.traceExit("No entity found in " + table + "with id {}", id);
        return Optional.empty();
    }

    @Override
    public Iterable<E> findAll() {
        logger.traceEntry("Retrieving all entities from " + table);
        Connection connection = dbUtils.getConnection();
        List<E> entities = new ArrayList<>();

        try(PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + table)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                E entity = extractEntity(resultSet);
                entities.add(entity);
            }
        }
        catch (SQLException sqlException) {
            logger.error(sqlException);
            throw new RuntimeException(sqlException);
        }

        logger.traceExit(entities);
        return entities;
    }

    @Override
    public Optional<E> save(E entity) throws IllegalArgumentException {
        logger.traceEntry("Saving entity {} in {} ", entity, table);
        Connection connection = dbUtils.getConnection();

        try(PreparedStatement saveStatement = saveStatement(connection, entity)) {
            int affectedRows = saveStatement.executeUpdate();
            if(affectedRows == 1) {
                ResultSet generatedKeys = saveStatement.getGeneratedKeys();
                if(generatedKeys.next()) {
                    Object generatedId = generatedKeys.getObject(1);
                    entity.setId((ID) generatedId);
                }

                logger.traceExit("Entity {} saved successfully", entity);
                return Optional.of(entity);
            }
        } catch (SQLException sqlException) {
            logger.error(sqlException);
            throw new RuntimeException(sqlException);
        }

        logger.traceExit("No rows affected, save was unsuccessful");
        return Optional.empty();
    }

    @Override
    public Optional<E> delete(ID id) throws IllegalArgumentException {
        logger.traceEntry("Deleting entity with id {} from {}", id, table);

        Optional<E> optionalEntity = findOne(id);
        if(optionalEntity.isPresent()) {
            Connection connection = dbUtils.getConnection();
            try (PreparedStatement deleteStatement = deleteStatement(connection, id)) {
                int affectedRows = deleteStatement.executeUpdate();
                if (affectedRows == 1) {
                    logger.traceExit("Entity {} deleted successfully", optionalEntity.get());
                    return optionalEntity;
                }
            } catch (SQLException sqlException) {
                logger.error(sqlException);
                throw new RuntimeException(sqlException);
            }
        }

        logger.traceExit("Entity with given id doesn't exist, delete unsuccessful");
        return Optional.empty();
    }

    @Override
    public Optional<E> update(E entity) throws IllegalArgumentException {
        logger.traceEntry("Updating entity with id {} as {} from {}", entity.getId(), entity, table);
        Optional<E> optionalEntity = findOne(entity.getId());

        if(optionalEntity.isPresent()) {
            Connection connection = dbUtils.getConnection();
            try(PreparedStatement updateStatement = updateStatement(connection, entity)) {
                int affectedRows = updateStatement.executeUpdate();
                if (affectedRows == 1) {
                    logger.traceExit("Entity {} updated successfully", optionalEntity.get());
                    return optionalEntity;
                }
            } catch (SQLException sqlException) {
                logger.error(sqlException);
                throw new RuntimeException(sqlException);
            }
        }

        logger.traceExit("Entity with given id doesn't exist, update unsuccessful");
        return Optional.empty();
    }
}
