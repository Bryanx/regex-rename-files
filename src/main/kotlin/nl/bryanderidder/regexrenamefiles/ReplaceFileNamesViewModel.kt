package nl.bryanderidder.regexrenamefiles

import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import javax.swing.JRootPane

class ReplaceFileNamesViewModel(private val selectedFiles: List<VirtualFile>, val view: IReplaceFileNamesDialog) {
    private var validationListener: (Boolean) -> Unit = {}
    val rootPane: JRootPane get() = view.rootPane
    val replaceFromText: String get() = view.replaceFromText
    val replaceToText: String get() = view.replaceToText
    val isUseRegex: Boolean get() = view.isUseRegex
    val isLowerCase: Boolean get() = view.isLowerCase

    init {
        initialiseViews()
        addEventListeners()
    }

    private fun initialiseViews() {
        updatePreview()
        view.setVisibleNestedFilesCheckBox(selectedFiles.any { it.isDirectory })
        view.setVisibleNestedDirectoriesCheckBox(selectedFiles.any { it.isDirectory })
    }

    private fun addEventListeners() {
        view.onUpdateFromTextField { updatePreview() }
        view.onUpdateToTextField { updatePreview() }
        view.onUpdateRegexCheckBox { updatePreview() }
        view.onUpdateRenameNestedFilesCheckBox { updatePreview() }
        view.onUpdateRenameNestedDirectoriesCheckBox { updatePreview() }
        view.onUpdateRenameToLowerCaseCheckBox { updatePreview() }
    }

    private fun updatePreview() {
        var isValid = true
        val firstFileName: String = getFirstFileName()
        view.setDescriptionText("Replace text in " + getFiles().size + " file names.")
        try {
            updatePreview(firstFileName, view.isUseRegex, view.isLowerCase)
        } catch (e: Exception) {
            view.setPreviewText(e.message ?: "")
            isValid = false
        }
        validationListener.invoke(isValid)
    }

    private fun updatePreview(name: String, isUseRegex: Boolean, isLowerCase: Boolean) {
        when {
            isUseRegex ->
                when {
                    isLowerCase ->
                        view.setPreviewText(
                            "Preview: " + name.replace(
                                view.replaceFromText.toRegex(),
                                view.replaceToText
                            ).lowercase()
                        )

                    else ->
                        view.setPreviewText(
                            "Preview: " + name.replace(
                                view.replaceFromText.toRegex(),
                                view.replaceToText
                            )
                        )
                }

            else ->
                when {
                    isLowerCase ->
                        view.setPreviewText(
                            "Preview: " + name.replace(view.replaceFromText, view.replaceToText).lowercase()
                        )

                    else ->
                        view.setPreviewText("Preview: " + name.replace(view.replaceFromText, view.replaceToText))
                }
        }
    }

    fun getFiles(): List<VirtualFile> {
        return selectedFiles + getNestedFiles()
    }

    private fun getNestedFiles(): List<VirtualFile> {
        return selectedFiles.flatMap { file: VirtualFile ->
            VfsUtil.collectChildrenRecursively(file)
                .filter { nestedFile: VirtualFile -> !selectedFiles.contains(nestedFile) }
                .filter { nestedFile: VirtualFile -> filterNestedFiles(nestedFile) }
        }
    }

    private fun filterNestedFiles(nestedFile: VirtualFile): Boolean {
        val isRenameNestedFiles: Boolean = view.isRenameNestedFilesSelected
        val isRenameNestedDirectories: Boolean = view.isRenameNestedDirectoriesSelected
        return when {
            !isRenameNestedDirectories && !isRenameNestedFiles -> false
            isRenameNestedDirectories && isRenameNestedFiles -> true
            isRenameNestedDirectories && !isRenameNestedFiles -> nestedFile.isDirectory
            else -> !nestedFile.isDirectory
        }
    }

    private fun getFirstFileName(): String {
        return getFiles()
            .map { it.name }
            .firstOrNull() ?: ""
    }

    fun listenForValidationChanges(listener: (Boolean) -> Unit) {
        validationListener = listener
    }
}
