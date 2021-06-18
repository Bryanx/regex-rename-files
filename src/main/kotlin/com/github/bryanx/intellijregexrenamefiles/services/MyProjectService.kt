package com.github.bryanx.intellijregexrenamefiles.services

import com.github.bryanx.intellijregexrenamefiles.MyBundle
import com.intellij.openapi.project.Project

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
