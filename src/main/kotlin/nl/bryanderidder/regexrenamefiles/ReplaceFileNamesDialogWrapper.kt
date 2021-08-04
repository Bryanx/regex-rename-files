package nl.bryanderidder.regexrenamefiles

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.vfs.VirtualFile
import javax.swing.JComponent

/**
 * @author Bryan de Ridder
 */
class ReplaceFileNamesDialogWrapper(private val selectedFiles: List<VirtualFile>) : DialogWrapper(true) {

    private lateinit var dialogVM: ReplaceFileNamesViewModel

    init {
        title = "Replace text in file names"
        init()
    }

    override fun createCenterPanel(): JComponent? {
        this.dialogVM = ReplaceFileNamesViewModel(selectedFiles, ReplaceFileNamesDialog())
        dialogVM.listenForValidationChanges { isValid -> okAction.isEnabled = isValid }
        return dialogVM.rootPane
    }

    override fun doOKAction() {
        if (okAction.isEnabled) {
            close(OK_EXIT_CODE)
        }
    }

    fun getFiles(): List<VirtualFile> = dialogVM.getFiles()
    fun getReplaceFromText(): String = dialogVM.replaceFromText
    fun getReplaceToText(): String = dialogVM.replaceToText
    fun isUseRegex(): Boolean = dialogVM.isUseRegex
}
