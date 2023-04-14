plugins {
    id("io.github.blackbaroness.rei.kotlin-library-conventions")
}

dependencies {
    compileOnly("org.jetbrains:annotations:24.0.1")

    implementation("org.hibernate.orm:hibernate-core:6.2.1.Final")
    implementation("org.hibernate.orm:hibernate-hikaricp:6.2.0.Final")
    implementation("org.hibernate.orm:hibernate-jcache:6.2.1.Final")
    implementation("com.google.inject:guice:5.1.0")
    implementation("io.github.blackbaroness:fastutil-extender-guice:1.2.0")
    implementation("com.fasterxml.uuid:java-uuid-generator:4.1.0")
    implementation("space.arim.dazzleconf:dazzleconf-ext-snakeyaml:1.3.0-M1")
    implementation("at.favre.lib:bcrypt:0.10.2")
    implementation("com.google.guava:guava:31.1-jre")
}
