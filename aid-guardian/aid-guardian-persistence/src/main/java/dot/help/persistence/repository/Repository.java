package dot.help.persistence.repository;

import dot.help.model.Entity;

import java.util.Optional;

public interface Repository<ID, E extends Entity<ID>> {

    /**
     * Searches for an entity by identifier and returns it
     *
     * @param id ID, the identifier of the entity to be returned
     *           id must not be null
     * @return an {@code Optional} - the entity if it exists,
     *                               null otherwise
     * @throws IllegalArgumentException if the identifier is null
     */
    Optional<E> findOne(ID id) throws IllegalArgumentException;

    /**
     * Finds all entities stored and returns them in an Iterable Data Structure
     *
     * @return Iterable<E>, a collection of all stored entities
     */
    Iterable<E> findAll();

    /**
     * Adds a new entity to the repository
     *
     * @param entity E, new entity added
     * @return an {@code Optional} - the entity if it was saved,
     *                               null if identifier already exists
     * @throws IllegalArgumentException if the given entity is null
     */
    Optional<E> save(E entity) throws IllegalArgumentException;

    /**
     * Deletes an entity with the specified id
     *
     * @param id - ID, the identifier of the desired entity
     * @return an {@code Optional} - null if there is no entity with the given id.
     *                               the removed entity otherwise
     * @throws IllegalArgumentException if the given id is null or not existent
     */
    Optional<E> delete(ID id) throws IllegalArgumentException;

    /**
     * Updates an already existing entity
     *
     * @param entity E, the updated entity to replace the currently existing one
     * @return an {@code Optional} - the entity if it was updated,
     *                              null otherwise (e.g. identifier does not exist)
     * @throws IllegalArgumentException if given entity is null
     */
    Optional<E> update(E entity) throws IllegalArgumentException;
}