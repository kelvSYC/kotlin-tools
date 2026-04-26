plugins {
    base
    publishing
}

group = "com.kelvsyc"

val cores = file("cores").list { dir, _ -> dir.isDirectory }?.toList().orEmpty()

tasks.clean {
    cores.forEach {
        dependsOn(gradle.includedBuild(it).task(":$name"))
    }
}

tasks.assemble {
    cores.forEach {
        dependsOn(gradle.includedBuild(it).task(":$name"))
    }
}

tasks.check {
    cores.forEach {
        dependsOn(gradle.includedBuild(it).task(":$name"))
    }
}

tasks.build {
    cores.forEach {
        dependsOn(gradle.includedBuild(it).task(":$name"))
    }
}

tasks.register("dokkaGenerate") {
    cores.forEach {
        dependsOn(gradle.includedBuild(it).task(":dokkaGeneratePublicationHtml"))
    }
}

tasks.publish {
    cores.forEach {
        dependsOn(gradle.includedBuild(it).task(":$name"))
    }
}
