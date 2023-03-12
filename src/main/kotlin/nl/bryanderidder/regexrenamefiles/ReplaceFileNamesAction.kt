package nl.bryanderidder.regexrenamefiles

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages.showErrorDialog
import com.intellij.openapi.util.text.StringUtil
import nl.bryanderidder.regexrenamefiles.icons.RegexRenameFilesIcons.ActionIcon
import java.io.IOException

/**
 * @author Bryan de Ridder
 */
class ReplaceFileNamesAction : DumbAwareAction(ActionIcon) {

    override fun update(event: AnActionEvent) {
        val selectedFiles = event.getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY)
        event.presentation.isEnabledAndVisible = event.project != null &&
                selectedFiles != null && when (selectedFiles.size) {
            0 -> false
            1 -> selectedFiles.first().children?.isNotEmpty() ?: false
            else -> true
        }
    }

    override fun actionPerformed(event: AnActionEvent) {
        val selectedFiles = event.getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY)?.toList() ?: return
        val project = event.project ?: return
        val dialog = ReplaceFileNamesDialogWrapper(selectedFiles)
        val groupId = "ReplaceFileNamesAction"
        if (dialog.showAndGet()) {
            val events: List<RenameEvent> = ReadAction.compute<List<RenameEvent>, Throwable> {
                prepareFilesRename(dialog, project)
            } ?: return
            WriteCommandAction.runWriteCommandAction(project, ReplaceFileNamesDialogWrapper.TITLE, groupId, {
                renameFiles(project, events)
            })
        }
    }

    private fun prepareFilesRename(dialog: ReplaceFileNamesDialogWrapper, project: Project): List<RenameEvent>? {
        val events = dialog.getFiles().mapNotNull { file ->
            val oldName = file.name
            val newName = createNewFileName(dialog, oldName)
            if (newName != oldName) RenameEvent(file, oldName, newName) else null
        }
        if (events.isEmpty()) return null
        val errors = validate(events)
        if (errors.isNotEmpty()) {
            val errorMessage = "Cannot rename ${errors.size} ${StringUtil.pluralize("file", errors.size)}:\n${errors.joinToString("\n")}"
            LOGGER.warn(errorMessage)
            invokeLater {
                showErrorDialog(project, errorMessage, ReplaceFileNamesDialogWrapper.TITLE)
            }
            return null
        }
        return events
    }

    private fun renameFiles(project: Project, events: List<RenameEvent>) {
        val performedEvents = ArrayList<RenameEvent>()
        for (renameEvent in events) {
            try {
                renameEvent.file.rename(this, renameEvent.newName)
                performedEvents.add(renameEvent)
            } catch (e: IOException) {
                val errorMessage = "Cannot rename file '${renameEvent.file.name}', rolled back other renames. Error: ${e.message}"
                LOGGER.warn(errorMessage, e)
                revertRenameFiles(performedEvents)
                invokeLater {
                    showErrorDialog(project, errorMessage, ReplaceFileNamesDialogWrapper.TITLE)
                }
                return
            }
        }
    }

    private fun validate(events: List<RenameEvent>): List<String> {
        val errors = ArrayList<String>()
        for (fs in events.map { it.file.fileSystem }.toSet()) {
            if (fs.isReadOnly) {
                errors.add("File system containing one of files is read only")
            }
        }
        for (it in events) {
            if (!it.file.isValid) {
                errors.add("File '${it.file.name}' is not valid")
                continue
            }
            if (!it.file.fileSystem.isValidName(it.newName)) {
                errors.add("New file name '${it.newName}' is not supported by the file system")
                continue
            }
            val parent = it.file.parent
            if (parent?.findChild(it.newName) != null) {
                errors.add("New name '${it.newName}' for '${it.oldName}' is already used in directory '${parent.path}'")
                continue
            }
        }
        return errors
    }

    private fun revertRenameFiles(performedEvents: List<RenameEvent>) = performedEvents.forEach {
        it.file.rename(this, it.oldName)
    }

    private fun createNewFileName(dialog: ReplaceFileNamesDialogWrapper, oldName: String): String {
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
