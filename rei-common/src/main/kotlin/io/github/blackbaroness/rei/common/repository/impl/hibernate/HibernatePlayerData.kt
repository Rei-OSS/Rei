package io.github.blackbaroness.rei.common.repository.impl.hibernate

import io.github.blackbaroness.rei.common.entity.PlayerData
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.*

@Entity
@Table(name = "rei_players")
class HibernatePlayerData : PlayerData {

    @Id
    private var nickname: String? = null
    private var uuid: UUID? = null
    private var password: String? = null
    private var registrationDate: Instant? = null

    constructor()

    constructor(nickname: String, uuid: UUID) {
        this.nickname = nickname
        this.uuid = uuid
    }

    override fun nickname(): String {
        return nickname!!
    }

    override fun uuid(): UUID {
        return uuid!!
    }

    override fun registrationDate(): Instant? {
        return registrationDate!!
    }

    override fun registrationDate(registrationDate: Instant?) {
        this.registrationDate = registrationDate
    }

    override fun password(): String? {
        return password!!
    }

    override fun password(password: String?) {
        this.password = password
    }
}
