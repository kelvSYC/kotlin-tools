rootProject.name = "kotlin-tools"

// Cores
file("cores").list { dir, _ -> dir.isDirectory }?.forEach {
    includeBuild("cores/$it")
}
