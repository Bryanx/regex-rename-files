package nl.bryanderidder.regexrenamefiles

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.vfs.VirtualFile
import javax.swing.JComponent

/**
 * @author Bryan de Ridder
 */
class ReplaceFileNamesDialogWrapper(private val selectedFiles: List<VirtualFile>) : DialogWrapper(true) {
    companion object {
        const val TITLE = "Replace Text in File Names"
    }

    private lateinit var dialogVM: ReplaceFileNamesViewModel

    init {
        title = TITLE
        init()
    }

    override fun createCenterPanel(): JComponent {
        this.dialogVM = ReplaceFileNamesViewModel(selectedFiles, ReplaceFileNamesDialog())
        dialogVM.listenForValidationChanges { isValid -> okAction.isEnabled = isValid }
        return dialogVM.rootPane
    }

    override fun doOKAction() {
        if (okAction.isEnabled) {
            close(OK_EXIT_CODE)
        }
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        return dialogVM.view.getTextField1()
    }

    fun getFiles(): List<VirtualFile> = dialogVM.getFiles()
    fun getReplaceFromText(): String = dialogVM.replaceFromText
    fun getReplaceToText(): String = dialogVM.replaceToText
    fun isUseRegex(): Boolean = dialogVM.isUseRegex
    fun isLowerCase(): Boolean = dialogVM.isLowerCase
}
