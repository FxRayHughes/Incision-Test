import io.izzel.taboolib.gradle.*
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    java
    id("io.izzel.taboolib") version "2.0.31"
    kotlin("jvm") version "2.3.0"
}

taboolib {
    env {
        // 安装模块
        install(Basic, Bukkit, BukkitHook, BukkitNMSUtil,BukkitNMS)
        install(BukkitUI)           // UI界面
        install(Database)           // 数据库
        install(Kether)             // 条件判断
        install(CommandHelper)      // 命令系统
        install("incision")
        repoTabooLib = project.repositories.mavenLocal().url.toString()
        debug = true
    }
    version {
        taboolib = "6.3.0-local-dev"
        coroutines = "1.8.1"
    }
    // relocate("top.maplex.arim","top.maplex.youerproject.arim") 工具库重定向
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        // 枫溪的仓库
        url = uri("https://nexus.maplex.top/repository/maven-public/")
        isAllowInsecureProtocol = true
    }
}

dependencies {
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))
//    compileOnly("ink.ptms.core:v11200:11200")
    // Source: https://mvnrepository.com/artifact/org.apache.commons/commons-lang3
    implementation("org.apache.commons:commons-lang3:3.20.0")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.fromTarget("1.8")
    }
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
