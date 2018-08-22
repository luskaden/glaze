package com.lucadenti.glaze

import org.apache.maven.model.Model
import java.io.FileReader
import org.apache.maven.model.io.xpp3.MavenXpp3Reader

object PomProperties {
    private val reader = MavenXpp3Reader()
    val data: Model = reader.read(FileReader("pom.xml"))!!
}