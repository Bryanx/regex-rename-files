package nl.bryanderidder.regexrenamefiles

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.vfs.VirtualFile
import javax.swing.JComponent

/**
 * @author Bryan de Ridder
 */
class ReplaceFileNamesDialogWrapper(val selectedFiles: Array<VirtualFile>?) : DialogWrapper(true) {

    private lateinit var dialog: ReplaceFileNamesDialog

    init {
        title = "Replace text in file names"
        init()
    }

    override fun createCenterPanel(): JComponent? {
        this.dialog = ReplaceFileNamesDialog(selectedFiles)
        dialog.listenForValidationChanges { isValid -> okAction.isEnabled = isValid }
        return dialog.rootPane
    }

    override fun doOKAction() {
        if (okAction.isEnabled) {
            close(OK_EXIT_CODE)
        }
    }

    fun getReplaceFromText(): String = dialog.replaceFromText ?: ""
    fun getReplaceToText(): String = dialog.replaceToText ?: ""
    fun isUseRegex(): Boolean = dialog.isUseRegex
}
