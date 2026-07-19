package practice.G2.Resolucion

plugins {
    java
    application
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://jogamp.org/deployment/maven")
    }
}

dependencies {
    implementation("com.formdev:flatlaf:3.4")
    implementation("org.jogamp.gluegen:gluegen-rt-main:2.3.2")
    implementation("org.jogamp.jogl:jogl-all-main:2.3.2")
}

application {
    mainClass.set("ProyectoCapas3D")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
