package nl.bryanderidder.regexrenamefiles

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages.showErrorDialog
import nl.bryanderidder.regexrenamefiles.icons.RegexRenameFilesIcons.ActionIcon
import org.jetbrains.annotations.NotNull
import java.io.IOException

/**
 * @author Bryan de Ridder
 */
class ReplaceFileNamesAction : DumbAwareAction(ActionIcon) {

    private var changesList = mutableListOf<RenameEvent>()

    override fun update(event: AnActionEvent) {
        val selectedFiles = event.getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY)
        event.presentation.isEnabledAndVisible = event.project != null &&
                selectedFiles != null &&
                (selectedFiles.size > 1 || selectedFiles.isNotEmpty() && selectedFiles[0].children.isNotEmpty())
    }

    override fun actionPerformed(event: AnActionEvent) {
        val selectedFiles = event.getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY)?.toList() ?: listOf()
        val project = event.project
        val dialog = ReplaceFileNamesDialogWrapper(selectedFiles)
        val groupId = "ProjectViewPopupMenu"
        if (dialog.showAndGet()) {
            WriteCommandAction.runWriteCommandAction(
                project, dialog.title, groupId,
                {
                    renameFiles(dialog, project)
                }
            )
        }
    }

    private fun renameFiles(dialog: ReplaceFileNamesDialogWrapper, project: Project?) {
        lateinit var renameEvent: RenameEvent
        try {
            dialog.getFiles().forEach { file ->
                renameEvent = RenameEvent(file, file.name, createNewFileName(dialog, file.name))
                file.rename(this, renameEvent.newName)
                changesList.add(renameEvent)
            }
        } catch (e: IOException) {
            val errorMessage = "File '${renameEvent.newName}' already exists " +
                    "in directory '${renameEvent.file.parent.path}'"
            LOGGER.warn(errorMessage, e)
            revertRenameFiles()
            invokeLater {
                showErrorDialog(project, errorMessage, dialog.title)
            }
        }
        changesList.clear()
    }

    private fun revertRenameFiles() = changesList.forEach {
        it.file.rename(this, it.oldName)
    }

    private fun createNewFileName(dialog: ReplaceFileNamesDialogWrapper, oldName: @NotNull String): String {
        val fromText = dialog.getReplaceFromText()
        val toText = dialog.getReplaceToText()
        return when {
            dialog.isUseRegex() -> when {
                dialog.isLowerCase() ->
                    oldName.replace(fromText.toRegex(), toText).lowercase()

                else -> oldName.replace(fromText.toRegex(), toText)
            }

            else -> when {
                dialog.isLowerCase() ->
                    oldName.replace(fromText, toText).lowercase()

                else -> oldName.replace(fromText, toText)
            }
        }
    }

    companion object {
        private val LOGGER: Logger = Logger.getInstance(ReplaceFileNamesAction::class.java)
    }
}
