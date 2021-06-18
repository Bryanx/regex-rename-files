package com.github.bryanx.intellijregexreplacefiles.services

import com.github.bryanx.intellijregexreplacefiles.MyBundle
import com.intellij.openapi.project.Project

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
