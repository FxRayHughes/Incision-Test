import io.izzel.taboolib.gradle.*
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    java
    id("io.izzel.taboolib") version "2.0.31"
    kotlin("jvm") version "2.3.0"
}

taboolib {
    description {
        dependencies {
            // 只约束测试启动顺序；peer 缺失时仍允许主插件加载并给出明确的硬性用例失败。
            name("Incision-Bridge-Peer").optional(true)
        }
    }
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
    // JMH 由服务端进程内运行；fork=0 才能复用已经完成 Incision 织入的插件 ClassLoader。
    implementation("org.openjdk.jmh:jmh-core:1.37")
    annotationProcessor("org.openjdk.jmh:jmh-generator-annprocess:1.37")
    // Instrumentation 矩阵显式提供标准 attach 实现，避免把发行版缺少 tools.jar 误判为 Incision 性能差异。
    implementation("net.bytebuddy:byte-buddy-agent:1.14.18")
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
