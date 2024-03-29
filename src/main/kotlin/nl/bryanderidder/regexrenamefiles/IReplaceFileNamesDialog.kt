package nl.bryanderidder.regexrenamefiles

import javax.swing.JRootPane
import javax.swing.JTextField

/**
 * Interface for view dialog
 *
 * @author Bryan de Ridder
 */
interface IReplaceFileNamesDialog {
    val rootPane: JRootPane
    val replaceFromText: String
    val replaceToText: String
    val isUseRegex: Boolean
    val isLowerCase: Boolean
    fun setDescriptionText(text: String)
    fun setPreviewText(text: String)
    fun onUpdateRegexCheckBox(onUpdateRegexCheckBox: Runnable)
    fun onUpdateRenameNestedFilesCheckBox(onUpdateRenameNestedFilesCheckBox: Runnable)
    fun onUpdateRenameNestedDirectoriesCheckBox(onUpdateRenameNestedDirectoriesCheckBox: Runnable)
    fun onUpdateRenameToLowerCaseCheckBox(onUpdateRenameToLowerCaseCheckBox: Runnable)
    fun setVisibleNestedFilesCheckBox(isVisible: Boolean)
    fun setVisibleNestedDirectoriesCheckBox(isVisible: Boolean)
    fun onUpdateFromTextField(onUpdateFromTextField: Runnable)
    fun onUpdateToTextField(onUpdateToTextField: Runnable)
    fun getTextField1(): JTextField?
    val isRenameNestedFilesSelected: Boolean
    val isRenameNestedDirectoriesSelected: Boolean
}
