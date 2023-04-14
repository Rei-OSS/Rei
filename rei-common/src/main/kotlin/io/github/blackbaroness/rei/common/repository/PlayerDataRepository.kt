package io.github.blackbaroness.rei.common.repository

import io.github.blackbaroness.rei.common.entity.PlayerData
import org.checkerframework.checker.index.qual.NonNegative
import java.util.*

interface PlayerDataRepository {

    fun openConnection()

    fun closeConnection()

    fun name(): String?

    fun create(nickname: String, uuid: UUID): PlayerData

    fun merge(playerData: PlayerData): PlayerData?

    fun refresh(playerData: PlayerData)

    fun delete(playerData: PlayerData)

    fun ping(): @NonNegative Long

    fun findByNickname(nickname: String): PlayerData?

    fun findByUuid(uuid: UUID): PlayerData?

    fun findAllByUuid(uuid: UUID): Collection<PlayerData>

    fun findAllByRegistrationDateBetween(from: Date, to: Date): Collection<PlayerData>
}
