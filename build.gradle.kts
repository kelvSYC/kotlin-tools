plugins {
    base
    publishing
}

group = "com.kelvsyc"

val components = file("cores").list { dir, _ -> dir.isDirectory }?.toList().orEmpty()

tasks.clean {
    components.forEach {
        dependsOn(gradle.includedBuild(it).task(":$name"))
    }
    dependsOn(gradle.includedBuild("metadata").task(":bom:$name"))
}

tasks.assemble {
    components.forEach {
        dependsOn(gradle.includedBuild(it).task(":$name"))
    }
    dependsOn(gradle.includedBuild("aggregation").task(":dokka:$name"))
}

tasks.check {
    components.forEach {
        dependsOn(gradle.includedBuild(it).task(":$name"))
    }
}

tasks.build {
    components.forEach {
        dependsOn(gradle.includedBuild(it).task(":$name"))
    }
    dependsOn(gradle.includedBuild("metadata").task(":catalog:generateCatalogAsToml"))
    dependsOn(gradle.includedBuild("aggregation").task(":dokka:$name"))
    dependsOn(gradle.includedBuild("metadata").task(":bom:$name"))
}

tasks.register("dokkaGenerate") {
    dependsOn(gradle.includedBuild("aggregation").task(":dokka:dokkaGeneratePublicationHtml"))
}

tasks.publish {
    components.forEach {
        dependsOn(gradle.includedBuild(it).task(":$name"))
    }
    dependsOn(gradle.includedBuild("metadata").task(":catalog:$name"))
    dependsOn(gradle.includedBuild("metadata").task(":bom:$name"))
}
