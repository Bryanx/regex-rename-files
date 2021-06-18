package com.github.bryanx.intellijregexrenamefiles

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.DumbAwareAction
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
        val dialog = ReplaceFileNamesDialogWrapper(selectedFiles)
        if (dialog.showAndGet()) {
            selectedFiles?.forEach { file ->
                ApplicationManager.getApplication().runWriteAction {
                    val newName = createNewFileName(dialog, file.name)
                    file.rename(this, newName)
                }
            }
        }
    }

    private fun createNewFileName(dialog: ReplaceFileNamesDialogWrapper, oldName: @NotNull String): String {
        val fromText = dialog.getReplaceFromText()
        val toText = dialog.getReplaceToText()
        return if (dialog.isUseRegex())
            oldName.replace(fromText.toRegex(), toText)
        else
            oldName.replace(fromText, toText)
    }
}