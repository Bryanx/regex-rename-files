package nl.bryanderidder.regexrenamefiles

import com.intellij.openapi.vfs.VirtualFile

data class RenameEvent(val file: VirtualFile, val oldName: String, val newName: String)
