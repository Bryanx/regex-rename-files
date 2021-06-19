package com.github.bryanx.intellijregexrenamefiles

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.annotations.NotNull

/**
 * @author Bryan de Ridder
 */
class ReplaceFileNamesAction : DumbAwareAction() {
    override fun update(event: AnActionEvent) {
        val selectedFiles = event.getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY)
        val isEnabled = selectedFiles != null && selectedFiles.size > 1
        event.presentation.isEnabled = isEnabled
        event.presentation.isVisible = isEnabled
    }

    override fun actionPerformed(event: AnActionEvent) {
        val selectedFiles = event.getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY)
        val project = event.getData(PlatformDataKeys.PROJECT)
        val dialog = ReplaceFileNamesDialogWrapper(selectedFiles)
        val groupId = "ProjectViewPopupMenu"
        if (dialog.showAndGet()) {
            WriteCommandAction.runWriteCommandAction(project, dialog.title, groupId, {
                renameFiles(selectedFiles, dialog)
            })
        }
    }

    private fun renameFiles(selectedFiles: Array<out VirtualFile>?, dialog: ReplaceFileNamesDialogWrapper) {
        selectedFiles?.forEach { file ->
            val newName = createNewFileName(dialog, file.name)
            file.rename(this, newName)
        }
    }

    private fun createNewFileName(dialog: ReplaceFileNamesDialogWrapper, oldName: @NotNull String): String {
        val fromText = dialog.getReplaceFromText()
        val toText = dialog.getReplaceToText()
        return when {
            dialog.isUseRegex() -> oldName.replace(fromText.toRegex(), toText)
            else -> oldName.replace(fromText, toText)
        }
    }
}
