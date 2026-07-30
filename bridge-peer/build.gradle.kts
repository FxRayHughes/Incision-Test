import io.izzel.taboolib.gradle.Basic
import io.izzel.taboolib.gradle.Bukkit
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    java
    id("io.izzel.taboolib") version "2.0.31"
    kotlin("jvm") version "2.3.0"
}

group = "top.maplex.incisionpeer"
version = "1.0.0"

taboolib {
    description {
        // 固定名称供主测试插件精确禁用，避免误操作服务端上的其他插件。
        name = "Incision-Bridge-Peer"
    }
    env {
        install(Basic, Bukkit)
        install("incision")
        repoTabooLib = project.repositories.mavenLocal().url.toString()
        debug = true
    }
    version {
        taboolib = "6.3.0-local-dev"
    }
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://nexus.maplex.top/repository/maven-public/")
        isAllowInsecureProtocol = true
    }
}

dependencies {
    compileOnly(kotlin("stdlib"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

kotlin {
    compilerOptions {
        // 测试插件必须与 Incision 的 Java 8 公共边界一致，才能进入全部服务端版本矩阵。
        jvmTarget = JvmTarget.fromTarget("1.8")
    }
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
