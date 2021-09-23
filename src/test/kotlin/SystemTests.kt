import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.testFramework.TestActionEvent
import com.intellij.testFramework.TestDataProvider
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import nl.bryanderidder.regexrenamefiles.ReplaceFileNamesAction

class SystemTests : BasePlatformTestCase() {

    fun test_action() {
        val action = ReplaceFileNamesAction()
        val testDataProvider = TestDataProvider(project)
        testDataProvider.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY)
        val event = TestActionEvent(testDataProvider)
        event.dataContext
        action.update(event)
    }
}
