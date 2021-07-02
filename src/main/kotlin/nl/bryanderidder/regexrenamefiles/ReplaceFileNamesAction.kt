package nl.bryanderidder.regexrenamefiles

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages.showErrorDialog
import com.intellij.openapi.vfs.VirtualFile
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
        val isEnabled = selectedFiles != null && selectedFiles.size > 0
        event.presentation.isEnabled = isEnabled
        event.presentation.isVisible = isEnabled
    }

    override fun actionPerformed(event: AnActionEvent) {
        val selectedFiles = event.getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY)
        val project = event.getData(PlatformDataKeys.PROJECT)
        val dialog = ReplaceFileNamesDialogWrapper(selectedFiles)
        val groupId = "ProjectViewPopupMenu"
        if (dialog.showAndGet()) {
            WriteCommandAction.runWriteCommandAction(
                project, dialog.title, groupId,
                {
                    renameFiles(selectedFiles, dialog, project)
                }
            )
        }
    }

    private fun renameFiles(files: Array<out VirtualFile>?, dialog: ReplaceFileNamesDialogWrapper, project: Project?) {
        lateinit var renameEvent: RenameEvent
        try {
            files?.forEach { file ->
                if (file.isDirectory) {
                    renameFiles(file.children, dialog, project)
                }
                renameEvent = RenameEvent(file, file.name, createNewFileName(dialog, file.name))
                if (!renameEvent.newName.equals(file.name)) {
                    file.rename(this, renameEvent.newName)
                    changesList.add(renameEvent)
                }
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
            dialog.isUseRegex() -> oldName.replace(fromText.toRegex(), toText)
            else -> oldName.replace(fromText, toText)
        }
    }

    companion object {
        private val LOGGER: Logger = Logger.getInstance(ReplaceFileNamesAction::class.java)
    }
}
