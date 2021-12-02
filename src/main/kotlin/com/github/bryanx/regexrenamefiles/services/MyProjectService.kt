package com.github.bryanx.regexrenamefiles.services

import com.intellij.openapi.project.Project
import com.github.bryanx.regexrenamefiles.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
