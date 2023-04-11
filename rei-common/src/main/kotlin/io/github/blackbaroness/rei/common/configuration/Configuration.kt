package io.github.blackbaroness.rei.common.configuration

import at.favre.lib.crypto.bcrypt.BCrypt
import space.arim.dazzleconf.annote.ConfComments
import space.arim.dazzleconf.annote.ConfDefault.*
import space.arim.dazzleconf.annote.IntegerRange
import space.arim.dazzleconf.annote.SubSection

interface Configuration {

    @ConfComments("The repository where player data will be stored. Most often a database.")
    @SubSection
    fun repository(): Repository

    @ConfComments("Settings about players passwords.")
    @SubSection
    fun passwords(): Passwords

    interface Repository {

        @ConfComments(
            "Important feature. Look, the point is that if your repository suddenly becomes unavailable DURING STARTUP,",
            "the plugin simply won't work correctly. To avoid the total destruction of your server, we can disable the server.",
            "It is recommended to use this in conjunction with some kind of reboot system,",
            "then perhaps repeated attempts will lead to success."
        )
        @DefaultBoolean(true)
        fun shutdownServerOnConnectionFailure(): Boolean

        @ConfComments("Type of repository: H2, MYSQL, MARIADB or POSTGRESQL.")
        @DefaultString("H2")
        fun type(): Type?

        @ConfComments(
            "A cache is a thing that keeps a certain amount of data from a database in RAM.",
            "Thus, sometimes you don`t have to “knock” on the database - we already have everything!",
            "This greatly speeds up many operations."
        )
        @SubSection
        fun cache(): Cache

        @ConfComments("Settings for MySQL. If you selected another repository type, just ignore that.")
        @SubSection
        fun mysql(): SqlDatabase

        @ConfComments("Settings for MariaDB. If you selected another repository type, just ignore that.")
        @SubSection
        fun mariadb(): SqlDatabase

        @ConfComments("Settings for PostgreSQL. If you selected another repository type, just ignore that.")
        @SubSection
        fun postgresql(): SqlDatabase

        enum class Type {
            H2,
            MYSQL,
            MARIADB,
            POSTGRESQL
        }

        interface Cache {
            @ConfComments("Do you want to use cache?")
            @DefaultBoolean(true)
            fun enabled(): Boolean

            @ConfComments(
                "The maximum cache size. If the cache gets full, the oldest entries will be deleted.",
                "Too large size will cause the cache access to take longer than expected."
            )
            @IntegerRange(min = 0)
            @DefaultInteger(10000)
            fun maxSize(): Long

            @ConfComments(
                "Old (unused) cache can be cleaned up to make things faster and your RAM happier.",
                "Here you can set after what period of time old cache will be cleaned.",
                "If you don't want to clean up the old cache, set this to 0."
            )
            @DefaultLong(1200)
            fun expireAfterSeconds(): Long
        }

        interface SqlDatabase {

            @ConfComments("Address of the database.")
            @DefaultString("localhost")
            fun address(): String

            @ConfComments("Port of the database. Just left 0 if you want to use the default one.")
            @IntegerRange(max = 65535)
            @DefaultInteger(0)
            fun port(): Int

            @ConfComments("Database name")
            @DefaultString("my-awesome-database")
            fun databaseName(): String

            @ConfComments("Password to access the database.")
            @DefaultString("root")
            fun user(): String

            @ConfComments("Password to access the database. Left untouched if your database doesn't require a password.")
            @DefaultString("your_password")
            fun password(): String

            @ConfComments("Extra arguments to connection url. Left untouched if you don't know what is that.")
            @DefaultStrings("autoReconnect=true")
            fun arguments(): Collection<String>
        }
    }

    interface Passwords {

        @SubSection
        fun hashing(): Hashing

        interface Hashing {

            @ConfComments(
                "Specifies the rounds parameter for generating a BCrypt hash.",
                "The higher this number, the more secure the hash will be,",
                "but at the same time, its generation will take longer.",
                "",
                "If set to 0, Rei will benchmark the system and set to a value that takes approximately 1 second.",
                "This is the worldwide standard."
            )
            @DefaultInteger(0)
            @IntegerRange(min = 0, max = BCrypt.MAX_COST.toLong())
            fun bcryptRounds(): Int
        }
    }
}
