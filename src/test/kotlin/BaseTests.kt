import com.intellij.mock.MockVirtualFile
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.testFramework.TestActionEvent
import com.intellij.testFramework.TestDataProvider
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import nl.bryanderidder.regexrenamefiles.IReplaceFileNamesDialog
import nl.bryanderidder.regexrenamefiles.ReplaceFileNamesAction
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class BaseTests : BasePlatformTestCase() {

    private val mockDialog: IReplaceFileNamesDialog = mock {
        on { replaceFromText } doReturn ""
        on { replaceToText } doReturn ""
    }

    fun test_whenInitialized_mocksShouldReturnNonNullValues() {
        assertEquals(mockDialog.replaceFromText, "")
        assertEquals(mockDialog.replaceToText, "")
        assertEquals(mockDialog.isUseRegex, false)
        assertEquals(mockDialog.isRenameNestedFilesSelected, false)
        assertEquals(mockDialog.isRenameNestedDirectoriesSelected, false)
    }

    fun test_whenInitialized_selectedFilesShouldBeEmpty() {
        val testDataProvider = mock<TestDataProvider>()
        assertNull(testDataProvider.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY))
    }

    fun test_whenStubbingSelectedFiles_shouldBeAbleToRetrieveStubbedFile() {
        val testDataProvider = mock<TestDataProvider> {
            on { getData(CommonDataKeys.VIRTUAL_FILE_ARRAY) } doReturn arrayOf(MockVirtualFile("bla"))
        }
        assertEquals(testDataProvider.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY)?.first()?.name, "bla")
    }

    fun test_shouldBeAbleToCallAMockedAction() {
        val action = ReplaceFileNamesAction()
        val testDataProvider = TestDataProvider(project)
        val event = TestActionEvent(testDataProvider)
        event.dataContext
        action.update(event)
        assertFalse(event.presentation.isEnabledAndVisible)
    }
}
