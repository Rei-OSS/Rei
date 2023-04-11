package io.github.blackbaroness.rei.common.repository.impl.hibernate

import io.github.blackbaroness.rei.common.entity.PlayerData
import io.github.blackbaroness.rei.common.repository.PlayerDataRepository
import io.github.blackbaroness.rei.common.repository.exception.InvalidPlayerDataException
import io.github.blackbaroness.rei.common.repository.exception.RepositoryException
import org.checkerframework.checker.index.qual.NonNegative
import org.hibernate.Session
import org.hibernate.SessionFactory
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import java.util.function.Consumer
import java.util.function.Function

abstract class HibernatePlayerDataRepository : PlayerDataRepository {

    @Throws(RepositoryException::class)
    override fun create(nickname: String, uuid: UUID): PlayerData {
        val entity = HibernatePlayerData(nickname, uuid)
        makeTransaction { session -> session.persist(entity) }
        return entity
    }

    @Throws(RepositoryException::class)
    override fun merge(playerData: PlayerData): PlayerData {
        val entity = validateEntity(playerData)
        return makeTransactionAndReturn { session -> session.merge(entity) }
    }

    @Throws(RepositoryException::class)
    override fun refresh(playerData: PlayerData) {
        val entity = validateEntity(playerData)
        makeTransaction { session -> session.refresh(entity) }
    }

    @Throws(RepositoryException::class)
    override fun delete(playerData: PlayerData) {
        val entity = validateEntity(playerData)
        makeTransaction { session -> session.remove(entity) }
    }

    @Throws(RepositoryException::class)
    override fun ping(): @NonNegative Long {
        val startTime = System.currentTimeMillis()
        makeTransaction { session -> session.createNativeQuery<Int>("SELECT 1", Int::class.java).singleResult }
        return System.currentTimeMillis() - startTime
    }

    @Throws(RepositoryException::class)
    override fun findByNickname(nickname: String): PlayerData? {
        return makeTransactionAndReturn { session -> session.get(HibernatePlayerData::class.java, nickname) }
    }

    @Throws(RepositoryException::class)
    override fun findByUuid(uuid: UUID): PlayerData? {
        return makeTransactionAndReturn { session ->
            val builder = session.criteriaBuilder
            val query = builder.createQuery(HibernatePlayerData::class.java)
            val root = query.from(HibernatePlayerData::class.java)
            session.createQuery(
                query
                    .select(root)
                    .where(builder.equal(root.get<Any>("uuid"), uuid))
            ).setMaxResults(1).singleResultOrNull
        }
    }

    @Throws(RepositoryException::class)
    override fun findAllByUuid(uuid: UUID): Collection<PlayerData> {
        return makeTransactionAndReturn { session ->
            val builder = session.criteriaBuilder
            val query = builder.createQuery(HibernatePlayerData::class.java)
            val root = query.from(HibernatePlayerData::class.java)
            session.createQuery(
                query
                    .select(root)
                    .where(builder.equal(root.get<Any>("uuid"), uuid))
            ).list()
        }
    }

    @Throws(RepositoryException::class)
    override fun findAllByRegistrationDateBetween(from: Date, to: Date): Collection<PlayerData> {
        return makeTransactionAndReturn { session: Session ->
            val builder = session.criteriaBuilder
            val query = builder.createQuery(HibernatePlayerData::class.java)
            val root = query.from(HibernatePlayerData::class.java)
            session.createQuery(
                query
                    .select(root)
                    .where(builder.between(root.get("registrationDate"), from, to))
            ).list()
        }
    }

    private fun makeTransaction(action: Consumer<Session>) {
        try {
            getSessionFactory().inTransaction(action)
        } catch (e: Exception) {
            throw RepositoryException(e)
        }
    }

    private fun <T> makeTransactionAndReturn(action: Function<Session, T>): T {
        val reference = AtomicReference<T>()
        makeTransaction { session ->
            reference.set(action.apply(session))
        }
        return reference.get()
    }

    private fun validateEntity(playerData: PlayerData?): HibernatePlayerData {
        return playerData as? HibernatePlayerData
            ?: throw InvalidPlayerDataException("PlayerData must be an instance of HibernatePlayerData")
    }

    protected abstract fun getSessionFactory(): SessionFactory
}
